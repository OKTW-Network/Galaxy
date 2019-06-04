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

package one.oktw.galaxy.gui

import kotlinx.coroutines.launch
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.data.extensions.refresh
import one.oktw.galaxy.galaxy.planet.TeleportHelper
import one.oktw.galaxy.galaxy.planet.enums.PlanetType.*
import one.oktw.galaxy.item.enums.ButtonType.*
import one.oktw.galaxy.item.type.Button
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*
import java.util.Arrays.asList

class BrowserPlanet(private val galaxy: Galaxy) : PageGUI<UUID>() {
    private val lang = Main.translationService
    override val token = "BrowserPlanet-${galaxy.uuid}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.DOUBLE_CHEST)
        .property(InventoryTitle.of(lang.ofPlaceHolder("UI.Title.PlanetList")))
        .listener(InteractInventoryEvent::class.java, ::eventProcess)
        .build(Main.main)

    init {
        gotoPage(0)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    override suspend fun get(number: Int, skip: Int): List<Pair<ItemStack, UUID>> {
        return galaxy.refresh().planets
            .drop(skip)
            .take(number)
            .map {
                Pair(
                when (it.type) {
                    NORMAL -> Button(PLANET_O)
                    NETHER -> Button(PLANET_N)
                    END -> Button(PLANET_E)
                }.createItemStack().apply {
                    offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, it.name))
                    offer(
                        Keys.ITEM_LORE,
                        asList(
                            lang.ofPlaceHolder(
                                TextColors.AQUA,
                                lang.of("UI.Tip.PlayerCount"),
                                ": ",
                                TextColors.RESET,
                                0 // TODO player on planet
                            ),
                            lang.ofPlaceHolder(
                                TextColors.AQUA,
                                lang.of("UI.Tip.AllowVisit"),
                                ": ",
                                lang.of("UI.Tip.${it.visitable}")
                            )
                        )
                    )
                }, it.uuid
                )
            }
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        if (view.disabled) return

        val detail = view.getDetail(event)

        // ignore gui elements, because they are handled by the PageGUI
        if (isControl(detail)) {
            return
        }

        if (detail.affectGUI) {
            event.isCancelled = true
        }


        if (detail.primary?.type == Companion.Slot.ITEMS) {
            val player = event.source as Player
            val uuid = detail.primary.data?.data?: return

            launch {
                val planet = galaxyManager.get(planet = uuid)?.getPlanet(uuid) ?: return@launch

                if (TeleportHelper.teleport(player, planet).await()) GUIHelper.closeAll(player)
            }
        }
    }
}
