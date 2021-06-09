/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
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

package one.oktw.galaxy.command.commands

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import one.oktw.galaxy.command.Command
import one.oktw.galaxy.mixin.interfaces.MultiWorldMinecraftServer

class Test : Command {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("test")
                .executes { context ->
                    execute(context.source)
                }

        )
    }

    private fun execute(source: ServerCommandSource): Int {
        val player = source.player
        val id = Identifier("galaxy", "test")

        (player.server as MultiWorldMinecraftServer).createWorld(id)
        player.server.getWorld(RegistryKey.of(Registry.WORLD_KEY, id)).let(player::moveToWorld)

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
