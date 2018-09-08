package one.oktw.galaxy.command.admin.galaxyManage

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.command.CommandHelper
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.*

class SetSize : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .executor(this)
            .permission("oktw.command.admin.galaxyManage.setSize")
            .arguments(
                GenericArguments.firstParsing(
                    GenericArguments.uuid(Text.of("planet")),
                    GenericArguments.integer(Text.of("size"))
                ),
                GenericArguments.optional(GenericArguments.integer(Text.of("size")))
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
        val uuid = args.getOne<UUID>("planet").orElse(null)
        try {
            launch {
                val planet = CommandHelper.getGalaxyAndPlanet(uuid, src).planet
                planet.size = size
                PlanetHelper.updatePlanet(planet)
                src.sendMessage(Text.of(TextColors.GREEN, "Size of ${planet.name} was set to ${planet.size}!"))
            }
        } catch (e: IllegalArgumentException) {
            src.sendMessage(Text.of(TextColors.RED, "Error: ", e.message))
            if (e.message == "Not enough arguments!") {
                src.sendMessage(Text.of(TextColors.RED, Sponge.getCommandManager().getUsage(src)))
            }
        }
        return CommandResult.success()
    }
}
