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
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

public class CommandSpawn implements CommandBase {
    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .executor(this)
                .description(Text.of("傳送到世界重生點"))
                .arguments(GenericArguments.userOrSource(Text.of("Player")))
                .permission("oktw.command.home")
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player player = (Player) src;
            World world = player.getWorld();

            if (!player.setLocationSafely(world.getSpawnLocation())) {
                player.setLocation(Sponge.getGame().getTeleportHelper().getSafeLocation(world.getSpawnLocation(), 255, 9).get());
            }
        }

        return CommandResult.empty();
    }
}
