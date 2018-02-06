package one.oktw.galaxy.helper

import one.oktw.galaxy.Main.Companion.main
import org.spongepowered.api.scheduler.Task
import java.util.*

class CoolDownHelper {
    data class HeatStatus(
            val uuid: UUID,
            val cooling: Int,
            var now: Int = 0,
            val max: Int,
            private var overheat: Boolean = false
    ) {
        fun isOverheat(): Boolean {
            overheat = if (now > max) true else if (now <= 0) false else overheat
            return overheat
        }

        fun addHeat(int: Int): Boolean {
            now += int
            return isOverheat()
        }
    }

    companion object {
        private val coolDown = ArrayList<HeatStatus>()

        init {
            Task.builder().intervalTicks(1).name("CoolDown").async().execute { _ ->
                coolDown.filter { it -> it.now > 0 }.forEach { it -> it.now -= it.cooling }
            }.submit(main)
        }

        fun getCoolDown(uuid: UUID): HeatStatus? {
            return coolDown.firstOrNull { it.uuid == uuid }
        }

        fun addCoolDown(heatStatus: HeatStatus) {
            coolDown += heatStatus
        }

        fun removeCoolDown(heatStatus: HeatStatus) {
            coolDown -= heatStatus
        }
    }
}