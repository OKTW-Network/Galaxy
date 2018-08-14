package one.oktw.galaxy.util

import kotlinx.coroutines.experimental.*
import org.spongepowered.api.scheduler.Task

class DelayedExecute(private val plugin: Any) {
    fun <T> delayLaunch(tick: Long, block: () -> T): Job = launch(start = CoroutineStart.UNDISPATCHED) {
        delay(tick, block)
    }

    suspend fun <T> delay(tick: Long, block: () -> T) = suspendCancellableCoroutine<T> { cont ->
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
