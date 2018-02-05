package one.oktw.galaxy.event

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.helper.CoolDownHelper
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.title.Title

@Suppress("unused")
class CoolingBar {
    private val coolingBar = HashMap<Player, Task>()

    @Listener
    @Suppress("UNUSED_PARAMETER")
    fun onChangeHeld(event: ChangeInventoryEvent, @First player: Player) {
        val mainHand = player.getItemInHand(HandTypes.MAIN_HAND).filter { it[DataUUID.key].isPresent }.orElse(null)
        val offHand = player.getItemInHand(HandTypes.OFF_HAND).filter { it[DataUUID.key].isPresent }.orElse(null)

        coolingBar[player]?.cancel()
        coolingBar -= player

        if (mainHand == null) return

        val heatStatus1 = CoolDownHelper.getCoolDown(mainHand[DataUUID.key].get()) ?: return
        val heatStatus2 = if (offHand != null) CoolDownHelper.getCoolDown(offHand[DataUUID.key].get()) else null

        fun normalize(heatStatus: CoolDownHelper.HeatStatus): Int {
            return Math.min((heatStatus.now.toDouble() / heatStatus.max) * 100, 100.0).toInt()
        }

        fun color(heatStatus: CoolDownHelper.HeatStatus): TextColor {
            return if (heatStatus.isOverheat()) TextColors.RED else when (normalize(heatStatus)) {
                in 0..40 -> TextColors.AQUA
                in 41..70 -> TextColors.GOLD
                in 71..100 -> TextColors.RED
                else -> TextColors.RED
            }
        }

        coolingBar[player] = Task.builder()
                .name("CoolingBar")
                .intervalTicks(1)
                .execute({ _ ->
                    val temp1 = normalize(heatStatus1)

                    if (heatStatus2 == null) {
                        player.sendTitle(Title.builder()
                                .actionBar(Text.of(
                                        color(heatStatus1), "|".repeat(temp1 / 2), " ", heatStatus1.now, "°C ", "|".repeat(temp1 / 2)
                                ))
                                .build())
                    } else {
                        val temp2 = normalize(heatStatus2)
                        player.sendTitle(Title.builder()
                                .actionBar(Text.of(
                                        TextColors.GRAY, "|".repeat(50 - temp2 / 2),
                                        color(heatStatus2), "|".repeat(temp2 / 2), " ", heatStatus2.now, "°C",
                                        TextColors.RESET, " | ",
                                        color(heatStatus1), heatStatus1.now, "°C ", "|".repeat(temp1 / 2),
                                        TextColors.GRAY, "|".repeat(50 - temp1 / 2)
                                ))
                                .build())
                    }
                })
                .submit(main)
    }
}