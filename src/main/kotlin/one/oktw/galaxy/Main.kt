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

package one.oktw.galaxy

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.dedicated.MinecraftDedicatedServer
import one.oktw.galaxy.event.EventManager

@Suppress("unused")
class Main : ModInitializer {
    lateinit var server: MinecraftDedicatedServer
        private set
    lateinit var eventManager: EventManager
        private set

    companion object {
        var main: Main? = null
            private set
    }

    override fun onInitialize() {
        server = FabricLoader.getInstance().gameInstance as MinecraftDedicatedServer
        eventManager = EventManager(server)
        main = this
    }
}
