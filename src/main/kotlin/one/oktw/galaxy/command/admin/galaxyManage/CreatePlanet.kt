package one.oktw.galaxy.command.admin.galaxyManage

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.galaxy.data.extensions.createPlanet
import one.oktw.galaxy.galaxy.planet.enums.PlanetType
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors.GREEN
import org.spongepowered.api.text.format.TextColors.RED
import java.util.*

class CreatePlanet : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .executor(this)
            .permission("oktw.command.admin.galaxyManage.createPlanet")
            .arguments(
                GenericArguments.optionalWeak(GenericArguments.uuid(Text.of("galaxy"))),
                GenericArguments.string(Text.of("name")),
                GenericArguments.optional(GenericArguments.enumValue(Text.of("Type"), PlanetType::class.java), PlanetType.NORMAL)
            )
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val name = args.getOne<String>("name").get()
        val type = args.getOne<PlanetType>("Type").get()
        var galaxyUUID: UUID? = args.getOne<UUID>("galaxy").orElse(null)
        launch {
            val galaxy = galaxyUUID?.let { galaxyManager.get(it) } ?: (src as? Player)?.world?.let { galaxyManager.get(it) }

            if (galaxyUUID == null) {
                galaxyUUID = galaxy?.uuid
            }

            if (galaxyUUID != null) {
                try {
                    val planet = galaxy!!.createPlanet(name, type)
                    src.sendMessage(Text.of(GREEN, "Planet ${planet.name} (${planet.uuid}) created!"))
                } catch (e: IllegalArgumentException) {
                    src.sendMessage(Text.of(RED, "Error: ", e.message))
                } catch (e: NotImplementedError) {
                    src.sendMessage(Text.of(RED, "Error: ", e.message))
                }
            } else {
                src.sendMessage(Text.of(RED, "Not enough argument: galaxy not found or missing."))
            }
        }
        return CommandResult.success()
    }
}
