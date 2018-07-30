package one.oktw.galaxy.block.event

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.block.enums.CustomBlocks.TELEPORTER_ADVANCED
import one.oktw.galaxy.block.enums.CustomBlocks.TELEPORTER
import one.oktw.galaxy.data.DataBlockType
import one.oktw.galaxy.event.PlaceCustomBlockEvent
import one.oktw.galaxy.event.RemoveCustomBlockEvent
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.gui.GUIHelper
import one.oktw.galaxy.machine.teleporter.TeleporterHelper
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData
import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import java.util.Arrays.asList


class Teleporter {
    private val logger = main.logger

    private val lang = Main.languageService.getDefaultLanguage()

    init {
        Keys.IS_SNEAKING.registerEvent(Player::class.java) {
            if (it.endResult.successfulData.firstOrNull { it.key == Keys.IS_SNEAKING }?.get() != true) return@registerEvent

            val player = it.targetHolder as? Player ?: return@registerEvent
            val location = player.location.sub(0.0, 1.0, 0.0)

            if (location.blockType != BlockTypes.MOB_SPAWNER) return@registerEvent
            if (location[DataBlockType.key].orElse(null) !in asList(TELEPORTER, TELEPORTER_ADVANCED)) return@registerEvent

            launch {
                galaxyManager.get(player.world)?.getPlanet(player.world)?.let {
                    TeleporterHelper.get(
                        it,
                        location.blockX,
                        location.blockY,
                        location.blockZ
                    )
                }?.let {
                    one.oktw.galaxy.gui.machine.Teleporter(it)
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

            if (item == TELEPORTER || item == TELEPORTER_ADVANCED) {
                launch {
                    val name = if (item == TELEPORTER_ADVANCED) {
                        lang["block.TELEPORTER_ADVANCED"]
                    } else {
                        lang["block.TELEPORTER"]
                    }

                    galaxyManager.get(player.world)?.getPlanet(player.world)?.let {
                        TeleporterHelper.create(
                            it,
                            event.location.blockX,
                            event.location.blockY,
                            event.location.blockZ,
                            name,
                            item == TELEPORTER_ADVANCED
                        ).let {
                            if (!it) {
                                player.sendMessage(Text.of("Teleporter creation failed at ${event.location}"))
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
                    TeleporterHelper.remove(
                        it,
                        event.location.blockX,
                        event.location.blockY,
                        event.location.blockZ
                    )
                }?.let {
                    if (it) {
                        player.sendMessage(Text.of("You removed a teleporter"))
                    }
                }?: let {
                    player.sendMessage(Text.of("error: fail to get planet"))
                }
            }
        }
    }


    @Listener
    fun onClickBlock(event: InteractBlockEvent.Secondary.MainHand, @First player: Player) {
        if (player[Keys.IS_SNEAKING].orElse(false) == true) return

        when (event.targetBlock.location.orElse(null)?.get(DataBlockType.key)?.orElse(null) ?: return) {
            TELEPORTER, TELEPORTER_ADVANCED -> {
                val itemOnHand = player.getItemInHand(HandTypes.MAIN_HAND).orElse(null)?: return

                if (itemOnHand.type == ItemTypes.NAME_TAG) {
                    val newStationName = itemOnHand[DisplayNameData::class.java].orElse(null)?.displayName()?.get()?.toPlain()?: return

                    consumeItem(player, HandTypes.MAIN_HAND)

                    launch {
                        val position = event.targetBlock.location.orElse(null)?: return@launch
                        galaxyManager.get(player.world)?.getPlanet(player.world)?.let {
                            val site = TeleporterHelper.get(it,
                                position.blockX,
                                position.blockY,
                                position.blockZ
                            )?: return@launch

                            TeleporterHelper.update(site.copy(name = newStationName))
                        }
                    }
                }
            }
            else -> Unit
        }
    }

    private fun consumeItem(player: Player, hand: HandType) {
        if (player.gameMode().get() == GameModes.CREATIVE) return

        val item = player.getItemInHand(hand).orElse(null) ?: return

        item.quantity--

        if (item.quantity <= 0) player.setItemInHand(hand, ItemStack.empty()) else player.setItemInHand(hand, item)
    }
}
