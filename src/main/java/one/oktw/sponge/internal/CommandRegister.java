package one.oktw.sponge.internal;

import one.oktw.sponge.Main;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;

public class CommandRegister {

    public CommandRegister() {
        Main plugin = Main.getMain();
        CommandManager commandManager = Sponge.getCommandManager();

//        commandManager.register(plugin, new PlayerInfo().init(), "playerinfo");
    }
}
