package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.channels.any
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.reactive.openSubscription
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.galaxy.data.extensions.getGroup
import one.oktw.galaxy.galaxy.enums.Group.OWNER
import one.oktw.galaxy.gui.view.GridGUIView
import one.oktw.galaxy.item.enums.ButtonType.GALAXY
import one.oktw.galaxy.item.enums.ButtonType.PLUS
import one.oktw.galaxy.item.type.Button
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
import org.spongepowered.api.text.format.TextStyles
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

    private val lang = languageService.getDefaultLanguage()
    override val token = "MainMenu-${player.uniqueId}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.HOPPER)
        .property(InventoryTitle.of(Text.of(lang["UI.Title.StarShipController"])))
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
        Button(GALAXY).createItemStack()
            .apply {
                offer(
                    Keys.DISPLAY_NAME,
                    Text.of(GREEN, BOLD, lang["UI.Button.ListJoinedGalaxy"])
                )
            }
            .let { view.setSlot(Slot.LEFT, it, Action.OWN_GALAXY) }

        launch(serverThread) {
            if (galaxyManager.get(player).openSubscription().any { it.getGroup(player) == OWNER }) return@launch

            Button(PLUS).createItemStack()
                .apply {
                    offer(Keys.DISPLAY_NAME, Text.of(GREEN, BOLD, lang["UI.Button.CreateGalaxy"]))
                }
                .let { view.setSlot(Slot.MIDDLE, it, Action.CREATE_GALAXY) }
        }

        Button(GALAXY).createItemStack()
            .apply {
                offer(
                    Keys.DISPLAY_NAME,
                    Text.of(GREEN, BOLD, lang["UI.Button.ListAllGalaxy"])
                )
            }
            .let { view.setSlot(Slot.RIGHT, it, Action.ALL_GALAXY)  }

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
                    player.sendMessage(Text.of(RED, "你只能擁有一個星系"))
                    return@launch
                }

                val name = input(player, Text.of(AQUA, "請輸入一個名稱來創建星系："))?.toPlain()

                if (name == null) {
                    player.sendMessage(Text.of(RED, "已取消創建星系"))
                    return@launch
                }

                if (confirm(player, Text.of(AQUA, "確定要創建名為「", TextColors.RESET, BOLD, name, TextStyles.RESET, AQUA, "」的星系嗎？")) == true) {
                    if (galaxyManager.get(player).openSubscription().any { it.getGroup(player) == OWNER }) {
                        player.sendMessage(Text.of(RED, "你只能擁有一個星系"))
                        return@launch
                    }

                    val galaxy = galaxyManager.createGalaxy(name, player)
                    player.sendMessage(Text.of(YELLOW, "星系創建成功！"))
                    player.sendMessage(Text.of(UNDERLINE, AQUA, executeCallback { GUIHelper.open(player) { GalaxyManagement(galaxy) } }, "開啟管理介面"))
                } else {
                    player.sendMessage(Text.of(RED, "已取消創建星系"))
                }
            }
            Action.ALL_GALAXY -> GUIHelper.open(player) { BrowserGalaxy() }
        }
    }
}
