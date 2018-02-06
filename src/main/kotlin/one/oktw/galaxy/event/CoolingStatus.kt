package one.oktw.galaxy.event

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataOverheat
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
class CoolingStatus {
    private val coolingBar = HashMap<Player, Task>()

    @Listener
    @Suppress("UNUSED_PARAMETER")
    fun onChangeHeld(event: ChangeInventoryEvent, @First player: Player) {
        coolingBar[player]?.cancel()
        coolingBar -= player

        if (!player.getItemInHand(HandTypes.MAIN_HAND).filter { it[DataOverheat.key].isPresent }.isPresent) return

        fun normalize(heatStatus: CoolDownHelper.HeatStatus): Int {
            return Math.min((heatStatus.now.toDouble() / heatStatus.max) * 100, 100.0).toInt()
        }

        fun color(heatStatus: CoolDownHelper.HeatStatus?): TextColor {
            if (heatStatus == null) return TextColors.AQUA

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
                    val mainHand = player.getItemInHand(HandTypes.MAIN_HAND).filter { it[DataOverheat.key].isPresent }.orElse(null)
                    val offHand = player.getItemInHand(HandTypes.OFF_HAND).filter { it[DataOverheat.key].isPresent }.orElse(null)
                    val heatStatus1 = CoolDownHelper.getCoolDown(mainHand[DataUUID.key].get())
                    val heatStatus2 = if (offHand != null) CoolDownHelper.getCoolDown(offHand[DataUUID.key].get()) else null

                    val bar1 = if (heatStatus1 != null) normalize(heatStatus1) / 2 else 0
                    val temp1 = heatStatus1?.now ?: 0

                    if (temp1 == 0) {
                        mainHand.transform(DataOverheat.key, { false })
                        player.setItemInHand(HandTypes.MAIN_HAND, mainHand)
                    } else if (heatStatus1?.isOverheat() == true) {
                        mainHand.transform(DataOverheat.key, { true })
                        player.setItemInHand(HandTypes.MAIN_HAND, mainHand)
                    }

                    if (offHand == null || !offHand[DataOverheat.key].isPresent) {
                        player.sendTitle(Title.builder()
                                .actionBar(Text.of(
                                        color(heatStatus1), "|".repeat(bar1), " ", temp1, "°C ", "|".repeat(bar1)
                                ))
                                .build())
                    } else {
                        val bar2 = if (heatStatus2 != null) normalize(heatStatus2) / 2 else 0
                        val temp2 = heatStatus2?.now ?: 0

                        if (temp2 == 0) {
                            offHand.transform(DataOverheat.key, { false })
                            player.setItemInHand(HandTypes.OFF_HAND, offHand)
                        } else if (heatStatus2?.isOverheat() == true) {
                            offHand.transform(DataOverheat.key, { true })
                            player.setItemInHand(HandTypes.OFF_HAND, offHand)
                        }

                        player.sendTitle(Title.builder()
                                .actionBar(Text.of(
                                        TextColors.GRAY, "|".repeat(50 - bar2),
                                        color(heatStatus2), "|".repeat(bar2 / 2), " ", temp2, "°C",
                                        TextColors.RESET, " | ",
                                        color(heatStatus1), temp1, "°C ", "|".repeat(bar1),
                                        TextColors.GRAY, "|".repeat(50 - bar1)
                                ))
                                .build())
                    }
                })
                .submit(main)
    }
}