/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2023
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
import net.minecraft.util.Util
import net.minecraft.util.thread.TaskExecutor
import net.minecraft.util.thread.TaskQueue
import net.minecraft.util.thread.TaskQueue.PrioritizedTask
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class KotlinCoroutineTaskExecutor<T>(private val queue: TaskQueue<in T, out Runnable>, name: String) : TaskExecutor<T>(queue, null, name), CoroutineScope {
    companion object {
        private var index = 0
        private val dispatcher = Executors.newFixedThreadPool(16) { r -> Thread(r, "IO-Kotlin-${index++}").apply { isDaemon = true } }.asCoroutineDispatcher()
    }

    private val job = SupervisorJob()
    private val executePriority = AtomicInteger(0)
    private val executingTask = AtomicInteger(0)

    override val coroutineContext = dispatcher + job

    override fun send(message: T) {
        queue.add(message)
        launch { runTasks() }
    }

    private fun runTasks() {
        while (!queue.isEmpty) {
            val task = queue.poll() ?: continue

            // Check task priority
            if (task is PrioritizedTask && executingTask.get() > 0 && task.priority > executePriority.get()) {
                // executing task priority higher than next task, wait all task done
                @Suppress("UNCHECKED_CAST")
                queue.add(task as T)
                break
            }

            // Run task
            if (task is PrioritizedTask) executePriority.set(task.priority)
            executingTask.incrementAndGet()
            launch {
                Util.debugRunnable(name, task).run()
                if (executingTask.decrementAndGet() <= 0) runTasks() // Trigger next write batch
            }
        }
    }

    override fun close() {
        super.close()
        runBlocking { job.cancelAndJoin() }
    }
}
