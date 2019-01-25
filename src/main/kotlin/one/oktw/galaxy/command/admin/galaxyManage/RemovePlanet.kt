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

package one.oktw.galaxy.command.admin.galaxyManage

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.data.extensions.removePlanet
import one.oktw.galaxy.util.Chat
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextColors.GREEN
import org.spongepowered.api.text.format.TextColors.RED
import java.util.*

class RemovePlanet : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .executor(this)
            .permission("oktw.command.admin.galaxyManage.removePlanet")
            .arguments(
                GenericArguments.optional(GenericArguments.uuid(Text.of("planet")))
            )
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src !is Player) return CommandResult.empty()
        var planetUUID: UUID? = args.getOne<UUID>("planet").orElse(null)
        launch {
            val galaxy = planetUUID?.let { galaxyManager.get(planet = it) } ?: (src as? Player)?.world?.let { galaxyManager.get(it) }
            val planet = planetUUID?.let { galaxy?.getPlanet(it) } ?: (src as? Player)?.world?.let { galaxy?.getPlanet(it) }

            if (planetUUID == null) {
                planetUUID = planet?.uuid
            }

            if (planetUUID != null) {
                if (Chat.confirm(src, Text.of(TextColors.AQUA, "Are you sure you want to remove the planet ${planet!!.name}?")) == true) {
                    withContext(serverThread) { galaxy!!.removePlanet(planetUUID!!) }
                    src.sendMessage(Text.of(GREEN, "Planet ${planet.name} on ${galaxy!!.name} (${galaxy.uuid}) deleted!"))
                } else {
                    src.sendMessage(Text.of(RED, "Not enough argument: Planet not found or missing."))
                }
            }
        }
        return CommandResult.success()
    }
}
