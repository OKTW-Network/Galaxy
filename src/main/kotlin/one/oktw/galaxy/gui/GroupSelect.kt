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
import org.spongepowered.api.text.format.TextStyles
import java.util.*

class GroupSelect : GUI() {
    override val token = "GroupSelect-${UUID.randomUUID()}"
    override val inventory: Inventory = Inventory.builder()
            .of(InventoryArchetypes.HOPPER)
            .property(InventoryTitle.of(Text.of("選擇一個身分組")))
            .listener(InteractInventoryEvent::class.java, this::eventProcess)
            .build(main)
    private val buttonID = Array(3) { UUID.randomUUID() }

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        // put button
        val ownerMemberButton = ItemStack.builder()
                .itemType(ItemTypes.BARRIER)
                .itemData(DataUUID(buttonID[0]))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, TextStyles.BOLD, "OWNER"))
                .build()
        val adminPlanetButton = ItemStack.builder()
                .itemType(ItemTypes.BARRIER)
                .itemData(DataUUID(buttonID[1]))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, TextStyles.BOLD, "ADMIN"))
                .build()
        val memberMemberButton = ItemStack.builder()
                .itemType(ItemTypes.BARRIER)
                .itemData(DataUUID(buttonID[2]))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, "MEMBER"))
                .build()
        val visitorManagementButton = ItemStack.builder()
                .itemType(ItemTypes.BARRIER)
                .itemData(DataUUID(buttonID[3]))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, TextStyles.BOLD, "VISITOR"))
                .build()
        inventory.set(0, 0, ownerMemberButton)
        inventory.set(1, 0, adminPlanetButton)
        inventory.set(3, 0, memberMemberButton)
        inventory.set(4, 0, visitorManagementButton)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true
        val player = event.source as? Player ?: return
        when (event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return) {
            buttonID[0] -> TODO()
            buttonID[1] -> TODO()
            buttonID[2] -> TODO()
            buttonID[3] -> TODO()
        }
    }
}