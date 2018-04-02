package one.oktw.galaxy.item.service

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.item.type.Overheat
import org.spongepowered.api.scheduler.Task
import java.util.concurrent.ConcurrentHashMap

data class Temp(var overheated: Boolean = false, var temp: Int = 0)

class CoolDown {
    companion object {
        private val coolDown = ConcurrentHashMap<Overheat, Temp>()

        init {
            Task.builder()
                .name("CoolDown")
                .async()
                .intervalTicks(1)
                .execute(::doTick)
                .submit(main)
        }

        fun getTemp(overheat: Overheat) = coolDown.getOrDefault(overheat, Temp())

        fun heating(overheat: Overheat): Temp {
            return coolDown.getOrDefault(overheat, Temp()).apply {
                temp += overheat.heat
                if (temp > overheat.maxTemp) overheated = true
            }
        }

        private fun doTick() {
            coolDown.forEachEntry(4) {
                it.value.temp -= 1
                if (it.value.temp <= 0) coolDown -= it.key
            }
        }
    }
}