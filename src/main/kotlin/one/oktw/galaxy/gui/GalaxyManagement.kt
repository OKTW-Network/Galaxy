package one.oktw.galaxy.gui

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.internal.LangSys
import one.oktw.galaxy.item.enums.ButtonType.*
import one.oktw.galaxy.item.type.Button
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

class GalaxyManagement(private val galaxy: Galaxy) : GUI() {
    override val token = "GalaxyManagement-${galaxy.uuid}"
    //Todo check player lang
    val lang = LangSys().rootNode.getNode("ui","GalaxyManagement")!!
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.CHEST)
        .property(InventoryTitle.of(Text.of(galaxy.name)))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(main)
    private val buttonID = Array(7) { UUID.randomUUID() }

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        // button
        Button(PLUS).createItemStack()
            .apply {
                offer(DataUUID(buttonID[0]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang.getNode("new_planet").string))
            }
            .let { inventory.set(1, 1, it) }

        Button(LIST).createItemStack()
            .apply {
                offer(DataUUID(buttonID[1]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang.getNode("manage_member").string))
            }
            .let { inventory.set(2, 1, it) }

        Button(MEMBER_ADD).createItemStack()
            .apply {
                offer(DataUUID(buttonID[2]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang.getNode("add_member").string))
            }
            .let { inventory.set(3, 1, it) }

        Button(MEMBER_ASK).createItemStack()
            .apply {
                offer(DataUUID(buttonID[3]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang.getNode("join_application").string))
            }
            .let { inventory.set(4, 1, it) }

        Button(WRITE).createItemStack()
            .apply {
                offer(DataUUID(buttonID[4]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang.getNode("rename").string))
            }
            .let { inventory.set(5, 1, it) }

        Button(WRITE).createItemStack()
            .apply {
                offer(DataUUID(buttonID[4]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang.getNode("change_info").string))
            }
            .let { inventory.set(6, 1, it) }

        Button(WRITE).createItemStack()
            .apply {
                offer(DataUUID(buttonID[4]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang.getNode("change_notification").string))
            }
            .let { inventory.set(7, 1, it) }

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true

        val player = event.source as Player

        when (event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return) {
            buttonID[0] -> GUIHelper.open(player) { CreatePlanet() }
            buttonID[1] -> GUIHelper.open(player) { BrowserMember(galaxy, true) }
            buttonID[2] -> GUIHelper.open(player) { AddMember() }
            buttonID[3] -> GUIHelper.open(player) { GalaxyJoinRequest(galaxy) }
            buttonID[4] -> GUIHelper.open(player) { RenameGalaxy() }
            buttonID[5] -> Unit // TODO edit info
            buttonID[6] -> Unit // TODO edit notice
        }
    }
}
