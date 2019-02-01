/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2018
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package one.oktw.galaxy.gui.machine

import com.flowpowered.math.vector.Vector3d
import com.flowpowered.math.vector.Vector3i
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.reactive.openSubscription
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import one.oktw.galaxy.galaxy.planet.TeleportHelper
import one.oktw.galaxy.galaxy.planet.enums.PlanetType
import one.oktw.galaxy.gui.GUIHelper
import one.oktw.galaxy.gui.PageGUI
import one.oktw.galaxy.item.enums.ButtonType
import one.oktw.galaxy.item.type.Button
import one.oktw.galaxy.machine.teleporter.TeleporterHelper
import one.oktw.galaxy.machine.teleporter.data.Teleporter
import one.oktw.galaxy.player.data.ActionBarData
import one.oktw.galaxy.player.service.ActionBar
import one.oktw.galaxy.translation.extensions.toLegacyText
import one.oktw.galaxy.util.CountDown
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.EventListener
import org.spongepowered.api.event.entity.DestructEntityEvent
import org.spongepowered.api.event.entity.MoveEntityEvent
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList

class Teleporter(private val teleporter: Teleporter) : PageGUI<UUID>() {
    companion object {
        class MoveEventListener(private val teleporter: one.oktw.galaxy.gui.machine.Teleporter) : EventListener<MoveEntityEvent> {
            override fun handle(event: MoveEntityEvent) {
                teleporter.moveEvent(event)
            }
        }

        class DestructEntityListener(private val teleporter: one.oktw.galaxy.gui.machine.Teleporter) : EventListener<DestructEntityEvent> {
            override fun handle(event: DestructEntityEvent) {
                teleporter.destructEntityEvent(event)
            }
        }

        class PrePickupListener(private val teleporter: one.oktw.galaxy.gui.machine.Teleporter) : EventListener<ChangeInventoryEvent.Pickup.Pre> {
            override fun handle(event: ChangeInventoryEvent.Pickup.Pre) {
                teleporter.prePickupEvent(event)
            }
        }

        // must bigger than -1
        private const val OFFSET_LOWER_BOUND = -0.5
        // must smaller than 2
        private const val OFFSET_HIGHER_BOUND = 1.5

        private data class PassengerNode(
            val node: Entity,
            val children: ArrayList<PassengerNode> = ArrayList()
        ) {
            fun flatten(): List<Entity> {
                val list = ArrayList<Entity>()
                list.add(node)

                children.forEach {
                    list.addAll(it.flatten())
                }

                return list
            }

            fun unmountAll() {
                children.forEach { child ->
                    node.clearPassengers()
                    child.unmountAll()
                }
            }

            fun mountAll() {
                children.forEach { child ->
                    node.addPassenger(child.node)
                    child.mountAll()
                }
            }
        }

        private fun makePassengerTree(entity: Entity): PassengerNode {
            val node = PassengerNode(entity)

            entity.passengers.forEach {
                node.children += makePassengerTree(it)
            }

            return node
        }
    }

    private val MAX_FRAME = 64

    private val lang = Main.translationService
    private val list = TeleporterHelper.getAvailableTargets(teleporter)

    private var job: Job? = null
    private val waitingEntities: ArrayList<Entity> = ArrayList()
    private val sourceFrames: ArrayList<Location<World>> = ArrayList()

    private val moveEventListener = MoveEventListener(this)
    private val destructEntityEventListener = DestructEntityListener(this)
    private val prePickupListener = PrePickupListener(this)

    override val token = "Teleporter-${UUID.randomUUID()}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.DOUBLE_CHEST)
        .property(InventoryTitle.of(
            teleporter.crossPlanet
                .let {
                    if (it) {
                        lang.of("UI.Title.AdvancedTeleporter", teleporter.name)
                    } else {
                        lang.of("UI.Title.Teleporter", teleporter.name)
                    }
                }
                .let {
                    lang.ofPlaceHolder(it)
                }
        ))
        .listener(InteractInventoryEvent::class.java, ::eventProcess)
        .build(Main.main)

