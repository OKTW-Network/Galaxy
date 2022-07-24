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

import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import one.oktw.galaxy.event.annotation.EventListener
import one.oktw.galaxy.event.type.*
import one.oktw.galaxy.item.CustomItemHelper
import one.oktw.galaxy.item.Gun
import one.oktw.galaxy.sound.GalaxySound
import java.lang.Math.random
import kotlin.math.abs
import kotlin.math.roundToInt

class Gun {
    @EventListener(true)
    fun onPlayerInteractItem(event: PlayerInteractItemEvent) {
        val items = getWeaponsFromHands(event.player)
        val gun = items[Hand.MAIN_HAND] ?: items[Hand.OFF_HAND] ?: return
        shoot(gun, event.player, event.player.world as ServerWorld)
    }

    @EventListener(true)
    fun onPlayerSneak(event: PlayerSneakEvent) = switchAiming(event.player, true)

    @EventListener(true)
    fun onPlayerSneakRelease(event: PlayerSneakReleaseEvent) = switchAiming(event.player, false)

    @EventListener(true)
    fun onUpdateSelectedSlot(event: UpdateSelectedSlotEvent) = switchAiming(event.player, event.player.shouldCancelInteraction())

    @EventListener(true)
    fun onSwapItem(event: PlayerSwapItemInHandEvent) = switchAiming(event.player, event.player.shouldCancelInteraction())

    // cancel aiming before dropping
    @EventListener(true)
    fun onDropItem(event: PlayerDropItemEvent) = switchAiming(event.player, false)

    @EventListener(true)
    fun onPickupItem(event: PlayerPickupItemEvent) = switchAiming(event.player, event.player.shouldCancelInteraction())

    private fun getWeaponsFromHands(player: ServerPlayerEntity): Map<Hand, Gun?> = mapOf(
        Hand.MAIN_HAND to CustomItemHelper.getItem(player.getStackInHand(Hand.MAIN_HAND)) as? Gun,
        Hand.OFF_HAND to CustomItemHelper.getItem(player.getStackInHand(Hand.OFF_HAND)) as? Gun
    )

    private fun switchAiming(player: ServerPlayerEntity, aiming: Boolean) {
        val items = getWeaponsFromHands(player)

        val hand = if (items[Hand.MAIN_HAND] != null) Hand.MAIN_HAND else if (items[Hand.OFF_HAND] != null) Hand.OFF_HAND else return

        if (hand == Hand.MAIN_HAND && items[Hand.OFF_HAND] != null) { // turn weapon on offhand aiming off
            switchAiming(items[Hand.OFF_HAND]!!, false)?.let { player.setStackInHand(Hand.OFF_HAND, it) }
        }

        switchAiming(items[hand]!!, aiming)?.let { player.setStackInHand(hand, it) }
    }

    private fun shoot(item: Gun, player: ServerPlayerEntity, world: ServerWorld) {
        showTrajectory(item, player, world)
        playSound(item, player.server, world, player.blockPos)
    }

    private fun switchAiming(item: Gun, aiming: Boolean): ItemStack? {
        val newItem = if (aiming) {
            when (item) {
                Gun.PISTOL_LASOR -> Gun.PISTOL_LASOR_AIMING
                Gun.SNIPER -> Gun.SNIPER_AIMING
                Gun.RAILGUN -> Gun.RAILGUN_AIMING
                else -> item
            } as Gun
        } else {
            when (item) {
                Gun.PISTOL_LASOR_AIMING -> Gun.PISTOL_LASOR
                Gun.SNIPER_AIMING -> Gun.SNIPER
                Gun.RAILGUN_AIMING -> Gun.RAILGUN
                else -> item
            } as Gun
        }
        if (item != newItem) {
            return newItem.apply {
                migrateData(item)
            }.createItemStack()
        }
        return null
    }

    private fun showTrajectory(item: Gun, player: ServerPlayerEntity, world: ServerWorld) {
        val gun = item.weaponData
        var playerLookVec = player.rotationVector
        if (!player.shouldCancelInteraction()) playerLookVec = drift(playerLookVec)
        val line = playerLookVec.multiply(gun.range)

        val interval = when (maxAxis(vecAbs(line))) {
            0 -> vecAbs(line).x.div(0.3)
            1 -> vecAbs(line).y.div(0.3)
            2 -> vecAbs(line).z.div(0.3)
            else -> 10.0
        }
        var pos = Vec3d(player.x, player.eyeY, player.z).add(vecDiv(line, interval))

        for (i in 0..interval.roundToInt()) {
            world.spawnParticles(ParticleTypes.ENCHANTED_HIT, pos.x, pos.y, pos.z, 1, 0.0, 0.0, 0.0, 0.0)
            pos = pos.add(vecDiv(line, interval))
        }
    }

    private fun drift(vec: Vec3d) = vecDiv(
        vec.multiply(10.0).add(random(), random(), random())
            .subtract(random(), random(), random()), 10.0
    )

    private fun playSound(item: Gun, server: MinecraftServer, world: ServerWorld, pos: BlockPos) = when (item) {
        Gun.PISTOL, Gun.PISTOL_LASOR, Gun.PISTOL_LASOR_AIMING ->
            GalaxySound.playSound(
                server,
                world,
                null,
                pos,
                GalaxySound.GUN_SHOOT,
                SoundCategory.PLAYERS,
                1.0f,
                (1 + random() / 10 - random() / 10).toFloat()
            )
        Gun.SNIPER, Gun.SNIPER_AIMING -> {
            world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_BLAZE_HURT,
                SoundCategory.PLAYERS,
                1.0f,
                2.0f
            )
            world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST,
                SoundCategory.PLAYERS,
                1.0f,
                0.0f
            )
            world.playSound(
                null,
                pos,
                SoundEvents.BLOCK_PISTON_EXTEND,
                SoundCategory.PLAYERS,
                1.0f,
                2.0f
            )
        }
        else -> Unit // TODO RailGun
    }

    private fun vecAbs(vec: Vec3d) = Vec3d(abs(vec.x), abs(vec.y), abs(vec.z))

    private fun maxAxis(vec: Vec3d) = if (vec.x < vec.y) {
        if (vec.y < vec.z) 2 else 1
    } else {
        if (vec.x < vec.z) 2 else 0
    }

    private fun vecDiv(vec: Vec3d, value: Double) = Vec3d(vec.x / value, vec.y / value, vec.z / value)
}
