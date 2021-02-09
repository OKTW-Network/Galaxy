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
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.BlockPosArgumentType
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.block.CustomBlock

class RegisterBlock {
    companion object {
        val command: LiteralArgumentBuilder<ServerCommandSource> = CommandManager.literal("registerBlock")
            .then(
                CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                    .then(RegisterBlock().block)
            )
    }


    private val block =
        CommandManager.argument("block", IdentifierArgumentType.identifier())
            .suggests { _, builder ->
                val blocks: Set<Identifier> = CustomBlock.registry.getAll().keys
                return@suggests CommandSource.suggestIdentifiers(blocks, builder)
            }
            .executes { context ->
                registerBlock(
                    context.source,
                    CustomBlock.registry.get(IdentifierArgumentType.getIdentifier(context, "block"))!!,
                    BlockPosArgumentType.getLoadedBlockPos(context, "pos")
                )
            }

    private fun registerBlock(source: ServerCommandSource, block: CustomBlock, blockPos: BlockPos): Int {
        source.world.setBlockEntity(blockPos, block.createBlockEntity())
        source.sendFeedback(LiteralText("Registered block at ${blockPos.x}, ${blockPos.y}, ${blockPos.z} to ${block.identifier}"), true)

        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
