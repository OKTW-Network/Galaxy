package one.oktw.galaxy.internal.register

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.Admin
import one.oktw.galaxy.command.Sign
import one.oktw.galaxy.command.Spawn
import org.spongepowered.api.Sponge

class CommandRegister {
    private val logger = main.logger

    init {
        logger.info("Register command...")
        Sponge.getCommandManager().apply {
            register(main, Sign().spec, "sign")
            register(main, Admin().spec, "admin")
            register(main, Spawn().spec, "spawn")
        }
    }
}
