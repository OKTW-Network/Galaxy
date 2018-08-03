package one.oktw.galaxy.command.admin

import com.flowpowered.math.imaginary.Quaterniond
import one.oktw.galaxy.command.CommandBase
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.data.property.entity.EyeLocationProperty
import org.spongepowered.api.entity.living.player.Player


class RemoveEntity : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
        .permission("oktw.command.admin.removeentity")
        .executor(this)
        .build()

    override fun execute(player: CommandSource, args: CommandContext): CommandResult {

        if (player !is Player) return CommandResult.empty()

        val direction =
            Quaterniond.fromAxesAnglesDeg(player.rotation.x, -player.rotation.y, player.rotation.z).direction

        val source = player.getProperty(EyeLocationProperty::class.java)
            .map(EyeLocationProperty::getValue).orElse(null)?.add(direction) ?: return CommandResult.empty()

        for (entity in player.world.getIntersectingEntities(source, direction, 5.toDouble())) {
            entity.entity.remove()
        }

        return CommandResult.success()

    }
}
