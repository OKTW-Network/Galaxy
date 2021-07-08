/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
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

package one.oktw.galaxy.mixin.tweak;

import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import one.oktw.galaxy.mixin.interfaces.InventoryAvailableSlots;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Arrays;

@Mixin(DoubleInventory.class)
public abstract class MixinOptimizeContainer_AvailableSlots2 implements InventoryAvailableSlots, Inventory {
    private int[] availableSlots;

    @Override
    public int[] getAvailableSlots() {
        if (availableSlots == null) {
            int[] array = new int[size()];
            Arrays.setAll(array, i -> i);
            availableSlots = array;
        }

        return availableSlots;
    }
}
