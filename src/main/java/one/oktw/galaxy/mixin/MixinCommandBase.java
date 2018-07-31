package one.oktw.galaxy.mixin;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nullable;
import java.util.UUID;

@Mixin(CommandBase.class)
public class MixinCommandBase {
    /**
     * @author james58899
     * @reason Java 8 UUID.fromString vary slow
     */
    @Overwrite
    private static EntityPlayerMP getPlayer(MinecraftServer server, @Nullable EntityPlayerMP player, String input) throws CommandException {
        if (player == null) {
            if (input.length() == 36) { // test length match UUID
                try {
                    player = server.getPlayerList().getPlayerByUUID(UUID.fromString(input));
                } catch (IllegalArgumentException ignored) { /* ignored */ }
            } else {
                player = server.getPlayerList().getPlayerByUsername(input);
            }
        }

        if (player == null) throw new PlayerNotFoundException("commands.generic.player.notFound", input);

        return player;
    }
}
