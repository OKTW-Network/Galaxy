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

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(StructureTemplate.class)
public class MixinCustomBlockEntity_Structure {
    @Inject(method = "placeInWorld",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/ServerLevelAccessor;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void hackPlace(ServerLevelAccessor world, BlockPos pos, BlockPos pivot, StructurePlaceSettings placementData, RandomSource random, int flags, CallbackInfoReturnable<Boolean> cir, List<StructureTemplate.StructureBlockInfo> list, BoundingBox blockBox, List<BlockPos> list2, List<BlockPos> list3, List<Pair<BlockPos, CompoundTag>> list4, int i, int j, int k, int l, int m, int n, List<StructureTemplate.StructureBlockInfo> list5, ProblemReporter.ScopedCollector logging, Iterator<StructureTemplate.StructureBlockInfo> var20, StructureTemplate.StructureBlockInfo structureBlockInfo, BlockPos blockPos, FluidState fluidState, BlockState blockState) {
        // Workaround structure barrier bug
        if (structureBlockInfo.state().getBlock() == Blocks.BARRIER) {
            world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
        }
    }
}
