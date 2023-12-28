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

package one.oktw.galaxy.util

import com.google.gson.JsonParseException
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemStack.DISPLAY_KEY
import net.minecraft.item.ItemStack.LORE_KEY
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtList
import net.minecraft.nbt.NbtString
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.Texts
import net.minecraft.util.Formatting.DARK_PURPLE

class LoreEditor private constructor(val item: ItemStack) {
    companion object {
        // Edit ItemStack Lore
        fun ItemStack.loreEditor(block: LoreEditor.() -> Unit) {
            val builder = LoreEditor(this) // Receiver
            builder.block() // Run lambda block
            this.apply { builder.apply() } // Return Item
        }

        // Get ItemStack Lore
        fun ItemStack.getLoreTexts() = LoreEditor(this).getLoreList()
    }

    private val list = item.nbt?.getCompound(DISPLAY_KEY)?.getList(LORE_KEY, NbtElement.STRING_TYPE.toInt()) ?: NbtList()

    fun addText(text: Text): LoreEditor {
        this.list.add(NbtString.of(Text.Serialization.toJsonString(text)))
        return this
    }

    fun addText(texts: ArrayList<Text>): LoreEditor {
        for (text in texts) {
            addText(text)
        }
        return this
    }

    fun clear(): LoreEditor {
        this.list.clear()
        return this
    }

    private fun getLoreList(): List<Text> {
        val textList = ArrayList<Text>()
        for (i in 0 until list.size) {
            val string = list.getString(i) ?: continue
            try {
                val text: MutableText = Text.Serialization.fromJson(string) ?: continue
                textList.add(Texts.setStyleIfAbsent(text, Style.EMPTY.withColor(DARK_PURPLE).withItalic(true))) // default lore style
            } catch (e: JsonParseException) {
                continue // skip lore
            }
        }
        return textList
    }

    private fun apply(): ItemStack = item.apply { orCreateNbt.apply { getCompound(DISPLAY_KEY).apply { put(LORE_KEY, list) } } }
}
