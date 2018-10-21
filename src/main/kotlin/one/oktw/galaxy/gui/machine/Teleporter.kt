package one.oktw.galaxy.gui.machine

import com.flowpowered.math.vector.Vector3d
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.toList
import kotlinx.coroutines.experimental.reactive.openSubscription
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.main
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
import org.spongepowered.api.event.entity.MoveEntityEvent
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
    }

    private val MAX_FRAME = 64

    private val lang = Main.translationService
    private val list = TeleporterHelper.getAvailableTargets(teleporter)

    private var job: Job? = null
    private val waitingEntities: ArrayList<Entity> = ArrayList()
    private val sourceFrames: ArrayList<Location<World>> = ArrayList()

    private val moveEventListener = MoveEventListener(this)

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

    private suspend fun getEntities(world: World, teleporter: Teleporter): List<Entity> {
        val sourceFrames = teleporter.position
            .run { Location(world, x, y, z) }
            .let { TeleporterHelper.searchTeleporterFrame(it, MAX_FRAME); }
            ?: return asList()

        return world.entities.filter {
            sourceFrames[Triple(it.location.blockX, it.location.blockY - 1, it.location.blockZ)] != null
        }
    }

    private fun doWhenPlayer(list: List<Entity>, block: (Player) -> Unit) {
        list.forEach { entity ->
            (entity as? Player)?.let { player ->
                block.invoke(player)
            }
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
                player.sendMessage(lang.ofPlaceHolder(TextColors.RED, lang.of("Respond.Teleporting")).toLegacyText(player))
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

            if (targetFrames == null) {
                doWhenPlayer(waitingEntities) {
                    it.sendMessage(lang.of("Respond.TooMuchFramesThatSide").toLegacyText(it))
                }
                return@launch
            }

            var index = 0

            waitingEntities.forEach {
                val target = if (targetFrames.size != 0) targetFrames[index % targetFrames.size] else Location(
                    targetWorld,
                    targetTeleporter.position.x,
                    targetTeleporter.position.y,
                    targetTeleporter.position.z
                )

                index++

                if (it.type == EntityTypes.PLAYER) {
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
                        )
                    } else {
                        TeleportHelper.teleport(
                            it,
                            // offset y by 1, so you are on the block, offset x and z by 0.5, so you are on the center of block
                            Location(targetWorld, target.x + 0.5, target.y + 1, target.z + 0.5)
                        )
                    }

                } else {
                    withContext(Main.serverThread) {
                        // ignore special entities
                        if (it[DataUUID.key].orElse(null) != null) return@withContext

                        it.transferToWorld(
                            targetWorld,
                            Vector3d(target.x + 0.5, target.y + 1, target.z + 0.5)
                        )
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
        }
    }

    private fun moveEvent(event: MoveEntityEvent) {

        val player = event.source as? Player ?: return
        // main.logger.info("event detect")

        val location = event.toTransform.position

        if (player !in waitingEntities) return
        // main.logger.info("user match")

        if (
            !sourceFrames.any {
                Main.main.logger.info(it.blockPosition.add(0, 1, 0).toString())
                Main.main.logger.info(location.toInt().toString())
                it.blockPosition.add(0, 1, 0) == location.toInt()
            }
        ) {
            waitingEntities.remove(player)
            ActionBar.setActionBar(player, ActionBarData(Text.of(TextColors.RED, lang.of("Respond.TeleportCancelled")).toLegacyText(player), 3))
        }

        if (waitingEntities.size == 0) {
            CountDown.instance.cancel(teleporter.uuid)
            return
        }
    }
}
