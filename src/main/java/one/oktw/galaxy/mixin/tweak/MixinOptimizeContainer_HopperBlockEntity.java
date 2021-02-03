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

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Hopper;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import one.oktw.galaxy.mixin.interfaces.InventoryAvailableSlots;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Arrays;

@Mixin(HopperBlockEntity.class)
public abstract class MixinOptimizeContainer_HopperBlockEntity extends LootableContainerBlockEntity {
    protected MixinOptimizeContainer_HopperBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    @Shadow
    private static boolean extract(Hopper hopper, Inventory inventory, int slot, Direction side) {
        return false;
    }

    @Shadow
    private static boolean canMergeItems(ItemStack first, ItemStack second) {
        return false;
    }

    /**
     * @author James58899
     * @reason No stream forEach
     */
    @Overwrite
    private static boolean isInventoryEmpty(Inventory inv, Direction facing) {
        for (int i : getAvailableSlots_NoStream(inv, facing)) {
            if (!inv.getStack(i).isEmpty()) return false;
        }

        return true;
    }

    /**
     * @author James58899
     * @reason No stream forEach
     */
    @Overwrite
    private boolean isInventoryFull(Inventory inv, Direction direction) {
        for (int i : getAvailableSlots_NoStream(inv, direction)) {
            ItemStack itemStack = inv.getStack(i);
            if (itemStack.getCount() < itemStack.getMaxCount()) return false;
        }
        return true;
    }

    @Inject(method = "extract(Lnet/minecraft/block/entity/Hopper;Lnet/minecraft/inventory/Inventory;ILnet/minecraft/util/math/Direction;)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;", shift = At.Shift.BEFORE),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true)
    private static void extract_EarlyCheck(Hopper hopper, Inventory inventory, int slot, Direction side, CallbackInfoReturnable<Boolean> cir, ItemStack itemStack) {
        for (int i : getAvailableSlots_NoStream(hopper, null)) {
            ItemStack slotItem = hopper.getStack(i);
            if (slotItem.isEmpty() || canMergeItems(slotItem, itemStack)) return;
        }

        cir.setReturnValue(false);
    }

    @Inject(method = "insert",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;getStack(I)Lnet/minecraft/item/ItemStack;", shift = At.Shift.BEFORE),
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true)
    private void insert_EarlyCheck(CallbackInfoReturnable<Boolean> cir, Inventory inventory, Direction direction, int slot) {
        ItemStack itemStack = getStack(slot);
        for (int i : getAvailableSlots_NoStream(inventory, direction)) {
            ItemStack slotItem = inventory.getStack(i);
            if (slotItem.isEmpty() || canMergeItems(slotItem, itemStack)) return;
        }

        cir.setReturnValue(false);
    }

    @Inject(method = "extract(Lnet/minecraft/block/entity/Hopper;)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/HopperBlockEntity;isInventoryEmpty(Lnet/minecraft/inventory/Inventory;Lnet/minecraft/util/math/Direction;)Z"),
        cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
    private static void extract_NoStream(Hopper hopper, CallbackInfoReturnable<Boolean> cir, Inventory inventory, Direction direction) {
        if (!isInventoryEmpty(inventory, direction)) {
            for (int i : getAvailableSlots_NoStream(inventory, direction)) {
                if (extract(hopper, inventory, i, direction)) {
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
        cir.setReturnValue(false);
    }

    private static int[] getAvailableSlots_NoStream(Inventory inventory, Direction side) {
        if (inventory instanceof SidedInventory) return ((SidedInventory) inventory).getAvailableSlots(side);
        if (inventory instanceof InventoryAvailableSlots) return (((InventoryAvailableSlots) inventory).getAvailableSlots());

        int[] array = new int[inventory.size()];
        Arrays.setAll(array, i -> i);
        return array;
    }
}
