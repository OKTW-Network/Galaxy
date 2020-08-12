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

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import one.oktw.galaxy.Main;
import one.oktw.galaxy.event.enums.BreakType;
import one.oktw.galaxy.event.type.BlockBreakEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Explosion.class)
public class MixinBlockBreak_Explosion {
    @Redirect(method = "affectWorld", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"
    ))
    private boolean onDestroyByExplosion(World world, BlockPos pos, BlockState state) {
        Main main = Main.Companion.getMain();
        if (main == null) return world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        if (!main.getEventManager().emit(new BlockBreakEvent(world, pos, world.getBlockState(pos), BreakType.EXPLOSION, null)).getCancel()) {
            System.out.println("done");
            return world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
        }
        return false;
    }
}
