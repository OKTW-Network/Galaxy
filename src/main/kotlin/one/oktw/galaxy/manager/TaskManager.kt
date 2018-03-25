package one.oktw.galaxy.manager

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.task.CoolingStatus
import org.spongepowered.api.scheduler.Task

class TaskManager {
    val coolingStatus = CoolingStatus()

    init {
        Task.builder().name("Cooling Status").intervalTicks(1).execute(coolingStatus).submit(main)
    }
}
