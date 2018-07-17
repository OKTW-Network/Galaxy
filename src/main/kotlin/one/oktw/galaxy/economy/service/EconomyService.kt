package one.oktw.galaxy.economy.service

import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.Main.Companion.serverThread
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
            launch(serverThread) {
                galaxyManager.listGalaxy().await().consumeEach {
                    it.giveInterest()
                    galaxyManager.saveGalaxy(it)
                }
            }
        }
    }
}
