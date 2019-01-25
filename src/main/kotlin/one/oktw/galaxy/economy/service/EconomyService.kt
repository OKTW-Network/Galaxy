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

package one.oktw.galaxy.economy.service

import kotlinx.coroutines.reactive.consumeEach
import kotlinx.coroutines.runBlocking
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.main
import org.spongepowered.api.scheduler.Task
import java.util.concurrent.TimeUnit

class EconomyService {
    companion object {
        init {
            Task.builder()
                .name("EconomyService")
                .async()
                .delay(20, TimeUnit.MINUTES)
                .interval(20, TimeUnit.MINUTES)
                .execute(::dailyTask)
                .submit(main)
        }

        private fun dailyTask() = runBlocking {
            galaxyManager.listGalaxy().consumeEach {
                it.giveInterest()
                galaxyManager.saveGalaxy(it)
            }
        }
    }
}
