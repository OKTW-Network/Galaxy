/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2019
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package one.oktw.galaxy.command

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        GlobalScope.launch {
            for (i in 0..4) {
                ActionBar.setActionBar(src, ActionBarData(Text.of(TextColors.GREEN, lang.of("command.spawn.countdown", 5 - i)).toLegacyText(src), 3))
                delay(TimeUnit.SECONDS.toMillis(1))
            }

            withContext(serverThread) { src.setLocationSafely(src.world.spawnLocation) }

            lock -= src
        }

        return CommandResult.success()
    }
}
