package one.oktw.galaxy.machine.portal

import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.block.enums.CustomBlocks.*
import one.oktw.galaxy.data.DataBlockType
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.event.PlaceCustomBlockEvent
import one.oktw.galaxy.item.enums.ItemType
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.text.Text
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.event.RemoveCustomBlockEvent
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.gui.GUIHelper
import one.oktw.galaxy.gui.machine.Portal
import java.util.Arrays.asList
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData



class PortalManager {
    private val logger = main.logger

    init {
        Keys.IS_SNEAKING.registerEvent(Player::class.java) {
            if (it.endResult.successfulData.firstOrNull { it.key == Keys.IS_SNEAKING }?.get() != true) return@registerEvent

            val player = it.targetHolder as? Player ?: return@registerEvent
            val location = player.location.sub(0.0, 1.0, 0.0)

            if (location.blockType != BlockTypes.MOB_SPAWNER) return@registerEvent
            if (location[DataBlockType.key].orElse(null) !in asList(PORTAL, ADVANCED_PORTAL)) return@registerEvent

            launch {
                galaxyManager.get(player.world)?.getPlanet(player.world)?.let {
                    PortalHelper.getPortalAt(
                        it,
                        location.blockX,
                        location.blockY,
                        location.blockZ
                    )
                }?.let {
                    Portal(it)
                }?.let {
                    GUIHelper.open(player) { it }
                }
            }
        }
    }

    @Listener
    fun onPlace(event: PlaceCustomBlockEvent) {
        val player = event.cause.first(Player::class.java).orElse(null) ?: return

        event.item.let {
            val item = it[DataBlockType.key].get()

            if (item == PORTAL || item == ADVANCED_PORTAL) {
                launch {
                    val name = it[DisplayNameData::class.java].orElse(null)?.displayName()?.get()?.toPlain()?: "A Portal"

                    galaxyManager.get(player.world)?.getPlanet(player.world)?.let {
                        PortalHelper.createPortal(
                            it,
                            event.location.blockX,
                            event.location.blockY,
                            event.location.blockZ,
                            name,
                            item == ADVANCED_PORTAL
                        ).let {
                            if (!it) {
                                player.sendMessage(Text.of("Portal creation failed at ${event.location.toString()}"))
                            }
                        }
                    }?: let {
                        player.sendMessage(Text.of("error: fail to get planet"))
                    }
                }
            }
        }
    }


    @Listener
    fun onRemove(event: RemoveCustomBlockEvent) {
        val player = event.cause.first(Player::class.java).orElse(null) ?: return

        launch {
            event.location.let {
                galaxyManager.get(player.world)?.getPlanet(player.world)?.let {
                    PortalHelper.removePortal(
                        it,
                        event.location.blockX,
                        event.location.blockY,
                        event.location.blockZ
                    )
                }?.let {
                    if (it) {
                        player.sendMessage(Text.of("You remvoed a portal"))
                    }
                }?: let {
                    player.sendMessage(Text.of("error: fail to get planet"))
                }
            }
        }
    }
}