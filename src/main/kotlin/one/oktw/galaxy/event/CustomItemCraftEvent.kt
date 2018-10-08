package one.oktw.galaxy.event

import one.oktw.galaxy.galaxy.traveler.data.Traveler
import one.oktw.galaxy.item.type.Item
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.impl.AbstractEvent

class CustomItemCraftEvent(
    val item: Item,
    val player: Player,
    val traveler: Traveler,
    cause: Cause
): AbstractEvent() {
    private var myCause: Cause = cause

    override fun getCause(): Cause {
        return myCause
    }
}
