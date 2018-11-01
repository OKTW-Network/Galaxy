package one.oktw.galaxy.block.event

import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.block.enums.CustomBlocks.TELEPORTER
import one.oktw.galaxy.block.enums.CustomBlocks.TELEPORTER_ADVANCED
import one.oktw.galaxy.data.DataBlockType
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.event.PlaceCustomBlockEvent
import one.oktw.galaxy.event.RemoveCustomBlockEvent
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.gui.GUIHelper
import one.oktw.galaxy.machine.teleporter.TeleporterHelper
import one.oktw.galaxy.translation.extensions.toLegacyText
import one.oktw.galaxy.util.CountDown
import org.spongepowered.api.Sponge
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData
import org.spongepowered.api.data.type.HandType
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.cause.EventContextKeys
import org.spongepowered.api.event.cause.entity.spawn.SpawnType
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes
import org.spongepowered.api.event.filter.IsCancelled
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.util.Tristate
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World
import java.util.*
import java.util.Arrays.asList


class Teleporter {
    private val lang = Main.translationService

    init {
        Keys.IS_SNEAKING.registerEvent(Player::class.java) { event ->
            val player = event.targetHolder as? Player ?: return@registerEvent

            if (event.endResult.successfulData.firstOrNull { it.key == Keys.IS_SNEAKING }?.get() != true) return@registerEvent

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
                    if (CountDown.instance.isCounting(it.uuid)) {
                        player.sendMessage(Text.of(TextColors.RED, lang.of("Respond.Teleporting")).toLegacyText(player))

                        return@launch
                    }
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

        event.item.let { itemStack ->
            val item = itemStack[DataBlockType.key].get()

            if (item == TELEPORTER || item == TELEPORTER_ADVANCED) {
                launch {
                    val name = if (item == TELEPORTER_ADVANCED) {
                        lang.of("block.TELEPORTER_ADVANCED")
                    } else {
                        lang.of("block.TELEPORTER")
                    }.toLegacyText(player).toPlain()

                    galaxyManager.get(player.world)?.getPlanet(player.world)?.let { planet ->
                        TeleporterHelper.create(
                            planet,
                            event.location.blockX,
                            event.location.blockY,
                            event.location.blockZ,
                            name,
                            item == TELEPORTER_ADVANCED
                        ).let { success ->
                            if (!success) {
                                player.sendMessage(Text.of("Teleporter creation failed at ${event.location}"))
                                return@launch
                            }

                            TeleporterHelper.get(planet, event.location.blockX, event.location.blockY, event.location.blockZ)
                                ?.let {
                                    createOrUpdateArmorStand(
                                        Location(
                                            event.location.extent,
                                            event.location.blockX + 0.5,
                                            event.location.blockY + 2.0,
                                            event.location.blockZ + 0.5
                                        ),
                                        it.uuid,
                                        it.name
                                    )
                                }

                        }
                    } ?: let {
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
            event.location.let { location ->
                galaxyManager.get(player.world)?.getPlanet(player.world)?.let { planet ->
                    TeleporterHelper.get(
                        planet,
                        location.blockX,
                        location.blockY,
                        location.blockZ
                    )?.let {
                        removeArmorStand(event.location, it.uuid)
                    }

                    TeleporterHelper.remove(
                        planet,
                        location.blockX,
                        location.blockY,
                        location.blockZ
                    )
                } ?: let {
                    player.sendMessage(Text.of("error: fail to get planet"))
                }
            }
        }
    }


    @IsCancelled(value = Tristate.UNDEFINED)
    @Listener
    fun onClickBlock(event: InteractBlockEvent.Secondary.MainHand, @First player: Player) {
        if (player[Keys.IS_SNEAKING].orElse(false) == true) return

        when (event.targetBlock.location.orElse(null)?.get(DataBlockType.key)?.orElse(null) ?: return) {
            TELEPORTER, TELEPORTER_ADVANCED -> {
                val itemOnHand = player.getItemInHand(HandTypes.MAIN_HAND).orElse(null) ?: return

                if (itemOnHand.type == ItemTypes.NAME_TAG) {
                    event.isCancelled = true

                    val newStationName = itemOnHand[DisplayNameData::class.java].orElse(null)?.displayName()?.get()?.toPlain() ?: return

                    // do not consume item of creative players
                    if (player.gameMode().get() != GameModes.CREATIVE) {
                        consumeItem(player, HandTypes.MAIN_HAND)
                    }

                    launch {
                        val position = event.targetBlock.location.orElse(null) ?: return@launch
                        galaxyManager.get(player.world)?.getPlanet(player.world)?.let {
                            val site = TeleporterHelper.get(
                                it,
                                position.blockX,
                                position.blockY,
                                position.blockZ
                            ) ?: return@launch

                            TeleporterHelper.update(site.copy(name = newStationName))

                            createOrUpdateArmorStand(
                                Location(
                                    position.extent,
                                    position.blockX + 0.5,
                                    position.blockY + 2.0,
                                    position.blockZ + 0.5
                                ),
                                site.uuid,
                                newStationName
                            )
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

    private suspend fun removeArmorStand(position: Location<World>, uuid: UUID) = withContext (Main.serverThread) {
        position.extent.entities.filter {
            it.type == EntityTypes.ARMOR_STAND
        }.filter {
            it[DataUUID.key].orElse(null) == uuid
        } .forEach {
            it.remove()
        }
    }

    private suspend fun createOrUpdateArmorStand(position: Location<World>, uuid: UUID, name: String) = withContext (Main.serverThread) {
        // remove the old one
        removeArmorStand(position, uuid)

        val armor = try {
            position.createEntity(EntityTypes.ARMOR_STAND)
        } catch (err: Throwable) {
            return@withContext
        }

        armor.offer(Keys.DISPLAY_NAME, Text.of(TextColors.AQUA, name))
        armor.offer(Keys.CUSTOM_NAME_VISIBLE, true)
        armor.offer(Keys.INVULNERABLE, true)
        armor.offer(Keys.INVISIBLE, true)
        armor.offer(Keys.ARMOR_STAND_MARKER, true)
        armor.offer(Keys.HAS_GRAVITY, false)

        armor.offer(DataUUID(uuid))

        Sponge.getCauseStackManager().pushCauseFrame().use { frame ->
            frame.addContext<SpawnType>(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN)
            position.spawnEntity(armor)
        }
    }
}
