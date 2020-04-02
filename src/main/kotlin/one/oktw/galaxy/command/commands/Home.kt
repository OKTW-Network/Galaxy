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

package one.oktw.galaxy.command.commands

import com.mojang.brigadier.CommandDispatcher
import kotlinx.coroutines.*
import net.minecraft.client.network.packet.TitleS2CPacket
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.world.dimension.DimensionType
import one.oktw.galaxy.command.Command
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class Home : Command {

    private val lock = ConcurrentHashMap.newKeySet<UUID>()

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("home")
                .executes { context ->
                    execute(context.source)
                }
        )
    }

    private fun execute(source: ServerCommandSource): Int {
        val player = source.player

        if (player == null || lock.contains(player.uuid)) return com.mojang.brigadier.Command.SINGLE_SUCCESS

        lock += player.uuid

        val spawnPoint = PlayerEntity.findRespawnPosition(
            source.minecraftServer.getWorld(player.dimension),
            player.spawnPosition,
            player.isSpawnForced
        )

        if (!spawnPoint.isPresent) {
            player.sendMessage(TranslatableText("block.minecraft.bed.not_valid").styled { style -> style.color = Formatting.RED })
            lock -= player.uuid
        } else {
            GlobalScope.launch {
                for (i in 0..4) {
                    val component = LiteralText("請等待 ${5 - i} 秒鐘").styled { style -> style.color = Formatting.GREEN }
                    player.networkHandler.sendPacket(TitleS2CPacket(TitleS2CPacket.Action.ACTIONBAR, component))
                    delay(TimeUnit.SECONDS.toMillis(1))
                }
                withContext(player.server.asCoroutineDispatcher()) {
                    player.teleport(
                        source.minecraftServer.getWorld(DimensionType.OVERWORLD),
                        player.spawnPosition.x.toDouble(),
                        player.spawnPosition.y.toDouble(),
                        player.spawnPosition.z.toDouble(),
                        0.0F,
                        0.0F
                    )
                }
                lock -= player.uuid
            }
        }
        return com.mojang.brigadier.Command.SINGLE_SUCCESS
    }
}
