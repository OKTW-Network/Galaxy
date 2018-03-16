package one.oktw.galaxy.gui

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.enums.ButtonType.*
import one.oktw.galaxy.helper.GUIHelper
import one.oktw.galaxy.helper.ItemHelper
import one.oktw.galaxy.types.Galaxy
import one.oktw.galaxy.types.item.Button
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.type.GridInventory
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
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

        // button
        ItemHelper.getItem(Button(LIST))?.apply {
            offer(DataUUID(buttonID[0]))
            offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "星球列表"))
        }?.let { inventory.set(0, 0, it) }

        ItemHelper.getItem(Button(PLUS))?.apply {
            offer(DataUUID(buttonID[1]))
            offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "新增星球"))
        }?.let { inventory.set(2, 0, it) }

        ItemHelper.getItem(Button(MEMBER_ADD))?.apply {
            offer(DataUUID(buttonID[2]))
            offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "添加成員"))
        }?.let { inventory.set(4, 0, it) }

        ItemHelper.getItem(Button(MEMBER_ASK))?.apply {
            offer(DataUUID(buttonID[3]))
            offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "加入申請"))
        }?.let { inventory.set(6, 0, it) }

        // TODO button icon
        ItemHelper.getItem(Button(BLANK))?.apply {
            offer(DataUUID(buttonID[4]))
            offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "重新命名"))
        }?.let { inventory.set(8, 0, it) }

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true
        val player = event.source as? Player ?: return
        when (event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return) {
            buttonID[0] -> GUIHelper.open(player) { BrowserMember(uuid, true) }
            buttonID[1] -> GUIHelper.open(player) { CreatePlanet() }
            buttonID[2] -> GUIHelper.open(player) { AddMember() }
            buttonID[3] -> GUIHelper.open(player) { GalaxyJoinRequest(uuid) }
            buttonID[4] -> GUIHelper.open(player) { RenameGalaxy() }
        }
    }
}
