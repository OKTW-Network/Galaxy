/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2018
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
                } catch (IllegalArgumentException ex) {
                    player = server.getPlayerList().getPlayerByUsername(input);
                }
            } else {
                player = server.getPlayerList().getPlayerByUsername(input);
            }
        }

        if (player == null) throw new PlayerNotFoundException("commands.generic.player.notFound", input);

        return player;
    }
}
