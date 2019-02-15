/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2018
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

package one.oktw.galaxy.block.data

import one.oktw.galaxy.Main
import one.oktw.galaxy.block.enums.CustomBlocks
import one.oktw.galaxy.data.DataBlockType
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.item.ItemUtil.Companion.removeCoolDown
import one.oktw.galaxy.item.ItemUtil.Companion.removeDamage
import one.oktw.galaxy.item.enums.ItemType.BLOCK
import one.oktw.galaxy.item.type.Item
import org.spongepowered.api.data.key.Keys.*
import org.spongepowered.api.item.ItemTypes.WOODEN_SWORD
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.format.TextColors.BLUE
import org.spongepowered.api.text.format.TextStyles.BOLD

data class FakeBlockItem(private val block: CustomBlocks) : Item {
    private val lang = Main.translationService

    override val itemType = BLOCK

    override fun createItemStack(): ItemStack = ItemStack.builder()
        .itemType(WOODEN_SWORD)
        .itemData(DataItemType(BLOCK))
        .itemData(DataBlockType(block))
        .add(DISPLAY_NAME, lang.ofPlaceHolder(BOLD, BLUE, lang.of("block.${block.name}")))
        .add(UNBREAKABLE, true)
        .add(HIDE_UNBREAKABLE, true)
        .add(HIDE_MISCELLANEOUS, true)
        .add(HIDE_ATTRIBUTES, true)
        .add(HIDE_ENCHANTMENTS, true)
        .add(ITEM_DURABILITY, block.id!!)
        .build()
        .let(::removeDamage)
        .let(::removeCoolDown)

    override fun displayedItems() = listOfNotNull(createItemStack().createSnapshot())

    override fun test(itemStack: ItemStack): Boolean {
        return itemStack[DataItemType.key].orElse(null) == BLOCK && itemStack[ITEM_DURABILITY].orElse(null) == block.id
    }
}
