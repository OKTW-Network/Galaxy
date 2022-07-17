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

package one.oktw.galaxy.item.event

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.*
import one.oktw.galaxy.item.data.Weapon

class Weapon {
    @EventListener(true)
    fun onPlayerInteractItem(event: PlayerInteractItemEvent) {
        var hand = Hand.MAIN_HAND
        var weapon = Weapon.fromItem(event.player.getStackInHand(hand))
        if (event.packet.hand == Hand.OFF_HAND) {
            if (weapon == null) {
                hand = Hand.OFF_HAND
                weapon = Weapon.fromItem(event.player.getStackInHand(hand))
            }
        }
        if (weapon == null) return
        weapon.shoot(event.player, event.player.world as ServerWorld)
    }

    @EventListener(true)
    fun onPlayerSneak(event: PlayerSneakEvent) = switchAiming(event.player, true)

    @EventListener(true)
    fun onPlayerSneakRelease(event: PlayerSneakReleaseEvent) = switchAiming(event.player, false)

    @EventListener(true)
    fun onUpdateSelectedSlot(event: UpdateSelectedSlotEvent) = updateInventory(event.player)

    @EventListener(true)
    fun onHotBarSlotUpdate(event: HotBarSlotUpdateEvent) = updateInventory(event.player)

    private fun updateInventory(player: ServerPlayerEntity) = switchAiming(player, player.shouldCancelInteraction())

    private fun switchAiming(player: ServerPlayerEntity, aiming: Boolean) {
        var hand = Hand.MAIN_HAND
        var weapon = Weapon.fromItem(player.getStackInHand(hand))
        if (weapon == null) {
            hand = Hand.OFF_HAND
            weapon = Weapon.fromItem(player.getStackInHand(hand)) ?: return
        } else {
            val offWeapon = Weapon.fromItem(player.getStackInHand(Hand.OFF_HAND))
            if (offWeapon != null) { // turn off offhand aim
                player.setStackInHand(Hand.OFF_HAND, offWeapon.switchAiming(false))
            }
        }
        player.setStackInHand(hand, weapon.switchAiming(aiming))
    }
}
