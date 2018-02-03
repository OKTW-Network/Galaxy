package one.oktw.galaxy.command

import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.enums.UpgradeType.THROUGH
import one.oktw.galaxy.types.Upgrade
import one.oktw.galaxy.types.item.Gun
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text

class Gun : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.gun")
                .arguments(
                        GenericArguments.doubleNum(Text.of("CoolDown")),
                        GenericArguments.doubleNum(Text.of("Range")),
                        GenericArguments.doubleNum(Text.of("Damage")),
                        GenericArguments.optional(GenericArguments.integer(Text.of("Through")))
                )
                .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src is Player) {
            val traveler = travelerManager.getTraveler(src)
            val gun = Gun(
                    args.getOne<Double>("CoolDown").orElse(null),
                    args.getOne<Double>("Range").orElse(null),
                    args.getOne<Double>("Damage").orElse(null)
            )

            args.getOne<Int>("Through").ifPresent { gun.upgrade += Upgrade(THROUGH, it) }

            traveler.item.gun = gun
            traveler.save()
        }
        return CommandResult.success()
    }
}