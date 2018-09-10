package one.oktw.galaxy.command.admin.galaxyManage

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.data.extensions.update
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

class SetVisit : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .executor(this)
            .permission("oktw.command.admin.galaxyManage.setVisit")
            .arguments(
                GenericArguments.optional(GenericArguments.uuid(Text.of("planet"))),
                GenericArguments.bool(Text.of("visitable"))
            )
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val visitable = args.getOne<Boolean>("visitable").get()
        var planetUUID: UUID? = args.getOne<UUID>("planet").orElse(null)
        launch {
            val galaxy = planetUUID?.let { galaxyManager.get(planet = it) } ?: (src as? Player)?.world?.let { galaxyManager.get(it) }

            if (planetUUID == null) {
                planetUUID = galaxy?.getPlanet((src as Player).world)?.uuid
            }

            if (planetUUID != null) {
                galaxy!!.update { getPlanet(planetUUID!!)!!.visitable = visitable }
                src.sendMessage(Text.of(GREEN, "Visitable of ${galaxy.getPlanet(planetUUID!!)!!.name} was set to $visitable!"))
            } else {
                src.sendMessage(Text.of(RED, "Not enough argument: Planet not found or missing."))
            }
        }
        return CommandResult.success()
    }
}
