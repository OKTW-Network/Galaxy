package one.oktw.galaxy.helper

import one.oktw.galaxy.Main.Companion.main
import org.spongepowered.api.scheduler.Task
import java.util.concurrent.TimeUnit

class DelayHelper {
    companion object {
        fun delay(code: Runnable, delay: Long = 3) {
            Task.builder()
                    .execute(code)
                    .delay(delay, TimeUnit.SECONDS)
                    .submit(main)
        }
    }
}