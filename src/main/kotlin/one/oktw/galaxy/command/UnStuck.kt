package one.oktw.galaxy.command

import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors

class UnStuck : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
                .executor(this)
                .description(Text.of("卡點自救"))
                .permission("oktw.command.unstuck")
                .build()

    override fun execute(src: CommandSource, args: CommandContext?): CommandResult {
        if (src is Player) {
            if (src.setLocationSafely(src.location)) {
                src.sendMessage(Text.of(TextColors.GREEN, "已嘗試自救"))
                return CommandResult.affectedEntities(1)
            } else {
                src.sendMessage(Text.of(TextColors.RED, "自救失敗，找不到安全位置"))
                return CommandResult.success()
            }
        }
        return CommandResult.empty()
    }
}
