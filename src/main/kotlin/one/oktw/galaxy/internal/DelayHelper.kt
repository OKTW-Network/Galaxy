package one.oktw.galaxy.internal

import one.oktw.galaxy.Main.Companion.main
import org.spongepowered.api.scheduler.Task;
import java.util.concurrent.TimeUnit



class DelayHelper {
    companion object {
        fun Delay( code : Runnable , delay : Long = 3){
            Task.builder()
                    .execute(code)
                    .delay(delay,TimeUnit.SECONDS).submit(main);
            }
        }
}