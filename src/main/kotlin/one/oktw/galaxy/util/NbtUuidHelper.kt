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

package one.oktw.galaxy.util

import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtIntArray
import net.minecraft.util.Uuids
import java.util.*

// From Minecraft 20w10a to 1.21.4
object NbtUuidHelper {
    /**
     * Serializes a [UUID] into its equivalent NBT representation.
     */
    fun fromUuid(uuid: UUID): NbtIntArray {
        return NbtIntArray(Uuids.toIntArray(uuid))
    }

    /**
     * Deserializes an NBT element into a [UUID].
     * The NBT element's data must have the same structure as the output of [.fromUuid].
     *
     * @throws IllegalArgumentException if `element` is not a valid representation of a UUID
     */
    fun toUuid(element: NbtElement): UUID {
        require(element.nbtType === NbtIntArray.TYPE) { "Expected UUID-Tag to be of type ${NbtIntArray.TYPE.crashReportName}, but found ${element.nbtType.crashReportName}." }
        val intArray = (element as NbtIntArray).intArray
        require(intArray.size == 4) { "Expected UUID-Array to be of length 4, but found ${intArray.size}." }
        return Uuids.toUuid(intArray)
    }
}
