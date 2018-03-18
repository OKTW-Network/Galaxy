package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.async
import one.oktw.galaxy.data.DataType
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.enums.ButtonType
import one.oktw.galaxy.enums.ItemType
import one.oktw.galaxy.helper.ItemHelper
import one.oktw.galaxy.types.item.Button
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.inventory.type.GridInventory
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*

abstract class PageGUI : GUI() {
    abstract val pages: Sequence<List<List<ItemStack>>>
    abstract val gridInventory: GridInventory
    private var pageNumber = 0
    private val buttonID = Array(2) { UUID.randomUUID() }

    init {
        registerEvent(ClickInventoryEvent::class.java, ::clickEvent)
    }

    protected fun offerPage(pageNumber: Int) = async {
        gridInventory.clear()
        pages.elementAt(pageNumber).forEachIndexed(this@PageGUI::offerLine)
        offerButton(pageNumber != 0, pages.drop(pageNumber + 1).firstOrNull() != null)
    }

    protected fun isButton(uuid: UUID) = buttonID.contains(uuid)

    private fun offerLine(line: Int, list: List<ItemStack>) {
        list.forEachIndexed { slot, item -> gridInventory.set(slot, line, item) }
    }

    private fun offerButton(previous: Boolean, next: Boolean) {
        if (previous) {
            ItemHelper.getItem(Button(ButtonType.ARROW_LEFT))?.apply {
                offer(DataUUID(buttonID[0]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, "Previous"))
            }?.let { gridInventory.set(0, 5, it) }
        }

        if (next) {
            ItemHelper.getItem(Button(ButtonType.ARROW_RIGHT))?.apply {
                offer(DataUUID(buttonID[1]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, "Next"))
            }?.let { gridInventory.set(8, 5, it) }
        }
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        val item = event.cursorTransaction.default

        if (item[DataType.key].orElse(null) == ItemType.BUTTON) {
            when (item[DataUUID.key].orElse(null) ?: return) {
                buttonID[0] -> offerPage(--pageNumber)
                buttonID[1] -> offerPage(++pageNumber)
                else -> return
            }

            event.cursorTransaction.apply {
                setCustom(ItemStackSnapshot.NONE)
                isValid = false
            }
        } else {
            event.isCancelled = true
        }
    }
}
