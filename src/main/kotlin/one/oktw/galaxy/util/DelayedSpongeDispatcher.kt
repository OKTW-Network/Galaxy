package one.oktw.galaxy.util

import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Runnable
import org.spongepowered.api.scheduler.Task
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.experimental.CoroutineContext

open class DelayedSpongeDispatcher(private val delay: Long, private val plugin: Any): CoroutineDispatcher() {
    companion object {
        class DispatcherFactory(private val plugin: Any): DelayedSpongeDispatcher(1, plugin) {
            private val cache: ConcurrentHashMap<Long, DelayedSpongeDispatcher> = ConcurrentHashMap()

            fun delay(time: Long = 1): DelayedSpongeDispatcher {
                if (cache.containsKey(time)) {
                    return cache[time]!!
                }

                val newDispatcher = DelayedSpongeDispatcher(time, plugin)

                cache[time] = newDispatcher

                return newDispatcher
            }
        }

        fun factory(plugin: Any): DispatcherFactory = DispatcherFactory(plugin)
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        Task.builder()
            .execute(block)
            .delayTicks(delay)
            .name("Galaxy - delayed task after $delay tick")
            .submit(plugin)
    }
}
