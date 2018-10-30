package one.oktw.galaxy.command.admin

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.galaxy.data.extensions.getMember
import one.oktw.galaxy.galaxy.data.extensions.saveMember
import one.oktw.galaxy.galaxy.traveler.TravelerHelper
import one.oktw.galaxy.item.enums.ItemType.SWORD
import one.oktw.galaxy.item.enums.SwordType
import one.oktw.galaxy.item.enums.SwordType.SCABBARD
import one.oktw.galaxy.item.type.Sword
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.data.type.HandTypes.MAIN_HAND
import org.spongepowered.api.data.type.HandTypes.OFF_HAND
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors.RED

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
        src.sendMessage(Sponge.getCommandManager().getUsage(src))

        return CommandResult.success()
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
            if (src !is Player) return CommandResult.empty()
            launch {
                val traveler = TravelerHelper.getTraveler(src).await()
                if (traveler == null) {
                    src.sendMessage(Text.of(RED, "Galaxy not found or missing: Join a galaxy which you are member first"))
                    return@launch
                }

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
                launch {
                    galaxyManager.get(src.world)?.run {
                        getMember(src.uniqueId)?.also {
                            saveMember(traveler)
                        }
                    }
                }

                sword.createItemStack().let {
                    src.setItemInHand(
                        if (args.getOne<SwordType>("Type").get() == SCABBARD) OFF_HAND else MAIN_HAND, it
                    )
                }
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
            if (src !is Player) return CommandResult.empty()
            launch {
                val traveler = TravelerHelper.getTraveler(src).await() ?: return@launch
                traveler.item.removeAt(args.getOne<Int>("Sword").get())
                launch {
                    galaxyManager.get(src.world)?.run {
                        getMember(src.uniqueId)?.also {
                            saveMember(traveler)
                        }
                    }
                }
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
            if (src !is Player) return CommandResult.empty()
            launch {
                val traveler = TravelerHelper.getTraveler(src).await() ?: return@launch
                val sword = traveler.item[args.getOne<Int>("Sword").get()] as? Sword ?: return@launch

                sword.createItemStack().let {
                    src.setItemInHand(
                        if (sword.type == SCABBARD) OFF_HAND else MAIN_HAND, it
                    )
                }
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
            if (src !is Player) return CommandResult.empty()

            launch {
                val traveler = TravelerHelper.getTraveler(src).await()
                if (traveler != null) {
                    src.sendMessage(Text.of(traveler.item.toString()))
                } else {
                    src.sendMessage(Text.of(RED, "Fetch failed: Galaxy not found or missing"))
                }
            }
            return CommandResult.success()
        }
    }
}
