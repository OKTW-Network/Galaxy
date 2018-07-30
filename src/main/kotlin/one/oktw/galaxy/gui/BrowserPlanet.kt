package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.data.extensions.refresh
import one.oktw.galaxy.galaxy.planet.TeleportHelper
import one.oktw.galaxy.galaxy.planet.enums.PlanetType.*
import one.oktw.galaxy.item.enums.ButtonType.*
import one.oktw.galaxy.item.type.Button
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.Arrays.asList

class BrowserPlanet(private val galaxy: Galaxy) : PageGUI() {
    private val lang = languageService.getDefaultLanguage()
    override val token = "BrowserPlanet-${galaxy.uuid}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.DOUBLE_CHEST)
        .property(InventoryTitle.of(Text.of(lang["UI.Title.PlanetList"])))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(Main.main)

    init {
        offerPage(0)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    override suspend fun get(number: Int, skip: Int): List<ItemStack> {
        return galaxy.refresh().planets
            .drop(skip)
            .take(number)
            .map {
                when (it.type) {
                    NORMAL -> Button(PLANET_O)
                    NETHER -> Button(PLANET_N)
                    END -> Button(PLANET_E)
                }.createItemStack().apply {
                    offer(DataUUID(it.uuid))
                    offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, it.name))
                    offer(
                        Keys.ITEM_LORE,
                        asList(
                            Text.of(
                                TextColors.AQUA,
                                "${lang["UI.Tip.PlayerCount"]}: ",
                                TextColors.RESET,
                                0 // TODO player on planet
                            ),
                            Text.of(
                                TextColors.AQUA,
                                "${lang["UI.Tip.AllowVisit"]}: ",
                                TextColors.RESET,
                                lang["UI.Tip.${it.visitable}"]
                            )
                        )
                    )
                }
            }
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        if (view.disabled) return

        // ignore gui elements, because they are handled by the PageGUI
        if (!isControl(event.cursorTransaction.default)) {
            event.isCancelled = true
        }

        val player = event.source as Player
        val item = event.cursorTransaction.default
        val uuid = item[DataUUID.key].orElse(null) ?: return

        if (view.getNameOf(uuid)?.first == Companion.Slot.ITEMS) {
            launch {
                val planet = galaxyManager.get(planet = uuid)?.getPlanet(uuid) ?: return@launch

                if (TeleportHelper.teleport(player, planet).await()) GUIHelper.closeAll(player)
            }
        }
    }
}
