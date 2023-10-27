/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2022
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

class Gui private constructor(id: String, modelData: Int) : CustomItem(Identifier("galaxy", "item/gui/base/$id"), DIAMOND_HOE, modelData) {
    companion object {
        val BLANK = registry.register(Gui("blank", 1000000))
        val MAIN_FIELD = registry.register(Gui("main_field", 1010000))
        val MAIN_FIELD_ENCLOSED = registry.register(Gui("main_field_enclosed", 1010001))
        val MAIN_FIELD_TOP_BOTTOM_LEFT = registry.register(Gui("main_field_top_bottom_left", 1010002))
        val MAIN_FIELD_TOP_BOTTOM_RIGHT = registry.register(Gui("main_field_top_bottom_right", 1010003))
        val MAIN_FIELD_TOP_LEFT_RIGHT = registry.register(Gui("main_field_top_left_right", 1010004))
        val MAIN_FIELD_BOTTOM_LEFT_RIGHT = registry.register(Gui("main_field_bottom_left_right", 1010005))
        val MAIN_FIELD_TOP_BOTTOM = registry.register(Gui("main_field_top_bottom", 1010006))
        val MAIN_FIELD_TOP_LEFT = registry.register(Gui("main_field_top_left", 1010007))
        val MAIN_FIELD_TOP_RIGHT = registry.register(Gui("main_field_top_right", 1010008))
        val MAIN_FIELD_BOTTOM_LEFT = registry.register(Gui("main_field_bottom_left", 1010009))
        val MAIN_FIELD_BOTTOM_RIGHT = registry.register(Gui("main_field_bottom_right", 1010010))
        val MAIN_FIELD_LEFT_RIGHT = registry.register(Gui("main_field_left_right", 1010011))
        val MAIN_FIELD_TOP = registry.register(Gui("main_field_top", 1010012))
        val MAIN_FIELD_BOTTOM = registry.register(Gui("main_field_bottom", 1010013))
        val MAIN_FIELD_LEFT = registry.register(Gui("main_field_left", 1010014))
        val MAIN_FIELD_RIGHT = registry.register(Gui("main_field_right", 1010015))
        val MAIN_FIELD_TOP_LEFT_CORNER_BORDERED = registry.register(Gui("main_field_top_left_corner_bordered", 1010016))
        val MAIN_FIELD_TOP_RIGHT_CORNER_BORDERED = registry.register(Gui("main_field_top_right_corner_bordered", 1010017))
        val MAIN_FIELD_BOTTOM_LEFT_CORNER_BORDERED = registry.register(Gui("main_field_bottom_left_corner_bordered", 1010018))
        val MAIN_FIELD_BOTTOM_RIGHT_CORNER_BORDERED = registry.register(Gui("main_field_bottom_right_corner_bordered", 1010019))
        val MAIN_FIELD_CORNER_BORDERED = registry.register(Gui("main_field_corner_bordered", 1010020))
        val MAIN_FIELD_CORNER_BORDER_TOP_LEFT = registry.register(Gui("main_field_corner_border_top_left", 1010021))
        val MAIN_FIELD_CORNER_BORDER_TOP_RIGHT = registry.register(Gui("main_field_corner_border_top_right", 1010022))
        val MAIN_FIELD_CORNER_BORDER_BOTOOM_LEFT = registry.register(Gui("main_field_corner_border_botoom_left", 1010023))
        val MAIN_FIELD_CORNER_BORDER_BOTTOM_RIGHT = registry.register(Gui("main_field_corner_border_bottom_right", 1010024))
        val SUB_FIELD = registry.register(Gui("sub_field", 1020000))
        val SUB_FIELD_ENCLOSED = registry.register(Gui("sub_field_enclosed", 1020001))
        val SUB_FIELD_TOP_BOTTOM_LEFT = registry.register(Gui("sub_field_top_bottom_left", 1020002))
        val SUB_FIELD_TOP_BOTTOM_RIGHT = registry.register(Gui("sub_field_top_bottom_right", 1020003))
        val SUB_FIELD_TOP_LEFT_RIGHT = registry.register(Gui("sub_field_top_left_right", 1020004))
        val SUB_FIELD_BOTTOM_LEFT_RIGHT = registry.register(Gui("sub_field_bottom_left_right", 1020005))
        val SUB_FIELD_TOP_BOTTOM = registry.register(Gui("sub_field_top_bottom", 1020006))
        val SUB_FIELD_TOP_LEFT = registry.register(Gui("sub_field_top_left", 1020007))
        val SUB_FIELD_TOP_RIGHT = registry.register(Gui("sub_field_top_right", 1020008))
        val SUB_FIELD_BOTTOM_LEFT = registry.register(Gui("sub_field_bottom_left", 1020009))
        val SUB_FIELD_BOTTOM_RIGHT = registry.register(Gui("sub_field_bottom_right", 1020010))
        val SUB_FIELD_LEFT_RIGHT = registry.register(Gui("sub_field_left_right", 1020011))
        val SUB_FIELD_TOP = registry.register(Gui("sub_field_top", 1020012))
        val SUB_FIELD_BOTTOM = registry.register(Gui("sub_field_bottom", 1020013))
        val SUB_FIELD_LEFT = registry.register(Gui("sub_field_left", 1020014))
        val SUB_FIELD_RIGHT = registry.register(Gui("sub_field_right", 1020015))
        val SUB_FIELD_TOP_LEFT_CORNER_BORDERED = registry.register(Gui("sub_field_top_left_corner_bordered", 1020016))
        val SUB_FIELD_TOP_RIGHT_CORNER_BORDERED = registry.register(Gui("sub_field_top_right_corner_bordered", 1020017))
        val SUB_FIELD_BOTTOM_LEFT_CORNER_BORDERED = registry.register(Gui("sub_field_bottom_left_corner_bordered", 1020018))
        val SUB_FIELD_BOTTOM_RIGHT_CORNER_BORDERED = registry.register(Gui("sub_field_bottom_right_corner_bordered", 1020019))
        val SUB_FIELD_CORNER_BORDERED = registry.register(Gui("sub_field_corner_bordered", 1020020))
        val SUB_FIELD_CORNER_BORDER_TOP_LEFT = registry.register(Gui("sub_field_corner_border_top_left", 1020021))
        val SUB_FIELD_CORNER_BORDER_TOP_RIGHT = registry.register(Gui("sub_field_corner_border_top_right", 1020022))
        val SUB_FIELD_CORNER_BORDER_BOTOOM_LEFT = registry.register(Gui("sub_field_corner_border_botoom_left", 1020023))
        val SUB_FIELD_CORNER_BORDER_BOTTOM_RIGHT = registry.register(Gui("sub_field_corner_border_bottom_right", 1020024))
        val INFO = registry.register(Gui("info", 1100000))
        val PROGRESS_RIGHT_0 = registry.register(Gui("progress_right_0", 1110000))
        val PROGRESS_RIGHT_1 = registry.register(Gui("progress_right_1", 1110001))
        val PROGRESS_RIGHT_2 = registry.register(Gui("progress_right_2", 1110002))
        val PROGRESS_RIGHT_3 = registry.register(Gui("progress_right_3", 1110003))
        val PROGRESS_RIGHT_4 = registry.register(Gui("progress_right_4", 1110004))
        val PROGRESS_RIGHT_5 = registry.register(Gui("progress_right_5", 1110005))
        val PROGRESS_RIGHT_6 = registry.register(Gui("progress_right_6", 1110006))
        val PROGRESS_RIGHT_7 = registry.register(Gui("progress_right_7", 1110007))
        val PROGRESS_RIGHT_8 = registry.register(Gui("progress_right_8", 1110008))
        val PROGRESS_RIGHT_9 = registry.register(Gui("progress_right_9", 1110009))
        val PROGRESS_RIGHT_10 = registry.register(Gui("progress_right_10", 1110010))
        val PROGRESS_RIGHT_11 = registry.register(Gui("progress_right_11", 1110011))
        val PROGRESS_RIGHT_12 = registry.register(Gui("progress_right_12", 1110012))
        val PROGRESS_RIGHT_13 = registry.register(Gui("progress_right_13", 1110013))
        val PROGRESS_RIGHT_14 = registry.register(Gui("progress_right_14", 1110014))
        val PROGRESS_RIGHT_15 = registry.register(Gui("progress_right_15", 1110015))
        val PROGRESS_RIGHT_16 = registry.register(Gui("progress_right_16", 1110016))
        val PROGRESS_RIGHT_17 = registry.register(Gui("progress_right_17", 1110017))
        val PROGRESS_RIGHT_18 = registry.register(Gui("progress_right_18", 1110018))
        val PROGRESS_RIGHT_19 = registry.register(Gui("progress_right_19", 1110019))
        val PROGRESS_RIGHT_20 = registry.register(Gui("progress_right_20", 1110020))
        val PROGRESS_RIGHT_21 = registry.register(Gui("progress_right_21", 1110021))
        val PROGRESS_RIGHT_22 = registry.register(Gui("progress_right_22", 1110022))
        val PROGRESS_RIGHT_BLOCKED = registry.register(Gui("progress_right_blocked", 1110023))
        val PROGRESS_DOWN_0 = registry.register(Gui("progress_down_0", 1120000))
        val PROGRESS_DOWN_1 = registry.register(Gui("progress_down_1", 1120001))
        val PROGRESS_DOWN_2 = registry.register(Gui("progress_down_2", 1120002))
        val PROGRESS_DOWN_3 = registry.register(Gui("progress_down_3", 1120003))
        val PROGRESS_DOWN_4 = registry.register(Gui("progress_down_4", 1120004))
        val PROGRESS_DOWN_5 = registry.register(Gui("progress_down_5", 1120005))
        val PROGRESS_DOWN_6 = registry.register(Gui("progress_down_6", 1120006))
        val PROGRESS_DOWN_7 = registry.register(Gui("progress_down_7", 1120007))
        val PROGRESS_DOWN_8 = registry.register(Gui("progress_down_8", 1120008))
        val PROGRESS_DOWN_9 = registry.register(Gui("progress_down_9", 1120009))
        val PROGRESS_DOWN_10 = registry.register(Gui("progress_down_10", 1120010))
        val PROGRESS_DOWN_11 = registry.register(Gui("progress_down_11", 1120011))
        val PROGRESS_DOWN_12 = registry.register(Gui("progress_down_12", 1120012))
        val PROGRESS_DOWN_BLOCKED = registry.register(Gui("progress_down_blocked", 1120013))
        val NUMBER_0 = registry.register(Gui("number_0", 1400050))
        val NUMBER_1 = registry.register(Gui("number_1", 1400051))
        val NUMBER_2 = registry.register(Gui("number_2", 1400052))
        val NUMBER_3 = registry.register(Gui("number_3", 1400053))
        val NUMBER_4 = registry.register(Gui("number_4", 1400054))
        val NUMBER_5 = registry.register(Gui("number_5", 1400055))
        val NUMBER_6 = registry.register(Gui("number_6", 1400056))
        val NUMBER_7 = registry.register(Gui("number_7", 1400057))
        val NUMBER_8 = registry.register(Gui("number_8", 1400058))
        val NUMBER_9 = registry.register(Gui("number_9", 1400059))
        val UNDERSCORE = registry.register(Gui("underscore", 1400060))
    }

    override fun getName(): Text = Text.literal("").styled { it.withItalic(false) }
}
