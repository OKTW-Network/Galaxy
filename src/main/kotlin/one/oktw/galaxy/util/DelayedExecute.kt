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

import kotlinx.coroutines.*
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.scheduler.Task
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DelayedExecute(private val plugin: PluginContainer) {
    fun <T> launch(tick: Long = 1, block: () -> T): Job = GlobalScope.launch(start = CoroutineStart.UNDISPATCHED) {
        run(tick, block)
    }

    suspend fun <T> run(tick: Long = 1, block: () -> T) = suspendCancellableCoroutine<T> { cont ->
        val task = Task.builder()
            .execute { _ ->
                try {
                    cont.resume(block.invoke())
                } catch (err: Throwable) {
                    cont.resumeWithException(err)
                }
            }
            .delayTicks(tick)
            .name("Galaxy - delayed task after $tick tick")
            .submit(plugin)


        cont.invokeOnCancellation {
            task.cancel()
        }
    }
}
