package one.oktw.sponge.internal;

import one.oktw.sponge.Main;
import one.oktw.sponge.event.DisablePortal;
import one.oktw.sponge.event.Disconnect;
import one.oktw.sponge.event.ForceGamemode;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.EventManager;

public class EventRegister {

    public EventRegister() {
        EventManager eventManager = Sponge.getEventManager();
        Main main = Main.getMain();
        Logger logger = main.getLogger();

        logger.info("Registering Event...");
        eventManager.registerListeners(main, new DisablePortal());
        eventManager.registerListeners(main, new ForceGamemode());
        eventManager.registerListeners(main, new Disconnect());

//        eventManager.registerListeners(getCore(), new PlayerJoin());
//        eventManager.registerListeners(getCore(), new EventExplosion());
//        eventManager.registerListeners(getCore(), new ChangeBlockFilter());
    }
}
