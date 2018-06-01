package one.oktw.galaxy.economy.event

import net.minecraftforge.event.entity.player.PlayerPickupXpEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.internal.LangSys
import one.oktw.galaxy.traveler.data.ActionBarData
import one.oktw.galaxy.traveler.service.ActionBar
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors

class TravelerEvent {
    @SubscribeEvent
    fun onPickupExp(event: PlayerPickupXpEvent) {
        val player = event.entityPlayer as Player
        //Todo check player lang
        val lang = LangSys().rootNode.getNode("traveler","event","get_dust")

        travelerManager.getTraveler(player).giveStarDust(event.orb.xpValue)

        ActionBar.setActionBar(player, ActionBarData(Text.of(TextColors.AQUA, lang.string.format(event.orb.xpValue)), 2, 10))
    }
}
