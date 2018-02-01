package one.oktw.galaxy.event

import kotlinx.coroutines.experimental.launch
import org.spongepowered.api.block.BlockTypes
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.effect.particle.ParticleEffect
import org.spongepowered.api.effect.particle.ParticleTypes
import org.spongepowered.api.effect.sound.SoundCategories
import org.spongepowered.api.effect.sound.SoundTypes
import org.spongepowered.api.entity.living.Living
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.item.inventory.InteractItemEvent
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.util.blockray.BlockRay

class Gun {
    @Listener
    @Suppress("unused")
    fun onInteractItem(event: InteractItemEvent.Secondary, @Getter("getSource") player: Player) {
        if (event.itemStack.type != ItemTypes.WOODEN_SWORD) return

        val world = player.world
        val target = world.getIntersectingEntities(
                player,
                20.0,
                { it.entity !is Player && it.entity is Living && (it.entity as Living).health().get() > 0 }
        ).firstOrNull()

        if (target != null) {
            val entity = target.entity as Living
            val wall = BlockRay.from(player)
                    .distanceLimit(target.distance)
                    .stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                    .end().filter { it.location.block.type != BlockTypes.AIR }

            if (!wall.isPresent) {
                player.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, player.location.position, 1.0, 0.5)

                entity.damage(0.0, DamageSources.MAGIC)
                entity.offer(Keys.HEALTH, entity.health().get() - 5)
            }

            launch {
                var pos = player.location.position.add(0.0, 1.5, 0.0)
                val direction = if (wall.isPresent) wall.get().position.sub(pos) else target.intersection.sub(pos)
                val interval = when (direction.abs().maxAxis) {
                    0 -> direction.abs().x.div(0.3)
                    1 -> direction.abs().y.div(0.3)
                    2 -> direction.abs().z.div(0.3)
                    else -> 20.0
                }

                for (i in 0..interval.toInt()) {
                    world.spawnParticles(
                            ParticleEffect.builder()
                                    .type(ParticleTypes.MAGIC_CRITICAL_HIT)
                                    .build(),
                            pos
                    )
                    pos = pos.add(direction.div(interval))
                }
            }
        }

        world.playSound(SoundTypes.ENTITY_PLAYER_SMALL_FALL, SoundCategories.PLAYER, player.location.position, 1.0, 0.5)
    }
}