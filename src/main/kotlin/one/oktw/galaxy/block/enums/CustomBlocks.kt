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

package one.oktw.galaxy.block.enums

enum class CustomBlocks(val id: Int? = null, val hasGUI: Boolean = false) {
    DUMMY,
    CONTROL_PANEL(null, true),
    PLANET_TERMINAL(null, true),
    HT_CRAFTING_TABLE(1, true),
    ELEVATOR(2),
    TELEPORTER(3, true),
    TELEPORTER_ADVANCED(4, true),
    TELEPORTER_FRAME(5)
}
