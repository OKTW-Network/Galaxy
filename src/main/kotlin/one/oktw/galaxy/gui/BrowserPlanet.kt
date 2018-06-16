package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.enums.AccessLevel.DENY
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.planet.TeleportHelper
import one.oktw.galaxy.galaxy.planet.data.extensions.checkPermission
import one.oktw.galaxy.galaxy.planet.data.extensions.loadWorld
import one.oktw.galaxy.item.enums.ButtonType.PLANET_O
import one.oktw.galaxy.item.enums.ItemType.BUTTON
import one.oktw.galaxy.item.type.Button
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.Arrays.asList

class BrowserPlanet(galaxy: Galaxy) : PageGUI() {
    // Todo get player lang
    private val lang = languageService.getDefaultLanguage()
    override val token = "BrowserPlanet-${galaxy.uuid}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.DOUBLE_CHEST)
        .property(InventoryTitle.of(Text.of(lang["UI.BrowserPlanet.Title"])))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(Main.main)
    override val pages = galaxy.planets.asSequence()
        .map {
            // TODO planet type
            Button(PLANET_O).createItemStack().apply {
                offer(DataUUID(it.uuid))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, it.name))
                offer(
                    Keys.ITEM_LORE,
                    asList(
                        Text.of(
                            TextColors.AQUA,
                            "${lang["UI.BrowserPlanet.Details.Players"]}: ",
                            TextColors.RESET,
                            0
                        ), // TODO
                        Text.of(
                            TextColors.AQUA,
                            "${lang["UI.BrowserPlanet.Details.Security"]}: ",
                            TextColors.RESET,
                            it.security.toString()
                        )
                    )
                )
            }
        }
        .chunked(9)
        .chunked(5)

    init {
        offerPage(0)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        val player = event.source as Player
        val item = event.cursorTransaction.default
        val uuid = item[DataUUID.key].orElse(null) ?: return

        if (item[DataItemType.key].orElse(null) == BUTTON && !isButton(uuid)) {
            event.isCancelled = true

            launch {
                val planet = galaxyManager.getPlanet(uuid).await() ?: return@launch

                if (planet.checkPermission(player) != DENY) {
                    withContext(serverThread) {
                        TeleportHelper.teleport(player, planet.loadWorld().get().spawnLocation)
                    }
                    GUIHelper.closeAll(player)
                }
            }
        }
    }
}
