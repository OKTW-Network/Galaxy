package one.oktw.galaxy.internal

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.CommandTest
import one.oktw.galaxy.command.CommandUnStuck
import org.spongepowered.api.Sponge

class CommandRegister {
    private val logger = main.logger

    init {
        logger.info("Register command...")
        Sponge.getCommandManager().apply {
            register(main, CommandUnStuck().spec, "unstuck")
            register(main, CommandTest().spec, "test")
        }
    }
}
