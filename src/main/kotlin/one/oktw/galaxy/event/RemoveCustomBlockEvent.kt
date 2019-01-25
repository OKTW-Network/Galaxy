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

package one.oktw.galaxy.event

import com.google.inject.Inject
import one.oktw.galaxy.Main.Companion.main
import org.slf4j.Logger
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.impl.AbstractEvent
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.world.Location
import org.spongepowered.api.world.World

class RemoveCustomBlockEvent(
    val location: Location<World>,
    cause: Cause
): AbstractEvent() {
    private var  myCause: Cause = cause

    override fun getCause(): Cause {
        return myCause
    }
}
