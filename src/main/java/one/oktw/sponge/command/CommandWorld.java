package one.oktw.sponge.command;

import one.oktw.sponge.Main;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Optional;

public class CommandWorld implements CommandBase {
    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .executor(this)
                .description(Text.of("傳送到自己的世界"))
                .permission("oktw.command.world")
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Server server = Sponge.getServer();

        if (src instanceof Player) {
            Player player = (Player) src;
            Optional<WorldProperties> propertiesOptional = server.getWorldProperties(player.getUniqueId().toString());

            if (propertiesOptional.isPresent()) {
                WorldProperties properties = propertiesOptional.get();

//                properties.setGenerateSpawnOnLoad(false);
//                server.saveWorldProperties(properties);
                Optional<World> worldOptional = server.loadWorld(properties);
                if (worldOptional.isPresent()) {
                    World world = worldOptional.get();
                    if (!player.setLocationSafely(world.getSpawnLocation())) {
                        player.setLocation(Sponge.getGame().getTeleportHelper().getSafeLocation(world.getSpawnLocation(), 255, 9).get());
                    }
                    return CommandResult.affectedEntities(1);
                }
            } else {
                player.sendMessages(Text.of(TextColors.RED, "個人世界不存在\n")
                        .concat(Text.of(TextColors.AQUA, TextStyles.UNDERLINE, TextActions.runCommand("/create"), "立即創建")));
            }
        } else {
            src.sendMessage(Text.of("Player Only Command!"));
        }
        return CommandResult.empty();
    }
}
