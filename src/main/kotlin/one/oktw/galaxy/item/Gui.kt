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
        val BLANK = registry.register(Gui("blank", 1001000))
        val EXTEND = registry.register(Gui("extend", 1005000))
        val TUBE_END_TOP = registry.register(Gui("tube_end_top", 1003000))
        val TUBE_VERTICAL_CENTER = registry.register(Gui("tube_vertical_center", 1003001))
        val TUBE_END_BOTTOM = registry.register(Gui("tube_end_bottom", 1003002))
        val TUBE_END_LEFT = registry.register(Gui("tube_end_left", 1004000))
        val TUBE_HORIZONTAL_CENTER = registry.register(Gui("tube_horizontal_center", 1004001))
        val TUBE_END_RIGHT = registry.register(Gui("tube_end_right", 1004002))
        val EDGE_ALONE = registry.register(Gui("edge_alone", 1002000))
        val EDGE_LEFT = registry.register(Gui("edge_left", 1005001))
        val EDGE_CORNER_TOP_LEFT = registry.register(Gui("edge_corner_top_left", 1005002))
        val EDGE_TOP = registry.register(Gui("edge_top", 1005003))
        val EDGE_CORNER_TOP_RIGHT = registry.register(Gui("edge_corner_top_right", 1005004))
        val EDGE_RIGHT = registry.register(Gui("edge_right", 1005005))
        val EDGE_CORNER_BOTTOM_RIGHT = registry.register(Gui("edge_corner_bottom_right", 1005006))
        val EDGE_BOTTOM = registry.register(Gui("edge_bottom", 1005007))
        val EDGE_CORNER_BOTTOM_LEFT = registry.register(Gui("edge_corner_bottom_left", 1005008))
        val BEND_TOP_LEFT = registry.register(Gui("bend_top_left", 1006000))
        val BEND_TOP_RIGHT = registry.register(Gui("bend_top_right", 1006001))
        val BEND_BOTTOM_RIGHT = registry.register(Gui("bend_bottom_right", 1006002))
        val BEND_BOTTOM_LEFT = registry.register(Gui("bend_bottom_left", 1006003))
        val GAP_CENTER = registry.register(Gui("gap_center", 1007000))
        val GAP_TOP_LEFT = registry.register(Gui("gap_top_left", 1007001))
        val GAP_TOP_RIGHT = registry.register(Gui("gap_top_right", 1007002))
        val GAP_BOTTOM_RIGHT = registry.register(Gui("gap_bottom_right", 1007003))
        val GAP_BOTOOM_LEFT = registry.register(Gui("gap_botoom_left", 1007004))
        val NUM_0 = registry.register(Gui("num_0", 1008000))
        val NUM_1 = registry.register(Gui("num_1", 1008001))
        val NUM_2 = registry.register(Gui("num_2", 1008002))
        val NUM_3 = registry.register(Gui("num_3", 1008003))
        val NUM_4 = registry.register(Gui("num_4", 1008004))
        val NUM_5 = registry.register(Gui("num_5", 1008005))
        val NUM_6 = registry.register(Gui("num_6", 1008006))
        val NUM_7 = registry.register(Gui("num_7", 1008007))
        val NUM_8 = registry.register(Gui("num_8", 1008008))
        val NUM_9 = registry.register(Gui("num_9", 1008009))
        val NUM_NONE = registry.register(Gui("num_none", 1008010))
        val HORIZONTAL_PROGRESS_0 = registry.register(Gui("horizontal_progress_0", 1009000))
        val HORIZONTAL_PROGRESS_1 = registry.register(Gui("horizontal_progress_1", 1009001))
        val HORIZONTAL_PROGRESS_2 = registry.register(Gui("horizontal_progress_2", 1009002))
        val HORIZONTAL_PROGRESS_3 = registry.register(Gui("horizontal_progress_3", 1009003))
        val HORIZONTAL_PROGRESS_4 = registry.register(Gui("horizontal_progress_4", 1009004))
        val HORIZONTAL_PROGRESS_5 = registry.register(Gui("horizontal_progress_5", 1009005))
        val HORIZONTAL_PROGRESS_6 = registry.register(Gui("horizontal_progress_6", 1009006))
        val HORIZONTAL_PROGRESS_7 = registry.register(Gui("horizontal_progress_7", 1009007))
        val HORIZONTAL_PROGRESS_8 = registry.register(Gui("horizontal_progress_8", 1009008))
        val HORIZONTAL_PROGRESS_9 = registry.register(Gui("horizontal_progress_9", 1009009))
        val HORIZONTAL_PROGRESS_10 = registry.register(Gui("horizontal_progress_10", 1009010))
        val HORIZONTAL_PROGRESS_11 = registry.register(Gui("horizontal_progress_11", 1009011))
        val HORIZONTAL_PROGRESS_12 = registry.register(Gui("horizontal_progress_12", 1009012))
        val HORIZONTAL_PROGRESS_13 = registry.register(Gui("horizontal_progress_13", 1009013))
        val HORIZONTAL_PROGRESS_14 = registry.register(Gui("horizontal_progress_14", 1009014))
        val HORIZONTAL_PROGRESS_15 = registry.register(Gui("horizontal_progress_15", 1009015))
        val HORIZONTAL_PROGRESS_16 = registry.register(Gui("horizontal_progress_16", 1009016))
        val HORIZONTAL_PROGRESS_17 = registry.register(Gui("horizontal_progress_17", 1009017))
        val HORIZONTAL_PROGRESS_18 = registry.register(Gui("horizontal_progress_18", 1009018))
        val HORIZONTAL_PROGRESS_19 = registry.register(Gui("horizontal_progress_19", 1009019))
        val HORIZONTAL_PROGRESS_20 = registry.register(Gui("horizontal_progress_20", 1009020))
        val HORIZONTAL_PROGRESS_21 = registry.register(Gui("horizontal_progress_21", 1009021))
        val HORIZONTAL_PROGRESS_22 = registry.register(Gui("horizontal_progress_22", 1009022))
        val HORIZONTAL_PROGRESS_BLOCKED = registry.register(Gui("horizontal_progress_blocked", 1009023))
        val VERTICAL_PROGRESS_0 = registry.register(Gui("vertical_progress_0", 1010000))
        val VERTICAL_PROGRESS_1 = registry.register(Gui("vertical_progress_1", 1010001))
        val VERTICAL_PROGRESS_2 = registry.register(Gui("vertical_progress_2", 1010002))
        val VERTICAL_PROGRESS_3 = registry.register(Gui("vertical_progress_3", 1010003))
        val VERTICAL_PROGRESS_4 = registry.register(Gui("vertical_progress_4", 1010004))
        val VERTICAL_PROGRESS_5 = registry.register(Gui("vertical_progress_5", 1010005))
        val VERTICAL_PROGRESS_6 = registry.register(Gui("vertical_progress_6", 1010006))
        val VERTICAL_PROGRESS_7 = registry.register(Gui("vertical_progress_7", 1010007))
        val VERTICAL_PROGRESS_8 = registry.register(Gui("vertical_progress_8", 1010008))
        val VERTICAL_PROGRESS_9 = registry.register(Gui("vertical_progress_9", 1010009))
        val VERTICAL_PROGRESS_10 = registry.register(Gui("vertical_progress_10", 1010010))
        val VERTICAL_PROGRESS_11 = registry.register(Gui("vertical_progress_11", 1010011))
        val VERTICAL_PROGRESS_12 = registry.register(Gui("vertical_progress_12", 1010012))
        val VERTICAL_PROGRESS_BLOCKED = registry.register(Gui("vertical_progress_blocked", 1010013))
        val INFO = registry.register(Gui("info", 1011000))
        val HTCT_TAB_1 = registry.register(Gui("htct_tab_1", 1012000))
        val HTCT_TAB_2 = registry.register(Gui("htct_tab_2", 1012001))
        val HTCT_TAB_3 = registry.register(Gui("htct_tab_3", 1012002))
        val HTCT_TAB_4 = registry.register(Gui("htct_tab_4", 1012003))
        val HTCT_TAB_5 = registry.register(Gui("htct_tab_5", 1012004))
    }

    override fun getName(): Text = Text.literal("").styled { it.withItalic(false) }
}