    init {
        offerPage(0)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
        registerEvent(InteractInventoryEvent.Close::class.java, this::closeInventoryEvent)
    }

    override suspend fun get(number: Int, skip: Int): List<Pair<ItemStack, UUID>> {
        val res = ArrayList<Pair<ItemStack, UUID>>()

        val teleporters = list
            .skip(skip)
            .limit(number)
            .openSubscription()
            .toList()

        for (targetTransporter in teleporters) {
            // don't tp to itself
            if (teleporter.uuid == targetTransporter.uuid) continue
            // don't tp from advanced to normal one in another planet
            if (
                teleporter.crossPlanet &&
                !targetTransporter.crossPlanet &&
                teleporter.position.planet != targetTransporter.position.planet
            ) continue

            val uuid = targetTransporter.position.planet ?: continue
            val planet = Main.galaxyManager.get(null, uuid)?.getPlanet(uuid) ?: continue

            when (planet.type) {
                PlanetType.NORMAL -> Button(ButtonType.PLANET_O)
                PlanetType.NETHER -> Button(ButtonType.PLANET_N)
                PlanetType.END -> Button(ButtonType.PLANET_E)
            }.createItemStack()
                .apply {
                    offer(Keys.DISPLAY_NAME, Text.of(TextColors.YELLOW, TextStyles.BOLD, targetTransporter.name))
                    offer(
                        Keys.ITEM_LORE,
                        Arrays.asList(
                            lang.ofPlaceHolder(
                                TextColors.GREEN,
                                lang.of(
                                    "UI.Tip.Target",
                                    "${planet.name} ${targetTransporter.position.x}, ${targetTransporter.position.y}, ${targetTransporter.position.z}"
                                ),
                                TextColors.RESET
                            ),
                            lang.ofPlaceHolder(
                                TextColors.GREEN,
                                lang.of(
                                    "UI.Tip.CanCrossPlanet",
                                    if (targetTransporter.crossPlanet) {
                                        lang.of("UI.Tip.true")
                                    } else {
                                        lang.of("UI.Tip.false")
                                    }
                                ),
                                TextColors.RESET
                            )
                        )
                    )
                }
                .let {
                    res.add(Pair(it, targetTransporter.uuid))
                }
        }

        return res
    }

    // get ALl base entities in this range
    private suspend fun getEntities(world: World, teleporter: Teleporter): List<Entity> {
        val sourceFrames = teleporter.position
            .run { Location(world, x, y, z) }
            .let { TeleporterHelper.searchTeleporterFrame(it, MAX_FRAME); }
            ?: return asList()

        return world.entities.filter {
            // not base, just ignore
            if (it.vehicle.isPresent) {
                return@filter false
            }

            // is that jumping?
            if ((it.location.y % 1.0) < (OFFSET_HIGHER_BOUND % 1.0)) {
                if (sourceFrames[Triple(it.location.blockX, it.location.blockY - 2, it.location.blockZ)] != null) {
                    return@filter true
                }
            }

            // is that under floor?
            if ((it.location.y % 1.0) > ((OFFSET_LOWER_BOUND + 1.0) % 1.0)) {
                if (sourceFrames[Triple(it.location.blockX, it.location.blockY, it.location.blockZ)] != null) {
                    return@filter true
                }
            }

            // is there a block on its foot?
            return@filter sourceFrames[Triple(it.location.blockX, it.location.blockY - 1, it.location.blockZ)] != null
        }
    }

