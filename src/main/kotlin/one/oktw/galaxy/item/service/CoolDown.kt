package one.oktw.galaxy.item.service

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.item.type.Overheat
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
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
                .execute { _ ->
                    coolDown.forEachEntry(4) {
                        it.value.temp -= it.key.cooling
                        if (it.value.temp <= 0) coolDown -= it.key
                    }
                }
                .submit(main)
        }

        fun getTemp(overheat: Overheat) = coolDown.getOrDefault(overheat, Temp())

        fun heating(overheat: Overheat): Temp {
            return coolDown.getOrPut(overheat) { Temp() }
                .apply {
                    temp += overheat.heat
                    if (temp > overheat.maxTemp) overheated = true
                }
        }

        fun getActionBar(item1: Overheat, item2: Overheat? = null): Text? {
            if (item2 == null) {
                val temp = getTemp(item1).apply { if (temp == 0) return null }.temp
                val pct = (temp * 50 / item1.maxTemp.toFloat()).toInt()

                return Text.of(getHeatColor(item1), "${"|".repeat(pct)} ${temp}°C ${"|".repeat(pct)}")
            } else {
                val temp1 = getTemp(item1).temp
                val temp2 = getTemp(item2).temp
                val pct1 = (temp1 * 50 / item1.maxTemp.toFloat()).toInt()
                val pct2 = (temp2 * 50 / item2.maxTemp.toFloat()).toInt()

                if (temp1 + temp2 == 0) return null

                return Text.of(
                    TextColors.GRAY, "|".repeat(50 - pct2),
                    getHeatColor(item2), "${"|".repeat(pct2)} $temp2 °C",
                    TextColors.RESET, " | ",
                    getHeatColor(item1), "$temp1°C ${"|".repeat(pct1)}",
                    TextColors.GRAY, "|".repeat(50 - pct1)
                )
            }
        }

        private fun getHeatColor(overheat: Overheat): TextColor {
            val temp = getTemp(overheat)
            val pct = (temp.temp * 100 / overheat.maxTemp.toFloat()).toInt()

            return if (temp.overheated) TextColors.RED else when (pct) {
                in 0..40 -> TextColors.AQUA
                in 41..70 -> TextColors.GOLD
                in 71..100 -> TextColors.RED
                else -> TextColors.RED
            }
        }
    }
}
