package one.oktw.galaxy.economy.event

import net.minecraftforge.common.MinecraftForge
import one.oktw.galaxy.economy.service.EconomyService
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameStartedServerEvent

class EconomyEvent {
    @Listener
    fun onStarted(event: GameStartedServerEvent) {
        // Init service
        EconomyService

        // register forge event
        MinecraftForge.EVENT_BUS.register(TravelerEvent())
    }
}