    private fun doWhenPlayer(list: List<Entity>, block: (Player) -> Unit) {
        list.forEach { entity ->
            (entity as? Player)?.let { player ->
                block.invoke(player)
            }

            // recursive though all its passengers
            doWhenPlayer(entity.passengers, block)
        }
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        val detail = view.getDetail(event)

        if (detail.affectGUI) {
            event.isCancelled = true
        }

        val player = event.source as Player
        val world = player.world
        val uuid = detail.primary?.data?.data ?: return

        launch {
            GUIHelper.closeAll(player)

            if (CountDown.instance.isCounting(teleporter.uuid)) {
                player.sendMessage(Text.of(TextColors.RED, lang.of("Respond.Teleporting")).toLegacyText(player))
                return@launch
            }

            val targetTeleporter = TeleporterHelper.get(uuid) ?: return@launch
            val planetId = targetTeleporter.position.planet ?: return@launch
            val targetPlanet = Main.galaxyManager.get(null, planetId)?.getPlanet(planetId) ?: return@launch
            val targetWorld = PlanetHelper.loadPlanet(targetPlanet) ?: return@launch

            // pre check
            val sourceFramesPre = teleporter.position
                .run { Location(world, x, y, z) }
                .let { TeleporterHelper.searchTeleporterFrame(it, MAX_FRAME); }

            if (sourceFramesPre == null) {
                player.sendMessage(lang.of("Respond.TooMuchFramesThisSide").toLegacyText(player))

                return@launch
            }

            this@Teleporter.sourceFrames.addAll(sourceFramesPre.values)

            val targetFramesPre = targetTeleporter.position
                .let { Location(targetWorld, it.x, it.y, it.z) }
                .let { TeleporterHelper.searchTeleporterFrame(it, MAX_FRAME) }
                ?.run { ArrayList(values) }

            if (targetFramesPre == null) {
                player.sendMessage(lang.of("Respond.TooMuchFramesThatSide").toLegacyText(player))

                return@launch
            }

            // delay listener register to here to prevent listener leak
            Sponge.getEventManager().registerListener(main, MoveEntityEvent::class.java, moveEventListener)
            Sponge.getEventManager().registerListener(main, DestructEntityEvent::class.java, destructEntityEventListener)
            Sponge.getEventManager().registerListener(main, ChangeInventoryEvent.Pickup.Pre::class.java, prePickupListener)

            waitingEntities.addAll(getEntities(player.world, teleporter))

            if (waitingEntities.size == 0) {
                player.sendMessage(Text.of(TextColors.RED, lang.of("Respond.TeleportNothing")).toLegacyText(player))

                return@launch
            }

            job = async {
                (5 downTo 1).forEach { number ->
                    doWhenPlayer(waitingEntities) { player ->
                        ActionBar.setActionBar(
                            player,
                            ActionBarData(Text.of(TextColors.GREEN, lang.of("Respond.TeleportCountDown", number)).toLegacyText(player), 3)
                        )
                    }

                    delay(1000)
                }

                doWhenPlayer(waitingEntities) { player ->
                    ActionBar.setActionBar(player, ActionBarData(Text.of(TextColors.GREEN, lang.of("Respond.TeleportStart")).toLegacyText(player), 3))
                }
            }

            CountDown.instance.countDown(teleporter.uuid, 5000) {
                job?.cancel()
            }

            job?.join()

            Sponge.getEventManager().unregisterListeners(moveEventListener)
            Sponge.getEventManager().unregisterListeners(destructEntityEventListener)
            Sponge.getEventManager().unregisterListeners(prePickupListener)
            // main.logger.info("event unregistered")

            if (job?.isCancelled == true) {
                return@launch
            }

            val sourceFrames = teleporter.position
                .run { Location(world, x, y, z) }
                .let { TeleporterHelper.searchTeleporterFrame(it, MAX_FRAME); }

            if (sourceFrames == null) {
                doWhenPlayer(waitingEntities) {
                    it.sendMessage(lang.of("Respond.TooMuchFramesThisSide").toLegacyText(it))
                }

                return@launch
            }

            val targetFrames = targetTeleporter.position
                .let { Location(targetWorld, it.x, it.y, it.z) }
                .let { TeleporterHelper.searchTeleporterFrame(it, MAX_FRAME) }
                ?.let { TeleporterHelper.filterSafeLocation(it) }
                ?.run { ArrayList(values) }
                ?.apply {
                    sortBy {
                        it.blockPosition.distance(
                            targetTeleporter.position.run {
                                Vector3i(x, y, z)
                            }
                        )
                    }
                }

            if (targetFrames == null) {
                doWhenPlayer(waitingEntities) {
                    it.sendMessage(lang.of("Respond.TooMuchFramesThatSide").toLegacyText(it))
                }
                return@launch
            }

            var index = 0

            val exactLocation = Location(
                targetWorld,
                targetTeleporter.position.x,
                targetTeleporter.position.y,
                targetTeleporter.position.z
            )

            withContext(serverThread) {
                waitingEntities.forEach { root ->

                    index++

                    // dismount everything

                    val tree = makePassengerTree(root)
                    tree.unmountAll()

                    val entities = tree.flatten()

                    val target = when {
                        entities.contains(player) -> exactLocation
                        targetFrames.size != 0 -> targetFrames[index % targetFrames.size]
                        else -> exactLocation
                    }

                    entities.map {
                        if (it.type == EntityTypes.PLAYER && TeleporterHelper.isSafeLocation(exactLocation)) {
                            val currentPlayer = it as Player

                            // we teleport the main player to exact position
                            if (currentPlayer == player) {
                                TeleportHelper.teleport(
                                    it,
                                    // offset y by 1, so you are on the block, offset x and z by 0.5, so you are on the center of block
                                    Location(
                                        targetWorld,
                                        targetTeleporter.position.x + 0.5,
                                        targetTeleporter.position.y + 1,
                                        targetTeleporter.position.z + 0.5
                                    )
                                ).await()
                            } else {
                                TeleportHelper.teleport(
                                    it,
                                    // offset y by 1, so you are on the block, offset x and z by 0.5, so you are on the center of block
                                    Location(targetWorld, target.x + 0.5, target.y + 1, target.z + 0.5)
                                ).await()
                            }

                        } else {
                            // ignore special entities
                            if (it[DataUUID.key].orElse(null) != null) return@map false

                            it.transferToWorld(
                                targetWorld,
                                Vector3d(target.x + 0.5, target.y + 1, target.z + 0.5)
                            )
                        }
                    }.all { it }.let { allSuccess ->
                        if (!allSuccess) {
                            return@let
                        }

                        tree.mountAll()
                    }
                }
            }
        }
    }

