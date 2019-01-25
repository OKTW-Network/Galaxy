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

package one.oktw.galaxy.gui.machine

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import one.oktw.galaxy.Main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.traveler.TravelerHelper
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUIHelper
import one.oktw.galaxy.gui.view.GridGUIView
import one.oktw.galaxy.item.enums.ButtonType
import one.oktw.galaxy.item.type.Button
import one.oktw.galaxy.recipe.Recipes
import one.oktw.galaxy.util.OrderedLaunch
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.text.format.TextColors
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList

class HiTechCraftingTableList(private val player: Player) : GUI() {
    companion object {
        private const val CHANGE_PAGE_DELAY = 100L
        private const val OFFER_ITEM_DELAY = 100L
        private val lang = Main.translationService

        private enum class Action {
            NONE,
            CRAFT,
            SELECT_CATALOG,
            PREV_PAGE,
            NEXT_PAGE
        }

        private enum class Slot {
            NULL,
            CATALOG,
            CATALOG_SELECT,
            RECIPE,
            PREV_PAGE,
            NEXT_PAGE
        }

        private data class Data(
            val action: Action = Action.NONE,
            val index: Int = 0,
            val catalog: Recipes.Companion.Type = Recipes.Companion.Type.MACHINE
        )

        private val N = Slot.NULL
        private val C = Slot.CATALOG
        private val S = Slot.CATALOG_SELECT
        private val R = Slot.RECIPE
        private val P = Slot.PREV_PAGE
        private val Q = Slot.NEXT_PAGE


        private const val WIDTH = 9
        private const val HEIGHT = 6

        private val layout = asList(
            N, N, C, C, C, C, C, S, N,
            R, R, R, R, R, R, R, R, P,
            R, R, R, R, R, R, R, R, N,
            R, R, R, R, R, R, R, R, N,
            R, R, R, R, R, R, R, R, N,
            R, R, R, R, R, R, R, R, Q
        )
    }

    override val token: String = "HiTechCraftingTableList-${UUID.randomUUID()}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.DOUBLE_CHEST)
        .property(InventoryTitle.of(lang.ofPlaceHolder("UI.Title.HiTechCraftingTableList")))
        .listener(InteractInventoryEvent::class.java, ::eventProcess)
        .build(Main.main)

    private val view: GridGUIView<Slot, Data> = GridGUIView(
        inventory,
        layout,
        Pair(WIDTH, HEIGHT)
    )

    private val isCreative = player.gameMode().get() == GameModes.CREATIVE
    private val catalog = if (isCreative) {
        Recipes.creativeCatalog
    } else {
        Recipes.catalog
    }

    private var currentPage: Recipes.Companion.Type = Recipes.types[0]
    private var currentOffset: Int = 0

    private val lock = OrderedLaunch()

    init {
        offerPage(currentPage, currentOffset)
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
        registerEvent(InteractInventoryEvent.Open::class.java, this::openInventoryEvent)
    }

    private fun offerPage(page: Recipes.Companion.Type, offset: Int) = lock.launch {
        view.disabled = true
        view.clear()
        delay(CHANGE_PAGE_DELAY)
        view.clear()
        delay(OFFER_ITEM_DELAY)
        offerEmpty()
        offerPageButton(page, offset)
        offerCatalog(page)
        offerRecipes(page, offset)
        view.disabled = false
    }

    private fun getGUIItem(type: ButtonType): ItemStack {
        return Button(type).createItemStack()
            .apply {
                offer(DataUUID(UUID.randomUUID()))
            }
    }

    private fun offerCatalog(page: Recipes.Companion.Type) {
        val row = ArrayList<Pair<ItemStack?, Data?>>()
        val maxLength = view.countSlots(Slot.CATALOG)

        val iterator = Recipes.types.iterator()

        var selected = 0

        for (i in (1..maxLength)) {
            if (iterator.hasNext()) {
                iterator.next().let {
                    Recipes.icons[it]!!.createStack()
                        .apply {
                            offer(Keys.DISPLAY_NAME, lang.ofPlaceHolder(TextColors.AQUA, Recipes.names[it]!!))
                        }
                        .let { stack ->
                            row.add(
                                if (it == page) {
                                    selected = i
                                    Pair(stack, null)
                                } else {
                                    Pair(stack, Data(action = Action.SELECT_CATALOG, catalog = it))
                                }
                            )
                        }
                }
            } else {
                row.add(Pair(getGUIItem(ButtonType.GUI_CENTER), null))
            }
        }

        view.setSlotPairs(Slot.CATALOG, row)
        view.setSlot(
            Slot.CATALOG_SELECT,
            when (selected) {
                1 -> getGUIItem(ButtonType.GUI_HTCT_TAB_1)
                2 -> getGUIItem(ButtonType.GUI_HTCT_TAB_2)
                3 -> getGUIItem(ButtonType.GUI_HTCT_TAB_3)
                4 -> getGUIItem(ButtonType.GUI_HTCT_TAB_4)
                5 -> getGUIItem(ButtonType.GUI_HTCT_TAB_5)
                else -> getGUIItem(ButtonType.GUI_CENTER)
            }
        )
    }

