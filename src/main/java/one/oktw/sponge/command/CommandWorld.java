package one.oktw.sponge.command;

import one.oktw.sponge.command.world.create;
import one.oktw.sponge.command.world.home;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class CommandWorld implements CommandBase {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Sponge.getCommandManager().process(src, "help world");
        return CommandResult.success();
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .executor(this)
                .description(Text.of("世界管理指令"))
                .permission("oktw.command.world")
                .child(new create().getSpec(), "create")
                .child(new home().getSpec(), "home")
                .build();
    }
}
