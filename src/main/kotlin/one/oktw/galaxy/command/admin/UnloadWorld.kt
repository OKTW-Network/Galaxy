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
import org.spongepowered.api.world.storage.WorldProperties

class UnloadWorld : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
        .permission("oktw.command.admin.unloadWorld")
        .arguments(GenericArguments.world(Text.of("World")))
        .executor(this)
        .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val server = Sponge.getServer()
        server.getWorld(args.getOne<WorldProperties>("World").get().uniqueId).ifPresent {
            if (server.unloadWorld(it)) {
                src.sendMessage(Text.of(TextColors.GREEN, "World unloaded!"))
            } else {
                src.sendMessage(Text.of(TextColors.RED, "World unload failed!"))
            }
        }
        return CommandResult.success()
    }
}
