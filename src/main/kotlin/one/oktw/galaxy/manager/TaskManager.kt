package one.oktw.galaxy.manager

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.task.CoolingStatus
import one.oktw.galaxy.task.PlayerEffect
import org.spongepowered.api.scheduler.Task

class TaskManager {
    val coolingStatus = CoolingStatus()
    val effect = PlayerEffect()

    init {
        Task.builder().name("Cooling Status").intervalTicks(1).execute(coolingStatus).submit(main)
        Task.builder().name("Player Effect").intervalTicks(20).execute(effect).submit(main)
    }
}