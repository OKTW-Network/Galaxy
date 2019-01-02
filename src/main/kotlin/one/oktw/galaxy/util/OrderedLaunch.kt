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
