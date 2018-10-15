package one.oktw.galaxy.block.event

import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.block.enums.CustomBlocks.*
import one.oktw.galaxy.data.DataBlockType
import one.oktw.galaxy.gui.GUIHelper
import one.oktw.galaxy.gui.MainMenu
import one.oktw.galaxy.gui.machine.HiTechCraftingTableList
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

        val blockType = event.targetBlock.location.orElse(null)?.get(DataBlockType.key)?.orElse(null) ?: return

        when (blockType) {
            DUMMY -> Unit
            CONTROL_PANEL -> GUIHelper.open(player) { MainMenu(player) }
            PLANET_TERMINAL -> GlobalScope.launch(Dispatchers.Default) {
                galaxyManager.get(player.world)?.let { GUIHelper.open(player) { PlanetTerminal(it, player) } }
            }
            HT_CRAFTING_TABLE -> GUIHelper.open(player) { HiTechCraftingTableList(player) }
            else -> Unit
        }

        if (blockType.hasGUI) {
            event.isCancelled = true
        }
    }
}
