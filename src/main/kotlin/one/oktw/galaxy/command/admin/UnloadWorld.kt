package one.oktw.galaxy.command.admin

import one.oktw.galaxy.command.CommandBase
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.*

class UnloadWorld : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
        .permission("oktw.command.admin.unloadWorld")
        .arguments(GenericArguments.uuid(Text.of("world")))
        .executor(this)
        .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val server = Sponge.getServer()
        server.getWorld(args.getOne<UUID>("world").get()).ifPresent {
            server.unloadWorld(it)
            src.sendMessage(Text.of(TextColors.RED, "World unloaded!"))
        }
        return CommandResult.success()
    }
}
