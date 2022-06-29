/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2022
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

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.server.command.CloneCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

@Mixin(CloneCommand.class)
public class MixinCustomBlockEntity_Clone {
    @Inject(method = "execute", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", ordinal = 3), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void hackClone(ServerCommandSource source, BlockPos begin, BlockPos end, BlockPos destination, Predicate<CachedBlockPosition> filter, CloneCommand.Mode mode, CallbackInfoReturnable<Integer> cir, BlockBox blockBox, BlockPos blockPos, BlockBox blockBox2, int i, ServerWorld serverWorld, List<CloneCommand.BlockInfo> list, List<CloneCommand.BlockInfo> list2, List<CloneCommand.BlockInfo> list3, Deque<BlockPos> deque, BlockPos blockPos2, List<CloneCommand.BlockInfo> list4, List<CloneCommand.BlockInfo> list5, int l, Iterator<CloneCommand.BlockInfo> var19, CloneCommand.BlockInfo blockInfo) {
        // Workaround clone override custom block NBT
        if (blockInfo.state.getBlock() == Blocks.BARRIER) {
            serverWorld.setBlockState(blockInfo.pos, Blocks.AIR.getDefaultState(), Block.NO_REDRAW | Block.FORCE_STATE);
        }
    }
}
