package one.oktw.galaxy.register

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.task.Armor
import one.oktw.galaxy.task.CoolingStatus
import org.spongepowered.api.scheduler.Task

class TaskRegister {
    init {
        // Cooling status
        Task.builder().name("Cooling Status").intervalTicks(1).execute(CoolingStatus()).submit(main)

        //Armor Effect
        Task.builder().name("Armor Effect").intervalTicks(20).execute(Armor()).submit(main)
    }
}