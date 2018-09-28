package one.oktw.galaxy.command.admin.galaxyManage

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.data.extensions.update
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextColors.GREEN
import org.spongepowered.api.text.format.TextColors.RED
import java.util.*

class SetSize : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .executor(this)
            .permission("oktw.command.admin.galaxyManage.setSize")
            .arguments(
                GenericArguments.optionalWeak(GenericArguments.uuid(Text.of("planet"))),
                GenericArguments.integer(Text.of("size"))
            )
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val maxChunkSize = 375000
        val minChunkSize = 0
        val size = args.getOne<Int>("size").get()
        if (size > maxChunkSize || size < minChunkSize) {
            src.sendMessage(Text.of(TextColors.RED, "Error: Size must be between $minChunkSize and $maxChunkSize"))
            return CommandResult.empty()
        }
        var planetUUID: UUID? = args.getOne<UUID>("planet").orElse(null)
        launch {
            val galaxy = planetUUID?.let { galaxyManager.get(planet = it) } ?: (src as? Player)?.world?.let { galaxyManager.get(it) }

            if (planetUUID == null) {
                planetUUID = galaxy?.getPlanet((src as Player).world)?.uuid
            }

            if (planetUUID != null) {
                //update database
                galaxy!!.update { getPlanet(planetUUID!!)!!.size = size }
                //update boarder
                val planet = galaxy.getPlanet(planetUUID!!)
                PlanetHelper.updatePlanet(planet!!)
                src.sendMessage(Text.of(GREEN, "Size of ${planet.name} was set to ${planet.size}!"))
            } else {
                src.sendMessage(Text.of(RED, "Not enough argument: Planet not found or missing."))
            }
        }
        return CommandResult.success()
    }
}
