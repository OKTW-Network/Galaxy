package one.oktw.galaxy.command

import one.oktw.galaxy.internal.DelayHelper
import one.oktw.galaxy.internal.TeleportHelper
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColors
import java.util.*
import kotlin.collections.HashMap
import org.spongepowered.api.text.format.TextStyles
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World


class CommandTeleportAsk : CommandBase {

    var teleportAskTemp = HashMap<UUID,Player>()

    override val spec: CommandSpec
        get() = CommandSpec.builder()
                .executor(this)
                .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("Player"))))
                .permission("oktw.command.teleportask")
                .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {

        if (src is Player) {

            if(args.getOne<Player>("Player").isPresent){

                val uuid = UUID.randomUUID()
                val target = args.getOne<Player>("Player").get()
                teleportAskTemp.put(uuid,target)

                DelayHelper.Delay(Runnable {
                    if(teleportAskTemp.containsKey(uuid)){
                        teleportAskTemp.remove(uuid)
                    }
                    },300)

                val teleportMsg = Text.builder("玩家 ").color(TextColors.YELLOW).append(Text.builder(src.name).color(TextColors.AQUA).build()).append(Text.builder(" 想要傳送到你這，是否接受?").color(TextColors.YELLOW).build())
                        .append(Text.NEW_LINE).append(Text.builder("接受").onHover(TextActions.showText(Text.of(TextColors.RED,"請勿輕易接受其他人的邀請"))).style(TextStyles.UNDERLINE).color(TextColors.GREEN).onClick(
                            TextActions.executeCallback {
                                if(teleportAskTemp.containsKey(uuid)){
                                    src.sendMessage(Text.of(TextColors.GREEN, "對方已接受傳送請求"))
                                    target.sendMessage(Text.of(TextColors.GREEN, "已接受傳送請求"))
                                    Teleport(src, target.location)
                                    teleportAskTemp.remove(uuid)
                                }
                            }).build())
                        .append(Text.of(TextStyles.RESET, " "))
                        .append(Text.builder("拒絕").style(TextStyles.UNDERLINE).onHover(TextActions.showText(Text.of(TextColors.RED,"請勿輕易拒絕其他人的邀請"))).color(TextColors.RED).onClick(
                                TextActions.executeCallback {
                                    if(teleportAskTemp.containsKey(uuid)){
                                        src.sendMessage(Text.of(TextColors.RED, "對方已拒絕傳送請求"))
                                        target.sendMessage(Text.of(TextColors.RED, "已拒絕傳送請求"))
                                        teleportAskTemp.remove(uuid)
                                    }
                                }).build()).build()
                src.sendMessage(Text.of(TextColors.GREEN, "已傳送請求"))
                target.sendMessage(teleportMsg)
                return CommandResult.success()
            }else{
                return CommandResult.empty()
            }

        }

        return CommandResult.empty()

    }

    private fun Teleport(player : Player, location : Location<World>){

        val first_location = player.location

        player.sendMessage(Text.of(TextColors.YELLOW,"請站在原地不要移動，將在 3 秒後傳送......").toText())

        DelayHelper.Delay(Runnable {
            if (first_location.equals(player.location)) {
                if (TeleportHelper.teleport(player,location,false)){
                    player.sendMessage(Text.of(TextColors.GREEN, "傳送成功").toText())
                }else{
                    player.sendMessage(Text.of(TextColors.RED, "傳送失敗，或許是對方或是你的權限不足").toText())
                }

            } else {
                player.sendMessage(Text.of(TextColors.RED, "位置移動，傳送取消").toText())
            }
        },3)
    }

}
