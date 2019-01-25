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

import net.minecraftforge.common.MinecraftForge
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.armor.event.Armor
import one.oktw.galaxy.block.event.BlockGUI
import one.oktw.galaxy.block.event.Elevator
import one.oktw.galaxy.block.event.FakeBlock
import one.oktw.galaxy.economy.event.EconomyEvent
import one.oktw.galaxy.galaxy.planet.event.SpawnProtect
import one.oktw.galaxy.item.event.Gun
import one.oktw.galaxy.item.event.ItemProtect
import one.oktw.galaxy.machine.chunkloader.ChunkLoader
import one.oktw.galaxy.block.event.Teleporter
import one.oktw.galaxy.player.event.Harvest
import one.oktw.galaxy.player.event.PlayerControl
import one.oktw.galaxy.player.event.Viewer
import org.spongepowered.api.Sponge

class EventRegister {
    init {
        main.logger.info("Registering Event...")
        Sponge.getEventManager().apply {
            registerListeners(main, Viewer())
            registerListeners(main, Armor())
            registerListeners(main, ItemProtect())
            registerListeners(main, SpawnProtect())
            registerListeners(main, PlayerControl())
            registerListeners(main, Gun())
            registerListeners(main, BlockGUI())
            registerListeners(main, ChunkLoader())
            registerListeners(main, FakeBlock())
            registerListeners(main, Harvest())
            registerListeners(main, Teleporter())
        }

        MinecraftForge.EVENT_BUS.apply {
            register(EconomyEvent())
            register(Elevator())
        }
    }
}
