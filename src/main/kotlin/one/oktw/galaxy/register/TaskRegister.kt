package one.oktw.galaxy.register

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.task.CoolingStatus
import org.spongepowered.api.scheduler.Task

class TaskRegister {
    init {
        // Cooling status
        Task.builder().intervalTicks(1).execute(CoolingStatus()).submit(main)
    }
}