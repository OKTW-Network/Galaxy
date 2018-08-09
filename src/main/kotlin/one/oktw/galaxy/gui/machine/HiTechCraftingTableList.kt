package one.oktw.galaxy.gui.machine

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
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
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList

class HiTechCraftingTableList(private val player: Player) : GUI() {
    companion object {
        private const val CHANGE_PAGE_DELAY = 100
        private const val OFFER_ITEM_DELAY = 100
        private val lang = Main.languageService.getDefaultLanguage()

        private enum class Action {
            NONE,
            CRAFT,
            SELECT_CATALOG,
            PREV_PAGE,
            NEXT_PAGE
        }

        private enum class Slot {
            NULL,
            CATALOG_UP,
            CATALOG_BOTTOM,
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
        private val U = Slot.CATALOG_UP
        private val D = Slot.CATALOG_BOTTOM
        private val R = Slot.RECIPE
        private val P = Slot.PREV_PAGE
        private val Q = Slot.NEXT_PAGE


        private const val WIDTH = 9
        private const val HEIGHT = 6

        private val layout = asList(
            N, U, U, U, U, U, U, U, N,
            N, D, D, D, D, D, D, D, N,
            R, R, R, R, R, R, R, R, P,
            R, R, R, R, R, R, R, R, N,
            R, R, R, R, R, R, R, R, N,
            R, R, R, R, R, R, R, R, Q
        )
    }

    override val token: String = "HiTechCraftingTableList-${UUID.randomUUID()}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.DOUBLE_CHEST)
        .property(InventoryTitle.of(Text.of(lang["UI.Title.HiTechCraftingTableList"])))
        .listener(InteractInventoryEvent::class.java, ::eventProcess)
        .build(Main.main)

    private val view: GridGUIView<Slot, Data> = GridGUIView(
        inventory,
        layout,
        Pair(WIDTH, HEIGHT)
    )

    private var currentPage: Recipes.Companion.Type = Recipes.types[0]
    private var curretOffset: Int = 0

    private val lock = OrderedLaunch()

    init {
        offerPage(currentPage, curretOffset)
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
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
        val upRow = ArrayList<Pair<ItemStack?, Data?>>()
        val bottomRow = ArrayList<ItemStack>()
        val maxLength = view.countSlots(Slot.CATALOG_UP)

        val iterator = Recipes.types.iterator()

        for (i in (1..maxLength - 2)) {
            if (iterator.hasNext()) {
                iterator.next().let {
                    if (it == page) {
                        upRow.add(Pair(getGUIItem(ButtonType.GUI_LEFT), null))
                        bottomRow.add(getGUIItem(ButtonType.GUI_CORNER_BOTTOM_LEFT))
                    }

                    Recipes.icons[it]!!.createStack()
                        .apply {
                            offer(DataUUID(UUID.randomUUID()))
                            offer(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, Recipes.names[it]!!))
                        }
                        .let { stack ->
                            upRow.add(
                                if (it == page) {
                                    Pair(stack, null)
                                } else {
                                    Pair(stack, Data(action = Action.SELECT_CATALOG, catalog = it))
                                }
                            )
                        }

                    bottomRow.add(
                        if (it == page) {
                            getGUIItem(ButtonType.GUI_BOTTOM)
                        } else {
                            getGUIItem(ButtonType.GUI_CENTER)
                        }
                    )

                    if (it == page) {
                        upRow.add(Pair(getGUIItem(ButtonType.GUI_RIGHT), null))
                        bottomRow.add(getGUIItem(ButtonType.GUI_CORNER_BOTTOM_RIGHT))
                    }
                }
            } else {
                upRow.add(Pair(null, null))
                bottomRow.add(getGUIItem(ButtonType.GUI_CENTER))
            }
        }

        view.setSlotPairs(Slot.CATALOG_UP, upRow)
        view.setSlots(Slot.CATALOG_BOTTOM, bottomRow, null)
    }

    private suspend fun offerRecipes(page: Recipes.Companion.Type, offset: Int = 0) {
        val traveler = TravelerHelper.getTraveler(player).await() ?: return
        val recipes = Recipes.catalog[page] ?: return
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
        val recipes = Recipes.catalog[page]?.size ?: return
        val slots = view.countSlots(Slot.RECIPE)

        val hasPrev = offset > 0
        val hasNext = offset + slots < recipes

        if (hasPrev) {
            view.setSlot(Slot.PREV_PAGE, getGUIItem(ButtonType.ARROW_UP), Data(action = Action.PREV_PAGE))
        } else {
            view.setSlot(Slot.PREV_PAGE, getGUIItem(ButtonType.BLANK), null)
        }


        if (hasNext) {
            view.setSlot(Slot.NEXT_PAGE, getGUIItem(ButtonType.ARROW_DOWN), Data(action = Action.NEXT_PAGE))
        } else {
            view.setSlot(Slot.NEXT_PAGE, getGUIItem(ButtonType.BLANK), null)
        }
    }

    private fun offerEmpty() {
        (1..view.countSlots(Slot.NULL)).map {
            getGUIItem(ButtonType.GUI_CENTER)
        }.let {
            view.setSlots(Slot.NULL, it, null)
        }
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        // we trap everything when gui is disabled
        if (view.disabled) {
            if (view.getNameOf(event) != null) {
                // item on the gui
                // because we are going to wipe the gui, items should not be rollback by the sponge
                event.cursorTransaction.apply {
                    setCustom(ItemStackSnapshot.NONE)
                    // isValid = false
                }
            } else {
                // item in the user's inventory
                // should be rollback
                event.isCancelled = true
            }

            return
        }

        val data = view.getDataOf(event)
        val player = event.source as Player

        if (data == null) {
            event.isCancelled = true
            return
        }

        val (action, index, catalog) = data

        when (action) {
            Action.SELECT_CATALOG -> {
                event.cursorTransaction.apply {
                    setCustom(ItemStackSnapshot.NONE)
                }

                currentPage = catalog
                curretOffset = 0
                offerPage(currentPage, curretOffset)
            }

            Action.CRAFT -> {
                event.isCancelled = true

                // player.sendMessage(Text.of("recipe $catalog $index"))

                launch {
                    val traveler = TravelerHelper.getTraveler(player).await() ?: return@launch

                    GUIHelper.open(player) { HiTechCraftingTableRecipe(player, traveler, Recipes.catalog[catalog]!![index]) }
                }
            }

            Action.PREV_PAGE -> {
                event.cursorTransaction.apply {
                    setCustom(ItemStackSnapshot.NONE)
                }

                curretOffset -= view.countSlots(Slot.RECIPE)
                offerPage(currentPage, curretOffset)
            }

            Action.NEXT_PAGE -> {
                event.cursorTransaction.apply {
                    setCustom(ItemStackSnapshot.NONE)
                }

                curretOffset += view.countSlots(Slot.RECIPE)
                offerPage(currentPage, curretOffset)
            }

            else -> {
                event.isCancelled = true
            }
        }
    }
}
