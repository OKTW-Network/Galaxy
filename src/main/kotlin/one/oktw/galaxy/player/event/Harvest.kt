package one.oktw.galaxy.player.event

import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.util.math.BlockPos
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.block.InteractBlockEvent
import org.spongepowered.api.event.filter.cause.First

class Harvest {
    @Listener
    fun onClick(event: InteractBlockEvent.Secondary.MainHand, @First player: Player) {
        val block = event.targetBlock
        val age = block[Keys.GROWTH_STAGE].orElse(null) ?: return
        val max = when (block.state.type) {
            BlockTypes.COCOA -> 2
            BlockTypes.MELON_STEM -> 8
            BlockTypes.PUMPKIN_STEM -> 8
            else -> 7
        }

        if (age != max) return

        if ((player as EntityPlayerMP).interactionManager.tryHarvestBlock(block.position.run { BlockPos(x, y, z) })) {
            block.location.get().block = block.state.with(Keys.GROWTH_STAGE, 0).get()
        }
    }
}
