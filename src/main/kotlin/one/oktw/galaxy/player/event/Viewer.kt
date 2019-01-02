package one.oktw.galaxy.player.event

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldServer
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.block.enums.CustomBlocks.CONTROL_PANEL
import one.oktw.galaxy.block.enums.CustomBlocks.PLANET_TERMINAL
import one.oktw.galaxy.data.DataBlockType
import one.oktw.galaxy.data.DataItemType
import org.spongepowered.api.Sponge
import org.spongepowered.api.block.BlockTypes.STANDING_SIGN
import org.spongepowered.api.block.BlockTypes.WALL_SIGN
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.key.Keys.GAME_MODE
import org.spongepowered.api.data.key.Keys.POTION_EFFECTS
import org.spongepowered.api.effect.potion.PotionEffect
import org.spongepowered.api.effect.potion.PotionEffectTypes
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameModes.ADVENTURE
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.block.ChangeBlockEvent
import org.spongepowered.api.event.block.CollideBlockEvent
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.entity.CollideEntityEvent
import org.spongepowered.api.event.entity.DamageEntityEvent
import org.spongepowered.api.event.entity.InteractEntityEvent
import org.spongepowered.api.event.entity.SpawnEntityEvent
import org.spongepowered.api.event.entity.ai.SetAITargetEvent
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.filter.cause.All
import org.spongepowered.api.event.filter.cause.Root
import org.spongepowered.api.event.item.inventory.DropItemEvent
import org.spongepowered.api.event.item.inventory.InteractItemEvent
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.item.ItemTypes.*
import org.spongepowered.api.scheduler.Task
import java.util.*
import java.util.Arrays.asList
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.TimeUnit

@Suppress("unused")
class Viewer {
    companion object {
        private val viewer = CopyOnWriteArraySet<UUID>()

        fun setViewer(uuid: UUID) {
            viewer += uuid
            GlobalScope.launch(serverThread) {
                Sponge.getServer().getPlayer(uuid).ifPresent {
                    it.offer(GAME_MODE, ADVENTURE)
                    it.isSleepingIgnored = true
                }
            }
        }

        fun isViewer(uuid: UUID): Boolean {
            if (viewer.isEmpty()) return false

            return viewer.contains(uuid)
        }

        fun removeViewer(uuid: UUID) {
            viewer -= uuid
            GlobalScope.launch(serverThread) { Sponge.getServer().getPlayer(uuid).ifPresent { it.isSleepingIgnored = false } }
        }
    }

    init {
        Task.builder()
            .interval(1, TimeUnit.MINUTES)
            .execute { _ ->
                Sponge.getServer().onlinePlayers.forEach { player ->
                    if (!isViewer(player.uniqueId)) return@forEach

                    player.transform(POTION_EFFECTS) {
                        (it ?: ArrayList()).apply { add(PotionEffect.of(PotionEffectTypes.SATURATION, 100, 1)) }
                    }
                }
            }
            .submit(main)
    }

    @Listener(order = Order.POST)
    fun onDisconnect(event: ClientConnectionEvent.Disconnect) {
        removeViewer(event.targetEntity.uniqueId)
    }

    @Listener(order = Order.FIRST)
    fun onTarget(event: SetAITargetEvent, @Getter("getTarget") player: Player) {
        if (isViewer(player.uniqueId)) event.isCancelled = true
    }

    @Listener(order = Order.FIRST)
    fun onDropItem(event: DropItemEvent.Pre, @Root player: Player) {
        if (isViewer(player.uniqueId)) event.isCancelled = true
    }

    @Listener(order = Order.FIRST)
    fun onCollideBlock(event: CollideBlockEvent, @Root player: Player) {
        if (isViewer(player.uniqueId)) event.isCancelled = true
    }

    @Listener(order = Order.FIRST)
    fun onSpawnEntity(event: SpawnEntityEvent, @All players: Array<Player>) {
        if (players.any { isViewer(it.uniqueId) }) event.isCancelled = true
    }

    @Listener(order = Order.FIRST)
    fun onChangeBlock(event: ChangeBlockEvent.Pre, @Root player: Player) {
        if (isViewer(player.uniqueId)) {
            event.isCancelled = true
            event.locations.forEach {
                (it.extent as WorldServer).playerChunkMap.markBlockForUpdate(BlockPos(it.blockX, it.blockY, it.blockZ))
            }
        }
    }

    @Listener(order = Order.FIRST)
    fun onInteractBlock(event: InteractBlockEvent, @Root player: Player) {
        if (isViewer(player.uniqueId)) {
            // only allow event if it is going to open gui
            if (player[Keys.IS_SNEAKING].orElse(false) == false) {
                // whitelist some custom blocks
                if (event.targetBlock.state.type in asList(STANDING_SIGN, WALL_SIGN)) return

                event.targetBlock.location.orElse(null)?.get(DataBlockType.key)?.orElse(null)?.let {
                    if (it in asList(PLANET_TERMINAL, CONTROL_PANEL)) return
                }
            }

            event.isCancelled = true
            event.targetBlock.location.ifPresent {
                (it.extent as WorldServer).playerChunkMap.markBlockForUpdate(BlockPos(it.blockX, it.blockY, it.blockZ))
            }
        }
    }

    @Listener(order = Order.FIRST)
    fun onInteractEntity(event: InteractEntityEvent, @Getter("getTargetEntity") entity: Entity) {
        event.isCancelled = entity is Player && isViewer(entity.uniqueId) || event.cause.any { it is Player && isViewer(it.uniqueId) }
    }

    @Listener(order = Order.FIRST)
    fun onDamageEntity(event: DamageEntityEvent) {
        event.isCancelled = event.targetEntity.let { it is Player && isViewer(it.uniqueId) } || event.cause.any { it is Player && isViewer(it.uniqueId) }
    }

    @Listener(order = Order.FIRST)
    fun onInteractItem(event: InteractItemEvent.Secondary, @Root player: Player) {
        val blockList = asList(
            BOW,
            ENDER_PEARL,
            ENDER_EYE,
            SNOWBALL,
            WATER_BUCKET,
            LAVA_BUCKET,
            BUCKET,
            SPLASH_POTION,
            LINGERING_POTION,
            FISHING_ROD,
            BOAT,
            ACACIA_BOAT,
            BIRCH_BOAT,
            DARK_OAK_BOAT,
            JUNGLE_BOAT,
            SPRUCE_BOAT,
            MAP,
            EXPERIENCE_BOTTLE,
            CHORUS_FRUIT,
            DRAGON_BREATH
        )

        if (isViewer(player.uniqueId) && (event.itemStack.type in blockList || event.itemStack[DataItemType.key].isPresent)) {
            event.isCancelled = true
        }
    }

    @Listener(order = Order.FIRST)
    fun onCollideEntity(event: CollideEntityEvent) {
        if (event.cause.firstOrNull().let { it is Player && isViewer(it.uniqueId) }) {
            event.isCancelled = true
        } else {
            event.entities.removeIf { it is Player && isViewer(it.uniqueId) }
        }
    }

// TODO add more Listener
}
