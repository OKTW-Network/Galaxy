package one.oktw.galaxy.register

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.event.*
import org.spongepowered.api.Sponge

class EventRegister {
    init {
        main.logger.info("Registering Event...")
        Sponge.getEventManager().apply {
            registerListeners(main, TravelerWatcher())
            registerListeners(main, Viewer())
            registerListeners(main, Gun())
            registerListeners(main, ChunkLoader())
            registerListeners(main, ItemProtect())
            registerListeners(main, Armor())
        }
    }
}
