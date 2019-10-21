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
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandSource
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.TranslatableText
import one.oktw.galaxy.block.Block
import one.oktw.galaxy.block.type.BlockType
import one.oktw.galaxy.item.*
import one.oktw.galaxy.item.type.*

class GetItem {
    companion object {
        val command: LiteralArgumentBuilder<ServerCommandSource> = CommandManager.literal("getItem")
            .then(GetItem().button)
            .then(GetItem().gui)
            .then(GetItem().material)
            .then(GetItem().tool)
            .then(GetItem().upgrade)
            .then(GetItem().weapon)
            .then(GetItem().block)
    }

    private val button =
        CommandManager.literal("button")
            .then(
                CommandManager.argument("item", StringArgumentType.string())
                    .suggests { _, builder ->
                        return@suggests CommandSource.suggestMatching(
                            ButtonType.values().map { button -> button.name },
                            builder
                        )
                    }
                    .executes { context ->
                        getItem(context.source, Button(ButtonType.valueOf(StringArgumentType.getString(context, "item"))))
                    }
            )

    private val gui =
        CommandManager.literal("gui")
            .then(
                CommandManager.argument("item", StringArgumentType.string())
                    .suggests { _, builder ->
                        return@suggests CommandSource.suggestMatching(
                            GuiType.values().map { gui -> gui.name },
                            builder
                        )
                    }
                    .executes { context ->
                        getItem(context.source, Gui(GuiType.valueOf(StringArgumentType.getString(context, "item"))))
                    }
            )

    private val material =
        CommandManager.literal("material")
            .then(
                CommandManager.argument("item", StringArgumentType.string())
                    .suggests { _, builder ->
                        return@suggests CommandSource.suggestMatching(
                            MaterialType.values().map { material -> material.name },
                            builder
                        )
                    }
                    .executes { context ->
                        getItem(context.source, Material(MaterialType.valueOf(StringArgumentType.getString(context, "item"))))
                    }
            )

    private val tool =
        CommandManager.literal("tool")
            .then(
                CommandManager.argument("item", StringArgumentType.string())
                    .suggests { _, builder ->
                        return@suggests CommandSource.suggestMatching(
                            ToolType.values().map { tool -> tool.name },
                            builder
                        )
                    }
                    .executes { context ->
                        getItem(context.source, Tool(ToolType.valueOf(StringArgumentType.getString(context, "item"))))
                    }
            )

    private val upgrade =
        CommandManager.literal("upgrade")
            .then(
                CommandManager.argument("item", StringArgumentType.string())
                    .suggests { _, builder ->
                        return@suggests CommandSource.suggestMatching(
                            UpgradeType.values().map { upgrade -> upgrade.name },
                            builder
                        )
                    }
                    .executes { context ->
                        getItem(context.source, Upgrade(UpgradeType.valueOf(StringArgumentType.getString(context, "item"))))
                    }
            )

    private val weapon =
        CommandManager.literal("weapon")
            .then(
                CommandManager.argument("item", StringArgumentType.string())
                    .suggests { _, builder ->
                        return@suggests CommandSource.suggestMatching(
                            WeaponType.values().map { weapon -> weapon.name },
                            builder
                        )
                    }
                    .executes { context ->
                        getItem(context.source, Weapon(WeaponType.valueOf(StringArgumentType.getString(context, "item"))))
                    }
            )

    private val block =
        CommandManager.literal("block")
            .then(
                CommandManager.argument("block", StringArgumentType.string())
                    .suggests { _, builder ->
                        val blocks: MutableList<String> = mutableListOf()
                        BlockType.values().forEach { block ->
                            if (block.customModelData != null) {
                                blocks.add(block.name)
                            }
                        }
                        return@suggests CommandSource.suggestMatching(blocks, builder)
                    }
                    .executes { context ->
                        getItem(context.source, Block(BlockType.valueOf(StringArgumentType.getString(context, "block"))).item!!)
                    }
            )

    private fun getItem(source: ServerCommandSource, item: Item): Int {
        val itemStack = item.createItemStack()

        val success = source.player.inventory.insertStack(itemStack)
        if (!success) {
            val itemEntity = source.player.dropItem(itemStack, false)
            if (itemEntity != null) {
                itemEntity.resetPickupDelay()
                itemEntity.owner = source.player.uuid
            }
        }
        source.sendFeedback(TranslatableText("commands.give.success.single", 1, itemStack.toHoverableText(), source.displayName), true)
        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}


