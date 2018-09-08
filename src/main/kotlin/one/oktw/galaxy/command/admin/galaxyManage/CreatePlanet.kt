package one.oktw.galaxy.command.admin.galaxyManage

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.command.CommandHelper
import one.oktw.galaxy.galaxy.data.extensions.createPlanet
import one.oktw.galaxy.galaxy.planet.enums.PlanetType
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.*

class CreatePlanet : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .executor(this)
            .permission("oktw.command.admin.galaxyManage.createPlanet")
            .arguments(
                GenericArguments.optional(GenericArguments.uuid(Text.of("galaxy"))),
                GenericArguments.string(Text.of("name")),
                GenericArguments.optional(GenericArguments.enumValue(Text.of("Type"), PlanetType::class.java), PlanetType.NORMAL)
            )
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val uuid = args.getOne<UUID>("galaxy").orElse(null)
        launch {
            try {
                val galaxy = CommandHelper.getGalaxy(uuid, src)
                val planet = galaxy.createPlanet(args.getOne<String>("name").get(), args.getOne<PlanetType>("Type").get())
                src.sendMessage(Text.of(TextColors.GREEN, "Planet ${planet.name} created: ${planet.uuid}"))
            } catch (e: IllegalArgumentException) {
                src.sendMessage(Text.of(TextColors.RED, "Error: ", e.message))
                if (e.message == "Not enough arguments!") {
                    src.sendMessage(Text.of(TextColors.RED, Sponge.getCommandManager().getUsage(src)))
                }
            } catch (e: NotImplementedError) {
                src.sendMessage(Text.of(TextColors.RED, "Error: ", e.message))
            }
        }
        return CommandResult.success()
    }
}
