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

import kotlinx.coroutines.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.galaxy.data.extensions.getMember
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.player.event.Viewer.Companion.isViewer
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.service.pagination.PaginationList
import org.spongepowered.api.text.Text

class PlayerInfo : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
        .permission("oktw.command.admin.player")
        .arguments(GenericArguments.playerOrSource(Text.of("player")))
        .executor(this)
        .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val player = args.getOne<Player>("player").get()

        launch {
            val galaxy = galaxyManager.get(player.world)
            PaginationList.builder()
                .contents(
                    Text.of("Name: ", player.name),
                    Text.of("UUID: ", player.uniqueId),
                    Text.of("Viewer mode: ", isViewer(player.uniqueId)),
                    Text.of("StarDust: ", galaxy?.getMember(player.uniqueId)?.starDust),
                    Text.of("Galaxy: ${galaxy?.name} (${galaxy?.uuid})"),
                    Text.of("Planet: ", galaxy?.getPlanet(player.world)),
                    Text.of("World: ${player.world.name} (${player.world.uniqueId})")
                )
                .title(Text.of("Player Info"))
                .sendTo(src)
        }

        return CommandResult.success()
    }
}
