package one.oktw.galaxy.gui

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.enums.ButtonType.OK
import one.oktw.galaxy.enums.ButtonType.X
import one.oktw.galaxy.helper.ItemHelper
import one.oktw.galaxy.types.item.Button
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.type.GridInventory
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*

class Confirm(content: Text, private val callback: (Boolean) -> Unit) : GUI() {
    override val token = "Confirm-${UUID.randomUUID()}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.HOPPER)
        .property(InventoryTitle.of(content))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(main)
    private val buttonID = Array(3) { UUID.randomUUID() }

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        // button
        ItemHelper.getItem(Button(OK))?.apply {
            offer(DataUUID(buttonID[0]))
            offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, "是"))
        }?.let { inventory.set(1, 0, it) }

        ItemHelper.getItem(Button(X))?.apply {
            offer(DataUUID(buttonID[1]))
            offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, "否"))
        }?.let { inventory.set(3, 0, it) }

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true

        when (event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return) {
            buttonID[0] -> callback(true)
            buttonID[1] -> callback(false)
        }
    }
}
