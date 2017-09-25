package one.oktw.sponge.command.world;

import one.oktw.sponge.Main;
import one.oktw.sponge.command.CommandBase;
import one.oktw.sponge.internal.WorldManager;
import org.slf4j.Logger;
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
import org.spongepowered.api.world.storage.WorldProperties;

import java.io.IOException;

public class rebuild implements CommandBase {
    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .executor(this)
                .arguments(
                        GenericArguments.optional(GenericArguments.requiringPermission(GenericArguments.world(Text.of("world")), "oktw.command.world.rebuild.other"))
                )
                .description(Text.of("重新建構世界"))
                .permission("oktw.command.world.rebuild")
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Main main = Main.getMain();
        Logger logger = main.getLogger();
        WorldManager worldManager = main.getWorldManager();
        Server server = Sponge.getServer();
        WorldProperties originWorld;

        if (!args.hasAny("world") && src instanceof Player) {
            originWorld = server.getWorldProperties(((Player) src).getUniqueId().toString()).get();
        } else {
            originWorld = (WorldProperties) args.getOne("world").get();
        }

        Text confirmText = Text.of(TextColors.RED, "確定要重建世界嗎？\n")
                .concat(Text.of(TextColors.AQUA,
                        TextStyles.UNDERLINE,
                        TextActions.showText(Text.of(TextColors.RED, TextStyles.BOLD, "這將沒辦法還原！")),
                        TextActions.executeCallback(commandSource -> {
                            worldManager.removeWorld(originWorld.getWorldName());
                            try {
                                worldManager.createWorld(originWorld.getWorldName());
                            } catch (IOException e) {
                                src.sendMessages(Text.of(TextColors.RED, TextActions.showText(Text.of(e.getLocalizedMessage())), "創建世界失敗"));
                            }
                        }),
                        "確定重建"));
        src.sendMessage(confirmText);

        return CommandResult.success();
    }
}
