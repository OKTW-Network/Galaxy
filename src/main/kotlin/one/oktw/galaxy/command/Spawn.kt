package one.oktw.galaxy.command

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.galaxy.planet.TeleportHelper
import one.oktw.galaxy.player.data.ActionBarData
import one.oktw.galaxy.player.service.ActionBar
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class Spawn : CommandBase {
    private val lock = ConcurrentHashMap.newKeySet<Player>()

    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .executor(this)
            .permission("oktw.command.spawn")
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src !is Player || lock.contains(src)) return CommandResult.empty()

        lock += src

        launch {
            for (i in 0..4) {
                ActionBar.setActionBar(src, ActionBarData(Text.of(TextColors.GREEN, "請等待 ${5 - i} 後傳送")))
                delay(1, TimeUnit.SECONDS)
            }

            TeleportHelper.teleport(src, src.world)

            lock -= src
        }

        return CommandResult.success()
    }
}
