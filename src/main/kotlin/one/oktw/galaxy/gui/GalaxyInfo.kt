package one.oktw.galaxy.gui

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.helper.GUIHelper
import one.oktw.galaxy.types.Galaxy
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.type.GridInventory
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.*

class GalaxyInfo(val uuid: UUID) : GUI() {
    override val token = "GalaxyInfo-$uuid"
    val galaxy: Galaxy = TODO("Galaxy")
    override val inventory: Inventory = Inventory.builder()
            .of(InventoryArchetypes.HOPPER)
            .property(InventoryTitle.of(Text.of(galaxy.name)))
            .listener(InteractInventoryEvent::class.java, this::eventProcess)
            .build(main)
    private val buttonID = Array(3) { UUID.randomUUID() }

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        // put button
        val listMemberButton = ItemStack.builder()
                .itemType(ItemTypes.BARRIER)
                .itemData(DataUUID(buttonID[0]))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "成員列表"))
                .build()
        val listPlanetButton = ItemStack.builder()
                .itemType(ItemTypes.BARRIER)
                .itemData(DataUUID(buttonID[1]))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "星球列表"))
                .build()

        inventory.set(0, 0, listMemberButton)
        inventory.set(2, 0, listPlanetButton)
        //TODO Check if is admin or owner
        if (true) {
            val manageGalaxyButton = ItemStack.builder()
                    .itemType(ItemTypes.BARRIER)
                    .itemData(DataUUID(buttonID[2]))
                    .add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "管理星系"))
                    .build()
            inventory.set(4, 0, manageGalaxyButton)
        }

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true
        val player = event.source as? Player ?: return
        when (event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return) {
            buttonID[0] -> GUIHelper.open(player) { BrowserMember(uuid) }
            buttonID[1] -> GUIHelper.open(player) { BrowserPlanet(uuid) }
            buttonID[2] -> GUIHelper.open(player) { GalaxyManagement(uuid) } //travelerManager.getTraveler(player)
        }
    }
}
