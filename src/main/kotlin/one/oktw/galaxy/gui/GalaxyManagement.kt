package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.extensions.serialize
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.data.extensions.addMember
import one.oktw.galaxy.galaxy.data.extensions.refresh
import one.oktw.galaxy.galaxy.data.extensions.update
import one.oktw.galaxy.item.enums.ButtonType.*
import one.oktw.galaxy.item.type.Button
import one.oktw.galaxy.translation.extensions.toLegacyText
import one.oktw.galaxy.util.Chat.Companion.input
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.type.GridInventory
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextColors.*
import java.util.*

class GalaxyManagement(private val galaxy: Galaxy) : GUI() {
    override val token = "GalaxyManagement-${galaxy.uuid}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.CHEST)
        .property(InventoryTitle.of(Text.of(galaxy.name)))
        .listener(InteractInventoryEvent::class.java, ::eventProcess)
        .build(main)
    private val lang = Main.translationService
    private val buttonID = Array(7) { UUID.randomUUID() }

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        // button
        Button(PLANET_ADD).createItemStack()
            .apply {
                offer(DataUUID(buttonID[0]))
                offer(Keys.DISPLAY_NAME, lang.ofPlaceHolder(GREEN, lang.of("UI.Button.CreateNewPlanet")))
            }
            .let { inventory.set(1, 1, it) }

        Button(MEMBER_SETTING).createItemStack()
            .apply {
                offer(DataUUID(buttonID[1]))
                offer(Keys.DISPLAY_NAME, lang.ofPlaceHolder(GREEN, lang.of("UI.Button.ManageMember")))
            }
            .let { inventory.set(2, 1, it) }

        Button(MEMBER_ADD).createItemStack()
            .apply {
                offer(DataUUID(buttonID[2]))
                offer(Keys.DISPLAY_NAME, lang.ofPlaceHolder(GREEN, lang.of("UI.Button.AddMember")))
            }
            .let { inventory.set(3, 1, it) }

        Button(MEMBER_ASK).createItemStack()
            .apply {
                offer(DataUUID(buttonID[3]))
                offer(Keys.DISPLAY_NAME, lang.ofPlaceHolder(GREEN, lang.of("UI.Button.JoinRequestList")))
            }
            .let { inventory.set(4, 1, it) }

        Button(WRITE).createItemStack()
            .apply {
                offer(DataUUID(buttonID[4]))
                offer(Keys.DISPLAY_NAME, lang.ofPlaceHolder(GREEN, lang.of("UI.Button.Rename")))
            }
            .let { inventory.set(5, 1, it) }

        Button(WRITE).createItemStack()
            .apply {
                offer(DataUUID(buttonID[5]))
                offer(Keys.DISPLAY_NAME, lang.ofPlaceHolder(GREEN, lang.of("UI.Button.ChangeGalaxyInfo")))
            }
            .let { inventory.set(6, 1, it) }

        Button(WRITE).createItemStack()
            .apply {
                offer(DataUUID(buttonID[6]))
                offer(Keys.DISPLAY_NAME, lang.ofPlaceHolder(GREEN, lang.of("UI.Button.ChangeGalaxyNotification")))
            }
            .let { inventory.set(7, 1, it) }

        GUIHelper.fillEmptySlot(inventory)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true

        val player = event.source as Player

        when (event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return) {
            buttonID[0] -> GUIHelper.openAsync(player) { CreatePlanet(galaxy.refresh()) }
            buttonID[1] -> GUIHelper.openAsync(player) { BrowserMember(galaxy.refresh(), true) }
            buttonID[2] -> launch {
                val input = input(player, Text.of(AQUA, lang.of("Respond.inputPlayerId")).toLegacyText(player))?.toPlain()
                    ?: return@launch player.sendMessage(Text.of(RED, lang.of("Respond.cancelled")).toLegacyText(player))

                try {
                    val user: User? = Sponge.getServiceManager().provide(UserStorageService::class.java).get().get(input).orElse(null)

                    if (user != null) {
                        galaxy.addMember(user.uniqueId)
                        player.sendMessage(Text.of(GREEN, lang.of("Respond.addedPlayerToGalaxy", Text.of(TextColors.WHITE, user.name))).toLegacyText(player))
                    } else {
                        player.sendMessage(Text.of(RED, lang.of("Respond.cannotFindPlayer")).toLegacyText(player))
                    }
                } catch (e: RuntimeException) {
                    player.sendMessage(Text.of(RED, lang.of("Respond.badParameters")).toLegacyText(player))
                }
            }
            buttonID[3] -> GUIHelper.openAsync(player) { GalaxyJoinRequest(galaxy.refresh()) }
            buttonID[4] -> launch {
                val input = input(player, Text.of(AQUA, lang.of("Respond.inputNewName")).toLegacyText(player))?.toPlain()
                    ?: return@launch player.sendMessage(Text.of(RED, lang.of("Respond.cancelled")).toLegacyText(player))

                galaxy.update { name = input }
                player.sendMessage(Text.of(GREEN, lang.of("Respond.renameSuccess")).toLegacyText(player))
            }
            buttonID[5] -> launch {
                val input = input(player, Text.of(AQUA, lang.of("Respond.inputGalaxyInfo")).toLegacyText(player))?.serialize()
                    ?: return@launch player.sendMessage(Text.of(RED, lang.of("Respond.cancelled")).toLegacyText(player))

                galaxy.update { info = input }
                player.sendMessage(Text.of(GREEN, lang.of("Respond.settingSaved")).toLegacyText(player))
            }
            buttonID[6] -> launch {
                val input = input(player, Text.of(AQUA, lang.of("Respond.inputGalaxyNotification")).toLegacyText(player))?.serialize()
                    ?: return@launch player.sendMessage(Text.of(RED, lang.of("Respond.cancelled")).toLegacyText(player))

                galaxy.update { notice = input }
                player.sendMessage(Text.of(GREEN, lang.of("Respond.settingSaved")).toLegacyText(player))
            }
        }
    }
}
