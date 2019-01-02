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
