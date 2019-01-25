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

package one.oktw.galaxy.gui.view

import org.spongepowered.api.item.inventory.transaction.SlotTransaction

data class SlotAffected<EnumValue, Data>(
    // slot transaction
    val transaction: SlotTransaction,
    // type of the slot (only exist on gui slot)
    val type: EnumValue? = null,
    // index in the type (only exist on gui slot)
    val index: Int? = 0,
    // data associated with this slot (only may exist on gui slot)
    val data: Data? = null
)
