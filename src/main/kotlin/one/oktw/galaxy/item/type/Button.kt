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

package one.oktw.galaxy.item.type

import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.item.ItemUtil.Companion.removeCoolDown
import one.oktw.galaxy.item.ItemUtil.Companion.removeDamage
import one.oktw.galaxy.item.enums.ButtonType
import one.oktw.galaxy.item.enums.ButtonType.BLANK
import one.oktw.galaxy.item.enums.ItemType.BUTTON
import org.bson.codecs.pojo.annotations.BsonDiscriminator
import org.spongepowered.api.data.key.Keys.*
import org.spongepowered.api.item.ItemTypes.DIAMOND_HOE
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.Text

@BsonDiscriminator
data class Button(val type: ButtonType = BLANK) : Item {
    override val itemType = BUTTON

    override fun createItemStack(): ItemStack = ItemStack.builder()
        .itemType(DIAMOND_HOE)
        .itemData(DataItemType(BUTTON))
        .add(DISPLAY_NAME, Text.EMPTY)
        .add(UNBREAKABLE, true)
        .add(HIDE_UNBREAKABLE, true)
        .add(HIDE_MISCELLANEOUS, true)
        .add(HIDE_ATTRIBUTES, true)
        .add(HIDE_ENCHANTMENTS, true)
        .add(ITEM_DURABILITY, type.id)
        .build()
        .let(::removeDamage)
        .let(::removeCoolDown)

    override fun test(item: ItemStack): Boolean {
        return item[DataItemType.key].orElse(null) == itemType && item[ITEM_DURABILITY].orElse(null) == type.id
    }

    override fun displayedItems() = listOfNotNull(createItemStack().createSnapshot())
}
