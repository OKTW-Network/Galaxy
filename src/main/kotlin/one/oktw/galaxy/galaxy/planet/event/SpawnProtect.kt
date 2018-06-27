package one.oktw.galaxy.galaxy.planet.event

import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.block.ChangeBlockEvent
import org.spongepowered.api.util.AABB

class SpawnProtect {
    @Listener
    fun onChangeBlock(event: ChangeBlockEvent) {
        event.filter {
            !it.extent.spawnLocation.blockPosition.run { AABB(add(2, 3, 2), sub(2, 1, 2)) }.contains(it.blockPosition)
        }
    }

    @Listener
    fun onChangeBlock(event: ChangeBlockEvent.Pre) {
        if (event.locations.any {
                it.extent.spawnLocation.blockPosition
                    .run { AABB(add(2, 3, 2), sub(2, 1, 2)) }
                    .contains(it.blockPosition)
            }) event.isCancelled = true
    }
}
