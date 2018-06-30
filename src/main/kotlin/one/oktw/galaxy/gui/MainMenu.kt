package one.oktw.galaxy.gui

import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.traveler.TravelerHelper.Companion.getTraveler
import one.oktw.galaxy.item.enums.ButtonType.GALAXY
import one.oktw.galaxy.item.enums.ButtonType.PLUS
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
import org.spongepowered.api.text.format.TextStyles
import java.util.*

class MainMenu(val player: Player) : GUI() {
    // Todo get player lang
    private val lang = languageService.getDefaultLanguage()
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
                    Text.of(TextColors.GREEN, TextStyles.BOLD, lang["UI.MainMenu.list_joined_galaxy"])
                )
            }
            .let { inventory.set(0, 0, it) }

        Button(PLUS).createItemStack()
            .apply {
                offer(DataUUID(buttonID[1]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, lang["UI.MainMenu.create_galaxy"]))
            }
            .let { inventory.set(2, 0, it) }

        Button(GALAXY).createItemStack()
            .apply {
                offer(DataUUID(buttonID[2]))
                offer(
                    Keys.DISPLAY_NAME,
                    Text.of(TextColors.GREEN, TextStyles.BOLD, lang["UI.MainMenu.list_all_galaxy"])
                )
            }
            .let { inventory.set(4, 0, it) }

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true

        when (event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return) {
            buttonID[0] -> GUIHelper.open(player) { BrowserGalaxy(player) }
            buttonID[1] -> GUIHelper.open(player) { CreateGalaxy() }
            buttonID[2] -> GUIHelper.open(player) { BrowserGalaxy() }
        }
    }
}
