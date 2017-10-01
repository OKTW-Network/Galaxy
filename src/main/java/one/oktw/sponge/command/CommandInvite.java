package one.oktw.sponge.command;

import one.oktw.sponge.Main;
import one.oktw.sponge.internal.WorldManager;
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

public class CommandInvite implements CommandBase {
    private Server server = Sponge.getServer();
    private WorldManager worldManager = Main.getMain().getWorldManager();

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .executor(this)
                .description(Text.of("邀請別人到自己所在的世界"))
                .arguments(GenericArguments.player(Text.of("Player")))
                .permission("oktw.command.invite")
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Player owner = (Player) src;
        Player player = (Player) args.getOne("Player").get();

        if (server.getWorldProperties(owner.getUniqueId().toString()).isPresent()) {
            player.sendMessages(
                    Text.of(TextColors.GREEN, owner.getName(), TextColors.YELLOW, "邀請您加入他的世界"),
                    Text.of(TextColors.AQUA,
                            TextStyles.UNDERLINE,
                            TextActions.showText(Text.of(TextColors.RED, "請勿隨意接受陌生人的邀請")),
                            TextActions.executeCallback(commandSource -> {
                                if (owner.getWorld().getName().equals(owner.getUniqueId().toString())) {
                                    player.setLocationSafely(owner.getLocation());
                                } else {
                                    World world = worldManager.loadWorld(owner.getUniqueId().toString()).get();
                                    if (!player.setLocationSafely(world.getSpawnLocation())) {
                                        player.setLocation(Sponge.getGame().getTeleportHelper().getSafeLocation(world.getSpawnLocation(), 255, 0).get());
                                    }
                                }
                            }),
                            "接受邀請"
                    )
            );

            owner.sendMessages(Text.of(TextColors.YELLOW, "已邀請至您的世界"));

            return CommandResult.success();
        } else {
            owner.sendMessages(Text.of(TextColors.RED, "您沒有世界"));
            return CommandResult.empty();
        }
    }
}
