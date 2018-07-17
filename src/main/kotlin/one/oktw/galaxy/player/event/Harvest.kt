package one.oktw.galaxy.player.event

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.math.BlockPos
import org.spongepowered.api.block.BlockTypes.*
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.util.Direction
import org.spongepowered.api.world.gen.populator.Pumpkin

class Harvest {
    @Listener
    fun onClick(event: InteractBlockEvent.Secondary.MainHand, @First player: Player) {
        val block = event.targetBlock

        when (block.state.type) {
            PUMPKIN -> {
                val location = block.location.get();

                if (
                    location.add(0.0, -1.0, 0.0).blockType != GRASS &&
                    location.add(0.0, -1.0, 0.0).blockType != DIRT &&
                    location.add(0.0, -1.0, 0.0).blockType != FARMLAND
                ) {
                    return
                }

                if (
                    location.add(1.0, 0.0, 0.0).blockType != PUMPKIN_STEM &&
                    location.add(0.0, 0.0, 1.0).blockType != PUMPKIN_STEM &&
                    location.add(-1.0, 0.0, 0.0).blockType != PUMPKIN_STEM &&
                    location.add(0.0, 0.0, -1.0).blockType != PUMPKIN_STEM
                ) {
                    return
                }

                (player as EntityPlayerMP).interactionManager.tryHarvestBlock(block.position.run { BlockPos(x, y, z) })
            }
            MELON_BLOCK -> {
                val location = block.location.get();

                if (
                        location.add(0.0, -1.0, 0.0).blockType != GRASS &&
                        location.add(0.0, -1.0, 0.0).blockType != DIRT &&
                        location.add(0.0, -1.0, 0.0).blockType != FARMLAND
                ) {
                    return
                }

                if (
                        location.add(1.0, 0.0, 0.0).blockType != MELON_STEM &&
                        location.add(0.0, 0.0, 1.0).blockType != MELON_STEM &&
                        location.add(-1.0, 0.0, 0.0).blockType != MELON_STEM &&
                        location.add(0.0, 0.0, -1.0).blockType != MELON_STEM
                ) {
                    return
                }

                (player as EntityPlayerMP).interactionManager.tryHarvestBlock(block.position.run { BlockPos(x, y, z) })
            }
            else -> {
                val age = block[Keys.GROWTH_STAGE].orElse(null) ?: return
                val max = when (block.state.type) {
                    COCOA -> 2
                    BEETROOTS -> 3
                    PUMPKIN_STEM -> 8 // don't harvest it, we harvest block instead
                    MELON_STEM -> 8 // don't harvest it, we harvest block instead
                    else -> 7
                }

                if (age != max) return

                if ((player as EntityPlayerMP).interactionManager.tryHarvestBlock(block.position.run { BlockPos(x, y, z) })) {
                    block.location.get().block = block.state.with(Keys.GROWTH_STAGE, 0).get()
                }
            }
        }
    }
}
