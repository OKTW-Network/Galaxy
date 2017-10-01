package one.oktw.sponge.command;

import one.oktw.sponge.Main;
import one.oktw.sponge.internal.WorldManager;
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

import javax.annotation.Nonnull;
import java.util.Optional;

public class CommandWorld implements CommandBase {
    private WorldManager worldManager = Main.getMain().getWorldManager();

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .executor(this)
                .description(Text.of("傳送到自己的世界"))
                .permission("oktw.command.world")
                .build();
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player player = (Player) src;
            Optional<World> worldOptional = worldManager.loadWorld(player.getUniqueId().toString());

            if (worldOptional.isPresent()) {
                World world = worldOptional.get();
                if (!player.setLocationSafely(world.getSpawnLocation())) {
                    player.setLocation(Sponge.getGame().getTeleportHelper().getSafeLocation(world.getSpawnLocation(), 255, 9).get());
                }
                return CommandResult.affectedEntities(1);
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
