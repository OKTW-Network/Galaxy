package one.oktw.sponge.command;

import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

public interface CommandBase extends CommandExecutor {
    CommandSpec getSpec();
}
