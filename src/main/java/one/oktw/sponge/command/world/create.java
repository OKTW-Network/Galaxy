package one.oktw.sponge.command.world;

import one.oktw.sponge.Main;
import one.oktw.sponge.command.CommandBase;
import org.slf4j.Logger;
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
            Text transferText = Text.of(TextColors.AQUA, TextStyles.UNDERLINE, TextActions.runCommand("/world home"), "傳送到您的世界");

            if (Sponge.getServer().getWorldProperties(player.getUniqueId().toString()).isPresent()) {
                player.sendMessages(Text.of(TextColors.RED, "您已經擁有一個世界！\n").concat(transferText));
                return CommandResult.success();
            }

            try {
                main.getWorldManager().createWorld(player.getUniqueId().toString());
                player.sendMessages(Text.of(TextColors.YELLOW, "世界創建成功！\n").concat(transferText));
            } catch (IOException e) {
                src.sendMessages(Text.of(TextColors.RED, TextActions.showText(Text.of(e.getLocalizedMessage())), "創建世界失敗"));
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
