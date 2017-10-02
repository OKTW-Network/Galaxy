package one.oktw.sponge.command;

import one.oktw.sponge.Main;
import one.oktw.sponge.internal.WorldManager;
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
import org.spongepowered.api.world.storage.WorldProperties;

import javax.annotation.Nonnull;
import java.util.Optional;

public class CommandRebuild implements CommandBase {
    private Main main = Main.getMain();
    private WorldManager worldManager = main.getWorldManager();
    private Server server = Sponge.getServer();

    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .executor(this)
//                .arguments(
//                        GenericArguments.optional(GenericArguments.requiringPermission(GenericArguments.world(Text.of("world")), "oktw.command.world.CommandRebuild.other"))
//                )
                .description(Text.of("重新建構世界"))
                .permission("oktw.command.rebuild")
                .build();
    }

    @Nonnull
    @Override
    public CommandResult execute(@Nonnull CommandSource src, @Nonnull CommandContext args) throws CommandException {
        if (!args.hasAny("world") && src instanceof Player) {
            String name = ((Player) src).getUniqueId().toString();
            Optional<WorldProperties> optWorld = server.getWorldProperties(name);
            if (optWorld.isPresent()) {
                Text confirmText = Text.of(TextColors.RED, "確定要重建世界嗎？\n")
                        .concat(Text.of(TextColors.AQUA,
                                TextStyles.UNDERLINE,
                                TextActions.showText(Text.of(TextColors.RED, TextStyles.BOLD, "這將沒辦法還原！")),
                                TextActions.executeCallback(commandSource -> {
                                    worldManager.removeWorld(name);
                                    worldManager.createWorld(name);
                                    src.sendMessage(Text.of(TextColors.YELLOW, "重新創建成功！\n")
                                            .concat(Text.of(TextColors.AQUA, TextStyles.UNDERLINE, TextActions.runCommand("/world"), "傳送到您的世界")));
                                }),
                                "確定重建"));
                src.sendMessage(confirmText);
            } else {
                src.sendMessages(Text.of(TextColors.RED, "世界不存在"));
                return CommandResult.empty();
            }
        }
//        else {
//            Optional<WorldProperties> optWorld = args.getOne("world");
//            if (optWorld.isPresent()) {
//                originWorld = optWorld.get();
//            } else {
//                src.sendMessages(Text.of(TextColors.RED, "世界不存在"));
//            }
//            return CommandResult.empty();
//        }

        return CommandResult.success();
    }
}
