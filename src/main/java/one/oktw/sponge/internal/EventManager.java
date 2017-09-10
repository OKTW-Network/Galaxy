package one.oktw.sponge.internal;

import org.spongepowered.api.Sponge;

public class EventManager {

    public EventManager() {
        org.spongepowered.api.event.EventManager eventManager = Sponge.getEventManager();

//        eventManager.registerListeners(getCore(), new PlayerJoin());
//        eventManager.registerListeners(getCore(), new Event());
//        eventManager.registerListeners(getCore(), new EventExplosion());
//        eventManager.registerListeners(getCore(), new ChangeBlockFilter());
    }
}
