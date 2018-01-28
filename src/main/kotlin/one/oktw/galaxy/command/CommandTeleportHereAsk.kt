package one.oktw.galaxy.command

import one.oktw.galaxy.internal.TPManager
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
import java.util.*

class CommandTeleportHereAsk : CommandBase {

    var teleportHereAskTemp = HashMap<UUID,Player>()

    override val spec: CommandSpec
        get() = CommandSpec.builder()
                .executor(this)
                .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("Player"))))
                .permission("oktw.command.teleporthereask")
                .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {


        if (src is Player) {

            if(args.getOne<Player>("Player").get().isOnline){

                val uuid = UUID.randomUUID()
                val target = args.getOne<Player>("Player").get()
                teleportHereAskTemp.put(uuid,target)

                val teleportMsg = Text.builder("玩家 ").color(TextColors.YELLOW).append(Text.builder(src.name).color(TextColors.AQUA).build()).append(Text.builder(" 想要傳送你到他的位置，是否接受?").color(TextColors.YELLOW).build())
                        .append(Text.NEW_LINE).append(Text.builder("接受").onHover(TextActions.showText(Text.of(TextColors.RED,"請勿輕易接受其他人的邀請"))).style(TextStyles.UNDERLINE).color(TextColors.GREEN).onClick(
                        TextActions.executeCallback {
                            if(teleportHereAskTemp.containsKey(uuid)){
                                if(src.isOnline && target.isOnline) {
                                    src.sendMessage(Text.of(TextColors.GREEN, "對方已接受傳送請求"))
                                    target.sendMessage(Text.of(TextColors.GREEN, "已接受傳送請求"))
                                    TPManager.Teleport(target,src.location)
                                    teleportHereAskTemp.remove(uuid)
                                }else{
                                    teleportHereAskTemp.remove(uuid)
                                }
                            }
                        }).build())
                        .append(Text.builder("拒絕").style(TextStyles.UNDERLINE).onHover(TextActions.showText(Text.of(TextColors.RED,"請勿輕易拒絕其他人的邀請"))).color(TextColors.RED).onClick(
                                TextActions.executeCallback {
                                    if(teleportHereAskTemp.containsKey(uuid)){
                                        if(src.isOnline && target.isOnline) {
                                            src.sendMessage(Text.of(TextColors.RED, "對方已拒絕傳送請求"))
                                            target.sendMessage(Text.of(TextColors.RED, "已拒絕傳送請求"))
                                            teleportHereAskTemp.remove(uuid)
                                        }else{
                                            teleportHereAskTemp.remove(uuid)
                                        }
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
}
