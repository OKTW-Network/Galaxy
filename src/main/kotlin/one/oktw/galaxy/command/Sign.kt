package one.oktw.galaxy.command

import org.spongepowered.api.block.BlockTypes.STANDING_SIGN
import org.spongepowered.api.block.BlockTypes.WALL_SIGN
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.serializer.TextSerializers
import org.spongepowered.api.util.blockray.BlockRay

class Sign : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.sign")
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.integer(Text.of("Line"))),
                        GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("Text")))
                )
                .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src !is Player) return CommandResult.empty()

        val blockRay = BlockRay.from(src)
                .distanceLimit(7.0)
                .skipFilter { it.location.blockType == STANDING_SIGN || it.location.blockType == WALL_SIGN }
                .build()

        if (blockRay.hasNext()) {
            val block = blockRay.next().location.tileEntity.orElse(null) ?: return CommandResult.empty()

            val line = args.getOne<Int>("Line").get()
            val text = TextSerializers.FORMATTING_CODE.deserialize(args.getOne<String>("Text").orElse(""))
            val lines = block[Keys.SIGN_LINES].orElse(ArrayList<Text>())

            if (line < 1 || line > 4) {
                src.sendMessage(Text.of(TextColors.RED, "請輸入行數 1-4"))
                return CommandResult.empty()
            }

            if (text.toPlain().length > 16) {
                src.sendMessage(Text.of(TextColors.RED, "內容超過16字元"))
                return CommandResult.empty()
            }

            lines[line - 1] = text
            block.offer(Keys.SIGN_LINES, lines)
            src.sendMessage(Text.of(TextColors.GREEN, "修改成功"))
        } else {
            src.sendMessage(Text.of(TextColors.RED, "請把準心對準告示牌"))
        }

        return CommandResult.success()
    }
}
