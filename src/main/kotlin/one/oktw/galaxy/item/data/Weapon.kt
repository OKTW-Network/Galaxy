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

package one.oktw.galaxy.item.data

import net.minecraft.item.ItemStack
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import one.oktw.galaxy.item.CustomItemHelper
import one.oktw.galaxy.item.Weapon
import one.oktw.galaxy.sound.GalaxySound
import one.oktw.galaxy.util.LoreEditor.Companion.loreEditor
import java.lang.Math.random
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

data class Weapon(
    var item: Weapon,
    val heat: Int,
    val maxTemp: Int,
    val cooling: Double,
    val damage: Double,
    val range: Double,
    val through: Int,
    val uuid: UUID
) {
    companion object {
        fun fromItem(item: ItemStack): one.oktw.galaxy.item.data.Weapon? {
            val tag = item.nbt?.getCompound("CustomWeaponData") ?: return null

            if (CustomItemHelper.getItem(item) !is Weapon) return null

            return try {
                Weapon(
                    CustomItemHelper.getItem(item) as Weapon,
                    tag.getInt("heat"),
                    tag.getInt("maxTemp"),
                    tag.getDouble("cooling"),
                    tag.getDouble("damage"),
                    tag.getDouble("range"),
                    tag.getInt("through"),
                    tag.getUuid("gunUUID")
                )
            } catch (_: NullPointerException) {
                null
            } catch (_: IllegalArgumentException) {
                null
            }
        }

        fun default(item: Weapon) = Weapon(item, 1, 1, 1.0, 1.0, 1.0, 1, MathHelper.randomUuid())
    }

    fun toItemStack() = item.createItemStack().apply {
        orCreateNbt.apply {
            getCompound("CustomWeaponData").apply {
                putInt("heat", heat)
                putInt("maxTemp", maxTemp)
                putDouble("cooling", cooling)
                putDouble("damage", this@Weapon.damage)
                putDouble("range", range)
                putInt("through", through)
                putUuid("gunUUID", uuid)
            }
        }
        loreEditor {
            addText(loreText(Text.of("傷害"), damage.toString()))
            addText(loreText(Text.of("射程"), range.toString(), "B"))
            addText(loreText(Text.of("穿透"), through.toString(), ""))
            addText(loreText(Text.of("積熱"), heat.toString(), "K/shot"))
            addText(loreText(Text.of("耐熱"), maxTemp.toString(), "K"))
            addText(loreText(Text.of("冷卻"), cooling.toString(), "K/t"))
        }
    }

    private fun loreText(key: Text, value: String, unit: String = ""): Text =
        key.copy()
            .setStyle(Style.EMPTY.withColor(Formatting.AQUA))
            .append(Text.literal(": ").styled { it.withColor(Formatting.DARK_GRAY) })
            .append(Text.literal(value).styled { it.withColor(Formatting.GRAY) })
            .append(Text.literal(unit).styled { it.withColor(Formatting.DARK_GRAY) })
            .styled { it.withItalic(false) }

    fun shoot(player: ServerPlayerEntity, world: ServerWorld) {
        showTrajectory(player, world)
        playSound(player.server, world, player.blockPos)
    }

    fun switchAiming(aiming: Boolean): ItemStack {
        item = if (aiming) {
            when (item) {
                Weapon.PISTOL_LASOR -> Weapon.PISTOL_LASOR_AIMING
                Weapon.SNIPER -> Weapon.SNIPER_AIMING
                Weapon.RAILGUN -> Weapon.RAILGUN_AIMING
                else -> item
            } as Weapon
        } else {
            when (item) {
                Weapon.PISTOL_LASOR_AIMING -> Weapon.PISTOL_LASOR
                Weapon.SNIPER_AIMING -> Weapon.SNIPER
                Weapon.RAILGUN_AIMING -> Weapon.RAILGUN
                else -> item
            } as Weapon
        }
        return toItemStack()
    }

    private fun showTrajectory(player: ServerPlayerEntity, world: ServerWorld) {
        var playerLookVec = player.rotationVector
        if (!player.shouldCancelInteraction()) playerLookVec = drift(playerLookVec)
        val line = playerLookVec.multiply(this.range)

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

    private fun playSound(server: MinecraftServer, world: ServerWorld, pos: BlockPos) = when (item) {
        Weapon.PISTOL, Weapon.PISTOL_LASOR, Weapon.PISTOL_LASOR_AIMING ->
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
        Weapon.SNIPER, Weapon.SNIPER_AIMING -> {
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
