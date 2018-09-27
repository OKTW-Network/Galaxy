package one.oktw.galaxy.gui.machine

import one.oktw.galaxy.Main
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.planet.data.Planet
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUIHelper
import one.oktw.galaxy.gui.view.GridGUIView
import one.oktw.galaxy.item.enums.ButtonType
import one.oktw.galaxy.item.type.Button
import one.oktw.galaxy.item.type.Upgrade
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.text.Text
import java.util.Arrays.asList

class EnvironmentControlSystem(private var galaxy: Galaxy, private var planet: Planet) : GUI() {
    companion object {
        enum class Slot {
            NULL,
            UPGRADE,
            LEVEL
        }

        enum class Action {
            OPEN_UPGRADE,
            OPEN_LEVEL
        }


        const val WIDTH = 5
        const val HEIGHT = 1

        private val layout = asList(
            Slot.NULL,
            Slot.UPGRADE,
            Slot.NULL,
            Slot.LEVEL,
            Slot.NULL
        )


    }

    private val lang = Main.languageService.getDefaultLanguage()
    override val token = "EnvironmentControlSystem-${planet.uuid}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.HOPPER)
        .property(InventoryTitle.of(Text.of(lang["UI.Title.StarShipController"])))
        .listener(InteractInventoryEvent::class.java, ::eventProcess)
        .build(Main.main)


    val view: GridGUIView<Slot, Action> by lazy {
        GridGUIView<Slot, Action>(
            inventory,
            layout,
            Pair(WIDTH, HEIGHT)
        )
    }

    init {
        updateView()
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun updateView() {
        view.setSlot(Slot.NULL, Button(ButtonType.GUI_CENTER).createItemStack())

        view.setSlot(Slot.UPGRADE, Upgrade().createItemStack().apply {
            offer(
                Keys.DISPLAY_NAME, Text.of(
                    lang["UI.Tip.PlanetEffect"]
                )
            )
        }, Action.OPEN_UPGRADE)

        view.setSlot(Slot.LEVEL, Button(ButtonType.PLUS).createItemStack().apply {
            offer(
                Keys.DISPLAY_NAME, Text.of(
                    lang["UI.Tip.PlanetLevel"]
                )
            )
        }, Action.OPEN_LEVEL)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        val player = event.source as Player
        val detail = view.getDetail(event)

        if (detail.affectGUI) {
            event.isCancelled = true
        }

        when (detail.primary?.data) {
            Action.OPEN_LEVEL -> {
                GUIHelper.openAsync(player) { PlanetLevel(galaxy, planet) }
            }
            Action.OPEN_UPGRADE -> TODO()
            else -> Unit
        }
    }
}
