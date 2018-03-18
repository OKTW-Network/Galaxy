package one.oktw.galaxy.gui

import one.oktw.galaxy.Main
import one.oktw.galaxy.data.DataType
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.enums.ItemType.BUTTON
import one.oktw.galaxy.types.Galaxy
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.query.QueryOperationTypes.INVENTORY_TYPE
import org.spongepowered.api.item.inventory.type.GridInventory
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*
import java.util.Arrays.asList

class BrowserPlanet(uuid: UUID) : PageGUI() {
    override val token = "BrowserPlanet-$uuid"
    val galaxy: Galaxy = TODO("Galaxy")
    override val inventory: Inventory = Inventory.builder()
            .of(InventoryArchetypes.DOUBLE_CHEST)
            .property(InventoryTitle.of(Text.of("星球列表")))
            .listener(InteractInventoryEvent::class.java, this::eventProcess)
            .build(Main.main)
    override val gridInventory: GridInventory = inventory.query(INVENTORY_TYPE.of(GridInventory::class.java))
    override val pages = galaxy.planets.asSequence()
        .map {
            ItemStack.builder()
                .itemType(ItemTypes.BARRIER)
                .itemData(DataType(BUTTON))
                .itemData(DataUUID(it.uuid))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, it.name))
                .add(
                    Keys.ITEM_LORE,
                    asList(
                        Text.of(TextColors.AQUA, "Players: ", TextColors.RESET, 0), // TODO
                        Text.of(TextColors.AQUA, "Security: ", TextColors.RESET, it.security.toString())
                    )
                )
                .build()
        }
        .chunked(9)
        .chunked(5)

    init {
        offerPage(0)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        val item = event.cursorTransaction.default
        val uuid = item[DataUUID.key].orElse(null) ?: return

        if (item[DataType.key].orElse(null) == BUTTON && !isButton(uuid)) {
            // TODO join planet
        }
    }
}
