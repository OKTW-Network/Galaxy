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

package one.oktw.galaxy.command.admin

import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.command.admin.galaxyManage.*
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandSpec

class GalaxyManage : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .permission("oktw.command.admin.galaxyManage")
            .child(CreateGalaxy().spec, "createGalaxy")
            .child(CreatePlanet().spec, "createPlanet")
            .child(AddMember().spec, "addMember")
            .child(SetGroup().spec, "setGroup")
            .child(RemoveMember().spec, "removeMember")
            .child(TransferOwner().spec, "transferOwner")
            .child(Rename().spec, "rename")
            .child(Info().spec, "info")
            .child(Notice().spec, "notice")
            .child(SetSize().spec, "setSize")
            .child(SetVisit().spec, "setVisit")
            .child(RemoveGalaxy().spec, "removeGalaxy")
            .child(RemovePlanet().spec, "removePlanet")
            .child(Dividends().spec, "dividends")
            .child(GiveStarDust().spec, "giveStarDust")
            .child(TakeStarDust().spec, "takeStarDust")
            .child(SetStarDust().spec, "setStarDust")
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        src.sendMessage(spec.getUsage(src))

        return CommandResult.success()
    }
}
