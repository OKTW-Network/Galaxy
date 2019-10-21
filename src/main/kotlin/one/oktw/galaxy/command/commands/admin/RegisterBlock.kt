/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2019
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

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraft.command.arguments.BlockPosArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandSource
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.block.Block
import one.oktw.galaxy.block.type.BlockType

class RegisterBlock {
    companion object {
        val command: LiteralArgumentBuilder<ServerCommandSource> = CommandManager.literal("registerBlock")
            .then(
                CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                    .then(RegisterBlock().block)
            )
    }


    private val block =
        CommandManager.argument("block", StringArgumentType.string())
            .suggests { _, builder ->
                val blocks: MutableList<String> = mutableListOf()
                BlockType.values().forEach { block ->
                    if (block.customModelData == null) {
                        blocks.add(block.name)
                    }
                }
                return@suggests CommandSource.suggestMatching(blocks, builder)
            }
            .executes { context ->
                registerBlock(
                    context.source,
                    Block(BlockType.valueOf(StringArgumentType.getString(context, "block"))),
                    BlockPosArgumentType.getLoadedBlockPos(context, "pos")
                )
            }

    private fun registerBlock(source: ServerCommandSource, block: Block, blockPos: BlockPos): Int {
        GlobalScope.launch {
            if (block.activate(source.player.serverWorld, blockPos)) {
                source.sendFeedback(LiteralText("Registered block at ${blockPos.x}, ${blockPos.y}, ${blockPos.z} to ${block.type.name}"), true)
            } else {
                source.sendFeedback(LiteralText("Registered Failed"), false)
            }
        }
        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
