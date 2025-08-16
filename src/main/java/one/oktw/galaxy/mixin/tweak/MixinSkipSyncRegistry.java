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

package one.oktw.galaxy.mixin.tweak;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryAttributeHolder;
import net.fabricmc.fabric.impl.registry.sync.RegistrySyncManager;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = RegistrySyncManager.class, remap = false)
public class MixinSkipSyncRegistry {
    @WrapOperation(method = "createAndPopulateRegistryMap", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/event/registry/RegistryAttributeHolder;hasAttribute(Lnet/fabricmc/fabric/api/event/registry/RegistryAttribute;)Z"), require = 0, remap = false)
    private static boolean skipRegistryWithoutModded(RegistryAttributeHolder instance, RegistryAttribute registryAttribute, Operation<Boolean> original, @Local Registry<?> registry) {
        if (registryAttribute == RegistryAttribute.MODDED) {
            if (registry.getIds().stream().noneMatch(identifier -> !identifier.getNamespace().equals("minecraft") && !identifier.getNamespace().equals("galaxy"))) {
                return false;
            }
        }

        return original.call(instance, registryAttribute);
    }

    @WrapOperation(method = "createAndPopulateRegistryMap", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/Registry;getId(Ljava/lang/Object;)Lnet/minecraft/util/Identifier;"), require = 0)
    private static Identifier skipGalaxyEntries(Registry<?> instance, Object t, Operation<Identifier> original) {
        Identifier id = original.call(instance, t);
        if (id.getNamespace().equals("galaxy")) {
            return null;
        }
        return id;
    }
}
