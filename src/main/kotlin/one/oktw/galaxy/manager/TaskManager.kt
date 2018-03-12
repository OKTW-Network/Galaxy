package one.oktw.galaxy.manager

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.task.ArmorEffect
import one.oktw.galaxy.task.CoolingStatus
import org.spongepowered.api.scheduler.Task

class TaskManager {
    val armor = ArmorEffect()
    val coolingStatus = CoolingStatus()

    init {
        Task.builder().name("Cooling Status").intervalTicks(1).execute(coolingStatus).submit(main)
        Task.builder().name("Armor Effect").intervalTicks(20).execute(armor).submit(main)
    }
}