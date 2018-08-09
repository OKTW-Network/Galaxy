package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.delay
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.gui.view.GridGUIView
import one.oktw.galaxy.item.enums.ButtonType
import one.oktw.galaxy.item.enums.ButtonType.*
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
import kotlin.collections.ArrayList

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
            NUMBER
        }

        private val X = Slot.NULL
        private val P = Slot.PREV
        private val N = Slot.NEXT
        private val O = Slot.ITEMS
        private val V = Slot.NUMBER

        private const val WIDTH = 9
        private const val HEIGHT = 6

        private val layout = listOf(
            O, O, O, O, O, O, O, O, O,
            O, O, O, O, O, O, O, O, O,
            O, O, O, O, O, O, O, O, O,
            O, O, O, O, O, O, O, O, O,
            O, O, O, O, O, O, O, O, O,
            X, X, P, V, V, V, N, X, X
        )

        private val numbers = asList(
            ButtonType.NUMBER_0, ButtonType.NUMBER_1, ButtonType.NUMBER_2, ButtonType.NUMBER_3, ButtonType.NUMBER_4,
            ButtonType.NUMBER_5, ButtonType.NUMBER_6, ButtonType.NUMBER_7, ButtonType.NUMBER_8, ButtonType.NUMBER_9
        ).map { Button(it).createItemStack().apply { offer(DataUUID(UUID.randomUUID())) } }
    }

    private val lang = languageService.getDefaultLanguage()
    private var pageNumber = 0
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

    private fun getNumbers(number: Int, length: Int): List<ItemStack> {
        val result = ArrayList<ItemStack>()
        var remain = number

        for (i in 0 until length) {
            val digit = remain % 10
            remain /= 10
            result += numbers[digit]
        }

        return result.reversed()
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

        val showNextPage = !get(1, (pageNumber + 1) * maxItem).isEmpty()

        get(maxItem, pageNumber * maxItem).let { view.setSlots(Slot.ITEMS, ArrayList(it)) }
        offerButton(pageNumber != 0, showNextPage)
        offerNumber(pageNumber + 1) // make it start from one...
        offerEmptySlot(pageNumber == 0, !showNextPage)
        view.disabled = false
    }

    protected fun isControl(event: ClickInventoryEvent): Boolean {
        view.getDetail(event).affectedSlots.forEach {
            if (it.type in asList(Slot.NEXT, Slot.PREV, Slot.NUMBER, Slot.NULL)) {
                return true
            }
        }

        return false
    }
    private fun offerButton(previous: Boolean, next: Boolean) {
        if (previous) {
            Button(ARROW_LEFT).createItemStack()
                .apply {
                    offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, lang["UI.Button.PreviousPage"]))
                }
                .let { view.setSlot(Slot.PREV, it, Action.PrevPage) }
        }

        if (next) {
            Button(ARROW_RIGHT).createItemStack()
                .apply {
                    offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, lang["UI.Button.NextPage"]))
                }
                .let { view.setSlot(Slot.NEXT, it, Action.NextPage) }
        }
    }

    private fun offerNumber(pageNumber: Int) {
        val length = view.countSlots(Slot.NUMBER)

        view.setSlots(Slot.NUMBER, getNumbers(pageNumber, length))
    }

    private fun offerEmptySlot(fillPrev: Boolean, fillNext: Boolean) {
        view.countSlots(Slot.NULL)
            .let { (0 until it) }
            .map {
                Button(GUI_CENTER).createItemStack()
            }
            .let { view.setSlots(Slot.NULL, it) }

        if (fillPrev) {
            Button(GUI_CENTER).createItemStack()
                .let { view.setSlot(Slot.PREV, it, null) }
        }

        if (fillNext) {
            Button(GUI_CENTER).createItemStack()
                .let { view.setSlot(Slot.NEXT, it, null) }
        }
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        val info = view.getDetail(event)

        // we trap everything when gui is disabled
        if (view.disabled) {
            if (info.affectGUI) {
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

        // handle only buttons, let gui extends this decide how to handle them
        if (isControl(event)) {
            val action = info.primary?.data

            if (action != null) {
                // wipe it directly, because we are going to change page and we don't want the buttons to be rollback
                event.cursorTransaction.apply {
                    setCustom(ItemStackSnapshot.NONE)
                }
            } else {
                // other gui elements
                event.isCancelled = true
            }

            when (action) {
                Action.PrevPage -> if (pageNumber > 0) offerPage(--pageNumber)
                Action.NextPage -> offerPage(++pageNumber)
            }
        }
    }
}
