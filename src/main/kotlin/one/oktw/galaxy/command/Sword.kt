package one.oktw.galaxy.command

import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.item.enums.ItemType.SWORD
import one.oktw.galaxy.item.enums.SwordType
import one.oktw.galaxy.item.type.Sword
import one.oktw.galaxy.traveler.data.extensions.save
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text

class Sword : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .permission("oktw.command.sword")
            .child(Add().spec, "add")
            .child(Remove().spec, "remove")
            .child(Get().spec, "get")
            .child(List().spec, "list")
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        return CommandResult.empty()
    }

    class Add : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.sword.add")
                .arguments(
                    GenericArguments.integer(Text.of("Heat")),
                    GenericArguments.integer(Text.of("Max Heat")),
                    GenericArguments.doubleNum(Text.of("AOE Damage")),
                    GenericArguments.doubleNum(Text.of("Damage")),
                    GenericArguments.enumValue(Text.of("Type"), SwordType::class.java),
                    GenericArguments.optional(GenericArguments.integer(Text.of("Cooling")), 1)
                )
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            if (src is Player) {
                val traveler = travelerManager.getTraveler(src)

                val sword = Sword(
                    itemType = SWORD,
                    type = args.getOne<SwordType>("Type").get(),
                    maxTemp = args.getOne<Int>("Max Heat").get(),
                    cooling = args.getOne<Int>("Cooling").get(),
                    areaOfEffectDamage = args.getOne<Double>("AOE Damage").get(),
                    damage = args.getOne<Double>("Damage").get(),
                    heat = args.getOne<Int>("Heat").get()
                )

                traveler.item.add(sword)
                traveler.save()

                sword.createItemStack().let { src.setItemInHand(
                    if (args.getOne<SwordType>("Type").get() == SwordType.SCABBARD) HandTypes.OFF_HAND else HandTypes.MAIN_HAND, it) }
                src.sendMessage(Text.of(sword.uuid.toString()))
            }
            return CommandResult.success()
        }
    }

    class Remove : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.sword.remove")
                .arguments(GenericArguments.integer(Text.of("Sword")))
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            if (src is Player) {
                val traveler = travelerManager.getTraveler(src)
                traveler.item.removeAt(args.getOne<Int>("Sword").get())
                traveler.save()
            }
            return CommandResult.success()
        }
    }

    class Get : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.sword.get")
                .arguments(GenericArguments.integer(Text.of("Sword")))
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            if (src is Player) {
                val traveler = travelerManager.getTraveler(src)
                val sword = traveler.item[args.getOne<Int>("Sword").get()] as? Sword ?: return CommandResult.empty()

                sword.createItemStack().let { src.setItemInHand(
                    if (sword.type == SwordType.SCABBARD) HandTypes.OFF_HAND else HandTypes.MAIN_HAND, it) }
                src.sendMessage(Text.of(sword.uuid.toString()))
            }
            return CommandResult.success()
        }
    }

    class List : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.sword.list")
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            if (src is Player) {
                val traveler = travelerManager.getTraveler(src)

                src.sendMessage(Text.of(traveler.item.toString()))
            }
            return CommandResult.success()
        }
    }
}
