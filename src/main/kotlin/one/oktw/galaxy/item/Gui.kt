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
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items.DIAMOND_HOE
import net.minecraft.world.item.component.CustomModelData

class Gui(customModelData: CustomModelData, private val name: Component? = null) :
    CustomItem(
        ResourceLocation.fromNamespaceAndPath("galaxy", "gui"),
        DIAMOND_HOE,
        itemModel = ResourceLocation.fromNamespaceAndPath("galaxy", "gui/complex"),
        customModelData = customModelData,
        hideTooltip = name == null
    ) {

    override fun getName(): Component = name ?: Component.nullToEmpty("")
}
