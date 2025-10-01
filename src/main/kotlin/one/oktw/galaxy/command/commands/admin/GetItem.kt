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
import net.minecraft.command.argument.IdentifierArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import one.oktw.galaxy.item.CustomItem

class GetItem {
    val command: LiteralArgumentBuilder<ServerCommandSource> = CommandManager.literal("getItem")
        .then(
            CommandManager.argument("item", IdentifierArgumentType.identifier())
                .suggests { _, builder ->
                    CustomItem.registry.getAll().keys.forEach { identifier ->
                        if (identifier.toString().contains(builder.remaining, ignoreCase = true)) {
                            builder.suggest(identifier.toString())
                        }
                    }
                    return@suggests builder.buildFuture()
                }
                .executes {
                    val identifier = IdentifierArgumentType.getIdentifier(it, "item")
                    val item = CustomItem.registry.get(identifier)

                    if (item == null) {
                        it.source.sendError(Text.translatable("argument.item.id.invalid", identifier))
                        return@executes 0
                    }

                    val itemStack = item.createItemStack()
                    val player = it.source.playerOrThrow
                    if (player.inventory.insertStack(itemStack)) {
                        itemStack.count = 1
                        val itemEntity = player.dropItem(itemStack, false)
                        itemEntity?.setDespawnImmediately()

                        player.entityWorld.playSound(
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
                    it.source.sendFeedback({ Text.translatable("commands.give.success.single", 1, itemStack.toHoverableText(), it.source.displayName) }, true)

                    return@executes Command.SINGLE_SUCCESS
                }
        )
}
