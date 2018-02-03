package one.oktw.galaxy.command

import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors

class Hat : CommandBase {

    override val spec: CommandSpec
        get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.hat")
                .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src is Player) {
            if (src.getItemInHand(HandTypes.MAIN_HAND).isPresent) {
                var originHelmet: ItemStack = ItemStack.empty()
                src.helmet.ifPresent { originHelmet = src.helmet.get() }
                src.setHelmet(src.getItemInHand(HandTypes.MAIN_HAND).get())
                src.setItemInHand(HandTypes.MAIN_HAND, originHelmet)
                src.sendMessage(Text.of(TextColors.GREEN, "帽子已戴上了喔"))
            } else {
                src.sendMessage(Text.of(TextColors.RED, "請把物品拿在手上"))
            }
            return CommandResult.success()
        }
        return CommandResult.empty()
    }
}
