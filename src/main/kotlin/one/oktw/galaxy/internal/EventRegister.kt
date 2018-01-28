package one.oktw.galaxy.internal

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.event.TravelerWatcher
import org.spongepowered.api.Sponge

class EventRegister {
    init {
        main.logger.info("Registering Event...")
        Sponge.getEventManager().apply {
            registerListeners(main, TravelerWatcher())
        }
    }
}
