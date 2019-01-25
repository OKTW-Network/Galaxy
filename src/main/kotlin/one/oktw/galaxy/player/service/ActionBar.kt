/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2018
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

package one.oktw.galaxy.player.service

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.player.data.ActionBarData
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.title.Title
import java.util.concurrent.ConcurrentHashMap

class ActionBar {
    companion object {
        private val actionBar = ConcurrentHashMap<Player, ActionBarData>()

        init {
            Task.builder()
                .name("ActionBar")
                .intervalTicks(1)
                .execute(Companion::tick)
                .submit(main)
        }

        fun setActionBar(player: Player, data: ActionBarData) {
            if (actionBar[player]?.priority ?: 0 <= data.priority) actionBar[player] = data
        }

        private fun tick() {
            for ((player, data) in actionBar) {
                player.sendTitle(buildTitle(data.text))

                // remove if timeout
                if (data.time > 0) data.time-- else actionBar -= player
            }
        }

        private fun buildTitle(text: Text) = Title.builder().actionBar(text).build()
    }
}
