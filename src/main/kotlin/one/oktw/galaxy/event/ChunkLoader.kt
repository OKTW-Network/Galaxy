package one.oktw.galaxy.event

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.chunkLoaderManager
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.gui.ChunkLoader
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.entity.EnderCrystal
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.projectile.DamagingProjectile
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.entity.AttackEntityEvent
import org.spongepowered.api.event.entity.CollideEntityEvent
import org.spongepowered.api.event.entity.InteractEntityEvent
import org.spongepowered.api.event.entity.SpawnEntityEvent
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.event.world.ExplosionEvent

@Suppress("unused")
class ChunkLoader {
    @Listener
    fun onSpawnEntity(event: SpawnEntityEvent, @First player: Player) {
        val enderCrystal = event.entities.firstOrNull { it is EnderCrystal } as? EnderCrystal ?: return

        if (enderCrystal.location.add(0.0, -1.0, 0.0).blockType == BlockTypes.OBSIDIAN) {
            launch {
                val uuid = chunkLoaderManager.addChunkLoader(enderCrystal.location).uuid
                enderCrystal.offer(DataUUID(uuid))
            }
        }
    }

    @Listener
    @Suppress("UNUSED_PARAMETER")
    fun onInteractEntity(event: InteractEntityEvent.Secondary.MainHand, @First player: Player, @Getter("getTargetEntity") enderCrystal: EnderCrystal) {
        if (enderCrystal[DataUUID.key].isPresent) ChunkLoader(enderCrystal).open(player)
    }

    @Listener
    fun onAttackEntity(event: AttackEntityEvent, @Getter("getTargetEntity") enderCrystal: EnderCrystal) {
        if (enderCrystal[DataUUID.key].isPresent) {
            event.isCancelled = true
        }
    }

    @Listener
    fun onCollideEntity(event: CollideEntityEvent) {
        if (event.source is DamagingProjectile) event.filterEntities { !(it is EnderCrystal && it[DataUUID.key].isPresent) }
    }

    @Listener
    fun onExplosion(event: ExplosionEvent.Detonate) {
        event.filterEntities { !(it is EnderCrystal && it[DataUUID.key].isPresent) }
    }
}
