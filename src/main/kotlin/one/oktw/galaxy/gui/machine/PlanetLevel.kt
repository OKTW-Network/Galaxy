package one.oktw.galaxy.gui.machine

import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.enums.Group
import one.oktw.galaxy.galaxy.planet.data.Planet
import one.oktw.galaxy.galaxy.traveler.TravelerHelper
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUIHelper
import one.oktw.galaxy.gui.view.GridGUIView
import one.oktw.galaxy.item.enums.ButtonType
import one.oktw.galaxy.item.enums.ButtonType.*
import one.oktw.galaxy.item.type.Button
import one.oktw.galaxy.translation.extensions.toLegacyText
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
import java.util.Arrays.asList

class PlanetLevel(private var galaxy: Galaxy, private var planet: Planet) : GUI() {
    companion object {
        private val requirements = asList<Pair<Int, Int>>(
            Pair(10, 2000),
            Pair(20, 3600),
            Pair(30, 16800)
        )

        private fun getRequirement(currentLevel: Int): Int {
            requirements.forEach { (level, xp) ->
                if (currentLevel < level) {
                    return xp
                }
            }

            return -1
        }

        enum class Slot {
            NULL,
            NUMBER,
            CONFIRM,
            STAR_DUST
        }

        enum class Action {
            CONFIRM,
            CANCEL,
        }

        const val WIDTH = 9
        const val HEIGHT = 3

        private val X = Slot.NULL
        private val N = Slot.NUMBER
        private val C = Slot.CONFIRM
        private val S = Slot.STAR_DUST

        val layout: List<Slot> = asList(
            X, X, X, X, X, X, X, X, X,
            S, N, N, N, N, N, N, N, C,
            X, X, X, X, X, X, X, X, X
        )

        private val numbers = asList(
            NUMBER_0, NUMBER_1, NUMBER_2, NUMBER_3, NUMBER_4,
            NUMBER_5, NUMBER_6, NUMBER_7, NUMBER_8, NUMBER_9
        ).map { Button(it).createItemStack() }

        private fun createNumbers(number: Int, length: Int): List<ItemStack> {
            var current = number
            val res = ArrayList<ItemStack>()

            (0 until length).forEach { _ ->
                res.add(numbers[current % 10])
                current /= 10
            }

            return res.reversed()
        }
    }

    private val lang = Main.translationService
    override val token = "PlanetLevel-${planet.uuid}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.CHEST)
        .property(InventoryTitle.of(lang.ofPlaceHolder(lang.of("UI.Title.PlanetLevel"))))
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
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
        launch {
            refreshGalaxyAndPlanet()
            updateView()
        }
    }

    private suspend fun refreshGalaxyAndPlanet() {
        galaxy = Main.galaxyManager.get(galaxy.uuid) ?: return
        planet = galaxy.getPlanet(planet.uuid) ?: return
    }

    private fun updateView() {
        view.disabled = true
        view.clear()

        fillEmpty()
        fillDust()
        fillNumber()
        fillConfirm()
        view.disabled = false
    }

    private fun fillEmpty() {
        view.setSlot(Slot.NULL, Button(GUI_CENTER).createItemStack())
    }

    private fun fillDust() {
        view.setSlot(Slot.STAR_DUST, Button(ButtonType.STARS).createItemStack().apply {
            if (getRequirement(planet.level.toInt()) >= 0) {
                offer(
                    Keys.ITEM_LORE, asList<Text>(
                        lang.ofPlaceHolder(lang.of("UI.Tip.StarDustCount", galaxy.starDust.toString())),
                        lang.ofLiteralPlaceHolder(lang.of("UI.Tip.PlanetLevel"), ": ", planet.level.toString(), "->", (planet.level + 1).toString())
                    )
                )
            } else {
                offer(
                    Keys.ITEM_LORE, asList<Text>(
                        lang.ofPlaceHolder(lang.of("UI.Tip.StarDustCount", galaxy.starDust.toString())),
                        lang.ofLiteralPlaceHolder(lang.of("UI.Tip.PlanetLevel"), ": ", planet.level.toString())
                    )
                )
            }
        })
    }

    private fun fillNumber() {
        val requirement =  getRequirement(planet.level.toInt())

        requirement.let{
            if (it >= 0) {
                view.setSlots(
                    Slot.NUMBER,
                    createNumbers(
                        requirement,
                        view.countSlots(Slot.NUMBER)
                    )
                )
            } else {
                view.setSlot(
                    Slot.NUMBER,
                    Button(ButtonType.X).createItemStack()
                )
            }
        }
    }

    private fun fillConfirm() {
        val requirement =  getRequirement(planet.level.toInt())

        if (requirement >= 0 && galaxy.starDust > requirement) {
            view.setSlot(
                Slot.CONFIRM,
                Button(ButtonType.OK).createItemStack(),
                Action.CONFIRM
            )
        } else {
            view.setSlot(
                Slot.CONFIRM,
                Button(ButtonType.X).createItemStack(),
                Action.CANCEL
            )
        }
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        val player = event.source as Player
        val detail = view.getDetail(event)

        if (detail.affectGUI) {
            event.isCancelled = true
        }

        if (view.disabled) {
            return
        }

        when (detail.primary?.data) {
            Action.CONFIRM -> launch(start = CoroutineStart.UNDISPATCHED) {
                view.disabled = true

                if (TravelerHelper.getTraveler(player).await()?.group !in asList(Group.ADMIN, Group.OWNER)) {
                    player.sendMessage(Text.of(TextColors.RED, lang.of("UI.Tip.NotEnoughPermission")).toLegacyText(player))
                    view.disabled = false
                    return@launch
                }

                // to prevent desync due to multiple user change the starDust count of galaxy at same time
                refreshGalaxyAndPlanet()

                val requirement = getRequirement(planet.level.toInt())

                if (requirement > 0 && galaxy.starDust >= requirement) {
                    galaxy.takeStarDust(requirement)
                    planet.level++

                    Main.galaxyManager.saveGalaxy(galaxy)
                }

                view.disabled = false

                // refresh again after status modified
                refreshGalaxyAndPlanet()
                updateView()
            }


            Action.CANCEL -> {
                GUIHelper.close(token)
            }
            else -> Unit
        }
    }
}
