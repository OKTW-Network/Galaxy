package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.data.extensions.createPlanet
import one.oktw.galaxy.galaxy.data.extensions.refresh
import one.oktw.galaxy.galaxy.planet.TeleportHelper
import one.oktw.galaxy.item.enums.ButtonType.*
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
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*

class GalaxyManagement(private val galaxy: Galaxy) : GUI() {
    override val token = "GalaxyManagement-${galaxy.uuid}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.CHEST)
        .property(InventoryTitle.of(Text.of(galaxy.name)))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(main)
    // Todo get player lang
    private val lang = languageService.getDefaultLanguage()
    private val buttonID = Array(7) { UUID.randomUUID() }
    private lateinit var chatListener: EventListener<MessageChannelEvent.Chat>

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        // button
        Button(PLUS).createItemStack()
            .apply {
                offer(DataUUID(buttonID[0]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang["UI.Button.CreateNewPlanet"]))
            }
            .let { inventory.set(1, 1, it) }

        Button(LIST).createItemStack()
            .apply {
                offer(DataUUID(buttonID[1]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang["UI.Button.ManageMember"]))
            }
            .let { inventory.set(2, 1, it) }

        Button(MEMBER_ADD).createItemStack()
            .apply {
                offer(DataUUID(buttonID[2]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang["UI.Button.AddMember"]))
            }
            .let { inventory.set(3, 1, it) }

        Button(MEMBER_ASK).createItemStack()
            .apply {
                offer(DataUUID(buttonID[3]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang["UI.Button.JoinRequestList"]))
            }
            .let { inventory.set(4, 1, it) }

        Button(WRITE).createItemStack()
            .apply {
                offer(DataUUID(buttonID[4]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang["UI.Button.Rename"]))
            }
            .let { inventory.set(5, 1, it) }

        Button(WRITE).createItemStack()
            .apply {
                offer(DataUUID(buttonID[4]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang["UI.Button.ChangeGalaxyInfo"]))
            }
            .let { inventory.set(6, 1, it) }

        Button(WRITE).createItemStack()
            .apply {
                offer(DataUUID(buttonID[4]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, lang["UI.Button.ChangeGalaxyNotification"]))
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
            buttonID[0] -> {
                GUIHelper.closeAll(player)

                if (galaxy.planets.size >= 1) {
                    player.sendMessage(Text.of(TextColors.RED, "目前僅能創建一個星球！請等待日後開放"))
                    return
                }

                player.sendMessage(Text.of(TextColors.AQUA, "請輸入一個名稱來創建星球："))
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
                                try {
                                    val planet = galaxy.createPlanet(name)

                                    player.sendMessage(Text.of(TextColors.YELLOW, "星球創建成功！"))
                                    TeleportHelper.teleport(player, planet)
                                    // TODO try time
                                } catch (e: IllegalArgumentException) {
                                    player.sendMessage(Text.of(TextColors.RED, "星球創建失敗：", e.message))
                                }
                            }
                        }
                        val cancelCallback = TextActions.executeCallback {
                            if (lock) return@executeCallback
                            player.sendMessage(Text.of(TextColors.RED, "已取消創建星球"))
                            lock = true
                        }

                        player.sendMessage(
                            Text.of(
                                TextColors.AQUA, "確定要創建名為「",
                                TextColors.RESET,
                                TextStyles.BOLD, name,
                                TextStyles.RESET,
                                TextColors.AQUA, "」的星球嗎？\n",
                                TextColors.GREEN,
                                TextStyles.UNDERLINE,
                                TextActions.showText(Text.of(TextColors.GREEN, "確定")), confirmCallback, "確定",
                                TextStyles.RESET, " ",
                                TextColors.RED,
                                TextStyles.UNDERLINE,
                                TextActions.showText(Text.of(TextColors.RED, "取消")), cancelCallback, "取消"
                            )
                        )
                    }
                }
                Sponge.getEventManager().registerListener(main, MessageChannelEvent.Chat::class.java, chatListener)
            }
            buttonID[1] -> GUIHelper.openAsync(player) { BrowserMember(galaxy.refresh(), true) }
            buttonID[2] -> Unit // TODO GUIHelper.open(player) { AddMember() }
            buttonID[3] -> GUIHelper.openAsync(player) { GalaxyJoinRequest(galaxy.refresh()) }
            buttonID[4] -> Unit // TODO GUIHelper.open(player) { RenameGalaxy() }
            buttonID[5] -> Unit // TODO edit info
            buttonID[6] -> Unit // TODO edit notice
        }
    }
}
