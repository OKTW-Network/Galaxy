package one.oktw.galaxy.block.event

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.block.enums.CustomBlocks.TRANSPORTER_ADVANCED
import one.oktw.galaxy.block.enums.CustomBlocks.TRANSPORTER
import one.oktw.galaxy.data.DataBlockType
import one.oktw.galaxy.event.PlaceCustomBlockEvent
import one.oktw.galaxy.event.RemoveCustomBlockEvent
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.gui.GUIHelper
import one.oktw.galaxy.machine.transporter.TransporterHelper
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.text.Text
import java.util.Arrays.asList


class Transporter {
    private val logger = main.logger

    init {
        Keys.IS_SNEAKING.registerEvent(Player::class.java) {
            if (it.endResult.successfulData.firstOrNull { it.key == Keys.IS_SNEAKING }?.get() != true) return@registerEvent

            val player = it.targetHolder as? Player ?: return@registerEvent
            val location = player.location.sub(0.0, 1.0, 0.0)

            if (location.blockType != BlockTypes.MOB_SPAWNER) return@registerEvent
            if (location[DataBlockType.key].orElse(null) !in asList(TRANSPORTER, TRANSPORTER_ADVANCED)) return@registerEvent

            launch {
                galaxyManager.get(player.world)?.getPlanet(player.world)?.let {
                    TransporterHelper.get(
                        it,
                        location.blockX,
                        location.blockY,
                        location.blockZ
                    )
                }?.let {
                    one.oktw.galaxy.gui.machine.Transporter(it)
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

            if (item == TRANSPORTER || item == TRANSPORTER_ADVANCED) {
                launch {
                    val name = it[DisplayNameData::class.java].orElse(null)?.displayName()?.get()?.toPlain()?: "A Transporter"

                    galaxyManager.get(player.world)?.getPlanet(player.world)?.let {
                        TransporterHelper.create(
                            it,
                            event.location.blockX,
                            event.location.blockY,
                            event.location.blockZ,
                            name,
                            item == TRANSPORTER_ADVANCED
                        ).let {
                            if (!it) {
                                player.sendMessage(Text.of("Transporter creation failed at ${event.location}"))
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
                    TransporterHelper.remove(
                        it,
                        event.location.blockX,
                        event.location.blockY,
                        event.location.blockZ
                    )
                }?.let {
                    if (it) {
                        player.sendMessage(Text.of("You removed a transporter"))
                    }
                }?: let {
                    player.sendMessage(Text.of("error: fail to get planet"))
                }
            }
        }
    }
}
