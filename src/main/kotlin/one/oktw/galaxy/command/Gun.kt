package one.oktw.galaxy.command

import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.enums.GunType
import one.oktw.galaxy.enums.UpgradeType.THROUGH
import one.oktw.galaxy.types.Upgrade
import one.oktw.galaxy.types.item.Gun
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text

class Gun : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.gun")
                .arguments(
                        GenericArguments.integer(Text.of("Max Heat")),
                        GenericArguments.doubleNum(Text.of("Range")),
                        GenericArguments.doubleNum(Text.of("Damage")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("Cooling")), 1),
                        GenericArguments.optional(GenericArguments.integer(Text.of("Through"))),
                        GenericArguments.optional(GenericArguments.enumValue(Text.of("Type"), GunType::class.java), GunType.ORIGIN)
                )
                .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src is Player) {
            val traveler = travelerManager.getTraveler(src)
            val gun = Gun(
                    type = args.getOne<GunType>("Type").get(),
                    maxTemp = args.getOne<Int>("Max Heat").get(),
                    cooling = args.getOne<Int>("Cooling").get(),
                    range = args.getOne<Double>("Range").get(),
                    damage = args.getOne<Double>("Damage").get()
            )

            args.getOne<Int>("Through").ifPresent { gun.upgrade += Upgrade(THROUGH, it) }

            traveler.item.gun.clear()
            traveler.item.gun.add(gun)
            traveler.save()

            val item = ItemStack.builder()
                    .itemType(ItemTypes.WOODEN_SWORD)
                    .itemData(DataUUID.Immutable(gun.uuid))
                    .add(Keys.UNBREAKABLE, true)
                    .add(Keys.HIDE_UNBREAKABLE, true)
                    .add(Keys.HIDE_MISCELLANEOUS, true)
                    .add(Keys.HIDE_ATTRIBUTES, true)
                    .add(Keys.HIDE_ENCHANTMENTS, true)
                    .add(Keys.ITEM_DURABILITY, gun.type.id.toInt())
                    .build()

            src.setItemInHand(HandTypes.MAIN_HAND, item)
            src.sendMessage(Text.of(gun.uuid.toString()))
        }
        return CommandResult.success()
    }
}