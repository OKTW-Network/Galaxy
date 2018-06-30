package one.oktw.galaxy.gui.machine

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.enums.Group
import one.oktw.galaxy.galaxy.enums.Group.*
import one.oktw.galaxy.galaxy.enums.Group.MEMBER
import one.oktw.galaxy.galaxy.planet.TeleportHelper
import one.oktw.galaxy.galaxy.planet.data.Planet
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUIHelper
import one.oktw.galaxy.item.enums.ButtonType.*
import one.oktw.galaxy.item.type.Button
import org.spongepowered.api.Sponge
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
import org.spongepowered.api.text.format.TextColors.*
import org.spongepowered.api.text.format.TextStyles.BOLD
import java.util.*
import java.util.Arrays.asList

class PlanetTerminal(private val planet: Planet, group: Group = VISITOR) : GUI() {
    private val lang = languageService.getDefaultLanguage() // TODO set language
    private val buttonID = Array(3) { UUID.randomUUID() }
    private val buttonStarDust by lazy {
        Button(STARS).createItemStack()
            .apply {
                offer(DataUUID(buttonID[0]))
                offer(Keys.DISPLAY_NAME, Text.of(GREEN, BOLD, lang["UI.PlanetTerminal.Button.StarDust"]))
            }
    }
    private val buttonECS by lazy {
        Button(ECS).createItemStack()
            .apply {
                offer(DataUUID(buttonID[1]))
                offer(Keys.DISPLAY_NAME, Text.of(GREEN, BOLD, lang["UI.PlanetTerminal.Button.ECS"]))
            }
    }
    private val buttonExit by lazy {
        Button(EXIT).createItemStack()
            .apply {
                offer(DataUUID(buttonID[2]))
                offer(Keys.DISPLAY_NAME, Text.of(GREEN, BOLD, lang["UI.PlanetTerminal.Button.Exit"]))
            }
    }
    override val token = "PlanetTerminal-${planet.uuid}-$group"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.CHEST)
        .property(InventoryTitle.of(Text.of(lang["UI.PlanetTerminal.Title"])))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(main)

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        // Info
        Button(GUI_INFO).createItemStack()
            .apply {
                offer(Keys.DISPLAY_NAME, Text.of(GREEN, BOLD, lang["UI.PlanetTerminal.Button.info"]))
                offer(
                    Keys.ITEM_LORE, asList(
                        Text.of(BOLD, YELLOW, lang["UI.PlanetTerminal.Info.Level"], RESET, ":", planet.level),
                        Text.of(BOLD, YELLOW, lang["UI.PlanetTerminal.Info.Range"], RESET, ":", planet.size),
                        Text.of(BOLD, YELLOW, lang["UI.PlanetTerminal.Info.Effect"], RESET, ":")
                    ).apply { addAll(planet.effect.map { Text.of(BOLD, it.type.potionTranslation) }) }
                )
            }
            .let { inventory.set(4, 0, it) }

        when (group) {
            OWNER, ADMIN -> {
                inventory.set(2, 2, buttonStarDust)
                inventory.set(4, 2, buttonECS)
                inventory.set(6, 2, buttonExit)
            }
            MEMBER -> {
                inventory.set(3, 2, buttonStarDust)
                inventory.set(5, 2, buttonExit)
            }
            VISITOR -> {
                main.logger.info(buttonExit.toString())
                inventory.set(4, 2, buttonExit)
            }
        }

        GUIHelper.fillEmptySlot(inventory)

        // register event
        registerEvent(InteractInventoryEvent.Close::class.java, this::closeEventListener)
        registerEvent(ClickInventoryEvent::class.java, this::clickEventListener)
    }

    private fun closeEventListener(event: InteractInventoryEvent.Close) {
        event.cursorTransaction.setCustom(ItemStackSnapshot.NONE)
        event.cursorTransaction.isValid = true
    }

    private fun clickEventListener(event: ClickInventoryEvent) {
        event.isCancelled = true

        val itemUUID = event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return
        val player = event.source as Player

        when (itemUUID) {
            buttonID[0] -> clickStarDust(player)
            buttonID[1] -> clickECS(player)
            buttonID[2] -> launch {
                TeleportHelper.teleport(player, Sponge.getServer().run { getWorld(defaultWorldName).get() })
            }
        }
    }

    private fun clickStarDust(player: Player) {
        // TODO
    }

    private fun clickECS(player: Player) {
        // TODO
    }
}
