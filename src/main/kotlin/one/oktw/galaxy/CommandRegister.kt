package one.oktw.galaxy

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.*
import org.spongepowered.api.Sponge

class CommandRegister {
    private val logger = main.logger

    init {
        logger.info("Register command...")
        Sponge.getCommandManager().apply {
            register(main, UnStuck().spec, "unstuck")
            register(main, Test().spec, "test")
            register(main, TeleportHereAsk().spec, "tpahere")
            register(main, TeleportAsk().spec, "tpa")
            register(main, Hat().spec, "hat")
            register(main, Gun().spec, "gun")
        }
    }
}
