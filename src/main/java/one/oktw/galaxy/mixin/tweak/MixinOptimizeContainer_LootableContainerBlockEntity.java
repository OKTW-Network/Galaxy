/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2023
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

import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootableContainerBlockEntity.class)
public abstract class MixinOptimizeContainer_LootableContainerBlockEntity {
    @Shadow
    protected abstract DefaultedList<ItemStack> method_11282();

    @Inject(method = "isEmpty",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/LootableContainerBlockEntity;generateLoot(Lnet/minecraft/entity/player/PlayerEntity;)V", shift = At.Shift.AFTER),
        cancellable = true)
    private void replaceStream(CallbackInfoReturnable<Boolean> cir) {
        for (ItemStack itemStack : method_11282()) {
            if (!itemStack.isEmpty()) {
                cir.setReturnValue(false);
                return;
            }
        }

        cir.setReturnValue(true);
    }
}
