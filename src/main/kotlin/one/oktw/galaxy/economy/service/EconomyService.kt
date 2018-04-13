package one.oktw.galaxy.economy.service

import kotlinx.coroutines.experimental.runBlocking
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.main
import org.spongepowered.api.scheduler.Task
import java.util.concurrent.TimeUnit

class EconomyService {
    companion object {
        init {
            Task.builder()
                .name("EconomyService")
                .interval(20, TimeUnit.MINUTES)
                .async()
                .execute(::dailyTask)
                .submit(main)
        }

        private fun dailyTask() {
            runBlocking {
                galaxyManager.listGalaxy().await().forEach { it.giveInterest() }
            }
        }
    }
}