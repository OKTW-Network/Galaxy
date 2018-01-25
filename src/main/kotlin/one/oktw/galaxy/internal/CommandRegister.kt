package one.oktw.galaxy.internal

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.CommandTpa
import one.oktw.galaxy.command.CommandTpaHere
import one.oktw.galaxy.command.SaveMySelf
import one.oktw.galaxy.command.CommandTest
import org.spongepowered.api.Sponge

class CommandRegister {
    private val logger = main.logger

    init {
        logger.info("Register command...")
        Sponge.getCommandManager().apply {
            register(main, CommandTpa().spec, "tpa")
            register(main, CommandTpaHere().spec, "tpahere")
            register(main, SaveMySelf().spec, "savemyself")
            register(main, CommandTest().spec, "test")
        }
    }
}






