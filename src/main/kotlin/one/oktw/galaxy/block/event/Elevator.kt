package one.oktw.galaxy.block.event

import net.minecraftforge.event.entity.living.LivingEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import one.oktw.galaxy.block.enums.CustomBlocks.ELEVATOR
import one.oktw.galaxy.data.DataBlockType
import org.spongepowered.api.block.BlockTypes.MOB_SPAWNER
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.effect.sound.SoundTypes.ENTITY_ENDERMEN_TELEPORT
import org.spongepowered.api.entity.living.player.Player

class Elevator {
    init {
        Keys.IS_SNEAKING.registerEvent(Player::class.java) {
            if (it.endResult.successfulData.firstOrNull { it.key == Keys.IS_SNEAKING }?.get() != true) return@registerEvent

            val player = it.targetHolder as? Player ?: return@registerEvent
            val location = player.location.sub(0.0, 1.0, 0.0)

            if (location.blockType != MOB_SPAWNER || location[DataBlockType.key].orElse(null) != ELEVATOR) return@registerEvent

            var target: Double? = null
            for (i in 3..16) {
                if (location.sub(0.0, i.toDouble(), 0.0).get(DataBlockType.key).orElse(null) == ELEVATOR) {
                    target = i.toDouble()
                    break
                }
            }
            target?.also {
                player.location = player.location.sub(0.0, it, 0.0)
                player.world.playSound(ENTITY_ENDERMEN_TELEPORT, player.position, 1.0)
            }
        }
    }

    @SubscribeEvent
    fun onJump(event: LivingEvent.LivingJumpEvent) {
        if (event.entity !is Player) return

        val player = event.entity as Player
        val location = player.location.sub(0.0, 1.0, 0.0)

        if (location.blockType != MOB_SPAWNER || location[DataBlockType.key].orElse(null) != ELEVATOR) return

        var target: Double? = null
        for (i in 3..16) {
            if (location.add(0.0, i.toDouble(), 0.0).get(DataBlockType.key).orElse(null) == ELEVATOR) {
                target = i.toDouble()
                break
            }
        }
        target?.also {
            player.location = player.location.add(0.0, it, 0.0)
            player.world.playSound(ENTITY_ENDERMEN_TELEPORT, player.position, 1.0)
        }
    }
}
