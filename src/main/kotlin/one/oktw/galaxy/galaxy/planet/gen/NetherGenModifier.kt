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

package one.oktw.galaxy.galaxy.planet.gen

import one.oktw.galaxy.galaxy.planet.gen.populator.Spawn
import one.oktw.galaxy.galaxy.planet.gen.populator.SpawnPosFix
import org.spongepowered.api.data.DataContainer
import org.spongepowered.api.world.gen.WorldGenerator
import org.spongepowered.api.world.gen.WorldGeneratorModifier
import org.spongepowered.api.world.storage.WorldProperties

class NetherGenModifier : WorldGeneratorModifier {
    override fun modifyWorldGenerator(world: WorldProperties, settings: DataContainer, worldGenerator: WorldGenerator) {
        worldGenerator.populators.add(0, SpawnPosFix(32, 115))
        worldGenerator.populators.add(Spawn())
    }

    override fun getName() = "Planet-Nether"

    override fun getId() = "planet_nether"
}
