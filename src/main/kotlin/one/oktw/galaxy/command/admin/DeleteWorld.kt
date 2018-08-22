package one.oktw.galaxy.command.admin

import kotlinx.coroutines.experimental.future.await
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.*

class DeleteWorld : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
        .permission("oktw.command.admin.deleteWorld")
        .arguments(GenericArguments.uuid(Text.of("world")))
        .executor(this)
        .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        launch {
            if (PlanetHelper.removePlanet(args.getOne<UUID>("world").get()).await()) {
                src.sendMessage(Text.of(TextColors.RED, "World deleted!"))
            } else {
                src.sendMessage(Text.of(TextColors.RED,"Failed!"))
            }
        }
        return CommandResult.success()
    }
}
