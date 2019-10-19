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

enum class BlockType(val customModelData: Int?, val languageKey: String, val hasGUI: Boolean = false) {
    DUMMY(0, ""),
    CONTROL_PANEL(null, "", true),
    PLANET_TERMINAL(null, "", true),
    HT_CRAFTING_TABLE(1010100, "block.HT_CRAFTING_TABLE", true),
    ELEVATOR(1010200, "block.ELEVATOR"),
    TELEPORTER_CORE_BASIC(1010300, "block.TELEPORTER", true),
    TELEPORTER_CORE_ADVANCE(1010301, "block.TELEPORTER_ADVANCED", true),
    TELEPORTER_FRAME(1010302, "block.TELEPORTER_FRAME")
}