    private suspend fun offerRecipes(page: Recipes.Companion.Type, offset: Int = 0) {
        val traveler = TravelerHelper.getTraveler(player) ?: return

        val recipes = catalog[page] ?: return

        val slots = view.countSlots(Slot.RECIPE)

        fun fixBound(num: Int, max: Int): Int {
            if (num < 0) {
                return 0
            }

            if (num > max) {
                return max
            }

            return num
        }

        recipes
            .mapIndexed { index, item ->
                Pair(index, item)
            }
            .subList(fixBound(offset, recipes.size), fixBound(offset + slots, recipes.size))
            .map { (index, recipe) ->
                val item = recipe.previewResult(player, traveler)
                item.offer(DataUUID(UUID.randomUUID()))
                Pair(item, Data(action = Action.CRAFT, index = index, catalog = page))
            }.let {
                view.setSlotPairs(Slot.RECIPE, it)
            }
    }

    private fun offerPageButton(page: Recipes.Companion.Type, offset: Int = 0) {
        val recipes = catalog[page]?.size ?: return

        val slots = view.countSlots(Slot.RECIPE)

        val hasPrev = offset > 0
        val hasNext = offset + slots < recipes

        if (hasPrev) {
            view.setSlot(Slot.PREV_PAGE, getGUIItem(ButtonType.ARROW_UP), Data(action = Action.PREV_PAGE))
        } else {
            view.setSlot(Slot.PREV_PAGE, getGUIItem(ButtonType.UNCLICKABLE_ARROW_UP), null)
        }


        if (hasNext) {
            view.setSlot(Slot.NEXT_PAGE, getGUIItem(ButtonType.ARROW_DOWN), Data(action = Action.NEXT_PAGE))
        } else {
            view.setSlot(Slot.NEXT_PAGE, getGUIItem(ButtonType.UNCLICKABLE_ARROW_DOWN), null)
        }
    }

    private fun offerEmpty() {
        (1..view.countSlots(Slot.NULL)).map {
            getGUIItem(ButtonType.GUI_CENTER)
        }.let {
            view.setSlots(Slot.NULL, it, null)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun openInventoryEvent(event: InteractInventoryEvent.Open) {
        launch {
            offerRecipes(currentPage, currentOffset)
        }
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        val detail = view.getDetail(event)

        // don't let user move items in gui
        if (detail.affectGUI) {
            event.isCancelled = true
        }

        // we are updating gui
        if (view.disabled) {
            return
        }

        val player = event.source as? Player ?: return

        val (action, index, catalog) = detail.primary?.data ?: return

        when (action) {
            Action.SELECT_CATALOG -> {
                event.cursorTransaction.apply {
                    setCustom(ItemStackSnapshot.NONE)
                }

                currentPage = catalog
                currentOffset = 0
                offerPage(currentPage, currentOffset)
            }

            Action.CRAFT -> {
                event.isCancelled = true

                // player.sendMessage(Text.of("recipe $catalog $index"))

                launch {
                    val traveler = TravelerHelper.getTraveler(player) ?: return@launch

                    GUIHelper.open(player) {
                        HiTechCraftingTableRecipe(
                            player, traveler,
                            this@HiTechCraftingTableList.catalog[catalog]!![index]
                        )
                    }
                }
            }

            Action.PREV_PAGE -> {
                event.cursorTransaction.apply {
                    setCustom(ItemStackSnapshot.NONE)
                }

                currentOffset -= view.countSlots(Slot.RECIPE)
                offerPage(currentPage, currentOffset)
            }

            Action.NEXT_PAGE -> {
                event.cursorTransaction.apply {
                    setCustom(ItemStackSnapshot.NONE)
                }

                currentOffset += view.countSlots(Slot.RECIPE)
                offerPage(currentPage, currentOffset)
            }

            else -> {
                event.isCancelled = true
            }
        }
    }
}
