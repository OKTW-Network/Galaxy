package one.oktw.galaxy.player.event

import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import one.oktw.galaxy.block.enums.CustomBlocks.ELEVATOR
import one.oktw.galaxy.data.DataBlockType
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.effect.sound.SoundTypes.ENTITY_ENDERMEN_TELEPORT
import org.spongepowered.api.entity.living.player.Player

class Elevator {
    init {
        Keys.IS_SNEAKING.registerEvent(Player::class.java) {
            if (it.endResult.successfulData.firstOrNull { it.key == Keys.IS_SNEAKING }?.get() != true) return@registerEvent

            val player = it.targetHolder as? Player ?: return@registerEvent
            val location = player.location.sub(0.0, 1.0, 0.0)

            if (location[DataBlockType.key].orElse(null) != ELEVATOR) return@registerEvent

            var target: Double? = null
            for (i in 3..8) {
                if (location.sub(0.0, i.toDouble(), 0.0).get(DataBlockType.key).orElse(null) == ELEVATOR) {
                    target = i.toDouble()
                    break
                }
            }
            target?.also {
                player.location = player.location.sub(0.0, it, 0.0)
                player.world.playSound(ENTITY_ENDERMEN_TELEPORT, player.position.sub(0.0, it, 0.0), 1.0)
            }
        }
    }

    @SubscribeEvent
    fun onJump(event: LivingEvent.LivingJumpEvent) {
        val player = event.entity as? Player ?: return
        val location = player.location.sub(0.0, 1.0, 0.0)

        if (location[DataBlockType.key].orElse(null) != ELEVATOR) return

        var target: Double? = null
        for (i in 3..8) {
            if (location.add(0.0, i.toDouble(), 0.0).get(DataBlockType.key).orElse(null) == ELEVATOR) {
                target = i.toDouble()
                break
            }
        }
        target?.also {
            player.location = player.location.add(0.0, it, 0.0)
            player.world.playSound(ENTITY_ENDERMEN_TELEPORT, player.position.add(0.0, it, 0.0), 1.0)
        }
    }
}
