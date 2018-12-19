package one.oktw.galaxy.gui

import kotlinx.coroutines.launch
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.Main.Companion.translationService
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.item.enums.ButtonType.*
import one.oktw.galaxy.item.type.Button
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.key.Keys.DISPLAY_NAME
import org.spongepowered.api.data.key.Keys.ITEM_LORE
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.type.GridInventory
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors.YELLOW
import java.lang.Math.*
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList

class NumberSelect(content: Text, tip: List<Text> = emptyList(), private val callback: (Int) -> Unit) : GUI() {
    private var value = 0
    private val plusButton = Array(7) { UUID.randomUUID() }
    private val minusButton = Array(7) { UUID.randomUUID() }
    private val doneButton = UUID.randomUUID()
    private val numbers = asList(
        NUMBER_0, NUMBER_1, NUMBER_2, NUMBER_3, NUMBER_4,
        NUMBER_5, NUMBER_6, NUMBER_7, NUMBER_8, NUMBER_9
    ).map { Button(it).createItemStack() }
    override val token = "NumberSelect-${UUID.randomUUID()}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.CHEST)
        .property(InventoryTitle.of(content))
        .listener(InteractInventoryEvent::class.java, ::eventProcess)
        .build(Main.main)

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        // Plus button
        for (i in 0..6) {
            inventory.set(6 - i, 0, Button(ARROW_UP).createItemStack().apply { offer(DataUUID(plusButton[i])) })
        }

        // Minus button
        for (i in 0..6) {
            inventory.set(6 - i, 2, Button(ARROW_DOWN).createItemStack().apply { offer(DataUUID(minusButton[i])) })
        }

        // Number
        for (i in 0..6) {
            inventory.set(6 - i, 1, numbers[0])
        }

        // Info
        Button(GUI_INFO).createItemStack()
            .apply {
                offer(DISPLAY_NAME, translationService.ofPlaceHolder(YELLOW, translationService.of("UI.Button.Info")))
                offer(ITEM_LORE, tip)
            }
            .let { inventory.set(8, 0, it) }

        // Done button
        Button(OK).createItemStack()
            .apply {
                offer(DataUUID(doneButton))
                offer(Keys.DISPLAY_NAME, Text.EMPTY)
            }
            .let { inventory.set(8, 2, it) }

        // Fill empty
        GUIHelper.fillEmptySlot(inventory)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true

        val uuid = event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return

        when (uuid) {
            doneButton -> {
                callback(value)
                GUIHelper.close(token)
            }
            in plusButton -> {
                val pos = plusButton.indexOf(uuid)

                value = min(value + pow(10.0, pos.toDouble()).toInt(), 9999999)
                updateDisplay()
            }
            in minusButton -> {
                val pos = minusButton.indexOf(uuid)

                value = max(value - pow(10.0, pos.toDouble()).toInt(), 0)
                updateDisplay()
            }
        }
    }

    private fun updateDisplay() = launch(serverThread) {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        var temp = value
        val list = ArrayList<ItemStack>()

        for (i in 0..6) {
            list += numbers[temp % 10]
            temp /= 10
        }

        list.asReversed().forEachIndexed { index, item -> inventory.set(index, 1, item) }
    }
}
