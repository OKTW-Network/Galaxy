package one.oktw.galaxy.util

import kotlinx.coroutines.experimental.*
import one.oktw.galaxy.Main
import org.spongepowered.api.scheduler.Task

class DelayedExecute(private val plugin: Any) {
    fun delay(delay: Long, block: suspend CoroutineScope.() -> Unit): Job = launch(start = CoroutineStart.UNDISPATCHED) {
        suspendCancellableCoroutine<Unit> { cont ->
            val task = Task.builder()
                .execute { _ ->
                    launch(
                        // so we will resume on main thread
                        context = Main.serverThread,
                        // because we are already on main thread
                        start = CoroutineStart.UNDISPATCHED,
                        block = block
                    ).invokeOnCompletion {
                        if (it != null) {
                            cont.resumeWithException(it)
                        } else {
                            cont.resume(Unit)
                        }
                    }
                }
                .delayTicks(delay)
                .name("Galaxy - delayed task after $delay tick")
                .submit(plugin)


            cont.invokeOnCancellation {
                task.cancel()
            }
        }
    }
}
