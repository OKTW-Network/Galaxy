package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.async
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.item.enums.ButtonType.ARROW_LEFT
import one.oktw.galaxy.item.enums.ButtonType.ARROW_RIGHT
import one.oktw.galaxy.item.type.Button
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.inventory.query.QueryOperationTypes.INVENTORY_TYPE
import org.spongepowered.api.item.inventory.type.GridInventory
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*

abstract class PageGUI : GUI() {
    abstract val pages: Sequence<List<List<ItemStack>>>
    private var pageNumber = 0
    private val buttonID = Array(2) { UUID.randomUUID() }

    init {
        registerEvent(ClickInventoryEvent::class.java, ::clickEvent)
    }

    protected fun offerPage(pageNumber: Int) = async {
        inventory.clear()
        pages.elementAt(pageNumber).forEachIndexed(this@PageGUI::offerLine)
        offerButton(pageNumber != 0, pages.drop(pageNumber + 1).firstOrNull() != null)
    }

    protected fun isButton(uuid: UUID) = buttonID.contains(uuid)

    private fun offerLine(line: Int, list: List<ItemStack>) {
        val gridInventory: GridInventory = inventory.query(INVENTORY_TYPE.of(GridInventory::class.java))

        list.forEachIndexed { slot, item -> gridInventory[slot, line] = item }
    }

    private fun offerButton(previous: Boolean, next: Boolean) {
        val gridInventory: GridInventory = inventory.query(INVENTORY_TYPE.of(GridInventory::class.java))

        if (previous) {
            Button(ARROW_LEFT).createItemStack()
                .apply {
                    offer(DataUUID(buttonID[0]))
                    offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, "Previous"))
                }
                .let { gridInventory[0, 5] = it }
        }

        if (next) {
            Button(ARROW_RIGHT).createItemStack()
                .apply {
                    offer(DataUUID(buttonID[1]))
                    offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, "Next"))
                }
                .let { gridInventory[8, 5] = it }
        }
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        val item = event.cursorTransaction.default

        if (item[DataUUID.key].orElse(null) in buttonID) {
            when (item[DataUUID.key].get()) {
                buttonID[0] -> offerPage(--pageNumber)
                buttonID[1] -> offerPage(++pageNumber)
            }

            event.cursorTransaction.apply {
                setCustom(ItemStackSnapshot.NONE)
                isValid = false
            }
        } else {
            event.transactions.any { it.default[DataUUID.key].orElse(null) in buttonID }.let(event::setCancelled)
        }
    }
}
