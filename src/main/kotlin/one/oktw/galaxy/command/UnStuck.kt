package one.oktw.galaxy.command

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*
import java.util.concurrent.TimeUnit

class UnStuck : CommandBase {
    private val callbackLimit: ArrayList<UUID> = ArrayList()

    override val spec: CommandSpec
        get() = CommandSpec.builder()
                .executor(this)
                .description(Text.of("卡點自救"))
                .permission("oktw.command.unstuck")
                .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src is Player) {
            val random = UUID.randomUUID()
            val retryButton = Text.of(
                    TextColors.AQUA,
                    TextStyles.UNDERLINE,
                    TextActions.executeCallback {
                        if (random in callbackLimit) {
                            callbackLimit.remove(random)
                            if (src.setLocationSafely(src.location.add(0.0, 2.0, 0.0))) {
                                src.sendMessage(Text.of(TextColors.GREEN, "已嘗試自救"))
                            } else {
                                src.sendMessage(Text.of(TextColors.RED, "自救失敗，找不到安全位置"))
                            }
                        }
                    },
                    "高度提高一點試試"
            )

            if (src.setLocationSafely(src.location)) {
                callbackLimit.add(random)
                src.sendMessage(Text.of(TextColors.GREEN, "已嘗試自救\n", TextColors.GOLD, "覺得沒被救到嗎?", retryButton))
                launch {
                    delay(5, TimeUnit.MINUTES)
                    if (random in callbackLimit) {
                        callbackLimit.remove(random)
                    }
                }

                return CommandResult.affectedEntities(1)
            } else if (src.setLocationSafely(src.location.add(0.0, 2.0, 0.0))) {
                callbackLimit.add(random)
                src.sendMessage(Text.of(TextColors.GREEN, "已嘗試自救\n", TextColors.GOLD, "覺得沒被救到嗎?", retryButton))
                launch {
                    delay(5, TimeUnit.MINUTES)
                    if (random in callbackLimit) {
                        callbackLimit.remove(random)
                    }
                }

                return CommandResult.affectedEntities(1)
            }

            src.sendMessage(Text.of(TextColors.RED, "自救失敗，找不到安全位置"))
        }

        return CommandResult.empty()
    }
}
