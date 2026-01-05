/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2026
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

package one.oktw.galaxy.util;

import net.minecraft.util.Util;
import net.minecraft.util.thread.PriorityConsecutiveExecutor;
import net.minecraft.util.thread.StrictQueue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualTaskExecutor extends PriorityConsecutiveExecutor {
    private final ExecutorService executor;
    private final StrictQueue.FixedPriorityQueue queue;
    private final AtomicInteger executePriority = new AtomicInteger(0);
    private final AtomicInteger executingTask = new AtomicInteger(0);

    public VirtualTaskExecutor(int queueCount, String name) {
        super(queueCount, null, name);
        executor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().name(name, 0).factory());
        queue = new StrictQueue.FixedPriorityQueue(queueCount);
    }

    @Override
    public void schedule(StrictQueue.RunnableWithPriority runnable) {
        queue.push(runnable);
        executor.execute(this::runTasks);
    }

    private void runTasks() {
        while (!queue.isEmpty()) {
            StrictQueue.RunnableWithPriority task = (StrictQueue.RunnableWithPriority) queue.pop();
            if (task == null) continue;

            // Check task priority
            if (executingTask.get() > 0 && task.priority() > executePriority.get()) {
                // executing task priority higher than next task, wait all task done
                queue.push(task);
                break;
            }

            // Run task
            executePriority.set(task.priority());
            executingTask.incrementAndGet();
            executor.execute(() -> {
                Util.runNamed(task, name());
                if (executingTask.decrementAndGet() <= 0) runTasks(); // Trigger next write batch
            });
        }
    }

    @Override
    public void close() {
        executor.close();
    }
}
