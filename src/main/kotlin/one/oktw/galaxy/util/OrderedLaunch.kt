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

package one.oktw.galaxy.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue

class OrderedLaunch {
    private val todos = ConcurrentLinkedQueue<suspend CoroutineScope.() -> Unit>()
    private var activatedJob: Job? = null

    private fun runTask() {
        if (todos.size > 0) {
            val todo = todos.poll()
            activatedJob = GlobalScope.launch(block = todo)

            GlobalScope.launch {
                (activatedJob as Job).join()
                activatedJob = null

                runTask()
            }
        }
    }

    fun launch(block: suspend CoroutineScope.() -> Unit) {
        synchronized(this) {
            todos.add(block)

            if (activatedJob == null) {
                runTask()
            }
        }
    }
}
