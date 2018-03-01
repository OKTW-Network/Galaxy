package one.oktw.galaxy.command

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.helper.TeleportHelper
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player

class Spawn : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
            .executor(this)
            .permission("oktw.command.spawn")
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src !is Player) return CommandResult.empty()

        launch { TeleportHelper.teleport(src, src.world.spawnLocation) }

        return CommandResult.success()
    }
}