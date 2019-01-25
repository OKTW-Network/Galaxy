/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2018
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

import one.oktw.galaxy.command.admin.*
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandSpec

class Admin : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
        .permission("oktw.command.admin")
        .child(Gun().spec, "gun")
        .child(Viewer().spec, "viewer")
        .child(TPX().spec, "tpx")
        .child(PlayerInfo().spec, "player")
        .child(GalaxyInfo().spec, "galaxyInfo")
        .child(GalaxyManage().spec, "galaxyManage")
        .child(Block().spec, "block")
        .child(DeleteWorld().spec, "deleteWorld")
        .child(UnloadWorld().spec, "unloadWorld")
        .child(Book().spec, "book")
        .child(GivePlayerStarDust().spec, "givePlayerStarDust")
        .child(TakePlayerStarDust().spec, "takePlayerStarDust")
        .child(SetPlayerStarDust().spec, "setPlayerStarDust")
        .executor(this)
        .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        src.sendMessage(spec.getUsage(src))

        return CommandResult.success()
    }
}
