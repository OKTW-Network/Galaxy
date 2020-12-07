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

package one.oktw.galaxy.item.util

import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.text.Text

class ItemLoreBuilder {
    private val list = ListTag()

    fun addText(text: Text): ItemLoreBuilder {
        this.list.add(StringTag.of(Text.Serializer.toJson(text)))
        return this
    }

    fun toTag(): ListTag = this.list
}
