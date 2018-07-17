package one.oktw.galaxy.command.admin

import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.galaxy.data.extensions.getMember
import one.oktw.galaxy.galaxy.data.extensions.saveMember
import one.oktw.galaxy.galaxy.traveler.TravelerHelper.Companion.getTraveler
import one.oktw.galaxy.item.enums.GunStyle
import one.oktw.galaxy.item.enums.GunStyle.PISTOL_ORIGIN
import one.oktw.galaxy.item.enums.GunStyle.SNIPER_SIGHT
import one.oktw.galaxy.item.enums.ItemType.PISTOL
import one.oktw.galaxy.item.enums.ItemType.SNIPER
import one.oktw.galaxy.item.enums.UpgradeType.THROUGH
import one.oktw.galaxy.item.type.Gun
import one.oktw.galaxy.item.type.Upgrade
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text

class Gun : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .permission("oktw.command.admin.gun")
            .child(Add().spec, "add")
            .child(Remove().spec, "remove")
            .child(Get().spec, "get")
            .child(List().spec, "list")
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        src.sendMessage(Sponge.getCommandManager().getUsage(src))

        return CommandResult.success()
    }

    class Add : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.admin.gun.add")
                .arguments(
                    GenericArguments.integer(Text.of("Heat")),
                    GenericArguments.integer(Text.of("Max Heat")),
                    GenericArguments.doubleNum(Text.of("Range")),
                    GenericArguments.doubleNum(Text.of("Damage")),
                    GenericArguments.optional(GenericArguments.integer(Text.of("Cooling")), 1),
                    GenericArguments.optional(GenericArguments.integer(Text.of("Through"))),
                    GenericArguments.optional(
                        GenericArguments.enumValue(Text.of("Type"), GunStyle::class.java),
                        PISTOL_ORIGIN
                    )
                )
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            if (src is Player) {
                val traveler = runBlocking { getTraveler(src).await()!! }
                val type = if (args.getOne<GunStyle>("Type").get() == SNIPER_SIGHT) SNIPER else PISTOL
                val gun = Gun(
                    itemType = type,
                    style = args.getOne<GunStyle>("Type").get(),
                    maxTemp = args.getOne<Int>("Max Heat").get(),
                    cooling = args.getOne<Int>("Cooling").get(),
                    range = args.getOne<Double>("Range").get(),
                    damage = args.getOne<Double>("Damage").get(),
                    heat = args.getOne<Int>("Heat").get()
                )

                args.getOne<Int>("Through").ifPresent { gun.upgrade.add(Upgrade(THROUGH, it)) }

                traveler.item.add(gun)
                launch{
                    galaxyManager.get(src.world)?.run {
                        getMember(src.uniqueId)?.also {
                            saveMember(traveler)
                        }
                    }
                }

                gun.createItemStack().let { src.setItemInHand(HandTypes.MAIN_HAND, it) }
                src.sendMessage(Text.of(gun.uuid.toString()))
            }
            return CommandResult.success()
        }
    }

    class Remove : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.admin.gun.remove")
                .arguments(GenericArguments.integer(Text.of("Gun")))
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            if (src is Player) {
                val traveler = runBlocking { getTraveler(src).await()!! }
                traveler.item.removeAt(args.getOne<Int>("Gun").get())
            }
            return CommandResult.success()
        }
    }

    class Get : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.admin.gun.get")
                .arguments(GenericArguments.integer(Text.of("Gun")))
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            if (src is Player) {
                val traveler = runBlocking { getTraveler(src).await()!! }
                val gun = traveler.item[args.getOne<Int>("Gun").get()] as? Gun ?: return CommandResult.empty()

                gun.createItemStack().let { src.setItemInHand(HandTypes.MAIN_HAND, it) }
                src.sendMessage(Text.of(gun.uuid.toString()))
            }
            return CommandResult.success()
        }
    }

    class List : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.admin.gun.list")
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            if (src is Player) {
                val traveler = runBlocking { getTraveler(src).await()!! }

                src.sendMessage(Text.of(traveler.item.toString()))
            }
            return CommandResult.success()
        }
    }
}
