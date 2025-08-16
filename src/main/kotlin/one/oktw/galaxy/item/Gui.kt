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

package one.oktw.galaxy.item

import net.minecraft.item.Items.DIAMOND_HOE
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class Gui private constructor(id: String) : CustomItem(Identifier.of("galaxy", "item/gui/base/$id"), DIAMOND_HOE, hideTooltip = true) {
    companion object {
        val BLANK = registry.register(Gui("blank"))
        val MAIN_FIELD = registry.register(Gui("main_field"))
        val MAIN_FIELD_ENCLOSED = registry.register(Gui("main_field_enclosed"))
        val MAIN_FIELD_TOP_BOTTOM_LEFT = registry.register(Gui("main_field_top_bottom_left"))
        val MAIN_FIELD_TOP_BOTTOM_RIGHT = registry.register(Gui("main_field_top_bottom_right"))
        val MAIN_FIELD_TOP_LEFT_RIGHT = registry.register(Gui("main_field_top_left_right"))
        val MAIN_FIELD_BOTTOM_LEFT_RIGHT = registry.register(Gui("main_field_bottom_left_right"))
        val MAIN_FIELD_TOP_BOTTOM = registry.register(Gui("main_field_top_bottom"))
        val MAIN_FIELD_TOP_LEFT = registry.register(Gui("main_field_top_left"))
        val MAIN_FIELD_TOP_RIGHT = registry.register(Gui("main_field_top_right"))
        val MAIN_FIELD_BOTTOM_LEFT = registry.register(Gui("main_field_bottom_left"))
        val MAIN_FIELD_BOTTOM_RIGHT = registry.register(Gui("main_field_bottom_right"))
        val MAIN_FIELD_LEFT_RIGHT = registry.register(Gui("main_field_left_right"))
        val MAIN_FIELD_TOP = registry.register(Gui("main_field_top"))
        val MAIN_FIELD_BOTTOM = registry.register(Gui("main_field_bottom"))
        val MAIN_FIELD_LEFT = registry.register(Gui("main_field_left"))
        val MAIN_FIELD_RIGHT = registry.register(Gui("main_field_right"))
        val MAIN_FIELD_TOP_LEFT_CORNER_BORDERED = registry.register(Gui("main_field_top_left_corner_bordered"))
        val MAIN_FIELD_TOP_RIGHT_CORNER_BORDERED = registry.register(Gui("main_field_top_right_corner_bordered"))
        val MAIN_FIELD_BOTTOM_LEFT_CORNER_BORDERED = registry.register(Gui("main_field_bottom_left_corner_bordered"))
        val MAIN_FIELD_BOTTOM_RIGHT_CORNER_BORDERED = registry.register(Gui("main_field_bottom_right_corner_bordered"))
        val MAIN_FIELD_CORNER_BORDERED = registry.register(Gui("main_field_corner_bordered"))
        val MAIN_FIELD_CORNER_BORDER_TOP_LEFT = registry.register(Gui("main_field_corner_border_top_left"))
        val MAIN_FIELD_CORNER_BORDER_TOP_RIGHT = registry.register(Gui("main_field_corner_border_top_right"))
        val MAIN_FIELD_CORNER_BORDER_BOTOOM_LEFT = registry.register(Gui("main_field_corner_border_botoom_left"))
        val MAIN_FIELD_CORNER_BORDER_BOTTOM_RIGHT = registry.register(Gui("main_field_corner_border_bottom_right"))
        val SUB_FIELD = registry.register(Gui("sub_field"))
        val SUB_FIELD_ENCLOSED = registry.register(Gui("sub_field_enclosed"))
        val SUB_FIELD_TOP_BOTTOM_LEFT = registry.register(Gui("sub_field_top_bottom_left"))
        val SUB_FIELD_TOP_BOTTOM_RIGHT = registry.register(Gui("sub_field_top_bottom_right"))
        val SUB_FIELD_TOP_LEFT_RIGHT = registry.register(Gui("sub_field_top_left_right"))
        val SUB_FIELD_BOTTOM_LEFT_RIGHT = registry.register(Gui("sub_field_bottom_left_right"))
        val SUB_FIELD_TOP_BOTTOM = registry.register(Gui("sub_field_top_bottom"))
        val SUB_FIELD_TOP_LEFT = registry.register(Gui("sub_field_top_left"))
        val SUB_FIELD_TOP_RIGHT = registry.register(Gui("sub_field_top_right"))
        val SUB_FIELD_BOTTOM_LEFT = registry.register(Gui("sub_field_bottom_left"))
        val SUB_FIELD_BOTTOM_RIGHT = registry.register(Gui("sub_field_bottom_right"))
        val SUB_FIELD_LEFT_RIGHT = registry.register(Gui("sub_field_left_right"))
        val SUB_FIELD_TOP = registry.register(Gui("sub_field_top"))
        val SUB_FIELD_BOTTOM = registry.register(Gui("sub_field_bottom"))
        val SUB_FIELD_LEFT = registry.register(Gui("sub_field_left"))
        val SUB_FIELD_RIGHT = registry.register(Gui("sub_field_right"))
        val SUB_FIELD_TOP_LEFT_CORNER_BORDERED = registry.register(Gui("sub_field_top_left_corner_bordered"))
        val SUB_FIELD_TOP_RIGHT_CORNER_BORDERED = registry.register(Gui("sub_field_top_right_corner_bordered"))
        val SUB_FIELD_BOTTOM_LEFT_CORNER_BORDERED = registry.register(Gui("sub_field_bottom_left_corner_bordered"))
        val SUB_FIELD_BOTTOM_RIGHT_CORNER_BORDERED = registry.register(Gui("sub_field_bottom_right_corner_bordered"))
        val SUB_FIELD_CORNER_BORDERED = registry.register(Gui("sub_field_corner_bordered"))
        val SUB_FIELD_CORNER_BORDER_TOP_LEFT = registry.register(Gui("sub_field_corner_border_top_left"))
        val SUB_FIELD_CORNER_BORDER_TOP_RIGHT = registry.register(Gui("sub_field_corner_border_top_right"))
        val SUB_FIELD_CORNER_BORDER_BOTOOM_LEFT = registry.register(Gui("sub_field_corner_border_botoom_left"))
        val SUB_FIELD_CORNER_BORDER_BOTTOM_RIGHT = registry.register(Gui("sub_field_corner_border_bottom_right"))
        val INFO = registry.register(Gui("info"))
        val PROGRESS_RIGHT_0 = registry.register(Gui("progress_right_0"))
        val PROGRESS_RIGHT_1 = registry.register(Gui("progress_right_1"))
        val PROGRESS_RIGHT_2 = registry.register(Gui("progress_right_2"))
        val PROGRESS_RIGHT_3 = registry.register(Gui("progress_right_3"))
        val PROGRESS_RIGHT_4 = registry.register(Gui("progress_right_4"))
        val PROGRESS_RIGHT_5 = registry.register(Gui("progress_right_5"))
        val PROGRESS_RIGHT_6 = registry.register(Gui("progress_right_6"))
        val PROGRESS_RIGHT_7 = registry.register(Gui("progress_right_7"))
        val PROGRESS_RIGHT_8 = registry.register(Gui("progress_right_8"))
        val PROGRESS_RIGHT_9 = registry.register(Gui("progress_right_9"))
        val PROGRESS_RIGHT_10 = registry.register(Gui("progress_right_10"))
        val PROGRESS_RIGHT_11 = registry.register(Gui("progress_right_11"))
        val PROGRESS_RIGHT_12 = registry.register(Gui("progress_right_12"))
        val PROGRESS_RIGHT_13 = registry.register(Gui("progress_right_13"))
        val PROGRESS_RIGHT_14 = registry.register(Gui("progress_right_14"))
        val PROGRESS_RIGHT_15 = registry.register(Gui("progress_right_15"))
        val PROGRESS_RIGHT_16 = registry.register(Gui("progress_right_16"))
        val PROGRESS_RIGHT_17 = registry.register(Gui("progress_right_17"))
        val PROGRESS_RIGHT_18 = registry.register(Gui("progress_right_18"))
        val PROGRESS_RIGHT_19 = registry.register(Gui("progress_right_19"))
        val PROGRESS_RIGHT_20 = registry.register(Gui("progress_right_20"))
        val PROGRESS_RIGHT_21 = registry.register(Gui("progress_right_21"))
        val PROGRESS_RIGHT_22 = registry.register(Gui("progress_right_22"))
        val PROGRESS_RIGHT_BLOCKED = registry.register(Gui("progress_right_blocked"))
        val PROGRESS_DOWN_0 = registry.register(Gui("progress_down_0"))
        val PROGRESS_DOWN_1 = registry.register(Gui("progress_down_1"))
        val PROGRESS_DOWN_2 = registry.register(Gui("progress_down_2"))
        val PROGRESS_DOWN_3 = registry.register(Gui("progress_down_3"))
        val PROGRESS_DOWN_4 = registry.register(Gui("progress_down_4"))
        val PROGRESS_DOWN_5 = registry.register(Gui("progress_down_5"))
        val PROGRESS_DOWN_6 = registry.register(Gui("progress_down_6"))
        val PROGRESS_DOWN_7 = registry.register(Gui("progress_down_7"))
        val PROGRESS_DOWN_8 = registry.register(Gui("progress_down_8"))
        val PROGRESS_DOWN_9 = registry.register(Gui("progress_down_9"))
        val PROGRESS_DOWN_10 = registry.register(Gui("progress_down_10"))
        val PROGRESS_DOWN_11 = registry.register(Gui("progress_down_11"))
        val PROGRESS_DOWN_12 = registry.register(Gui("progress_down_12"))
        val PROGRESS_DOWN_BLOCKED = registry.register(Gui("progress_down_blocked"))
        val NUMBER_0 = registry.register(Gui("number_0"))
        val NUMBER_1 = registry.register(Gui("number_1"))
        val NUMBER_2 = registry.register(Gui("number_2"))
        val NUMBER_3 = registry.register(Gui("number_3"))
        val NUMBER_4 = registry.register(Gui("number_4"))
        val NUMBER_5 = registry.register(Gui("number_5"))
        val NUMBER_6 = registry.register(Gui("number_6"))
        val NUMBER_7 = registry.register(Gui("number_7"))
        val NUMBER_8 = registry.register(Gui("number_8"))
        val NUMBER_9 = registry.register(Gui("number_9"))
        val UNDERSCORE = registry.register(Gui("underscore"))
    }

    override fun getName(): Text = Text.literal("")
}
