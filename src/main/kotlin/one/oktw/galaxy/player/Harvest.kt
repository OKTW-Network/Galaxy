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

package one.oktw.galaxy.player

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.minecraft.block.*
import net.minecraft.block.Blocks.*
import net.minecraft.server.world.ServerWorld
import net.minecraft.state.property.IntProperty
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.event.type.PlayerInteractBlockEvent

class Harvest {
    companion object {
         fun registerEvent() = Harvest().registerEvents()
    }
    private fun registerEvents() {
        val playerInteractBlockListener = fun(event: PlayerInteractBlockEvent) {
            val world = main!!.server.getWorld(event.player.dimension)
            val hand = event.packet.hand
            if (hand != Hand.MAIN_HAND) {
                return
            }
            val blockHitResult = event.packet.hitY
            val blockState = world.getBlockState(blockHitResult.blockPos)

            val isMature = when (blockState.block) {
                in listOf(WHEAT, CARROTS, POTATOES) -> blockState.let((blockState.block as CropBlock)::isMature)
                BEETROOTS -> blockState.let((blockState.block as BeetrootsBlock)::isMature)
                COCOA -> blockState[CocoaBlock.AGE] >= 2
                NETHER_WART -> blockState[NetherWartBlock.AGE] >= 3
                MELON -> isNextTo(world, blockHitResult.blockPos, ATTACHED_MELON_STEM)
                PUMPKIN -> isNextTo(world, blockHitResult.blockPos, ATTACHED_PUMPKIN_STEM)
                else -> false
            }

            if (isMature) {
                val ageProperties = when (blockState.block) {
                    in listOf(WHEAT, CARROTS, POTATOES) -> CropBlock.AGE
                    BEETROOTS -> BeetrootsBlock.AGE
                    COCOA -> CocoaBlock.AGE
                    NETHER_WART -> NetherWartBlock.AGE
                    else -> IntProperty.of("AGE", 0, 1)
                }
                GlobalScope.launch {
                    withContext(main!!.server.asCoroutineDispatcher()) {
                        world.breakBlock(blockHitResult.blockPos, true)
                        if (blockState.block != PUMPKIN && blockState.block != MELON){
                            world.setBlockState(blockHitResult.blockPos, blockState.with(ageProperties, 0))
                            world.updateNeighbors(blockHitResult.blockPos, blockState.block)
                        }
                    }
                }
                return
            }
        }
        main!!.eventManager.register(PlayerInteractBlockEvent::class, listener = playerInteractBlockListener)
    }
    private fun isNextTo(world: ServerWorld, blockPos: BlockPos, block: Block): Boolean {
        return world.getBlockState(blockPos.add(1, 0, 0)).block == block ||
                world.getBlockState(blockPos.add(0, 0, 1)).block == block ||
                world.getBlockState(blockPos.add(-1, 0, 0)).block == block ||
                world.getBlockState(blockPos.add(0, 0, -1)).block == block
    }
}
