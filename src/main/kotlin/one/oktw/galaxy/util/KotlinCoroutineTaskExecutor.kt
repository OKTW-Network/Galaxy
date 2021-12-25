/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
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
import kotlinx.coroutines.scheduling.ExperimentalCoroutineDispatcher
import net.minecraft.util.Util
import net.minecraft.util.thread.TaskExecutor
import net.minecraft.util.thread.TaskQueue

class KotlinCoroutineTaskExecutor<T>(private val queue: TaskQueue<in T, out Runnable>, name: String) :
    TaskExecutor<T>(queue, null, name), CoroutineScope {
    companion object {
        @OptIn(InternalCoroutinesApi::class)
        private val dispatcher = (Dispatchers.Default as ExperimentalCoroutineDispatcher).blocking(16)
    }

    private val job = SupervisorJob()

    override val coroutineContext = dispatcher + job

    override fun send(message: T) {
        queue.add(message)
        launch {
            while (!queue.isEmpty) queue.poll()?.let { Util.debugRunnable(name, it).run() }
        }
    }

    override fun close() {
        super.close()
        runBlocking { job.cancelAndJoin() }
    }
}
