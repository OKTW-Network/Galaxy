package one.oktw.galaxy.internal

import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.logger
import one.oktw.galaxy.event.DisablePortal
import one.oktw.galaxy.event.Disconnect
import one.oktw.galaxy.event.ForceGamemode
import org.spongepowered.api.Sponge

class EventRegister {
    init {
        val eventManager = Sponge.getEventManager()

        logger.info("Registering Event...")
        eventManager.registerListeners(Main, DisablePortal())
        eventManager.registerListeners(Main, ForceGamemode())
        eventManager.registerListeners(Main, Disconnect())
    }
}
