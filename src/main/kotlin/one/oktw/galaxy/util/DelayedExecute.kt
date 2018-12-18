package one.oktw.galaxy.util

import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.scheduler.Task

class DelayedExecute(private val plugin: PluginContainer) {
    fun <T> launch(tick: Long = 1, block: () -> T): Job = launch(start = CoroutineStart.UNDISPATCHED) {
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
