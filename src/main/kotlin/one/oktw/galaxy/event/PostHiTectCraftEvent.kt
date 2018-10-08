package one.oktw.galaxy.event

import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.traveler.data.Traveler
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Cancellable
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.impl.AbstractEvent
import org.spongepowered.api.item.inventory.ItemStackSnapshot

class PostHiTectCraftEvent (
    val item: ItemStackSnapshot,
    val player: Player,
    val galaxy: Galaxy,
    val traveler: Traveler,
    cause: Cause
): AbstractEvent(), Cancellable {
    private var cancelled = false

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancelled || cancel
    }

    private var myCause: Cause = cause

    override fun getCause(): Cause {
        return myCause
    }
}
