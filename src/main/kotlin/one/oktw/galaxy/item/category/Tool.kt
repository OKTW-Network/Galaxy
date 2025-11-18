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

package one.oktw.galaxy.item.category

import net.minecraft.network.chat.Component
import one.oktw.galaxy.item.CustomItem
import one.oktw.galaxy.item.Tool

class Tool : CustomItemCategory() {
    override val displayName: Component = Component.translatable("recipe.catalog.TOOL")
    override val displayItem = Tool.WRENCH
    override val items = CustomItem.registry.getAll().filterValues { it is Tool }.values.toList()
}
