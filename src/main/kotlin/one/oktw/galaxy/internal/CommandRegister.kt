package one.oktw.galaxy.internal

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.CommandGalaxy
import one.oktw.galaxy.command.CommandTpa
import one.oktw.galaxy.command.CommandTpaHere
import org.spongepowered.api.Sponge

class CommandRegister {
    init {
        Sponge.getCommandManager().apply {
//                        register(main, CommandTest().spec, "test")
            register(main, CommandGalaxy().spec, "galaxy")
            register(main, CommandTpa().spec, "tpa")
            register(main, CommandTpaHere().spec, "tpahere")
        }
    }
}






