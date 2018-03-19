package one.oktw.galaxy.gui

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.enums.ButtonType.*
import one.oktw.galaxy.enums.Group.*
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
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.type.GridInventory
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.*
import java.util.Arrays.asList

class GalaxyInfo(private val galaxy: Galaxy, player: Player) : GUI() {
    override val token = "GalaxyInfo-${galaxy.uuid}-${player.uniqueId}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.HOPPER)
        .property(InventoryTitle.of(Text.of(galaxy.name)))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(main)
    private val buttonID = Array(4) { UUID.randomUUID() }

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))
        val member = galaxy.members.firstOrNull { it.uuid == player.uniqueId }
        val start = if (member?.group == MEMBER) 1 else 0

        // button
        ItemHelper.getItem(Button(MEMBERS))?.apply {
            offer(DataUUID(buttonID[0]))
            offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "成員列表"))
        }?.let { inventory.set(start, 0, it) }

        ItemHelper.getItem(Button(PLANET_O))?.apply {
            offer(DataUUID(buttonID[1]))
            offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "星球列表"))
        }?.let { inventory.set(start + 2, 0, it) }

        if (member?.group in asList(OWNER, ADMIN)) {
            ItemHelper.getItem(Button(LIST))?.apply {
                offer(DataUUID(buttonID[2]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "管理星系"))
            }?.let { inventory.set(4, 0, it) }
        } else if (member == null) {
            ItemHelper.getItem(Button(PLUS))?.apply {
                if (player.uniqueId in galaxy.joinRequest) {
                    offer(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, "已申請加入"))
                } else {
                    offer(DataUUID(buttonID[3]))
                    offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "申請加入"))
                }
            }?.let { inventory.set(4, 0, it) }
        }

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun replaceJoinButton() {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        ItemHelper.getItem(Button(PLUS))?.apply {
            offer(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, "已申請加入"))
        }?.let { inventory.set(4, 0, it) }
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true

        val player = event.source as Player

        when (event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return) {
            buttonID[0] -> GUIHelper.open(player) { BrowserMember(galaxy) }
            buttonID[1] -> GUIHelper.open(player) { BrowserPlanet(galaxy) }
            buttonID[2] -> GUIHelper.open(player) { GalaxyManagement(galaxy) }
            buttonID[3] -> {
                galaxy.requestJoin(player.uniqueId)

                event.isCancelled = false
                event.cursorTransaction.setCustom(ItemStackSnapshot.NONE)
                replaceJoinButton()
            }
        }
    }
}
