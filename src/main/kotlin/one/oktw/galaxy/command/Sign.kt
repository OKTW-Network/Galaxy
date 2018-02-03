package one.oktw.galaxy.command

import jdk.nashorn.internal.ir.Block
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import sun.awt.windows.ThemeReader.getPosition
import org.spongepowered.api.world.World
import org.spongepowered.api.util.blockray.BlockRayHit
import org.spongepowered.api.util.blockray.BlockRay
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.serializer.TextSerializers


class Sign : CommandBase {

    override val spec: CommandSpec
        get() = CommandSpec.builder()
                .executor(this)
                .arguments( GenericArguments.onlyOne(GenericArguments.integer(Text.of("Line"))),
                            GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("Text (max. length 16)"))))
                .permission("oktw.command.sign")
                .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src is Player) {
            val block = BlockRay.from(src).skipFilter { it.location.blockType != BlockTypes.AIR }.build().next().location
            if(block.blockType == BlockTypes.STANDING_SIGN || block.blockType == BlockTypes.WALL_SIGN){

                val line = args.getOne<Int>("Line").get()
                val lines = block.tileEntity.get().get(SignData::class.java).get().lines()

                if(line < 1 || line > 4){
                    src.sendMessage(Text.of(TextColors.RED,"請輸入行數 1-4"))
                    return CommandResult.empty()
                }

                if(!args.getOne<String>("Text (max. length 16)").isPresent){
                    lines.set(line - 1,Text.of(""))
                    block.tileEntity.get().offer(lines)
                    src.sendMessage(Text.of(TextColors.GREEN,"清空成功"))
                    return CommandResult.empty()
                }

                var text = args.getOne<String>("Text (max. length 16)").get().replace("(&([a-fk-or0-9]))".toRegex(), "\u00A7$2")

                if (TextSerializers.FORMATTING_CODE.deserialize(text).toPlain().length > 16) {
                    src.sendMessage(Text.of(TextColors.RED,"內容超過16字元"))
                    return CommandResult.empty()
                }

                lines.set(line - 1,Text.of(text))
                block.tileEntity.get().offer(lines)
                src.sendMessage(Text.of(TextColors.GREEN,"修改成功"))
            }else{
                src.sendMessage(Text.of(TextColors.RED,"請把準星對準告示牌"))
            }
            return CommandResult.success()
        }
        return CommandResult.empty()
    }
}
