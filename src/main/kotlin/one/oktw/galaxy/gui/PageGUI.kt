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

import kotlinx.coroutines.delay
import one.oktw.galaxy.Main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.gui.view.EventDetail
import one.oktw.galaxy.gui.view.GridGUIView
import one.oktw.galaxy.item.enums.ButtonType
import one.oktw.galaxy.item.enums.ButtonType.*
import one.oktw.galaxy.item.type.Button
import one.oktw.galaxy.util.OrderedLaunch
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.text.format.TextColors
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList

abstract class PageGUI<Data> : GUI() {
    companion object {
        private const val CHANGE_PAGE_INTERVAL = 100L
        private const val SET_ITEM_DELAY = 20L

        data class Operation<Data>(
            val action: Action = Action.Null,
            val data: Data? = null
        )

        enum class Action {
            PrevPage,
            NextPage,
            Item,
            Function,
            Null
        }

        enum class Slot {
            NULL,
            PREV,
            NEXT,
            ITEMS,
            NUMBER,
            FUNCTION
        }

        private val X = Slot.NULL
        private val P = Slot.PREV
        private val N = Slot.NEXT
        private val O = Slot.ITEMS
        private val V = Slot.NUMBER
        private val F = Slot.FUNCTION

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

        private val layoutWithFunction = listOf(
            O, O, O, O, O, O, O, O, F,
            O, O, O, O, O, O, O, O, F,
            O, O, O, O, O, O, O, O, F,
            O, O, O, O, O, O, O, O, F,
            O, O, O, O, O, O, O, O, F,
            X, X, P, V, V, V, N, X, X
        )

        private val numbers = asList(
            ButtonType.NUMBER_0, ButtonType.NUMBER_1, ButtonType.NUMBER_2, ButtonType.NUMBER_3, ButtonType.NUMBER_4,
            ButtonType.NUMBER_5, ButtonType.NUMBER_6, ButtonType.NUMBER_7, ButtonType.NUMBER_8, ButtonType.NUMBER_9
        ).map { Button(it).createItemStack().apply { offer(DataUUID(UUID.randomUUID())) } }
    }

    protected open val hasFunctionButtons: Boolean = false
    private val lang = Main.translationService
    private var pageNumber = 0
    private val lock = OrderedLaunch()

    val view: GridGUIView<Slot, Operation<Data>> by lazy {
        GridGUIView<Slot, Operation<Data>>(
            inventory,
            if (hasFunctionButtons) {
                layoutWithFunction
            } else {
                layout
            },
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

    protected abstract suspend fun get(number: Int, skip: Int): List<Pair<ItemStack, Data?>>

    protected open suspend fun getFunctionButtons(count: Int): List<Pair<ItemStack, Data?>> {
        return (0 until count).map {
            Pair(Button(GUI_CENTER).createItemStack(), null)
        }
    }

    protected fun offerPage() = offerPage(pageNumber)

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

        get(maxItem, pageNumber * maxItem)
            .map {
                Pair(it.first, Operation(Action.Item, it.second))
            }
            .let {
                view.setSlotPairs(Slot.ITEMS, it)
            }

        offerButton(pageNumber != 0, showNextPage)
        offerNumber(pageNumber + 1) // make it start from one...
        offerEmptySlot()

        if (hasFunctionButtons) {
            offerFunction()
        }

        view.disabled = false
    }

    protected fun isControl(detail: EventDetail<Slot, Operation<Data>>): Boolean {
        detail.affectedSlots.forEach {
            if (it.type in asList(Slot.NEXT, Slot.PREV, Slot.NUMBER, Slot.NULL, Slot.FUNCTION)) {
                return true
            }
        }

        return false
    }

    private fun offerButton(previous: Boolean, next: Boolean) {
        if (previous) {
            Button(ARROW_LEFT).createItemStack()
                .apply {
                    offer(Keys.DISPLAY_NAME, lang.ofPlaceHolder(TextColors.GREEN, lang.of("UI.Button.PreviousPage")))
                }
                .let { view.setSlot(Slot.PREV, it, Operation(Action.PrevPage)) }
        } else {
            Button(UNCLICKABLE_ARROW_LEFT).createItemStack()
                .let { view.setSlot(Slot.PREV, it, null) }
        }

        if (next) {
            Button(ARROW_RIGHT).createItemStack()
                .apply {
                    offer(Keys.DISPLAY_NAME, lang.ofPlaceHolder(TextColors.GREEN, lang.of("UI.Button.NextPage")))
                }
                .let { view.setSlot(Slot.NEXT, it, Operation(Action.NextPage)) }
        } else {
            Button(UNCLICKABLE_ARROW_RIGHT).createItemStack()
                .let { view.setSlot(Slot.NEXT, it, null) }
        }
    }

    private fun offerNumber(pageNumber: Int) {
        val length = view.countSlots(Slot.NUMBER)

        view.setSlots(Slot.NUMBER, getNumbers(pageNumber, length))
    }

    private fun offerEmptySlot() {
        view.countSlots(Slot.NULL)
            .let { (0 until it) }
            .map {
                Button(GUI_CENTER).createItemStack()
            }
            .let { view.setSlots(Slot.NULL, it) }
    }

    private suspend fun offerFunction() {
        getFunctionButtons(view.countSlots(Slot.FUNCTION)).map { (item, data) ->
            Pair(
                item,
                Operation(
                    Action.Function,
                    data
                )
            )
        }.let {
            view.setSlotPairs(Slot.FUNCTION, it)
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
        if (isControl(info)) {
            val action = info.primary?.data?.action

            if (action in asList(Action.PrevPage, Action.NextPage)) {
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
                else -> Unit
            }
        }
    }
}
