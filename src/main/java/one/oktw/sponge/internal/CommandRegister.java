package one.oktw.sponge.internal;

import one.oktw.sponge.Main;
import one.oktw.sponge.command.*;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;

public class CommandRegister {

    public CommandRegister() {
        Main plugin = Main.getMain();
        CommandManager commandManager = Sponge.getCommandManager();

        commandManager.register(plugin, new CommandCreate().getSpec(), "create");
        commandManager.register(plugin, new CommandWorld().getSpec(), "world");
        commandManager.register(plugin, new CommandRebuild().getSpec(), "rebuild");
        commandManager.register(plugin, new CommandHat().getSpec(), "hat");
        commandManager.register(plugin, new CommandSpawn().getSpec(), "spawn");
        commandManager.register(plugin, new CommandInvite().getSpec(), "invite");
        commandManager.register(plugin, new CommandRemove().getSpec(), "remove");
        commandManager.register(plugin, new CommandHub().getSpec(), "hub");
    }
}
