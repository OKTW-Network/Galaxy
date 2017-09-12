package one.oktw.sponge.command.world;

import one.oktw.sponge.Main;
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

import java.io.IOException;

public class create implements CommandBase {
    private Main main = Main.getMain();

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player player = (Player) src;
            Text transferText = Text.builder("傳送到您的世界")
                    .color(TextColors.AQUA)
                    .style(TextStyles.UNDERLINE)
                    .onClick(TextActions.suggestCommand("/world home"))
                    .build();

            if (Sponge.getServer().getWorldProperties(player.getUniqueId().toString()).isPresent()) {
                player.sendMessages(Text.of(TextColors.RED, "您已經擁有一個世界！\n").concat(transferText));
                return CommandResult.success();
            }

            try {
                main.getWorldManager().createWorld(player.getUniqueId().toString());
                player.sendMessages(Text.of(TextColors.YELLOW, "世界創建成功！\n").concat(transferText));
            } catch (IOException e) {
                player.sendMessages(Text.builder("創建世界失敗").color(TextColors.RED).onHover(TextActions.showText(Text.of(e.getLocalizedMessage()))).build());
            }
            return CommandResult.success();
        } else {
            src.sendMessage(Text.of("Player Only Command!"));
            return CommandResult.empty();
        }
    }

    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .executor(this)
                .description(Text.of("創造新的世界"))
                .permission("oktw.command.world.create")
                .build();
    }
}
