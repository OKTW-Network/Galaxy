/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2025
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

package one.oktw.galaxy.item

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Component.translatable
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items.RECOVERY_COMPASS

class Tool private constructor(id: String, private val name: String) :
    CustomItem(ResourceLocation.fromNamespaceAndPath("galaxy", "tool/$id"), RECOVERY_COMPASS, maxStack = 1) {
    override val cacheable = false

    companion object {
        val WRENCH = registry.register(Tool("wrench", "item.Tool.WRENCH"))
        val CROWBAR = registry.register(Tool("crowbar", "item.Tool.CROWBAR"))
    }

    override fun getName(): Component = translatable(name)
}
