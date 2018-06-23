package one.oktw.galaxy.block.event

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.block.CustomBlocks.*
import one.oktw.galaxy.data.DataBlockType
import one.oktw.galaxy.gui.GUIHelper
import one.oktw.galaxy.gui.machine.PlanetTerminal
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.filter.cause.First

class BlockGUI {
    @Listener
    fun onClickBlock(event: InteractBlockEvent.Primary, @First player: Player) {
        if (player[Keys.IS_SNEAKING].orElse(false) == true) return

        val type = event.targetBlock[DataBlockType.key].orElse(null) ?: return
        val worldUUID = player.world.uniqueId

        when (type) {
            DUMMY -> Unit
            CONTROL_PANEL -> GUIHelper.open(player) { TODO() }
            PLANET_TERMINAL -> launch {
                galaxyManager.getPlanetFromWorld(worldUUID).await()
                    ?.let { GUIHelper.open(player) { PlanetTerminal(it) } }
            }
            HT_CRAFTING_TABLE -> GUIHelper.open(player) { TODO() }
        }
    }
}
