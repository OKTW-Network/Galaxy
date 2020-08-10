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

package one.oktw.galaxy.command.commands

import com.mojang.brigadier.Command.SINGLE_SUCCESS
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import kotlinx.coroutines.*
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import one.oktw.galaxy.command.Command
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUISBackStackManager
import one.oktw.galaxy.item.Gui
import org.apache.logging.log4j.LogManager

class TestGUI : Command, CoroutineScope by CoroutineScope(Dispatchers.Default + SupervisorJob()) {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("gui")
                .executes { context ->
                    execute(context.source)
                }
                .then(
                    CommandManager.argument("row", IntegerArgumentType.integer(1, 51))
                        .suggests { _, builder ->
                            for (i in 1..6) builder.suggest(i)
                            builder.suggest(33)
                            builder.suggest(51)
                            builder.buildFuture()
                        }
                        .executes {
                            val row = IntegerArgumentType.getInteger(it, "row")
                            val gui = when (row) {
                                1 -> GUI(ScreenHandlerType.GENERIC_9X1, Text.empty())
                                2 -> GUI(ScreenHandlerType.GENERIC_9X2, Text.empty())
                                3 -> GUI(ScreenHandlerType.GENERIC_9X3, Text.empty())
                                4 -> GUI(ScreenHandlerType.GENERIC_9X4, Text.empty())
                                5 -> GUI(ScreenHandlerType.GENERIC_9X5, Text.empty())
                                6 -> GUI(ScreenHandlerType.GENERIC_9X6, Text.empty())
                                33 -> GUI(ScreenHandlerType.GENERIC_3X3, Text.empty())
                                51 -> GUI(ScreenHandlerType.HOPPER, Text.empty())
                                else -> return@executes SINGLE_SUCCESS
                            }

                            when (row) {
                                in 1..6 -> for (x in 0..8) for (y in 0 until row) gui.setAllowUse(x, y, true)
                                33 -> for (x in 0..2) for (y in 0..2) gui.setAllowUse(x, y, true)
                                51 -> for (i in 0..4) gui.setAllowUse(i, 0, true)
                            }

                            GUISBackStackManager.openGUI(it.source.playerOrThrow, gui)

                            return@executes SINGLE_SUCCESS
                        }
                )
        )
    }

    private fun execute(source: ServerCommandSource): Int {
        val player = source.playerOrThrow
        val gui = GUI(ScreenHandlerType.GENERIC_9X6, Text.of("Test"))
        val gui2 = GUI(ScreenHandlerType.GENERIC_9X1, Text.empty())

        gui2.editInventory {
            fillAll(Gui.BLANK.createItemStack())
        }

        gui.addBinding(0..8, 0..5) {
            LogManager.getLogger().info(item)
            gui.editInventory {
                player.sendMessage(Text.of("Action: $action"), false)
//                if (item.isEmpty) set(x, y, ItemStack(Items.STICK)) else set(x, y, ItemStack.EMPTY)
            }
        }

        for (x in 1..7) for (y in 1..4) gui.setAllowUse(x, y, true)

        gui.setAllowUse(1, 1, false)
        gui.addBinding(1, 1) {
            GUISBackStackManager.openGUI(player, gui2)
        }

        launch {
            while (!player.isDisconnected) {
                for (x in 0..8) {
                    gui.editInventory { set(x, 0, Gui.INFO.createItemStack()) }
                    delay(100)
                }

                for (x in 0..8) {
                    gui.editInventory { set(x, 0, ItemStack.EMPTY) }
                    delay(100)
                }
            }
        }

        launch {
            while (!player.isDisconnected) {
                for (x in 8 downTo 0) {
                    gui.editInventory { set(x, 5, Gui.INFO.createItemStack()) }
                    delay(100)
                }

                for (x in 8 downTo 0) {
                    gui.editInventory { set(x, 5, ItemStack.EMPTY) }
                    delay(100)
                }
            }
        }

        launch {
            while (!player.isDisconnected) {
                for (y in 1..4) {
                    gui.editInventory { set(0, y, Gui.INFO.createItemStack()) }
                    delay(100)
                }

                for (y in 1..4) {
                    gui.editInventory { set(0, y, ItemStack.EMPTY) }
                    delay(100)
                }
            }
        }

        launch {
            while (!player.isDisconnected) {
                for (y in 4 downTo 1) {
                    gui.editInventory { set(8, y, Gui.INFO.createItemStack()) }
                    delay(100)
                }

                for (y in 4 downTo 1) {
                    gui.editInventory { set(8, y, ItemStack.EMPTY) }
                    delay(100)
                }
            }
        }

//        gui.editInventory {
//            set(0, ItemStack(Items.STICK))
//        }

        GUISBackStackManager.openGUI(player, gui)

        return SINGLE_SUCCESS
    }
}
