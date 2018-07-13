package one.oktw.galaxy.block.event

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.block.enums.CustomBlocks.*
import one.oktw.galaxy.data.DataBlockType
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.gui.GUIHelper
import one.oktw.galaxy.gui.MainMenu
import one.oktw.galaxy.gui.machine.PlanetTerminal
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.filter.cause.First

class BlockGUI {
    @Listener
    fun onClickBlock(event: InteractBlockEvent.Secondary.MainHand, @First player: Player) {
        if (player[Keys.IS_SNEAKING].orElse(false) == true) return

        when (event.targetBlock.location.orElse(null)?.get(DataBlockType.key)?.orElse(null) ?: return) {
            DUMMY -> Unit
            CONTROL_PANEL -> GUIHelper.open(player) { MainMenu(player) }
            PLANET_TERMINAL -> launch {
                galaxyManager.get(player.world).await()?.getPlanet(player.world)?.let {
                    GUIHelper.open(player) { PlanetTerminal(it) }
                }
            }
            HT_CRAFTING_TABLE -> Unit // TODO GUIHelper.open(player) { }
            else -> Unit
        }
    }
}
