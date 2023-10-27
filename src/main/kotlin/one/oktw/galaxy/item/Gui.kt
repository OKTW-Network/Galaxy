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
        val EXTEND = registry.register(Gui("extend", 1010000))
        val EDGE_ALONE = registry.register(Gui("edge_alone", 1010001))
        val TUBE_END_LEFT = registry.register(Gui("tube_end_left", 1010002))
        val TUBE_END_RIGHT = registry.register(Gui("tube_end_right", 1010003))
        val TUBE_END_TOP = registry.register(Gui("tube_end_top", 1010004))
        val TUBE_END_BOTTOM = registry.register(Gui("tube_end_bottom", 1010005))
        val TUBE_HORIZONTAL_CENTER = registry.register(Gui("tube_horizontal_center", 1010006))
        val EDGE_CORNER_TOP_LEFT = registry.register(Gui("edge_corner_top_left", 1010007))
        val EDGE_CORNER_TOP_RIGHT = registry.register(Gui("edge_corner_top_right", 1010008))
        val EDGE_CORNER_BOTTOM_LEFT = registry.register(Gui("edge_corner_bottom_left", 1010009))
        val EDGE_CORNER_BOTTOM_RIGHT = registry.register(Gui("edge_corner_bottom_right", 1010010))
        val TUBE_VERTICAL_CENTER = registry.register(Gui("tube_vertical_center", 1010011))
        val EDGE_TOP = registry.register(Gui("edge_top", 1010012))
        val EDGE_BOTTOM = registry.register(Gui("edge_bottom", 1010013))
        val EDGE_LEFT = registry.register(Gui("edge_left", 1010014))
        val EDGE_RIGHT = registry.register(Gui("edge_right", 1010015))
        val BEND_TOP_LEFT = registry.register(Gui("bend_top_left", 1010016))
        val BEND_TOP_RIGHT = registry.register(Gui("bend_top_right", 1010017))
        val BEND_BOTTOM_LEFT = registry.register(Gui("bend_bottom_left", 1010018))
        val BEND_BOTTOM_RIGHT = registry.register(Gui("bend_bottom_right", 1010019))
        val GAP_CENTER = registry.register(Gui("gap_center", 1010020))
        val GAP_TOP_LEFT = registry.register(Gui("gap_top_left", 1010021))
        val GAP_TOP_RIGHT = registry.register(Gui("gap_top_right", 1010022))
        val GAP_BOTOOM_LEFT = registry.register(Gui("gap_botoom_left", 1010023))
        val GAP_BOTTOM_RIGHT = registry.register(Gui("gap_bottom_right", 1010024))
        val INFO = registry.register(Gui("info", 1100000))
        val HORIZONTAL_PROGRESS_0 = registry.register(Gui("horizontal_progress_0", 1110000))
        val HORIZONTAL_PROGRESS_1 = registry.register(Gui("horizontal_progress_1", 1110001))
        val HORIZONTAL_PROGRESS_2 = registry.register(Gui("horizontal_progress_2", 1110002))
        val HORIZONTAL_PROGRESS_3 = registry.register(Gui("horizontal_progress_3", 1110003))
        val HORIZONTAL_PROGRESS_4 = registry.register(Gui("horizontal_progress_4", 1110004))
        val HORIZONTAL_PROGRESS_5 = registry.register(Gui("horizontal_progress_5", 1110005))
        val HORIZONTAL_PROGRESS_6 = registry.register(Gui("horizontal_progress_6", 1110006))
        val HORIZONTAL_PROGRESS_7 = registry.register(Gui("horizontal_progress_7", 1110007))
        val HORIZONTAL_PROGRESS_8 = registry.register(Gui("horizontal_progress_8", 1110008))
        val HORIZONTAL_PROGRESS_9 = registry.register(Gui("horizontal_progress_9", 1110009))
        val HORIZONTAL_PROGRESS_10 = registry.register(Gui("horizontal_progress_10", 1110010))
        val HORIZONTAL_PROGRESS_11 = registry.register(Gui("horizontal_progress_11", 1110011))
        val HORIZONTAL_PROGRESS_12 = registry.register(Gui("horizontal_progress_12", 1110012))
        val HORIZONTAL_PROGRESS_13 = registry.register(Gui("horizontal_progress_13", 1110013))
        val HORIZONTAL_PROGRESS_14 = registry.register(Gui("horizontal_progress_14", 1110014))
        val HORIZONTAL_PROGRESS_15 = registry.register(Gui("horizontal_progress_15", 1110015))
        val HORIZONTAL_PROGRESS_16 = registry.register(Gui("horizontal_progress_16", 1110016))
        val HORIZONTAL_PROGRESS_17 = registry.register(Gui("horizontal_progress_17", 1110017))
        val HORIZONTAL_PROGRESS_18 = registry.register(Gui("horizontal_progress_18", 1110018))
        val HORIZONTAL_PROGRESS_19 = registry.register(Gui("horizontal_progress_19", 1110019))
        val HORIZONTAL_PROGRESS_20 = registry.register(Gui("horizontal_progress_20", 1110020))
        val HORIZONTAL_PROGRESS_21 = registry.register(Gui("horizontal_progress_21", 1110021))
        val HORIZONTAL_PROGRESS_22 = registry.register(Gui("horizontal_progress_22", 1110022))
        val HORIZONTAL_PROGRESS_BLOCKED = registry.register(Gui("horizontal_progress_blocked", 1110023))
        val VERTICAL_PROGRESS_0 = registry.register(Gui("vertical_progress_0", 1120000))
        val VERTICAL_PROGRESS_1 = registry.register(Gui("vertical_progress_1", 1120001))
        val VERTICAL_PROGRESS_2 = registry.register(Gui("vertical_progress_2", 1120002))
        val VERTICAL_PROGRESS_3 = registry.register(Gui("vertical_progress_3", 1120003))
        val VERTICAL_PROGRESS_4 = registry.register(Gui("vertical_progress_4", 1120004))
        val VERTICAL_PROGRESS_5 = registry.register(Gui("vertical_progress_5", 1120005))
        val VERTICAL_PROGRESS_6 = registry.register(Gui("vertical_progress_6", 1120006))
        val VERTICAL_PROGRESS_7 = registry.register(Gui("vertical_progress_7", 1120007))
        val VERTICAL_PROGRESS_8 = registry.register(Gui("vertical_progress_8", 1120008))
        val VERTICAL_PROGRESS_9 = registry.register(Gui("vertical_progress_9", 1120009))
        val VERTICAL_PROGRESS_10 = registry.register(Gui("vertical_progress_10", 1120010))
        val VERTICAL_PROGRESS_11 = registry.register(Gui("vertical_progress_11", 1120011))
        val VERTICAL_PROGRESS_12 = registry.register(Gui("vertical_progress_12", 1120012))
        val VERTICAL_PROGRESS_BLOCKED = registry.register(Gui("vertical_progress_blocked", 1120013))
        val NUM_0 = registry.register(Gui("num_0", 1400050))
        val NUM_1 = registry.register(Gui("num_1", 1400051))
        val NUM_2 = registry.register(Gui("num_2", 1400052))
        val NUM_3 = registry.register(Gui("num_3", 1400053))
        val NUM_4 = registry.register(Gui("num_4", 1400054))
        val NUM_5 = registry.register(Gui("num_5", 1400055))
        val NUM_6 = registry.register(Gui("num_6", 1400056))
        val NUM_7 = registry.register(Gui("num_7", 1400057))
        val NUM_8 = registry.register(Gui("num_8", 1400058))
        val NUM_9 = registry.register(Gui("num_9", 1400059))
        val NUM_NONE = registry.register(Gui("num_none", 9999999))
        val HTCT_TAB_1 = registry.register(Gui("htct_tab_1", 9999999))
        val HTCT_TAB_2 = registry.register(Gui("htct_tab_2", 9999999))
        val HTCT_TAB_3 = registry.register(Gui("htct_tab_3", 9999999))
        val HTCT_TAB_4 = registry.register(Gui("htct_tab_4", 9999999))
        val HTCT_TAB_5 = registry.register(Gui("htct_tab_5", 9999999))
    }

    override fun getName(): Text = Text.literal("").styled { it.withItalic(false) }
}
