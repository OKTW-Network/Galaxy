/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2019
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

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import one.oktw.galaxy.network.ItemFunctionAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Item.class)
public class MixinItem_Item implements ItemFunctionAccessor {

    @Shadow
    protected static HitResult rayTrace(World world_1, PlayerEntity playerEntity_1, RayTraceContext.FluidHandling rayTraceContext$FluidHandling_1) {
        return null;
    }

    @Override
    public HitResult getRayTrace(World world, PlayerEntity playerEntity, RayTraceContext.FluidHandling fluidHandling) {
        return rayTrace(world, playerEntity, fluidHandling);
    }
}
