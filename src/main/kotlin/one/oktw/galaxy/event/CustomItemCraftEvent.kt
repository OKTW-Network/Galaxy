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

package one.oktw.galaxy.event

import one.oktw.galaxy.galaxy.traveler.data.Traveler
import one.oktw.galaxy.item.type.Item
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.impl.AbstractEvent

class CustomItemCraftEvent(
    val item: Item,
    val player: Player,
    val traveler: Traveler,
    cause: Cause
): AbstractEvent() {
    private var myCause: Cause = cause

    override fun getCause(): Cause {
        return myCause
    }
}
