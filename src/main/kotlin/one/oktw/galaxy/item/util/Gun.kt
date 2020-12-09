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

import net.minecraft.item.ItemStack
import net.minecraft.nbt.ListTag
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.*

class Gun(
    val heat: Int,
    val maxTemp: Int,
    val cooling: Int,
    val damage: Double,
    val range: Double,
    val through: Int,
    val uuid: UUID
) {
    companion object {
        fun fromItem(item: ItemStack): Gun? {
            val tag = item.tag ?: return null

            return Gun(
                tag.getInt("heat"),
                tag.getInt("maxTemp"),
                tag.getInt("cooling"),
                tag.getDouble("damage"),
                tag.getDouble("range"),
                tag.getInt("through"),
                tag.getUuid("gunUUID")
            )
        }
    }

    fun toLoreTag(): ListTag {
        return ItemLoreBuilder() // TODO Localize
            .addText(loreText("傷害", damage.toString()))
            .addText(loreText("射程", range.toString(), "B"))
            .addText(loreText("穿透", through.toString(), ""))
            .addText(loreText("積熱", heat.toString(), "K/shot"))
            .addText(loreText("耐熱", maxTemp.toString(), "K"))
            .addText(loreText("冷卻", cooling.toString(), "K/t"))
            .toTag()
    }

    private fun loreText(key: String, value: String, unit: String = ""): Text =
        LiteralText(key).styled { it.withColor(Formatting.AQUA) }
            .append(LiteralText(": ").styled { it.withColor(Formatting.DARK_GRAY) })
            .append(LiteralText(value).styled { it.withColor(Formatting.GRAY) })
            .append(LiteralText(unit).styled { it.withColor(Formatting.DARK_GRAY) })
            .styled { it.withItalic(false) }
}
