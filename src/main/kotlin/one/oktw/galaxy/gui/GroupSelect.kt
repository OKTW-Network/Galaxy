package one.oktw.galaxy.gui

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.enums.Group
import one.oktw.galaxy.enums.Group.ADMIN
import one.oktw.galaxy.enums.Group.MEMBER
import one.oktw.galaxy.internal.LangSys
import one.oktw.galaxy.item.enums.ButtonType
import one.oktw.galaxy.item.enums.ButtonType.MANAGER
import one.oktw.galaxy.item.type.Button
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

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class GroupSelect(private val callback: (Group) -> Unit) : GUI() {
    override val token = "GroupSelect-${UUID.randomUUID()}"
    //Todo check player lang
    val lang = LangSys()
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.HOPPER)
        .property(InventoryTitle.of(Text.of(lang.getLangString("ui.GroupSelect.Title"))))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(main)
    private val buttonID = Array(3) { UUID.randomUUID() }

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        //  button
        Button(MANAGER).createItemStack()
            .apply {
                offer(DataUUID(buttonID[0]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.RED, TextStyles.BOLD, "ADMIN"))
            }
            .let { inventory.set(1, 0, it) }

        Button(ButtonType.MEMBER).createItemStack()
            .apply {
                offer(DataUUID(buttonID[1]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.RED, TextStyles.BOLD, "MEMBER"))
            }
            .let { inventory.set(3, 0, it) }

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true

        when (event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return) {
            buttonID[0] -> {
                callback(ADMIN)
                GUIHelper.close(token)
            }
            buttonID[1] -> {
                callback(MEMBER)
                GUIHelper.close(token)
            }
        }
    }
}
