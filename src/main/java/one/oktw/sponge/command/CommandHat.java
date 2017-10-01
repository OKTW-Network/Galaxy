package one.oktw.sponge.command;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class CommandHat implements CommandBase {
    @Override
    public CommandSpec getSpec() {
        return CommandSpec.builder()
                .executor(this)
                .description(Text.of("把手上的物品帶在頭上"))
                .permission("oktw.command.hat")
                .build();
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            Player player = (Player) src;
            Optional<ItemStack> optHat = player.getItemInHand(HandTypes.MAIN_HAND);

            if (optHat.isPresent()) {
                ItemStack hat = optHat.get();
                if (player.getHelmet().isPresent()) {
                    ItemStack originHat = player.getHelmet().get();
                    player.setHelmet(hat);
                    player.setItemInHand(HandTypes.MAIN_HAND, originHat);
                } else {
                    player.setHelmet(hat);
                    player.setItemInHand(HandTypes.MAIN_HAND, null);
                }
            } else {
                player.sendMessage(Text.of(TextColors.RED, "沒有物品在手上！"));
            }
        }

        return CommandResult.success();
    }
}
