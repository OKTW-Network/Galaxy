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

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

class CountDown {
    companion object {
        val instance = CountDown()
    }

    private val callbacks = ConcurrentHashMap<Any, () -> Unit>()
    private val pendingTask = ConcurrentHashMap<Any, Job>()

    fun countDown(obj: Any, time: Number, cancelCallback: (() -> Unit)? = null): Boolean {
        synchronized(this) {
            if (pendingTask[obj] != null) {
                return false
            }

            if (cancelCallback != null) {
                callbacks[obj] = cancelCallback
            }

            val task = GlobalScope.launch {
                delay(time.toLong())
                pendingTask.remove(obj)
                callbacks.remove(obj)
            }

            pendingTask[obj] = task

            return true
        }
    }

    fun isCounting(obj: Any): Boolean {
        return pendingTask[obj] != null
    }

    fun cancel(obj: Any): Boolean {
        synchronized(this) {
            if (pendingTask[obj] == null) {
                return false
            }

            callbacks[obj]?.invoke()
            pendingTask[obj]?.cancel()
            pendingTask.remove(obj)
            return true
        }
    }
}
