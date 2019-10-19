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

package one.oktw.galaxy.block.event

import one.oktw.galaxy.block.Block
import one.oktw.galaxy.block.type.BlockType
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerInteractBlockEvent
import one.oktw.galaxy.item.type.ItemType

class BlockEvents {
    @EventListener(true)
    fun onPlayerInteractBlock(event: PlayerInteractBlockEvent) {
        val position = event.packet.hitY.blockPos
        val placePosition = position.offset(event.packet.hitY.side)

        val hand = event.packet.hand

        val itemStack = event.player.getStackInHand(hand)

        val tag = itemStack.tag ?: return
        val itemType = tag.getString("customItemType") ?: return
        if (itemType != ItemType.BLOCK.name) return

        val itemBlock = Block(BlockType.valueOf(tag.getString("customBlockType") ?: return))

        //TODO Check for GUI, hand, sneak
        itemBlock.activate(placePosition)
    }
}
