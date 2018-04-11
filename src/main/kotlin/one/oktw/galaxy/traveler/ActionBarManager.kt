package one.oktw.galaxy.traveler

import one.oktw.galaxy.Main.Companion.main
import org.spongepowered.api.Sponge
import org.spongepowered.api.scheduler.Task

class ActionBarManager {
    init {
        Task.builder().execute { _ -> }.submit(main)
        Sponge.getScheduler().createSyncExecutor(main)
    }
}