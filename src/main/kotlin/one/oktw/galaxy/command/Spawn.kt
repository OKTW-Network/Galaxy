package one.oktw.galaxy.command

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.Main.Companion.translationService
import one.oktw.galaxy.player.data.ActionBarData
import one.oktw.galaxy.player.service.ActionBar
import one.oktw.galaxy.translation.extensions.toLegacyText
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
        val lang = translationService
        if (src !is Player || lock.contains(src)) return CommandResult.empty()

        lock += src

        launch {
            for (i in 0..4) {
                ActionBar.setActionBar(src, ActionBarData(Text.of(TextColors.GREEN, lang.of("command.spawn.countdown", 5 - i)).toLegacyText(src), 3))
                delay(1, TimeUnit.SECONDS)
            }

            withContext(serverThread) { src.setLocationSafely(src.world.spawnLocation) }

            lock -= src
        }

        return CommandResult.success()
    }
}
