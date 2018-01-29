package one.oktw.galaxy.command

import one.oktw.galaxy.Main.Companion.main
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*
import java.util.concurrent.TimeUnit

val Btnlist : ArrayList<UUID> = ArrayList()
class UnStuck : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
                .executor(this)
                .description(Text.of("卡點自救"))
                .permission("oktw.command.unstuck")
                .build()

    override fun execute(src: CommandSource, args: CommandContext?): CommandResult {
        if (src is Player) {
            val random = UUID.randomUUID()
            val retrybutton = Text.builder("高度提高一點試試")
                    .color(TextColors.AQUA)
                    .style(TextStyles.UNDERLINE)
                    .onClick(TextActions.executeCallback {
                        if (random in Btnlist) {
                            Btnlist.remove(random)
                            if (src.setLocationSafely(src.location.add(0.0, 2.0, 0.0))) {
                                src.sendMessage(Text.of(TextColors.GREEN, "已嘗試自救"))
                            } else {
                                src.sendMessage(Text.of(TextColors.RED, "自救失敗，找不到安全位置"))
                            }
                        }
                    })
                    .build()
            if (src.setLocationSafely(src.location)) {
                Btnlist.add(random)
                src.sendMessage(Text.of(TextColors.GREEN, "已嘗試自救\n",TextColors.GOLD,"覺得沒被救到嗎?",retrybutton))
                Task.builder()
                        .execute{->
                            Btnlist.remove(random)
                        }
                        .delay(300,TimeUnit.SECONDS).submit(main)
                return CommandResult.affectedEntities(1)
            } else {
                if (src.setLocationSafely(src.location.add(0.0, 2.0, 0.0))){
                    Btnlist.add(random)
                    src.sendMessage(Text.of(TextColors.GREEN, "已嘗試自救\n",TextColors.GOLD,"覺得沒被救到嗎?",retrybutton))
                    Task.builder()
                            .execute{->
                                Btnlist.remove(random)
                            }
                            .delay(300,TimeUnit.SECONDS).submit(main)
                    return CommandResult.affectedEntities(1)
                }else {
                    src.sendMessage(Text.of(TextColors.RED, "自救失敗，找不到安全位置"))
                    return CommandResult.success()
                }
            }}
        return CommandResult.empty()
    }
}
