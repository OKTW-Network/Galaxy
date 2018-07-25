package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.*
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.gui.view.GridGUIView
import one.oktw.galaxy.item.enums.ButtonType.ARROW_LEFT
import one.oktw.galaxy.item.enums.ButtonType.ARROW_RIGHT
import one.oktw.galaxy.item.type.Button
import one.oktw.galaxy.util.OrderedLaunch
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*
import java.util.Arrays.asList

abstract class PageGUI : GUI() {
    companion object {
        private const val CHANGE_PAGE_INTERVAL = 100
        private const val SET_ITEM_DELAY = 20

        enum class Action {
            PrevPage,
            NextPage
        }

        enum class Slot {
            NULL,
            PREV,
            NEXT,
            ITEMS,
        }

        private val X = Slot.NULL
        private val P = Slot.PREV
        private val N = Slot.NEXT
        private val O = Slot.ITEMS

        private const val WIDTH = 9
        private const val HEIGHT = 6

        private val layout = listOf(
            O, O, O, O, O, O, O, O, O,
            O, O, O, O, O, O, O, O, O,
            O, O, O, O, O, O, O, O, O,
            O, O, O, O, O, O, O, O, O,
            O, O, O, O, O, O, O, O, O,
            P, X, X, X, X, X, X, X, N
        )
    }

    private val lang = languageService.getDefaultLanguage()
    private var pageNumber = 0
    private val buttonID = Array(2) { UUID.randomUUID() }
    private val lock = OrderedLaunch()

    val view: GridGUIView<Slot, Action> by lazy {
        GridGUIView<Slot, Action>(
            inventory,
            layout,
            Pair(WIDTH, HEIGHT)
        )
    }

    init {
        registerEvent(ClickInventoryEvent::class.java, ::clickEvent)
    }

    protected abstract suspend fun get(number: Int, skip: Int): List<ItemStack>

    // There should be only one offerPage processed at same time, or the pages will be merged all together
    protected fun offerPage(pageNumber: Int) = lock.launch {
        view.disabled = true

        val maxItem = view.countSlots(Slot.ITEMS)

        // wipe the inventory, so the sponge won't trigger the inventory event for nonexistent items
        view.clear()

        // wait a moment, so the player won't be able to click too fast
        delay(CHANGE_PAGE_INTERVAL)

        // wipe the inventory again so even sponge rollback something by accident, the gui won't break
        view.clear()

        // wait a moment to prevent race condition in sponge inventory handling
        delay(SET_ITEM_DELAY)

        get(maxItem, pageNumber * maxItem).let { view.setSlots(Slot.ITEMS, ArrayList(it)) }
        offerButton(pageNumber != 0, !get(1, (pageNumber + 1) * maxItem).isEmpty())
        view.disabled = false
    }

    protected fun isButton(item: ItemStackSnapshot) = view.getNameOf(item)?.first in asList(Slot.NEXT, Slot.PREV)

    protected fun isButton(uuid: UUID) = view.getNameOf(uuid)?.first in asList(Slot.NEXT, Slot.PREV)

    private fun offerButton(previous: Boolean, next: Boolean) {
        if (previous) {
            Button(ARROW_LEFT).createItemStack()
                .apply {
                    offer(DataUUID(buttonID[0]))
                    offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, lang["UI.Button.PreviousPage"]))
                }
                .let { view.setSlot(Slot.PREV, it, Action.PrevPage) }
        }

        if (next) {
            Button(ARROW_RIGHT).createItemStack()
                .apply {
                    offer(DataUUID(buttonID[1]))
                    offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, lang["UI.Button.NextPage"]))
                }
                .let { view.setSlot(Slot.NEXT, it, Action.NextPage) }
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

        val item = event.cursorTransaction.default

        // handle only buttons, let gui extends this decide how to handle them
        if (isButton(item)) {
            val action = view.getDataOf(item)

            // wipe it directly, because we don't want the button to be rollback
            event.cursorTransaction.apply {
                setCustom(ItemStackSnapshot.NONE)
            }

            when (action) {
                Action.PrevPage -> if (pageNumber > 0) offerPage(--pageNumber)
                Action.NextPage -> offerPage(++pageNumber)
            }
        }
    }
}
