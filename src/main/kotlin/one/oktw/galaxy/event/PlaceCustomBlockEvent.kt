package one.oktw.galaxy.event

import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.impl.AbstractEvent
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

class PlaceCustomBlockEvent(
    val location: Location<World>,
    val item: ItemStack,
    cause: Cause
): AbstractEvent() {
    private var myCause: Cause = cause

    override fun getCause(): Cause {
        return myCause
    }
}
