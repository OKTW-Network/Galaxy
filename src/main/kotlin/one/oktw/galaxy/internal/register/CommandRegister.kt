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

package one.oktw.galaxy.internal.register

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.command.Admin
import one.oktw.galaxy.command.Sign
import one.oktw.galaxy.command.Spawn
import org.spongepowered.api.Sponge

class CommandRegister {
    private val logger = main.logger

    init {
        logger.info("Register command...")
        Sponge.getCommandManager().apply {
            register(main, Sign().spec, "sign")
            register(main, Admin().spec, "admin")
            register(main, Spawn().spec, "spawn")
        }
    }
}
