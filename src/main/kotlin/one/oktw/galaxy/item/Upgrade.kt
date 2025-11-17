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
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items.RECOVERY_COMPASS

class Upgrade private constructor(val type: Type, val level: Int) :
    CustomItem(ResourceLocation.fromNamespaceAndPath("galaxy", "upgrade/${type.id(level)}"), RECOVERY_COMPASS, macStack = 1) {
    companion object {
        val BASE = registry.register(Upgrade(Type.BASE, 0))
        val COOLING_LV1 = registry.register(Upgrade(Type.COOLING, 1))
        val COOLING_LV2 = registry.register(Upgrade(Type.COOLING, 2))
        val COOLING_LV3 = registry.register(Upgrade(Type.COOLING, 3))
        val COOLING_LV4 = registry.register(Upgrade(Type.COOLING, 4))
        val COOLING_LV5 = registry.register(Upgrade(Type.COOLING, 5))
        val RANGE_LV1 = registry.register(Upgrade(Type.RANGE, 1))
        val RANGE_LV2 = registry.register(Upgrade(Type.RANGE, 2))
        val RANGE_LV3 = registry.register(Upgrade(Type.RANGE, 3))
        val RANGE_LV4 = registry.register(Upgrade(Type.RANGE, 4))
        val RANGE_LV5 = registry.register(Upgrade(Type.RANGE, 5))
        val RANGE_LV6 = registry.register(Upgrade(Type.RANGE, 6))
        val RANGE_LV7 = registry.register(Upgrade(Type.RANGE, 7))
        val RANGE_LV8 = registry.register(Upgrade(Type.RANGE, 8))
        val RANGE_LV9 = registry.register(Upgrade(Type.RANGE, 9))
        val SPEED_LV1 = registry.register(Upgrade(Type.SPEED, 1))
        val SPEED_LV2 = registry.register(Upgrade(Type.SPEED, 2))
        val SPEED_LV3 = registry.register(Upgrade(Type.SPEED, 3))
        val SPEED_LV4 = registry.register(Upgrade(Type.SPEED, 4))
        val SPEED_LV5 = registry.register(Upgrade(Type.SPEED, 5))
        val THROUGH_LV1 = registry.register(Upgrade(Type.THROUGH, 1))
        val THROUGH_LV2 = registry.register(Upgrade(Type.THROUGH, 2))
        val THROUGH_LV3 = registry.register(Upgrade(Type.THROUGH, 3))
        val THROUGH_LV4 = registry.register(Upgrade(Type.THROUGH, 4))
        val THROUGH_LV5 = registry.register(Upgrade(Type.THROUGH, 5))
        val DAMAGE_LV1 = registry.register(Upgrade(Type.DAMAGE, 1))
        val DAMAGE_LV2 = registry.register(Upgrade(Type.DAMAGE, 2))
        val DAMAGE_LV3 = registry.register(Upgrade(Type.DAMAGE, 3))
        val DAMAGE_LV4 = registry.register(Upgrade(Type.DAMAGE, 4))
        val DAMAGE_LV5 = registry.register(Upgrade(Type.DAMAGE, 5))
        val DISTANCE_LV1 = registry.register(Upgrade(Type.DISTANCE, 1))
        val DISTANCE_LV2 = registry.register(Upgrade(Type.DISTANCE, 2))
        val DISTANCE_LV3 = registry.register(Upgrade(Type.DISTANCE, 3))
        val DISTANCE_LV4 = registry.register(Upgrade(Type.DISTANCE, 4))
        val DISTANCE_LV5 = registry.register(Upgrade(Type.DISTANCE, 5))
        val FIRE_RESISTANCE_LV1 = registry.register(Upgrade(Type.FIRE_RESISTANCE, 1))
        val FIRE_RESISTANCE_LV2 = registry.register(Upgrade(Type.FIRE_RESISTANCE, 2))
        val FIRE_RESISTANCE_LV3 = registry.register(Upgrade(Type.FIRE_RESISTANCE, 3))
        val FIRE_RESISTANCE_LV4 = registry.register(Upgrade(Type.FIRE_RESISTANCE, 4))
        val FIRE_RESISTANCE_LV5 = registry.register(Upgrade(Type.FIRE_RESISTANCE, 5))

        fun getFromItem(item: ItemStack): Upgrade? = CustomItemHelper.getItem(item) as? Upgrade
    }

    override fun getName(): Component = translatable("item.Upgrade.Item", translatable(type.itemName))
        .also { if (level > 0) it.append(" Lv.${level}") }

    enum class Type(private val id: String, val itemName: String) {
        BASE("base", "item.Upgrade.BASE"),
        COOLING("cooling", "item.Upgrade.COOLING"),
        RANGE("range", "item.Upgrade.RANGE"),
        SPEED("speed", "item.Upgrade.SPEED"),
        THROUGH("through", "item.Upgrade.THROUGH"),
        DAMAGE("damage", "item.Upgrade.DAMAGE"),
        DISTANCE("distance", "item.Upgrade.DISTANCE"),
        FIRE_RESISTANCE("fire_resistance", "item.Upgrade.HEAT");

        fun id(level: Int): String = if (level == 0) this.id else "${this.id}_lv$level"
    }
}
