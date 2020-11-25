/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2020
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

import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import one.oktw.galaxy.mixin.accessor.PlayerAbilitiesAccessor

class FlySpeed {
    val command: LiteralArgumentBuilder<ServerCommandSource> = CommandManager.literal("flySpeed")
        .then(CommandManager.argument("speed", FloatArgumentType.floatArg(0.0f, 1.0f)).executes { context -> execute(context) })

    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        val player = context.source.player ?: return com.mojang.brigadier.Command.SINGLE_SUCCESS

        (player.abilities as PlayerAbilitiesAccessor).setFlySpeed(context.getArgument("speed", Float::class.java))
        player.sendAbilitiesUpdate()

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}


