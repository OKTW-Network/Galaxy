package one.oktw.sponge.command;

import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public class CommandHub implements CommandBase {
    private Server server = Sponge.getServer();

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .executor(this)
                .description(Text.of("回到伺服器入口"))
                .arguments(GenericArguments.playerOrSource(Text.of("Player")))
                .permission("oktw.command.hub")
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player player = (Player) args.getOne("Player").get();
        player.setLocationSafely(server.getWorld(server.getDefaultWorldName()).get().getSpawnLocation());

        return CommandResult.success();
    }
}
