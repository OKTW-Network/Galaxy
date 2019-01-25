/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2018
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package one.oktw.galaxy.block.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.block.enums.CustomBlocks.*
import one.oktw.galaxy.data.DataBlockType
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.gui.BrowserGalaxy
import one.oktw.galaxy.gui.GUIHelper
import one.oktw.galaxy.gui.machine.HiTechCraftingTableList
import one.oktw.galaxy.gui.machine.PlanetTerminal
import one.oktw.galaxy.machine.teleporter.TeleporterHelper
import one.oktw.galaxy.translation.extensions.toLegacyText
import one.oktw.galaxy.util.CountDown
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.type.HandTypes.MAIN_HAND
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors

class BlockGUI : CoroutineScope {
    override val coroutineContext by lazy { Job() + serverThread }
    private val lang = Main.translationService

    @Listener
    fun onClickBlock(event: InteractBlockEvent.Secondary.MainHand, @First player: Player) {
        if (player[Keys.IS_SNEAKING].orElse(false) == true) return

        val location = event.targetBlock.location.orElse(null) ?: return
        val blockType = location.get(DataBlockType.key).orElse(null) ?: return

        when (blockType) {
            DUMMY -> Unit
            CONTROL_PANEL -> GUIHelper.open(player) { BrowserGalaxy(player) }
            PLANET_TERMINAL -> launch { galaxyManager.get(player.world)?.let { GUIHelper.open(player) { PlanetTerminal(it, player) } } }
            HT_CRAFTING_TABLE -> GUIHelper.open(player) { HiTechCraftingTableList(player) }
            TELEPORTER, TELEPORTER_ADVANCED -> launch {
                if (player.getItemInHand(MAIN_HAND).orElse(null)?.type == ItemTypes.NAME_TAG) {
                    // return, because player is renaming the teleportation site
                    return@launch
                }

                galaxyManager.get(player.world)?.getPlanet(player.world)?.let {
                    TeleporterHelper.get(
                        it,
                        location.blockX,
                        location.blockY,
                        location.blockZ
                    )
                }?.let {
                    if (CountDown.instance.isCounting(it.uuid)) {
                        player.sendMessage(Text.of(TextColors.RED, lang.of("Respond.Teleporting")).toLegacyText(player))

                        return@launch
                    }
                    one.oktw.galaxy.gui.machine.Teleporter(it)
                }?.let {
                    GUIHelper.open(player) { it }
                }
            }
            else -> Unit
        }

        if (blockType.hasGUI) {
            event.isCancelled = true
        }
    }
}
