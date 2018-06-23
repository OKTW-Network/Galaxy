package one.oktw.galaxy.economy.event

import kotlinx.coroutines.experimental.launch
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.galaxy.traveler.TravelerHelper.Companion.getTraveler
import one.oktw.galaxy.player.data.ActionBarData
import one.oktw.galaxy.player.service.ActionBar
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors

class TravelerEvent {
    @SubscribeEvent
    fun onPickupExp(event: PlayerPickupXpEvent) {
        val player = event.entityPlayer as Player

        launch { getTraveler(player).await()?.giveStarDust(event.orb.xpValue) }

        ActionBarData(
            Text.of(
                TextColors.AQUA,
                languageService.getDefaultLanguage()["traveler.event.get_dust"].format(event.orb.xpValue) // TODO set language
            ),
            2,
            10
        ).let { ActionBar.setActionBar(player, it) }
    }
}
