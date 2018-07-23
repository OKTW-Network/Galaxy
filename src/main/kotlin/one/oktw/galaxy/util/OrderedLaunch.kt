package one.oktw.galaxy.util

import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import one.oktw.galaxy.Main
import java.util.concurrent.ConcurrentLinkedQueue

class OrderedLaunch {
    private val todos = ConcurrentLinkedQueue<suspend CoroutineScope.() -> Unit>()
    private var activatedJob: Job? = null

    private fun runTask() {
        if (todos.size > 0) {
            Main.main.logger.info("starting a task")

            val todo = todos.poll()
            activatedJob = kotlinx.coroutines.experimental.launch(block = todo)

            kotlinx.coroutines.experimental.launch {
                (activatedJob as Job).join()
                activatedJob = null

                Main.main.logger.info("task finished, starting another task")
                runTask()
            }
        } else {
            Main.main.logger.info("task empty, bye")
        }
    }

    fun launch(block: suspend CoroutineScope.() -> Unit) {
        synchronized(this) {
            todos.add(block)

            if (activatedJob == null) {
                Main.main.logger.info("no active job found, starting the new task")
                runTask()
            }
        }
    }
}
