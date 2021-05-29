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

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

@Mixin(Structure.class)
public class MixinCustomBlockEntity_Structure {
    @Inject(method = "place(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/structure/StructurePlacementData;Ljava/util/Random;I)Z",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/ServerWorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void hackPlace(ServerWorldAccess serverWorldAccess, BlockPos pos, BlockPos blockPos, StructurePlacementData placementData, Random random, int i, CallbackInfoReturnable<Boolean> cir, List<Structure.StructureBlockInfo> list, BlockBox blockBox, List<BlockPos> list2, List<Pair<BlockPos, CompoundTag>> list3, int j, int k, int l, int m, int n, int o, List<Structure.StructureBlockInfo> list4, Iterator<Structure.StructureBlockInfo> var18, Structure.StructureBlockInfo structureBlockInfo, BlockPos blockPos2, FluidState fluidState, BlockState blockState) {
        // Workaround structure barrier bug
        if (structureBlockInfo.state.getBlock() == Blocks.BARRIER) serverWorldAccess.setBlockState(blockPos2, Blocks.AIR.getDefaultState(), 20);
    }
}
