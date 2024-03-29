/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2022
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

package one.oktw.galaxy.event.type

import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket
import net.minecraft.server.network.ServerPlayerEntity

class PlayerInteractBlockEvent(val packet: PlayerInteractBlockC2SPacket, val player: ServerPlayerEntity) : CancelableEvent() {
    /**
     * Set swing will cancel event.
     */
    var swing = false
        set(swing) {
            if (!this.cancel) cancel = true // swing hand need cancel event.
            field = swing
        }
}
