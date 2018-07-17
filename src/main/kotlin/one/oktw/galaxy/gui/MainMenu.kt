package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.data.extensions.getGroup
import one.oktw.galaxy.galaxy.enums.Group.OWNER
import one.oktw.galaxy.item.enums.ButtonType.GALAXY
import one.oktw.galaxy.item.enums.ButtonType.PLUS
import one.oktw.galaxy.item.type.Button
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.EventListener
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.event.message.MessageChannelEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.type.GridInventory
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.action.TextActions.showText
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextColors.*
import org.spongepowered.api.text.format.TextStyles
import org.spongepowered.api.text.format.TextStyles.BOLD
import org.spongepowered.api.text.format.TextStyles.UNDERLINE
import java.util.*

class MainMenu(player: Player) : GUI() {
    // Todo get player lang
    private val lang = languageService.getDefaultLanguage()
    private lateinit var chatListener: EventListener<MessageChannelEvent.Chat>
    override val token = "MainMenu-${player.uniqueId}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.HOPPER)
        .property(InventoryTitle.of(Text.of(lang["UI.MainMenu.Title"])))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(main)
    private val buttonID = Array(3) { UUID.randomUUID() }

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        // button
        Button(GALAXY).createItemStack()
            .apply {
                offer(DataUUID(buttonID[0]))
                offer(
                    Keys.DISPLAY_NAME,
                    Text.of(GREEN, BOLD, lang["UI.MainMenu.list_joined_galaxy"])
                )
            }
            .let { inventory.set(0, 0, it) }

        launch(serverThread) {
            if (galaxyManager.get(player).await().any { it.getGroup(player) == OWNER }) return@launch

            Button(PLUS).createItemStack()
                .apply {
                    offer(DataUUID(buttonID[1]))
                    offer(Keys.DISPLAY_NAME, Text.of(GREEN, BOLD, lang["UI.MainMenu.create_galaxy"]))
                }
                .let { inventory.set(2, 0, it) }
        }

        Button(GALAXY).createItemStack()
            .apply {
                offer(DataUUID(buttonID[2]))
                offer(
                    Keys.DISPLAY_NAME,
                    Text.of(GREEN, BOLD, lang["UI.MainMenu.list_all_galaxy"])
                )
            }
            .let { inventory.set(4, 0, it) }

        GUIHelper.fillEmptySlot(inventory)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        val player = event.source as Player

        event.isCancelled = true

        when (event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return) {
            buttonID[0] -> GUIHelper.open(player) { BrowserGalaxy(player) }
            buttonID[1] -> {
                GUIHelper.closeAll(player)

                player.sendMessage(Text.of(AQUA, "請輸入一個名稱來創建星系："))
                chatListener = EventListener {
                    if (it.source == player) {
                        Sponge.getEventManager().unregisterListeners(chatListener)
                        it.isCancelled = true

                        var lock = false
                        val name = it.rawMessage.toPlain()
                        val confirmCallback = TextActions.executeCallback {
                            if (lock) return@executeCallback
                            lock = true

                            launch {
                                if (galaxyManager.get(player).await().any { it.getGroup(player) == OWNER }) {
                                    player.sendMessage(Text.of(RED, "你只能擁有一個星系"))
                                    return@launch
                                }

                                galaxyManager.createGalaxy(name, player)
                                player.sendMessage(Text.of(YELLOW, "星系創建成功！"))
                            }
                        }
                        val cancelCallback = TextActions.executeCallback {
                            if (lock) return@executeCallback
                            player.sendMessage(Text.of(RED, "已取消創建星系"))
                            lock = true
                        }

                        player.sendMessage(
                            Text.of(
                                AQUA, "確定要創建名為「",
                                TextColors.RESET, BOLD, name,
                                TextStyles.RESET, AQUA, "」的星系嗎？\n",
                                GREEN, UNDERLINE, showText(Text.of(GREEN, "確定")), confirmCallback, "確定",
                                TextStyles.RESET, " ",
                                RED, UNDERLINE, showText(Text.of(RED, "取消")), cancelCallback, "取消"
                            )
                        )
                    }
                }
                Sponge.getEventManager().registerListener(main, MessageChannelEvent.Chat::class.java, chatListener)
            }
            buttonID[2] -> GUIHelper.open(player) { BrowserGalaxy() }
        }
    }
}
