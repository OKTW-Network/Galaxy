package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.channels.any
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.reactive.openSubscription
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.galaxy.data.extensions.getGroup
import one.oktw.galaxy.galaxy.enums.Group.OWNER
import one.oktw.galaxy.gui.view.GridGUIView
import one.oktw.galaxy.item.enums.ButtonType.*
import one.oktw.galaxy.item.type.Button
import one.oktw.galaxy.translation.extensions.toLegacyText
import one.oktw.galaxy.util.Chat.Companion.confirm
import one.oktw.galaxy.util.Chat.Companion.input
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
import org.spongepowered.api.text.action.TextActions.executeCallback
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextColors.*
import org.spongepowered.api.text.format.TextStyles.BOLD
import org.spongepowered.api.text.format.TextStyles.UNDERLINE
import java.util.Arrays.asList

class MainMenu(player: Player) : GUI() {
    companion object {
        enum class Slot {
            LEFT,
            MIDDLE,
            RIGHT,
            NULL
        }

        enum class Action {
            OWN_GALAXY,
            ALL_GALAXY,
            CREATE_GALAXY
        }

        const val WIDTH = 5
        const val HEIGHT = 1

        val layout: List<Slot> = asList(
            Slot.LEFT,
            Slot.NULL,
            Slot.MIDDLE,
            Slot.NULL,
            Slot.RIGHT
        )
    }

    private val lang = Main.translationService
    override val token = "MainMenu-${player.uniqueId}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.HOPPER)
        .property(InventoryTitle.of(lang.ofPlaceHolder("UI.Title.StarShipController")))
        .listener(InteractInventoryEvent::class.java, ::eventProcess)
        .build(main)

    val view: GridGUIView<Slot, Action> by lazy {
        GridGUIView<Slot, Action>(
            inventory,
            layout,
            Pair(WIDTH, HEIGHT)
        )
    }

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        // button
        Button(GALAXY_JOINED).createItemStack()
            .apply {
                offer(
                    Keys.DISPLAY_NAME,
                    lang.ofPlaceHolder(GREEN, BOLD, lang.of("UI.Button.ListJoinedGalaxy"))
                )
            }
            .let { view.setSlot(Slot.LEFT, it, Action.OWN_GALAXY) }

        launch(serverThread) {
            if (galaxyManager.get(player).openSubscription().any { it.getGroup(player) == OWNER }) return@launch

            Button(PLUS).createItemStack()
                .apply {
                    offer(Keys.DISPLAY_NAME, lang.ofPlaceHolder(GREEN, BOLD, lang.of("UI.Button.CreateGalaxy")))
                }
                .let { view.setSlot(Slot.MIDDLE, it, Action.CREATE_GALAXY) }
        }

        Button(GALAXY).createItemStack()
            .apply {
                offer(
                    Keys.DISPLAY_NAME,
                    lang.ofPlaceHolder(GREEN, BOLD, lang.of("UI.Button.ListAllGalaxy"))
                )
            }
            .let { view.setSlot(Slot.RIGHT, it, Action.ALL_GALAXY) }

        GUIHelper.fillEmptySlot(inventory)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        val player = event.source as Player
        val detail = view.getDetail(event)

        if (detail.affectGUI) {
            event.isCancelled = true
        }

        when (detail.primary?.data ?: return) {
            Action.OWN_GALAXY -> GUIHelper.open(player) { BrowserGalaxy(player) }
            Action.CREATE_GALAXY -> launch {
                if (galaxyManager.get(player).openSubscription().any { it.getGroup(player) == OWNER }) {
                    player.sendMessage(Text.of(RED, lang.of("Respond.maximumOneGalaxyAllowed")).toLegacyText(player))
                    return@launch
                }

                val name = input(player, Text.of(AQUA, lang.of("Respond.inputGalaxyName")).toLegacyText(player))?.toPlain()

                if (name == null) {
                    player.sendMessage(Text.of(RED, lang.of("Respond.createGalaxyCancel")).toLegacyText(player))
                    return@launch
                }

                if (
                    confirm(
                        player,
                        Text.of(AQUA, lang.of("Respond.createGalaxyConfirmName", Text.of(TextColors.WHITE, BOLD, name))).toLegacyText(player)
                    ) == true
                ) {
                    if (galaxyManager.get(player).openSubscription().any { it.getGroup(player) == OWNER }) {
                        player.sendMessage(Text.of(RED, lang.of("Respond.maximumOneGalaxyAllowed")).toLegacyText(player))
                        return@launch
                    }

                    val galaxy = galaxyManager.createGalaxy(name, player)
                    player.sendMessage(Text.of(YELLOW, lang.of("Respond.createGalaxySuccess")).toLegacyText(player))
                    player.sendMessage(Text.of(UNDERLINE, AQUA, executeCallback {
                        GUIHelper.open(player) { GalaxyManagement(galaxy) }
                    }, lang.of("Respond.openGalaxyConsole").toLegacyText(player)))
                } else {
                    player.sendMessage(Text.of(RED, lang.of("Respond.createGalaxyCancel")).toLegacyText(player))
                }
            }
            Action.ALL_GALAXY -> GUIHelper.open(player) { BrowserGalaxy() }
        }
    }
}
