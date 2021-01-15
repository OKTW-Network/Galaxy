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

package one.oktw.galaxy.item.util

import net.minecraft.item.ItemStack
import net.minecraft.nbt.ListTag
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import one.oktw.galaxy.item.type.GunType
import one.oktw.galaxy.sound.GalaxySoundEvents
import java.lang.Math.random
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt

data class Gun(
    val type: GunType,
    val heat: Int,
    val maxTemp: Int,
    val cooling: Double,
    val damage: Double,
    val range: Double,
    val through: Int,
    val uuid: UUID
) {
    companion object {
        fun fromItem(item: ItemStack): Gun? {
            val tag = item.tag ?: return null

            if (tag.getString("customItemType") != "GUN") return null

            return try {
                Gun(
                    GunType.valueOf(tag.getString("gunType")),
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
            }
        }
    }

    fun toLoreTag(): ListTag {
        return ItemLoreBuilder() // TODO Localize
            .addText(loreText(LiteralText("傷害"), damage.toString()))
            .addText(loreText(LiteralText("射程"), range.toString(), "B"))
            .addText(loreText(LiteralText("穿透"), through.toString(), ""))
            .addText(loreText(LiteralText("積熱"), heat.toString(), "K/shot"))
            .addText(loreText(LiteralText("耐熱"), maxTemp.toString(), "K"))
            .addText(loreText(LiteralText("冷卻"), cooling.toString(), "K/t"))
            .toTag()
    }

    private fun loreText(key: Text, value: String, unit: String = ""): Text =
        key.copy().styled { it.withColor(Formatting.AQUA) }
            .append(LiteralText(": ").styled { it.withColor(Formatting.DARK_GRAY) })
            .append(LiteralText(value).styled { it.withColor(Formatting.GRAY) })
            .append(LiteralText(unit).styled { it.withColor(Formatting.DARK_GRAY) })
            .styled { it.withItalic(false) }

    fun shoot(player: ServerPlayerEntity, world: ServerWorld) {
        val playerLookVec = player.rotationVector
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

        playSound(world, player.blockPos, type)
    }

    private fun playSound(world: ServerWorld, pos: BlockPos, type: GunType) {
        when (type) {
            GunType.PISTOL, GunType.PISTOL_LASOR, GunType.PISTOL_LASOR_AIMING -> world.playSound(
                null,
                pos,
                GalaxySoundEvents.GUN_SHOOT,
                SoundCategory.PLAYERS,
                1.0f,
                (1 + random() / 10 - random() / 10).toFloat()
            )
            GunType.SNIPER, GunType.SNIPER_AIMING -> {
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
            else -> Unit
        }
    }

    private fun vecAbs(vec: Vec3d): Vec3d {
        return Vec3d(abs(vec.x), abs(vec.y), abs(vec.z))
    }

    private fun maxAxis(vec: Vec3d): Int {
        return if (vec.x < vec.y) {
            if (vec.y < vec.z) 2 else 1
        } else {
            if (vec.x < vec.z) 2 else 0
        }
    }

    private fun vecDiv(vec: Vec3d, value: Double): Vec3d {
        return Vec3d(vec.x / value, vec.y / value, vec.z / value)
    }
}
