package one.oktw.galaxy.gui.machine

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.toList
import kotlinx.coroutines.experimental.joinChildren
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.reactive.openSubscription
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import one.oktw.galaxy.galaxy.planet.TeleportHelper
import one.oktw.galaxy.gui.GUIHelper
import one.oktw.galaxy.gui.GalaxyManagement
import one.oktw.galaxy.gui.PageGUI
import one.oktw.galaxy.machine.portal.PortalHelper
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import org.spongepowered.api.world.Location
import java.util.*
import kotlin.collections.ArrayList
import kotlin.streams.toList

class Portal(private val portal: one.oktw.galaxy.machine.portal.data.Portal) : PageGUI() {
    private val lang = Main.languageService.getDefaultLanguage()
    private val list = PortalHelper.getAvailableTargets(portal)

    override val token = "BrowserGalaxy-${UUID.randomUUID()}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.DOUBLE_CHEST)
        .property(InventoryTitle.of(Text.of(lang["UI.Portal.Title"])))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(Main.main)

    init {
        offerPage(0)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    override suspend fun get(number: Int, skip: Int): List<ItemStack> {
        val res = ArrayList<ItemStack>()

        val portals = list
            .skip(skip)
            .limit(number)
            .openSubscription()
            .toList()

        for (targetPortal in portals) {
            // don't tp to itset
            if (portal.uuid == targetPortal.uuid) continue
            val uuid = targetPortal.position.planet ?: continue
            val planet = Main.galaxyManager.get(null, uuid)?.getPlanet(uuid) ?: continue



            ItemStack.builder()
                .itemType(ItemTypes.DIAMOND_BLOCK)
                .itemData(DataUUID(targetPortal.uuid))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.YELLOW, TextStyles.BOLD, targetPortal.name))
                .add(
                    Keys.ITEM_LORE,
                    Arrays.asList(
                        Text.of(
                            TextColors.GREEN,
                            "Target: ${planet.name} ${targetPortal.position.x}, ${targetPortal.position.y}, ${targetPortal.position.z}",
                            TextColors.RESET
                        ),
                        Text.of(
                            TextColors.GREEN,
                            "Cross Planet: ${targetPortal.crossPlanet}",
                            TextColors.RESET
                        )
                    )
                )
                .build()
                .let { res.add(it) }
        }

        return res
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true

        val player = event.source as Player
        val item = event.cursorTransaction.default
        val uuid = item[DataUUID.key].orElse(null) ?: return

        val (slot) = view.getNameOf(event) ?: return

        if (slot == Companion.PageGUISlot.ITEMS) {
            launch {
                val target = PortalHelper.getPortal(uuid) ?: return@launch
                val planetId = target.position.planet?: return@launch
                val planet = Main.galaxyManager.get(null, planetId)?.getPlanet(planetId) ?: return@launch
                val world = PlanetHelper.loadPlanet(planet) ?: return@launch

                TeleportHelper.teleport(
                    player,
                    // offset y by 1, so you are on the block, offsset x and z by 0.5, so you are on the center of block
                    Location(world, target.position.x + 0.5, target.position.y + 1, target.position.z + 0.5)
                )

                GUIHelper.closeAll(player)
            }
        }
    }
}