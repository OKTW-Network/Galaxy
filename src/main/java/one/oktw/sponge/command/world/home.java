package one.oktw.sponge.command.world;

import one.oktw.sponge.command.CommandBase;
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

import java.util.Optional;

public class home implements CommandBase {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player player = (Player) src;

            Optional<World> worldOptional = Sponge.getServer().loadWorld(player.getUniqueId().toString());
            if (worldOptional.isPresent()) {
                World world = worldOptional.get();
                player.setLocationSafely(world.getSpawnLocation());
                return CommandResult.affectedEntities(1);
            } else {
                player.sendMessages(Text.of(TextColors.RED, "個人世界不存在\n")
                        .concat(Text.builder("立即創建")
                                .color(TextColors.AQUA)
                                .style(TextStyles.UNDERLINE)
                                .onClick(TextActions.suggestCommand("/world create"))
                                .build()));
                return CommandResult.empty();
            }
        } else {
            src.sendMessage(Text.of("Player Only Command!"));
            return CommandResult.empty();
        }
    }

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .executor(this)
                .description(Text.of("傳送到自己的世界"))
                .permission("oktw.command.world.home")
                .build();
    }
}