    @Suppress("UNUSED_PARAMETER")
    private fun closeInventoryEvent(event: InteractInventoryEvent.Close) {
        if (waitingEntities.size == 0) {
            // main.logger.info("event unregistered")
            Sponge.getEventManager().unregisterListeners(moveEventListener)
            Sponge.getEventManager().unregisterListeners(destructEntityEventListener)
            Sponge.getEventManager().unregisterListeners(prePickupListener)
        }
    }

    private fun moveEvent(event: MoveEntityEvent) {
        val entity = event.source as? Entity ?: return
        // main.logger.info("event detect")

        val location = event.toTransform.position

        if (entity !in waitingEntities) return
        // main.logger.info("user match")

        if (
            !sourceFrames.any {
                it.blockX == location.floorX &&
                    it.blockZ == location.floorZ &&
                    location.y > it.y + OFFSET_LOWER_BOUND + 1.0 &&
                    location.y < it.y + OFFSET_HIGHER_BOUND + 1.0
            }
        ) {
            waitingEntities.remove(entity)

            if (entity is Player) {
                ActionBar.setActionBar(entity, ActionBarData(Text.of(TextColors.RED, lang.of("Respond.TeleportCancelled")).toLegacyText(entity), 3))
            }
        }

        if (waitingEntities.size == 0) {
            CountDown.instance.cancel(teleporter.uuid)
            return
        }
    }

    private fun destructEntityEvent(event: DestructEntityEvent) {
        val entity = event.targetEntity
        if (entity !in waitingEntities) return
        waitingEntities.remove(entity)

        if (waitingEntities.size == 0) {
            CountDown.instance.cancel(teleporter.uuid)
            return
        }
    }

    private fun prePickupEvent(event: ChangeInventoryEvent.Pickup.Pre) {
        val entity = event.targetEntity
        if (entity !in waitingEntities) return
        waitingEntities.remove(entity)

        if (waitingEntities.size == 0) {
            CountDown.instance.cancel(teleporter.uuid)
            return
        }
    }
}
