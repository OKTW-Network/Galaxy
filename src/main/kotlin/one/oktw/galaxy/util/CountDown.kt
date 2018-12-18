package one.oktw.galaxy.util

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.ConcurrentHashMap

class CountDown {
    companion object {
        val instance = CountDown()
    }

    private val callbacks = ConcurrentHashMap<Any, ()->Unit>()
    private val pendingTask = ConcurrentHashMap<Any, Job>()

    fun countDown(obj: Any, time: Int, cancelCallback: (()->Unit)? = null): Boolean {
        synchronized(this) {
            if (pendingTask[obj] != null) {
                return false
            }

            if (cancelCallback != null) {
                callbacks[obj] = cancelCallback
            }

            val task = launch {
                delay(time)
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
