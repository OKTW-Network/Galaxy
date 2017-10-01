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
import org.spongepowered.api.text.format.TextColors;

public class CommandRemove implements CommandBase {
    private Server server = Sponge.getServer();

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .executor(this)
                .description(Text.of("將別人從自己的世界中踢出"))
                .arguments(GenericArguments.player(Text.of("Player")))
                .permission("oktw.command.remove")
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player owner = (Player) src;
        Player player = (Player) args.getOne("Player").get();

        if (player.getWorld().getName().equals(owner.getUniqueId().toString())) {
            player.setLocationSafely(server.getWorld(server.getDefaultWorldName()).get().getSpawnLocation());
            player.sendMessages(Text.of(TextColors.RED, "您已被世界擁有者踢出世界"));
            owner.sendMessages(Text.of(TextColors.YELLOW, "已將", TextColors.GREEN, player.getName(), TextColors.YELLOW, "踢出您的世界"));
        } else {
            owner.sendMessages(Text.of(TextColors.RED, "該玩家不在您的世界"));
        }

        return CommandResult.success();
    }
}
