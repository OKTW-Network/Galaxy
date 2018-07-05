package one.oktw.galaxy.player.event

import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.spongepowered.api.block.BlockTypes.IRON_BLOCK
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player

class Elevator {
    init {
        Keys.IS_SNEAKING.registerEvent(Player::class.java) {
            if (it.endResult.successfulData.firstOrNull()?.get() != true) return@registerEvent

            val player = it.targetHolder as? Player ?: return@registerEvent
            val location = player.location.sub(0.0, 1.0, 0.0)

            if (location.blockType != IRON_BLOCK) return@registerEvent

            var target: Double? = null
            for (i in 3..8) {
                if (location.sub(0.0, i.toDouble(), 0.0).blockType == IRON_BLOCK) {
                    target = i.toDouble()
                    break
                }
            }
            target?.let { player.setLocation(player.location.sub(0.0, it, 0.0)) }
        }
    }

    @SubscribeEvent
    fun onJump(event: LivingEvent.LivingJumpEvent) {
        val player = event.entity as? Player ?: return
        val location = player.location.sub(0.0, 1.0, 0.0)

        if (location.blockType != IRON_BLOCK) return

        var target: Double? = null
        for (i in 3..8) {
            if (location.add(0.0, i.toDouble(), 0.0).blockType == IRON_BLOCK) {
                target = i.toDouble()
                break
            }
        }
        target?.let { player.setLocation(player.location.add(0.0, it, 0.0)) }
    }
}
