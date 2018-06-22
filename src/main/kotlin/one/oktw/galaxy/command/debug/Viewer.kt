package one.oktw.galaxy.command.debug

import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.player.event.Viewer.Companion.isViewer
import one.oktw.galaxy.player.event.Viewer.Companion.removeViewer
import one.oktw.galaxy.player.event.Viewer.Companion.setViewer
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text

class Viewer : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
        .permission("oktw.command.debug.viewer")
        .arguments(
            GenericArguments.playerOrSource(Text.of("player")),
            GenericArguments.optional(GenericArguments.bool(Text.of("enable")))
        )
        .executor(this)
        .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val uuid = args.getOne<Player>("player").get().uniqueId

        if (args.hasAny("enable")) {
            if (args.getOne<Boolean>("enable").get()) setViewer(uuid) else removeViewer(uuid)
        }

        src.sendMessage(Text.of("Viewer mode: ", isViewer(uuid)))

        return CommandResult.success()
    }
}