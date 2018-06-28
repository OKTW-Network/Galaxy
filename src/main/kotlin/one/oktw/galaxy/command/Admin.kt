package one.oktw.galaxy.command

import one.oktw.galaxy.command.debug.*
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandSpec

class Admin : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
        .permission("oktw.command.debug")
        .child(Gun().spec, "gun")
        .child(Viewer().spec, "viewer")
        .child(TPX().spec, "tpx")
        .child(PlayerInfo().spec, "player")
        .child(GalaxyInfo().spec, "galaxy")
        .executor(this)
        .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        src.sendMessage(Sponge.getCommandManager().getUsage(src))

        return CommandResult.success()
    }
}
