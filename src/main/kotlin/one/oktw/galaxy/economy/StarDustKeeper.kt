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

package one.oktw.galaxy.economy

import kotlin.math.roundToLong

open class StarDustKeeper {
    open var interestRate = 0.0
    var starDust = 0L
        private set

    fun giveStarDust(number: Number): Boolean {
        val i = number.toLong()

        if (i < 0) return false

        starDust += i

        return true
    }

    fun changeStarDust(number: Number): Boolean {
        val i = number.toLong()

        if (i < 0) return false

        starDust = i

        return true
    }

    fun takeStarDust(number: Number): Boolean {
        val i = number.toLong()

        if (i < 0 || starDust < i) return false

        starDust -= i

        return true
    }

    fun giveInterest() {
        starDust += (starDust * interestRate).roundToLong()
    }
}
