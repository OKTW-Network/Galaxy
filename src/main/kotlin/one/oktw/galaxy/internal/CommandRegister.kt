package one.oktw.galaxy.internal

import org.spongepowered.api.Sponge

class CommandRegister {
    init {
        Sponge.getCommandManager().apply {
            //            register(main, CommandTest().spec, "test")
        }
    }
}
