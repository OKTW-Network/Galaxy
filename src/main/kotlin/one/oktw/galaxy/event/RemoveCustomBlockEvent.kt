package one.oktw.galaxy.event

import com.google.inject.Inject
import one.oktw.galaxy.Main.Companion.main
import org.slf4j.Logger
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.impl.AbstractEvent
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

class RemoveCustomBlockEvent(
    val location: Location<World>,
    cause: Cause
): AbstractEvent() {
    private var  myCause: Cause = cause

    override fun getCause(): Cause {
        return myCause
    }
}
