package one.oktw.galaxy.task

import one.oktw.galaxy.data.DataOverheat
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.helper.CoolDownHelper
import one.oktw.galaxy.helper.CoolDownHelper.HeatStatus
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.title.Title
import java.util.*
import java.util.function.Consumer

class CoolingStatus : Consumer<Task> {
    override fun accept(task: Task) {
        Sponge.getServer().onlinePlayers.forEach {
            val mainHand: ItemStack? = it.getItemInHand(HandTypes.MAIN_HAND)
                    .filter { it[DataOverheat.key].isPresent }
                    .orElse(null)
            val offHand: ItemStack? = it.getItemInHand(HandTypes.OFF_HAND)
                    .filter { it[DataOverheat.key].isPresent }
                    .orElse(null)

            fun updateOverheat(itemStack: ItemStack, handType: HandType) {
                val overheat = getHeatStatus(itemStack[DataUUID.key].get())?.isOverheat() ?: false
                if (itemStack[DataOverheat.key].get() != overheat) {
                    itemStack.transform(DataOverheat.key) { !it }
                    it.setItemInHand(handType, itemStack)
                }
            }

            mainHand?.let { updateOverheat(it, HandTypes.MAIN_HAND) }
            offHand?.let { updateOverheat(it, HandTypes.OFF_HAND) }

            if (mainHand != null && offHand != null) {
                val heatStatus1 = getHeatStatus(mainHand[DataUUID.key].get())
                val heatStatus2 = getHeatStatus(offHand[DataUUID.key].get())
                val bar1 = if (heatStatus1 != null) normalize(heatStatus1) / 2 else 0
                val bar2 = if (heatStatus2 != null) normalize(heatStatus2) / 2 else 0

                it.sendTitle(Title.builder()
                        .actionBar(Text.of(
                                TextColors.GRAY, "|".repeat(50 - bar2),
                                color(heatStatus2), "|".repeat(bar2), " ", heatStatus2?.now ?: 0, "°C",
                                TextColors.RESET, " | ",
                                color(heatStatus1), heatStatus1?.now ?: 0, "°C ", "|".repeat(bar1),
                                TextColors.GRAY, "|".repeat(50 - bar1)
                        ))
                        .build())
            } else if (mainHand != null) {
                val heatStatus1 = getHeatStatus(mainHand[DataUUID.key].get())
                val bar1 = if (heatStatus1 != null) normalize(heatStatus1) / 2 else 0

                it.sendTitle(Title.builder()
                        .actionBar(Text.of(
                                color(heatStatus1), "|".repeat(bar1), " ", heatStatus1?.now
                                ?: 0, "°C ", "|".repeat(bar1)
                        ))
                        .build())
            }
        }
    }

    private fun getHeatStatus(uuid: UUID): HeatStatus? {
        return CoolDownHelper.getCoolDown(uuid)
    }

    private fun normalize(heatStatus: HeatStatus): Int {
        return Math.max(Math.min((heatStatus.now.toDouble() / heatStatus.max) * 100, 100.0), 0.0).toInt()
    }

    private fun color(heatStatus: HeatStatus?): TextColor {
        if (heatStatus == null) return TextColors.AQUA

        return if (heatStatus.isOverheat()) TextColors.RED else when (normalize(heatStatus)) {
            in 0..40 -> TextColors.AQUA
            in 41..70 -> TextColors.GOLD
            in 71..100 -> TextColors.RED
            else -> TextColors.RED
        }
    }
}