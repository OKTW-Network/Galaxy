/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2025
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
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import one.oktw.galaxy.mixin.accessor.AbilitiesAccessor

class FlySpeed {
    val command: LiteralArgumentBuilder<CommandSourceStack> = Commands.literal("flySpeed")
        .then(Commands.argument("speed", FloatArgumentType.floatArg(0.0f, 1.0f)).executes { context -> execute(context) })

    private fun execute(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.player ?: return com.mojang.brigadier.Command.SINGLE_SUCCESS

        (player.abilities as AbilitiesAccessor).setFlyingSpeed(context.getArgument("speed", Float::class.java))
        player.onUpdateAbilities()

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}


