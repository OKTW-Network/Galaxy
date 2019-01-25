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

package one.oktw.galaxy.galaxy.data

import one.oktw.galaxy.economy.StarDustKeeper
import one.oktw.galaxy.galaxy.planet.data.Planet
import one.oktw.galaxy.galaxy.traveler.data.Traveler
import java.util.*
import kotlin.collections.ArrayList

data class Galaxy(
    val uuid: UUID = UUID.randomUUID(),
    var name: String = "",
    var info: String = "",
    var notice: String = "",
    val members: ArrayList<Traveler> = ArrayList(),
    val planets: ArrayList<Planet> = ArrayList(),
    val joinRequest: ArrayList<UUID> = ArrayList(),
    override var interestRate: Double = 0.0004
) : StarDustKeeper()
