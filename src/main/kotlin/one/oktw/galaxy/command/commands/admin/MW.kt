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

package one.oktw.galaxy.command.commands.admin

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.RegistryKey
import one.oktw.galaxy.mixin.interfaces.MultiWorldMinecraftServer

class MW {
    val command: LiteralArgumentBuilder<ServerCommandSource> = CommandManager.literal("mw")
        .then(CommandManager.argument("name", IdentifierArgumentType.identifier())
            .suggests { context, builder ->
                context.source.minecraftServer.worldRegistryKeys.forEach { builder.suggest(it.value.toString()) }
                builder.buildFuture()
            }
            .executes(::execute)
        )


    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.player
        val id = IdentifierArgumentType.getIdentifier(context, "name")

        (player.server as MultiWorldMinecraftServer).createWorld(id)
        player.server.getWorld(RegistryKey.of(Registry.WORLD_KEY, id)).let(player::moveToWorld)

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
