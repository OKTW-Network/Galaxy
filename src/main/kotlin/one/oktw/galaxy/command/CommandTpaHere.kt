package one.oktw.galaxy.command

import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions.executeCallback
import org.spongepowered.api.text.action.TextActions.showText
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*

val TPList : ArrayList<UUID> = ArrayList()
val prefix = Text.of(TextColors.AQUA,"[",TextColors.GOLD,"Galaxy",TextColors.AQUA,"]",TextColors.RESET)
class CommandTpaHere : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
                .executor(this)
//                .permission("oktw.command.tpahere")
                .description(Text.of("請求別人傳送到你這"))
                .arguments(GenericArguments.player(Text.of("Player")))
                .build()
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val target = args.getOne<Player>("Player")
        if (src is Player && target.isPresent) {
            if (target.get().name == src.name){
                src.sendMessage(Text.of(prefix,TextColors.GRAY,"你傳送你自己幹嘛?"))
                return CommandResult.empty()
            }
            val random = UUID.randomUUID()
            TPList.add(random)

            src.sendMessage(Text.of(prefix,TextColors.YELLOW, "傳送邀請已傳送給", TextColors.GREEN, target.get().name))

            val rejectbutton = Text.builder("[拒絕]")
                    .color(TextColors.RED)
                    .style(TextStyles.UNDERLINE)
//                    .onHover(showText(Text.of(TextColors.RED, "請勿隨意接受陌人。的邀請")))
                    .onClick(executeCallback{
                        if (random in TPList) {
                            TPList.remove(random)
                            target.get().sendMessage(Text.of(prefix,TextColors.RED, "您委婉地拒絕了請求"))
                            src.sendMessage(Text.of(prefix,TextColors.RED,target.get().name,"拒絕了您的請求"))
                        }
                    })
                    .build()
            val acceptbutton = Text.builder("[接受]")
                    .color(TextColors.AQUA)
                    .style(TextStyles.UNDERLINE)
                    .onHover(showText(Text.of(TextColors.RED, "請勿隨意接受陌人的請求")))
                    .onClick(executeCallback{
                        if (random in TPList) {
                            target.get().setLocationSafely(src.location)
                            TPList.remove(random)
                            target.get().sendMessage(Text.of(prefix, TextColors.GREEN, "已接受請求"))
                            src.sendMessage(Text.of(prefix, TextColors.GREEN, target.get().name, "接受了您的請求"))
                            src.sendMessage(Text.of(prefix, TextColors.GOLD, "正在傳送"))
                        }
                    })
                    .build()
            target.get().sendMessage(
                    Text.of(prefix,TextColors.GREEN, src.name, TextColors.YELLOW, "想要您傳送到他那裏\n",prefix)
                            .concat(acceptbutton).concat(rejectbutton)
            )
            return CommandResult.success()
        }
        return CommandResult.empty()
    }
}