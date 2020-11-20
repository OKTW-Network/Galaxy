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
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.recipe.RecipeType
import net.minecraft.server.dedicated.MinecraftDedicatedServer
import net.minecraft.util.Identifier
import one.oktw.galaxy.block.event.BlockEvents
import one.oktw.galaxy.block.event.ElevatorEvents
import one.oktw.galaxy.chat.Exchange
import one.oktw.galaxy.command.commands.Admin
import one.oktw.galaxy.command.commands.Home
import one.oktw.galaxy.command.commands.Join
import one.oktw.galaxy.command.commands.Spawn
import one.oktw.galaxy.event.EventManager
import one.oktw.galaxy.mixin.interfaces.CustomRecipeManager
import one.oktw.galaxy.player.Harvest
import one.oktw.galaxy.player.PlayerControl
import one.oktw.galaxy.player.Sign
import one.oktw.galaxy.proxy.api.ProxyAPI
import one.oktw.galaxy.recipe.blocks.Elevator
import one.oktw.galaxy.recipe.blocks.HTCraftingTable
import one.oktw.galaxy.recipe.easyRecipe.*
import one.oktw.galaxy.recipe.materials.CeramicPlate
import one.oktw.galaxy.recipe.tools.Wrench
import one.oktw.galaxy.resourcepack.ResourcePack
import java.util.*

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
        val selfUUID by lazy {
            try {
                System.getenv("GALAXY_ID")?.let { UUID.fromString(it) }
            } catch (err: Throwable) {
                null
            } ?: ProxyAPI.dummyUUID
        }
    }

    override fun onInitializeServer() {
        main = this

        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, _ ->
            listOf(Join(), Admin(), Home(), Spawn()).forEach { dispatcher.let(it::register) }
        })

        // Recipe
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Wrench())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Elevator())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, HTCraftingTable())
        CustomRecipeManager.addRecipe(RecipeType.SMELTING, CeramicPlate())
        // Easy Recipe
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, RedstoneLamp())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Dispenser())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, DispenserRight())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, DispenserLeft())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, RedstoneRepeater())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, CarrotOnAStickRight())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, CarrotOnAStick_Left())
        CustomRecipeManager.addRecipe(RecipeType.CRAFTING, Ladder())

        ServerLifecycleEvents.SERVER_STARTING.register(ServerLifecycleEvents.ServerStarting {
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
            eventManager.register(BlockEvents())
            eventManager.register(Sign())
            eventManager.register(ElevatorEvents())
        })

        // server.log("current server id is $selfUID
        println("current server id is $selfUUID")
    }
}
