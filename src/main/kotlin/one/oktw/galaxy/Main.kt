/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2020
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

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.fabricmc.api.DedicatedServerModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.server.ServerStartCallback
import net.minecraft.server.dedicated.MinecraftDedicatedServer
import net.minecraft.util.Identifier
import one.oktw.galaxy.chat.Exchange
import one.oktw.galaxy.command.commands.Admin
import one.oktw.galaxy.command.commands.Home
import one.oktw.galaxy.command.commands.Join
import one.oktw.galaxy.command.commands.Spawn
import one.oktw.galaxy.event.EventManager
import one.oktw.galaxy.player.Harvest
import one.oktw.galaxy.player.PlayerControl
import one.oktw.galaxy.player.Sign
import one.oktw.galaxy.resourcepack.ResourcePack

@Suppress("unused")
class Main : DedicatedServerModInitializer {
    lateinit var server: MinecraftDedicatedServer
        private set
    lateinit var eventManager: EventManager
        private set

    companion object {
        val PROXY_IDENTIFIER = Identifier("galaxy", "proxy")
        var main: Main? = null
            private set
    }

    override fun onInitializeServer() {
        main = this

        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _ ->
            listOf(Join(), Admin(), Home(), Spawn()).forEach { dispatcher.let(it::register) }
        })

        ServerStartCallback.EVENT.register(ServerStartCallback {
            server = it as MinecraftDedicatedServer
            eventManager = EventManager(server)

            val resourcePackUrl: String? = System.getenv("resourcePack")
            if (!resourcePackUrl.isNullOrBlank()) {
                GlobalScope.launch {
                    val resourcePack = ResourcePack.new(resourcePackUrl)
                    withContext(server.asCoroutineDispatcher()) {
                        server.setResourcePack(resourcePack.uri.toString(), resourcePack.hash)
                    }
                }
            }

            //Events
            eventManager.register(Exchange())
            eventManager.register(PlayerControl())
            eventManager.register(Harvest())
            eventManager.register(Sign())
        })
    }
}
