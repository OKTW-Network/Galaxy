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

package one.oktw.galaxy.command

import com.mojang.brigadier.Command
import net.fabricmc.fabric.api.registry.CommandRegistry
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource


class CommandHelper {
    companion object {
        fun register(commandName: String, execute: Command<ServerCommandSource>, dedicated: Boolean = false) {
            CommandRegistry.INSTANCE.register(dedicated) { command ->
                command.register(
                    CommandManager.literal(commandName)
                        .executes(execute)
                )
            }
        }
    }
}
