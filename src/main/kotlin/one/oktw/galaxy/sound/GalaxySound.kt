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

package one.oktw.galaxy.sound

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.s2c.play.PlaySoundIdS2CPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

object GalaxySound {
    val GUN_SHOOT = Identifier("galaxy:gun.shot")
    val GUN_OVERHEAT = Identifier("galaxy:gun.overheat")

    fun playSound(
        server: MinecraftServer,
        world: ServerWorld,
        player: PlayerEntity?,
        pos: BlockPos,
        soundID: Identifier,
        category: SoundCategory,
        volume: Float,
        pitch: Float
    ) {
        val vec3d = Vec3d(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble())
        server.playerManager.sendToAround(
            player,
            vec3d.x,
            vec3d.y,
            vec3d.z,
            if (volume > 1.0F) (16.0 * volume) else 16.0,
            world.registryKey,
            PlaySoundIdS2CPacket(soundID, category, vec3d, volume, pitch)
        )
    }
}
