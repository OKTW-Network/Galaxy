package one.oktw.galaxy.gui

import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.enums.Group.ADMIN
import one.oktw.galaxy.enums.Group.OWNER
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.data.extensions.requestJoin
import one.oktw.galaxy.item.enums.ButtonType.*
import one.oktw.galaxy.item.type.Button
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
import org.spongepowered.api.text.serializer.TextSerializers
import java.util.*
import java.util.Arrays.asList

class GalaxyInfo(private val galaxy: Galaxy, player: Player) : GUI() {
    override val token = "GalaxyInfo-${galaxy.uuid}-${player.uniqueId}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.HOPPER)
        .property(InventoryTitle.of(Text.of(galaxy.name)))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(main)
    // Todo get player lang
    private val lang = languageService.getDefaultLanguage()
    private val buttonID = Array(4) { UUID.randomUUID() }

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))
        val member = galaxy.members.firstOrNull { it.uuid == player.uniqueId }

        // button
        Button(MEMBERS).createItemStack()
            .apply {
                offer(DataUUID(buttonID[0]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang["UI.GalaxyInfo.member_list"]))
            }
            .let { inventory.set(0, 0, it) }

        Button(PLANET_O).createItemStack()
            .apply {
                offer(DataUUID(buttonID[1]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang["UI.GalaxyInfo.planet_list"]))
            }
            .let { inventory.set(2, 0, it) }

        when {
            member?.group in asList(OWNER, ADMIN) -> {
                Button(LIST).createItemStack()
                    .apply {
                        offer(DataUUID(buttonID[2]))
                        offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang["UI.GalaxyInfo.manage_galaxy"]))
                    }
                    .let { inventory.set(4, 0, it) }
            }
            member != null -> {
                Button(WARNING).createItemStack()
                    .apply {
                        offer(Keys.DISPLAY_NAME, Text.of(TextColors.YELLOW, lang["UI.GalaxyInfo.notice"]))
                        offer(
                            Keys.ITEM_LORE,
                            galaxy.notice.split('\n').map(TextSerializers.FORMATTING_CODE::deserialize)
                        )
                    }
                    .let { inventory.set(4, 0, it) }
            }
            else -> {
                Button(PLUS).createItemStack()
                    .apply {
                        if (player.uniqueId in galaxy.joinRequest) {
                            offer(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, lang["UI.GalaxyInfo.join_req_sent"]))
                        } else {
                            offer(DataUUID(buttonID[3]))
                            offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang["UI.GalaxyInfo.join_req"]))
                        }
                    }
                    .let { inventory.set(4, 0, it) }
            }
        }

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun replaceJoinButton() {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        Button(PLUS).createItemStack()
            .apply {
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GRAY, lang["UI.GalaxyInfo.join_req_sent"]))
            }
            .let { inventory.set(4, 0, it) }
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
