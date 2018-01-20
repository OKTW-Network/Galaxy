package one.oktw.galaxy.command

import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text

class CommandGalaxy : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.galaxy")
                .description(Text.of("銀河世界管理"))
                .build()
    override fun execute(src: CommandSource?, args: CommandContext?): CommandResult {
        if (src is Player) {
            src.sendMessage(Text.of("Command Executed Successfully").toText())
            return CommandResult.success()
        }
        return CommandResult.empty()
    }
}