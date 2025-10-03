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

package one.oktw.galaxy.command.commands

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import one.oktw.galaxy.command.Command
import one.oktw.galaxy.command.commands.admin.Creative
import one.oktw.galaxy.command.commands.admin.FlySpeed
import one.oktw.galaxy.command.commands.admin.GetItem
import one.oktw.galaxy.command.commands.admin.RegisterBlock

class Admin : Command {
    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("admin")
                .requires { source -> source.hasPermissionLevel(2) }
                .then(Creative().command)
                .then(GetItem().command)
                .then(RegisterBlock.command)
                .then(FlySpeed().command)
        )
    }
}
