package one.oktw.galaxy.gui

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.type.GridInventory
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*

class ManageMember(uuid: UUID) : GUI() {
    override val token = "ManageMember-$uuid"
    private val userStorage = Sponge.getServiceManager().provide(UserStorageService::class.java).get()
    val user = userStorage.get(uuid).get()
    override val inventory: Inventory = Inventory.builder()
            .of(InventoryArchetypes.HOPPER)
            .property(InventoryTitle.of(Text.of(user.name)))
            .listener(InteractInventoryEvent::class.java, this::eventProcess)
            .build(main)
    private val buttonID = Array(3) { UUID.randomUUID() }

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        // put button
        val removeMemberButton = ItemStack.builder()
                .itemType(ItemTypes.BARRIER)
                .itemData(DataUUID(buttonID[0]))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, TextStyles.BOLD, "移除成員"))
                .build()
        val changeGroupButton = ItemStack.builder()
                .itemType(ItemTypes.BARRIER)
                .itemData(DataUUID(buttonID[1]))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "更改身分組"))
                .build()

        inventory.set(1, 0, removeMemberButton)
        inventory.set(3, 0, changeGroupButton)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true

        when (event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return) {
            buttonID[0] -> TODO()
            buttonID[1] -> TODO()
        }
    }
}
