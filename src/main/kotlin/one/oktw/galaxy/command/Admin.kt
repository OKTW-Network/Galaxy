package one.oktw.galaxy.command

import one.oktw.galaxy.command.admin.*
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandSpec

class Admin : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
        .permission("oktw.command.admin")
        .child(Gun().spec, "gun")
        .child(Viewer().spec, "viewer")
        .child(TPX().spec, "tpx")
        .child(PlayerInfo().spec, "player")
        .child(GalaxyInfo().spec, "galaxyInfo")
        .child(GalaxyManage().spec, "galaxyManage")
        .child(Block().spec, "block")
        .child(DeleteWorld().spec, "deleteWorld")
        .child(UnloadWorld().spec, "unloadWorld")
        .executor(this)
        .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        src.sendMessage(Sponge.getCommandManager().getUsage(src))

        return CommandResult.success()
    }
}
