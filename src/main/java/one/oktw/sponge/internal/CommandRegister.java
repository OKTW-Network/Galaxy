package one.oktw.sponge.internal;

import one.oktw.sponge.Main;
import one.oktw.sponge.command.CommandCreate;
import one.oktw.sponge.command.CommandHat;
import one.oktw.sponge.command.CommandHome;
import one.oktw.sponge.command.CommandRebuild;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;

public class CommandRegister {

    public CommandRegister() {
        Main plugin = Main.getMain();
        CommandManager commandManager = Sponge.getCommandManager();

        commandManager.register(plugin, new CommandCreate().getSpec(), "create");
        commandManager.register(plugin, new CommandHome().getSpec(), "home");
        commandManager.register(plugin, new CommandRebuild().getSpec(), "rebuild");
        commandManager.register(plugin, new CommandHat().getSpec(), "hat");
    }
}
