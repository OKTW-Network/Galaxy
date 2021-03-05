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

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import one.oktw.galaxy.block.CustomBlock;
import one.oktw.galaxy.block.listener.CustomBlockClickListener;
import one.oktw.galaxy.block.listener.CustomBlockNeighborUpdateListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BarrierBlock.class)
public abstract class MixinCustomBlockEntity_BarrierBlock extends AbstractBlock implements BlockEntityProvider {
    public MixinCustomBlockEntity_BarrierBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return CustomBlock.Companion.getDUMMY().createBlockEntity();
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof CustomBlockClickListener) return ((CustomBlockClickListener) entity).onClick(player, hand, hit);
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof CustomBlockNeighborUpdateListener) ((CustomBlockNeighborUpdateListener) entity).onNeighborUpdate(false);
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        BlockEntity entity = world.getBlockEntity(pos);
        if (entity instanceof CustomBlockNeighborUpdateListener) ((CustomBlockNeighborUpdateListener) entity).onNeighborUpdate(true);
        return super.getStateForNeighborUpdate(state, direction, newState, world, pos, posFrom);
    }
}
