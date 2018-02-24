package one.oktw.galaxy.event

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.chunkLoaderManager
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.gui.ChunkLoader
import org.spongepowered.api.entity.EnderCrystal
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.entity.InteractEntityEvent
import org.spongepowered.api.event.entity.SpawnEntityEvent
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.filter.cause.First

@Suppress("unused")
class ChunkLoader {
    @Listener
    fun onSpawnEntity(
            event: SpawnEntityEvent,
            @First player: Player
    ) {
        val enderCrystal = event.entities.firstOrNull { it is EnderCrystal } as? EnderCrystal ?: return

        launch {
            val uuid = chunkLoaderManager.addChunkLoader(enderCrystal.location, 0)
            enderCrystal.offer(DataUUID(uuid))
        }
    }

    @Listener
    @Suppress("UNUSED_PARAMETER")
    fun onInteractEntity(
            event: InteractEntityEvent.Secondary.MainHand,
            @First player: Player,
            @Getter("getTargetEntity") enderCrystal: EnderCrystal
    ) {
        ChunkLoader(enderCrystal[DataUUID.key].orElse(null) ?: return).open(player)
    }
}
