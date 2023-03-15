/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2023
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
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.item.ItemStack
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import one.oktw.galaxy.item.CustomItem
import one.oktw.galaxy.item.Gun

class GetGun {
    // /admin getGun <item> [<heat>] <maxTemp> <cooling> <damage> <range> <through>
    private val throughArgument = CommandManager.argument("through", IntegerArgumentType.integer())
        .executes {
            val identifier = IdentifierArgumentType.getIdentifier(it, "item")
            val item = CustomItem.registry.get(identifier) as Gun?

            if (item == null) {
                it.source.sendError(Text.translatable("argument.item.id.invalid", identifier))
                return@executes 0
            }

            val itemStack = item.apply {
                weaponData.applyValue(
                    IntegerArgumentType.getInteger(it, "heat"),
                    IntegerArgumentType.getInteger(it, "maxTemp"),
                    DoubleArgumentType.getDouble(it, "cooling"),
                    DoubleArgumentType.getDouble(it, "damage"),
                    DoubleArgumentType.getDouble(it, "range"),
                    IntegerArgumentType.getInteger(it, "through")
                )
            }.createItemStack()
            return@executes sendItemToSource(it, itemStack)
        }

    private val rangeArgument = CommandManager.argument("range", DoubleArgumentType.doubleArg())
        .then(throughArgument)
    private val damageArgument = CommandManager.argument("damage", DoubleArgumentType.doubleArg())
        .then(rangeArgument)
    private val coolingArgument = CommandManager.argument("cooling", DoubleArgumentType.doubleArg())
        .then(damageArgument)
    private val maxTempArgument = CommandManager.argument("maxTemp", IntegerArgumentType.integer())
        .then(coolingArgument)
    private val heatArgument = CommandManager.argument("heat", IntegerArgumentType.integer())
        .then(maxTempArgument)

    val command: LiteralArgumentBuilder<ServerCommandSource> = CommandManager.literal("getGun")
        .then(
            CommandManager.argument("item", IdentifierArgumentType.identifier())
                .suggests { _, builder ->
                    CustomItem.registry.getAll().keys.forEach { identifier ->
                        if (CustomItem.registry.get(identifier) !is Gun) return@forEach
                        if (identifier.toString().contains(builder.remaining, ignoreCase = true)) {
                            builder.suggest(identifier.toString())
                        }
                    }
                    return@suggests builder.buildFuture()
                }
                .executes {
                    val identifier = IdentifierArgumentType.getIdentifier(it, "item")
                    val item = CustomItem.registry.get(identifier) as Gun?

                    if (item == null) {
                        it.source.sendError(Text.translatable("argument.item.id.invalid", identifier))
                        return@executes 0
                    }

                    return@executes sendItemToSource(it, item.createItemStack())
                }
                .then(heatArgument)
        )

    private fun sendItemToSource(it: CommandContext<ServerCommandSource>, itemStack: ItemStack): Int {
        val player = it.source.playerOrThrow
        if (player.inventory.insertStack(itemStack)) {
            itemStack.count = 1
            val itemEntity = player.dropItem(itemStack, false)
            itemEntity?.setDespawnImmediately()

            player.world.playSound(
                null,
                player.x,
                player.y,
                player.z,
                SoundEvents.ENTITY_ITEM_PICKUP,
                SoundCategory.PLAYERS,
                0.2F,
                ((player.random.nextFloat() - player.random.nextFloat()) * 0.7F + 1.0F) * 2.0F
            )
            player.playerScreenHandler.sendContentUpdates()
        } else {
            player.dropItem(itemStack, false)?.apply {
                resetPickupDelay()
                setOwner(player.uuid)
            }
        }
        it.source.sendFeedback(Text.translatable("commands.give.success.single", 1, itemStack.toHoverableText(), it.source.displayName), true)

        return Command.SINGLE_SUCCESS
    }
}
