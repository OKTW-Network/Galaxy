package one.oktw.galaxy.player.event

import net.minecraft.util.math.BlockPos
import net.minecraft.world.WorldServer
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataItemType
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.effect.potion.PotionEffect
import org.spongepowered.api.effect.potion.PotionEffectTypes
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.living.player.Player
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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

@Suppress("unused")
class Viewer {
    companion object {
        private val viewer = ConcurrentHashMap.newKeySet<UUID>()

        fun setViewer(uuid: UUID) {
            viewer += uuid
        }

        fun isViewer(uuid: UUID): Boolean {
            return viewer.contains(uuid)
        }

        fun removeViewer(uuid: UUID) {
            viewer -= uuid
        }
    }

    init {
        Task.builder()
            .interval(1, TimeUnit.MINUTES)
            .execute { _ ->
                Sponge.getServer().onlinePlayers.forEach {
                    if (!isViewer(it.uniqueId)) return@forEach

                    it.transform(Keys.POTION_EFFECTS) {
                        (it ?: ArrayList()).apply { add(PotionEffect.of(PotionEffectTypes.SATURATION, 100, 1)) }
                    }
                }
            }
            .submit(main)
    }

    @Listener
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
            event.isCancelled = true
            event.targetBlock.location.ifPresent {
                (it.extent as WorldServer).playerChunkMap.markBlockForUpdate(BlockPos(it.blockX, it.blockY, it.blockZ))
            }
        }
    }

    @Listener(order = Order.FIRST)
    fun onInteractEntity(event: InteractEntityEvent, @Getter("getTargetEntity") entity: Entity) {
        val target = entity is Player && isViewer(entity.uniqueId)
        val source = event.cause.allOf(Player::class.java).any { isViewer(it.uniqueId) }

        if (target || source) event.isCancelled = true
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
    fun onDamageEntity(event: DamageEntityEvent) {
        val target = event.targetEntity is Player && isViewer(event.targetEntity.uniqueId)
        val source = event.cause.allOf(Player::class.java).any { isViewer(it.uniqueId) }

        if (target || source) event.isCancelled = true
    }

    @Listener(order = Order.FIRST)
    fun onCollideEntity(event: CollideEntityEvent) {
        if (event.cause.first(Player::class.java).orElse(null)?.let { isViewer(it.uniqueId) } == true) {
            event.isCancelled = true
        } else {
            event.filterEntities { it !is Player || !isViewer(it.uniqueId) }
        }
    }

    @Listener(order = Order.FIRST)
    fun onCollideBlock(event: CollideBlockEvent, @Root player: Player) {
        if (isViewer(player.uniqueId)) event.isCancelled = true
    }

// TODO add more Listener
}
