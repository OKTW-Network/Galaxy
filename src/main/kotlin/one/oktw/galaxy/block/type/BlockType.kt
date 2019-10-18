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

package one.oktw.galaxy.block.type

enum class BlockType(val itemModelData: Int?, val blockModelData: Int?, val languageKey: String) {
    DUMMY(0, 0, ""),
    CONTROL_PANEL(null, null, ""),
    PLANET_TERMINAL(null, null, ""),
    HT_CRAFTING_TABLE(1010100, 1010100, ""),
    ELEVATOR(1010200, 1010200, ""),
    TELEPORTER_CORE_BASIC(1010300, 1010300, ""),
    TELEPORTER_CORE_ADVANCE(1010301, 1010301, ""),
    TELEPORTER_FRAME(1010302, 1010302, "")
}
