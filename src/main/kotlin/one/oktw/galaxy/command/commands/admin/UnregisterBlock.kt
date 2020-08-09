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

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.command.arguments.BlockPosArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.block.util.BlockUtil

class UnregisterBlock {
    companion object {
        val command: LiteralArgumentBuilder<ServerCommandSource> = CommandManager.literal("unregisterBlock")
            .then(
                CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                    .executes { context ->
                        UnregisterBlock().unregisterBlock(
                            context.source,
                            BlockPosArgumentType.getLoadedBlockPos(context, "pos")
                        )
                    }
            )
    }

    private fun unregisterBlock(source: ServerCommandSource, blockPos: BlockPos): Int {
        if (BlockUtil.unregisterBlock(source.player.serverWorld, blockPos)) {
            source.sendFeedback(LiteralText("Unregistered block at ${blockPos.x}, ${blockPos.y}, ${blockPos.z}"), true)
        } else {
            source.sendFeedback(LiteralText("Unregistered Failed"), false)
        }
        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
