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

class GalaxyManagement(val uuid: UUID) : GUI() {
    override val token = "GalaxyManagement-$uuid"
    val galaxy: Galaxy = TODO("Galaxy")
    override val inventory: Inventory = Inventory.builder()
            .of(InventoryArchetypes.CHEST)
            .property(InventoryTitle.of(Text.of(galaxy.name)))
            .listener(InteractInventoryEvent::class.java, this::eventProcess)
            .build(main)
    private val buttonID = Array(3) { UUID.randomUUID() }

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        // put button
        val manageMemberButton = ItemStack.builder()
                .itemType(ItemTypes.BARRIER)
                .itemData(DataUUID(buttonID[0]))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "管理成員"))
                .build()
        val createPlanetButton = ItemStack.builder()
                .itemType(ItemTypes.BARRIER)
                .itemData(DataUUID(buttonID[1]))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "新增星球"))
                .build()
        val addMemberButton = ItemStack.builder()
                .itemType(ItemTypes.BARRIER)
                .itemData(DataUUID(buttonID[2]))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "添加成員"))
                .build()
        val inviteManagementButton = ItemStack.builder()
                .itemType(ItemTypes.BARRIER)
                .itemData(DataUUID(buttonID[3]))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "審核加入申請"))
                .build()
        val renameGalaxyButton = ItemStack.builder()
                .itemType(ItemTypes.BARRIER)
                .itemData(DataUUID(buttonID[4]))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, TextStyles.BOLD, "重新命名"))
                .build()
        inventory.set(0, 0, manageMemberButton)
        inventory.set(2, 0, createPlanetButton)
        inventory.set(4, 0, addMemberButton)
        inventory.set(6, 0, inviteManagementButton)
        inventory.set(8, 0, renameGalaxyButton)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true
        val player = event.source as? Player ?: return
        when (event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return) {
            buttonID[0] -> GUIHelper.open(player) { BrowserMember(uuid,true) }
            buttonID[1] -> GUIHelper.open(player) { CreatePlanet() }
            buttonID[2] -> GUIHelper.open(player) { AddMember() }
            buttonID[3] -> GUIHelper.open(player) { GalaxyJoinRequest(uuid) }
            buttonID[4] -> GUIHelper.open(player) { RenameGalaxy() }
        }
    }
}
