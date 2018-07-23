package one.oktw.galaxy.util

import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import java.util.concurrent.ConcurrentLinkedQueue

class OrderedLaunch {
    private val todos = ConcurrentLinkedQueue<suspend CoroutineScope.() -> Unit>()
    private var activatedJob: Job? = null

    private fun runTask() {
        if (todos.size > 0) {
            val todo = todos.poll()
            activatedJob = kotlinx.coroutines.experimental.launch(block = todo)

            kotlinx.coroutines.experimental.launch {
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
