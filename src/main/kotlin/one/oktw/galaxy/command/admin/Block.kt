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

import one.oktw.galaxy.block.enums.CustomBlocks
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.data.DataBlockType
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.util.blockray.BlockRay

class Block : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
        .permission("oktw.command.admin.block")
        .arguments(GenericArguments.enumValue(Text.of("type"), CustomBlocks::class.java))
        .executor(this)
        .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (!args.hasAny("type") || src !is Player) return CommandResult.empty()

        val block = BlockRay.from(src)
            .stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
            .build().end().orElse(null)

        block?.location?.offer(DataBlockType(args.getOne<CustomBlocks>("type").get()))
                ?: src.sendMessage(Text.of("You need looking block!"))

        return CommandResult.success()
    }
}
