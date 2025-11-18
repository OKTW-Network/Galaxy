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

import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.commands.arguments.coordinates.BlockPosArgument
import net.minecraft.network.chat.Component
import one.oktw.galaxy.block.CustomBlock

class RegisterBlock {
    companion object {
        val command: LiteralArgumentBuilder<CommandSourceStack> = Commands.literal("registerBlock")
            .then(
                Commands.argument("pos", BlockPosArgument.blockPos())
                    .then(RegisterBlock().block)
            )
    }

    private val block =
        Commands.argument("block", ResourceLocationArgument.id())
            .suggests { _, builder ->
                CustomBlock.registry.getAll().keys.forEach { identifier ->
                    if (identifier.toString().contains(builder.remaining, ignoreCase = true)) {
                        builder.suggest(identifier.toString())
                    }
                }
                return@suggests builder.buildFuture()
            }
            .executes {
                val identifier = ResourceLocationArgument.getId(it, "block")
                val block = CustomBlock.registry.get(identifier)

                if (block == null) {
                    it.source.sendFailure(Component.translatable("argument.block.id.invalid", identifier))
                    return@executes 0
                }

                val blockPos = BlockPosArgument.getLoadedBlockPos(it, "pos")

                it.source.level.removeBlockEntity(blockPos)
                it.source.level.setBlockEntity(block.createBlockEntity(blockPos))
                it.source.sendSuccess({ Component.literal("Registered block at ${blockPos.x}, ${blockPos.y}, ${blockPos.z} to ${block.identifier}") }, true)

                return@executes Command.SINGLE_SUCCESS
            }
}
