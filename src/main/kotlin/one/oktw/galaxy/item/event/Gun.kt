/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
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

package one.oktw.galaxy.item.event

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Hand
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.PlayerInteractItemEvent
import one.oktw.galaxy.event.type.PlayerSneakEvent
import one.oktw.galaxy.event.type.PlayerSneakReleaseEvent
import one.oktw.galaxy.item.util.Gun

class Gun {
    @EventListener(true)
    fun onPlayerInteractItem(event: PlayerInteractItemEvent) {
        var hand = Hand.MAIN_HAND
        var gun = Gun.fromItem(event.player.getStackInHand(hand))
        if (event.packet.hand == Hand.OFF_HAND) {
            if (gun == null) {
                hand = Hand.OFF_HAND
                gun = Gun.fromItem(event.player.getStackInHand(hand))
            }
        }
        if (gun == null) return
        gun.shoot(event.player, event.player.serverWorld)
    }

    @EventListener(true)
    fun onPlayerSneak(event: PlayerSneakEvent) {
        switchAiming(event.player, true)
    }

    @EventListener(true)
    fun onPlayerSneakRelease(event: PlayerSneakReleaseEvent) {
        switchAiming(event.player, false)
    }

    private fun switchAiming(player: ServerPlayerEntity, aiming: Boolean) {
        var hand = Hand.MAIN_HAND
        var gun = Gun.fromItem(player.getStackInHand(hand))
        if (gun == null) {
            hand = Hand.OFF_HAND
            gun = Gun.fromItem(player.getStackInHand(hand)) ?: return
        }
        player.setStackInHand(hand, gun.switchAiming(aiming))
    }
}
