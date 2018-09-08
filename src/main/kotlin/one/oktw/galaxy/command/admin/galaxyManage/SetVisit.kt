package one.oktw.galaxy.command.admin.galaxyManage

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.command.CommandHelper
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.data.extensions.update
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.*

class SetVisit : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .executor(this)
            .permission("oktw.command.admin.galaxyManage.setVisit")
            .arguments(
                GenericArguments.firstParsing(
                    GenericArguments.uuid(Text.of("planet")),
                    GenericArguments.bool(Text.of("visitable"))
                ),
                GenericArguments.optional(GenericArguments.bool(Text.of("visitable")))
            )
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        try {
            launch {
                var (galaxy, planet, uuid) = CommandHelper.getGalaxyAndPlanet(
                    args.getOne<UUID>("planet").orElse(null), src
                )
                galaxy.update {
                    planet = this.getPlanet(uuid)!!
                    planet.visitable = args.getOne<Boolean>("visitable").get()
                }
                src.sendMessage(Text.of(TextColors.GREEN, "Visibility of ${planet.name} was set to ${planet.visitable}!"))
            }
        } catch (e: IllegalArgumentException) {
            src.sendMessage(Text.of(TextColors.RED, "Error: ", e.message))
            if (e.message == "Not enough arguments!") {
                src.sendMessage(Text.of(TextColors.RED, spec.getUsage(src)))
            }
        }
        return CommandResult.success()
    }
}
