package one.oktw.galaxy

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.event.ChunkLoader
import one.oktw.galaxy.event.Gun
import one.oktw.galaxy.event.TravelerWatcher
import one.oktw.galaxy.event.Viewer
import org.spongepowered.api.Sponge

class EventRegister {
    init {
        main.logger.info("Registering Event...")
        Sponge.getEventManager().apply {
            registerListeners(main, TravelerWatcher())
            registerListeners(main, Viewer())
//            registerListeners(main, Stardust())
            registerListeners(main, Gun())
            registerListeners(main, ChunkLoader())
        }
    }
}
