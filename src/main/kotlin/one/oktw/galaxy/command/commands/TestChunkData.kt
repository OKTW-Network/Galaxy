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

package one.oktw.galaxy.command.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType
import net.minecraft.command.arguments.BlockPosArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.world.chunk.WorldChunk
import one.oktw.galaxy.command.Command
import one.oktw.galaxy.worldData.ExtendedChunk
import one.oktw.galaxy.worldData.TestChunkDataProvider

class TestChunkData: Command {
    companion object {
        val NOT_A_PLAYER = SimpleCommandExceptionType(LiteralText("Not a player"))
    }
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager
                .literal("testChunk")
                .executes {
                    execute(it, false)
                }
                .then(
                    CommandManager
                        .argument("position", BlockPosArgumentType.blockPos())
                        .executes {
                            execute(it, true)
                        }
                )
                .then(
                    CommandManager.literal("clear").executes {
                        executeClear(it)
                    }
                )
        )

    }

    fun execute (ctx: CommandContext<ServerCommandSource>, withPos: Boolean): Int {
        if (ctx.source.player == null) {
            throw NOT_A_PLAYER.create()
        }

        val pos = if (withPos) {
            BlockPosArgumentType.getLoadedBlockPos(ctx, "position")
        } else {
            ctx.source.player.blockPos
        }

        val chunk = ctx.source.player.serverWorld.getChunk(pos)
        chunk as ExtendedChunk

        chunk.getData(TestChunkDataProvider.instance).positions.add(
            Triple(pos.x, pos.y, pos.z)
        )

        if (chunk is WorldChunk) {
            chunk.markDirty()
        }

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }

    fun executeClear (ctx: CommandContext<ServerCommandSource>): Int {
        if (ctx.source.player == null) {
            throw NOT_A_PLAYER.create()
        }

        val pos = ctx.source.player.blockPos
        val chunk = ctx.source.player.serverWorld.getChunk(pos)

        chunk as ExtendedChunk
        chunk.getData(TestChunkDataProvider.instance).positions.clear()

        if (chunk is WorldChunk) {
            chunk.markDirty()
        }

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
