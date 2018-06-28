package one.oktw.galaxy.internal.register

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.Admin
import one.oktw.galaxy.command.Sign
import one.oktw.galaxy.command.UnStuck
import org.spongepowered.api.Sponge

class CommandRegister {
    private val logger = main.logger

    init {
        logger.info("Register command...")
        Sponge.getCommandManager().apply {
            register(main, UnStuck().spec, "unstuck")
            register(main, Sign().spec, "sign")
            register(main, Admin().spec, "Admin")
        }
    }
}
