package one.oktw.galaxy.item.service

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.item.data.Heat
import one.oktw.galaxy.item.type.Overheat
import one.oktw.galaxy.traveler.data.ActionBarData
import one.oktw.galaxy.traveler.service.ActionBar.Companion.setActionBar
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
import java.util.concurrent.ConcurrentHashMap

class CoolDown {
    companion object {
        private val coolDown = ConcurrentHashMap<Overheat, Heat>()
        private val actionBar = HashMap<Player, Pair<Overheat, Overheat?>>()

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

            Task.builder()
                .name("CoolDownBar")
                .intervalTicks(1)
                .execute { _ ->
                    val iterator = actionBar.iterator()

                    while (iterator.hasNext()) {
                        val (player, items) = iterator.next()
                        val text = actionBarText(items.first, items.second)

                        if (getHeat(items.first).temp != 0 || (items.second?.let(::getHeat)?.temp ?: 0) != 0) {
                            setActionBar(player, ActionBarData(text, 1))
                        } else {
                            iterator.remove()
                            setActionBar(player, ActionBarData(text, 1, 60))
                        }
                    }
                }
                .submit(main)
        }

        fun getHeat(overheat: Overheat) = coolDown.getOrDefault(overheat, Heat())

        fun heating(overheat: Overheat): Heat {
            return coolDown.getOrPut(overheat) { Heat() }
                .apply {
                    temp += overheat.heat
                    if (temp > overheat.maxTemp) overheated = true
                }
        }

        fun showActionBar(player: Player, item1: Overheat, item2: Overheat?) {
            actionBar[player] = Pair(item1, item2)
        }

        private fun actionBarText(item1: Overheat, item2: Overheat? = null): Text {
            if (item2 == null) {
                val temp = getHeat(item1).temp
                val pct = (temp * 50 / item1.maxTemp.toFloat()).toInt()

                return Text.of(getHeatColor(item1), "${"|".repeat(pct)} ${temp}°C ${"|".repeat(pct)}")
            } else {
                val temp1 = getHeat(item1).temp
                val temp2 = getHeat(item2).temp
                val pct1 = Math.min((temp1 * 50 / item1.maxTemp.toFloat()).toInt(), 50)
                val pct2 = Math.min((temp2 * 50 / item2.maxTemp.toFloat()).toInt(), 50)

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
            val heat = getHeat(overheat)
            val pct = (heat.temp * 100 / overheat.maxTemp.toFloat()).toInt()

            return if (heat.overheated) TextColors.RED else when (pct) {
                in 0..40 -> TextColors.AQUA
                in 41..70 -> TextColors.GOLD
                in 71..100 -> TextColors.RED
                else -> TextColors.RED
            }
        }
    }
}
