package one.oktw.galaxy.internal.register

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.armor.event.Armor
import one.oktw.galaxy.block.event.FakeBlock
import one.oktw.galaxy.economy.event.EconomyEvent
import one.oktw.galaxy.item.event.Gun
import one.oktw.galaxy.item.event.ItemProtect
import one.oktw.galaxy.machine.chunkloader.ChunkLoader
import one.oktw.galaxy.traveler.event.Traveler
import one.oktw.galaxy.traveler.event.Viewer
import org.spongepowered.api.Sponge

class EventRegister {
    init {
        main.logger.info("Registering Event...")
        Sponge.getEventManager().apply {
            registerListeners(main, Traveler())
            registerListeners(main, Viewer())
            registerListeners(main, Gun())
            registerListeners(main, ChunkLoader())
            registerListeners(main, ItemProtect())
            registerListeners(main, Armor())
            registerListeners(main, EconomyEvent())
            registerListeners(main, FakeBlock())
        }
    }
}
