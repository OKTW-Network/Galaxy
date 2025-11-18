/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2025
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

package one.oktw.galaxy.gui

import com.google.common.collect.MapMaker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import one.oktw.galaxy.util.MinecraftAsyncExecutor
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentLinkedDeque

class GUISBackStackManager(player: ServerPlayer) :
    CoroutineScope by CoroutineScope(MinecraftAsyncExecutor(player.level().server).asCoroutineDispatcher()) {
    private val player = WeakReference(player)
    private val stack = ConcurrentLinkedDeque<GUI>()

    companion object {
        private val managers = MapMaker().weakKeys().makeMap<ServerPlayer, GUISBackStackManager>()

        fun openGUI(player: ServerPlayer, gui: GUI) {
            managers.getOrPut(player) { GUISBackStackManager(player) }.open(gui)
        }

        fun closeAll(player: ServerPlayer) {
            managers[player]?.stack?.clear()
            player.closeContainer()
        }
    }

    fun open(gui: GUI) {
        val player = player.get() ?: return
        gui.onClose { this.closeCallback(gui, it) }
        stack.offerLast(gui)
        if (player.level().server.isSameThread) {
            // Delay 1 tick to workaround open GUI on close callback
            launch { player.openMenu(gui) }
        } else runBlocking(player.level().server.asCoroutineDispatcher()) {
            player.openMenu(gui)
        }
    }

    private fun closeCallback(gui: GUI, player: Player) {
        if (player == this.player.get() && gui == stack.lastOrNull()) {
            stack.pollLast() // Remove closed

            // Delay 1 tick to workaround open GUI on close callback
            launch {
                while (stack.isNotEmpty()) {
                    if (stack.last().let(player::openMenu).isPresent) break // Try open previous
                    stack.pollLast() // Open fail, remove it
                }
            }
        }
    }
}
