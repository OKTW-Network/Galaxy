package one.oktw.galaxy.command

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.helper.SampleLock.Companion.checkLocked
import one.oktw.galaxy.helper.SampleLock.Companion.lock
import one.oktw.galaxy.helper.SampleLock.Companion.unlock
import one.oktw.galaxy.helper.TeleportHelper
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*
import java.util.concurrent.TimeUnit

class TeleportHereAsk : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .executor(this)
            .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("Player"))))
            .permission("oktw.command.teleport.ask.here")
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src is Player) {
            if (args.getOne<Player>("Player").isPresent) {
                val uuid = UUID.randomUUID()
                val target = args.getOne<Player>("Player").get()
                lock(uuid)

                launch {
                    delay(5, TimeUnit.MINUTES)
                    unlock(uuid)
                }

                val teleportMsg =
                    Text.of(TextColors.YELLOW, "玩家 ", TextColors.AQUA, src.name, TextColors.YELLOW, " 想要傳送你到他的位置，是否接受?")
                        .concat(Text.NEW_LINE)
                        .concat(
                            Text.of(
                                TextActions.showText(Text.of(TextColors.RED, "請勿輕易接受其他人的邀請")),
                                TextActions.executeCallback {
                                    if (!checkLocked(uuid)) {
                                        src.sendMessage(Text.of(TextColors.GREEN, "對方已接受傳送請求"))
                                        target.sendMessage(Text.of(TextColors.GREEN, "已接受傳送請求"))
                                        launch {
                                            teleport(target, src.location)
                                            unlock(uuid)
                                        }
                                    }
                                },
                                TextColors.GREEN,
                                TextStyles.UNDERLINE,
                                "接受",
                                TextStyles.RESET,
                                " ",
                                TextActions.executeCallback {
                                    if (checkLocked(uuid)) {
                                        src.sendMessage(Text.of(TextColors.RED, "對方已拒絕傳送請求"))
                                        target.sendMessage(Text.of(TextColors.RED, "已拒絕傳送請求"))
                                        unlock(uuid)
                                    }
                                },
                                TextColors.RED,
                                TextStyles.UNDERLINE,
                                "拒絕"
                            )
                        )

                src.sendMessage(Text.of(TextColors.GREEN, "已傳送請求"))
                target.sendMessage(teleportMsg)

                return CommandResult.success()
            } else {
                return CommandResult.empty()
            }
        }

        return CommandResult.empty()
    }

    private suspend fun teleport(player: Player, location: Location<World>) {
        val originLocation = player.location

        player.sendMessage(Text.of(TextColors.YELLOW, "請站在原地不要移動，將在 3 秒後傳送......").toText())

        delay(3, TimeUnit.SECONDS)
        if (originLocation == player.location) {
            if (TeleportHelper.teleport(player, location, false)) {
                player.sendMessage(Text.of(TextColors.GREEN, "傳送成功").toText())
            } else {
                player.sendMessage(Text.of(TextColors.RED, "傳送失敗，或許是對方或是你的權限不足").toText())
            }
        } else {
            player.sendMessage(Text.of(TextColors.RED, "位置移動，傳送取消").toText())
        }
    }
}
