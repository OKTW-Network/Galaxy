package one.oktw.galaxy.util

import kotlinx.coroutines.experimental.*
import org.spongepowered.api.scheduler.Task

class DelayedExecute(private val plugin: Any) {
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
