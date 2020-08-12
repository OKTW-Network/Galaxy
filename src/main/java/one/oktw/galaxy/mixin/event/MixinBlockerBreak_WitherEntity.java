/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2020
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

package one.oktw.galaxy.mixin.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import one.oktw.galaxy.Main;
import one.oktw.galaxy.event.enums.BreakType;
import one.oktw.galaxy.event.type.BlockBreakEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WitherEntity.class)
public class MixinBlockerBreak_WitherEntity {
    @Redirect(method = "mobTick", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/world/World;breakBlock(Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/entity/Entity;)Z"
    ))
    private boolean onBreakByWither(World world, BlockPos pos, boolean drop, Entity breakingEntity) {
        Main main = Main.Companion.getMain();
        if (main == null) return world.breakBlock(pos, drop, breakingEntity);
        if (!main.getEventManager().emit(new BlockBreakEvent(world, pos, world.getBlockState(pos), BreakType.WITHER, null)).getCancel()) {
            return world.breakBlock(pos, drop, breakingEntity);
        }
        return false;
    }
}
