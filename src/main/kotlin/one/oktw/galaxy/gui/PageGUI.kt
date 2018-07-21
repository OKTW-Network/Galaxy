package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.languageService
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

private const val ONE_PAGE = 45

abstract class PageGUI : GUI() {
    private val lang = languageService.getDefaultLanguage() // TODO get player lang
    private var pageNumber = 0
    private val buttonID = Array(2) { UUID.randomUUID() }

    init {
        registerEvent(ClickInventoryEvent::class.java, ::clickEvent)
    }

    protected abstract suspend fun get(number: Int, skip: Int): List<ItemStack>

    protected fun offerPage(pageNumber: Int) = launch {
        delay(100)

        inventory.clear()

        offerButton(pageNumber != 0, !get(1, (pageNumber + 1) * ONE_PAGE).isEmpty())
        get(ONE_PAGE, pageNumber * ONE_PAGE).chunked(9).forEachIndexed(this@PageGUI::offerLine)
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
                    offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, lang["UI.Button.PreviousPage"]))
                }
                .let { gridInventory[0, 5] = it }
        }

        if (next) {
            Button(ARROW_RIGHT).createItemStack()
                .apply {
                    offer(DataUUID(buttonID[1]))
                    offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, lang["UI.Button.NextPage"]))
                }
                .let { gridInventory[8, 5] = it }
        }
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        val item = event.cursorTransaction.default

        if (item[DataUUID.key].orElse(null) in buttonID) {
            when (item[DataUUID.key].get()) {
                buttonID[0] -> if (pageNumber > 0) offerPage(--pageNumber)
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
