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

package one.oktw.galaxy.worldData

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag

data class TestData (
    var positions: HashSet<Triple<Int, Int, Int>> = HashSet()
) {
    constructor (dataRoot: CompoundTag): this() {
        readNbt(dataRoot)
    }

    fun writeNbt (dataRoot: CompoundTag) {
        val nbtList = ListTag()

        positions.forEach {
            val pos = CompoundTag()
            pos.putInt("x", it.first)
            pos.putInt("y", it.second)
            pos.putInt("z", it.third)
            nbtList.add(pos)
        }

        dataRoot.put("positions", nbtList)
    }

    fun readNbt (dataRoot: CompoundTag) {
        if (!dataRoot.contains("positions")) {
            return
        }

        val newPositions = HashSet<Triple<Int, Int, Int>>()

        val nbtList = dataRoot.getList("positions", 10)

        nbtList.forEach {
            it as CompoundTag
            newPositions.add(
                Triple(
                    it.getInt("x"),
                    it.getInt("y"),
                    it.getInt("z")
                )
            )
        }

        positions = newPositions
    }
}
