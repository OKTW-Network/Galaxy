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
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import one.oktw.galaxy.item.CustomItem

class GetItem {
    val command: LiteralArgumentBuilder<CommandSourceStack> = Commands.literal("getItem")
        .then(
            Commands.argument("item", ResourceLocationArgument.id())
                .suggests { _, builder ->
                    CustomItem.registry.getAll().keys.forEach { identifier ->
                        if (identifier.toString().contains(builder.remaining, ignoreCase = true)) {
                            builder.suggest(identifier.toString())
                        }
                    }
                    return@suggests builder.buildFuture()
                }
                .executes {
                    val identifier = ResourceLocationArgument.getId(it, "item")
                    val item = CustomItem.registry.get(identifier)

                    if (item == null) {
                        it.source.sendFailure(Component.translatable("argument.item.id.invalid", identifier))
                        return@executes 0
                    }

                    val itemStack = item.createItemStack()
                    val player = it.source.playerOrException
                    player.handleExtraItemsCreatedOnUse(itemStack.copy())
                    val pitch = ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f
                    player.level().playSound(null, player.x, player.y, player.z, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, pitch)
                    it.source.sendSuccess({ Component.translatable("commands.give.success.single", 1, itemStack.displayName, it.source.displayName) }, true)

                    return@executes Command.SINGLE_SUCCESS
                }
        )
}
