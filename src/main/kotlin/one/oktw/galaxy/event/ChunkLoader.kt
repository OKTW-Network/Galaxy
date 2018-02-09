package one.oktw.galaxy.event

import org.spongepowered.api.entity.EnderCrystal
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.entity.InteractEntityEvent
import org.spongepowered.api.event.entity.SpawnEntityEvent
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.text.Text

@Suppress("unused")
class ChunkLoader {
    @Listener
    fun onSpawnEntity(event: SpawnEntityEvent, @First player: Player) {
        val enderCrystal = event.entities.firstOrNull { it is EnderCrystal } as? EnderCrystal ?: return
        // TODO
    }

    @Listener
    @Suppress("UNUSED_PARAMETER")
    fun onInteractEntity(event: InteractEntityEvent.Secondary.MainHand, @First player: Player, @Getter("getTargetEntity") enderCrystal: EnderCrystal) {
        player.sendMessage(Text.of(enderCrystal.toString()))
        // TODO
    }
}