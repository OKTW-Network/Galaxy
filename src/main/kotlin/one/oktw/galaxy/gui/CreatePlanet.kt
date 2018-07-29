package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.data.extensions.createPlanet
import one.oktw.galaxy.galaxy.data.extensions.refresh
import one.oktw.galaxy.galaxy.data.extensions.update
import one.oktw.galaxy.galaxy.planet.TeleportHelper
import one.oktw.galaxy.galaxy.planet.data.extensions.loadWorld
import one.oktw.galaxy.galaxy.planet.enums.PlanetType
import one.oktw.galaxy.galaxy.planet.enums.PlanetType.NETHER
import one.oktw.galaxy.galaxy.planet.enums.PlanetType.NORMAL
import one.oktw.galaxy.item.enums.ButtonType.*
import one.oktw.galaxy.item.type.Button
import one.oktw.galaxy.util.Chat.Companion.confirm
import one.oktw.galaxy.util.Chat.Companion.input
import org.spongepowered.api.data.key.Keys.DISPLAY_NAME
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
import org.spongepowered.api.text.format.TextColors.*
import org.spongepowered.api.text.format.TextStyles
import org.spongepowered.api.text.format.TextStyles.BOLD
import java.util.*

class CreatePlanet(private val galaxy: Galaxy) : GUI() {
    private val lang = languageService.getDefaultLanguage()
    private val buttonID = Array(3) { UUID.randomUUID() }
    override val token = "CreatePlanet-${galaxy.uuid}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.HOPPER)
        .property(InventoryTitle.of(Text.of(lang["UI.Title.CreatePlanet"])))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(main)

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        // button TODO show price
        Button(PLANET_O).createItemStack()
            .apply {
                offer(DataUUID(buttonID[0]))
                offer(DISPLAY_NAME, Text.of(GREEN, lang["UI.Button.PlanetTypeNormal"]))
            }
            .let { inventory.set(0, 0, it) }

        Button(PLANET_N).createItemStack()
            .apply {
                offer(DataUUID(buttonID[1]))
                offer(DISPLAY_NAME, Text.of(GREEN, lang["UI.Button.PlanetTypeNether"]))
            }
            .let { inventory.set(2, 0, it) }

        Button(PLANET_E).createItemStack()
            .apply {
                offer(DataUUID(buttonID[2]))
                offer(DISPLAY_NAME, Text.of(GREEN, lang["UI.Button.PlanetTypeEnd"]))
            }
            .let { inventory.set(4, 0, it) }

        // fill empty slot
        GUIHelper.fillEmptySlot(inventory)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true

        val player = event.source as Player

        when (event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return) {
            buttonID[0] -> {
                if (galaxy.planets.any { it.type == NORMAL }) {
                    player.sendMessage(Text.of(RED, "目前每種類別的星系僅能創建一個！請等待日後開放"))
                    return
                }

                launch { createPlanet(player, galaxy, NORMAL) }
            }
            buttonID[1] -> {
                if (galaxy.planets.any { it.type == NETHER }) {
                    player.sendMessage(Text.of(RED, "目前每種類別的星系僅能創建一個！請等待日後開放"))
                    return
                }

                if (galaxy.starDust < 1000) { // TODO price
                    player.sendMessage(Text.of(RED, "星系擁有的星塵不足！去貢獻點星塵給星系吧"))
                    return
                }

                launch { if (createPlanet(player, galaxy, NETHER)) galaxy.update { takeStarDust(1000) } }
            }
            buttonID[2] -> Unit // TODO planet type end
        }
    }

    private suspend fun createPlanet(player: Player, galaxy: Galaxy, type: PlanetType): Boolean {
        val name = input(player, Text.of(AQUA, "請輸入一個名稱來創建星球："))?.toPlain()

        if (name == null) {
            player.sendMessage(Text.of(RED, "已取消創建星球"))
            return false
        }

        if (confirm(player, Text.of(AQUA, "確定要創建名為「", TextColors.RESET, BOLD, name, TextStyles.RESET, AQUA, "」的星球嗎？")) == true) {
            val galaxy1 = galaxy.refresh()

            if (galaxy1.planets.any { it.type == type }) {
                player.sendMessage(Text.of(RED, "目前每種類別的星系僅能創建一個！請等待日後開放"))
                return false
            }

            try {
                val planet = galaxy1.createPlanet(name, type).apply { loadWorld() }

                player.sendMessage(Text.of(YELLOW, "星球創建成功！"))
                TeleportHelper.teleport(player, planet)
                return true
            } catch (e: IllegalArgumentException) {
                player.sendMessage(Text.of(RED, "星球創建失敗：", e.message))
            }

            return false
        }

        return false
    }
}
