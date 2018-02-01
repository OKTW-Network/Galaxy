package one.oktw.galaxy.event

import com.flowpowered.math.imaginary.Quaterniond
import com.flowpowered.math.vector.Vector3d
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.travelerManager
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
import org.spongepowered.api.world.World

class Gun {
    @Listener
    @Suppress("unused")
    fun onInteractItem(event: InteractItemEvent.Secondary, @Getter("getSource") player: Player) {
        if (event.itemStack.type != ItemTypes.WOODEN_SWORD) return

        val gun = travelerManager.getTraveler(player).item.gun ?: return
        val world = player.world
        val source = player.location.position.add(0.0, 1.5, 0.0)
        val target = world.getIntersectingEntities(
                player,
                gun.range,
                { it.entity !is Player && it.entity is Living && (it.entity as Living).health().get() > 0 }
        ).firstOrNull()
        val wall = BlockRay.from(player)
                .distanceLimit(target?.distance ?: gun.range)
                .stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                .end().filter { it.location.block.type != BlockTypes.AIR }

        if (target != null) {
            val entity = target.entity as Living

            if (!wall.isPresent) {
                entity.damage(0.0, DamageSources.MAGIC)
                entity.transform(Keys.HEALTH) { it - gun.damage }
                if (entity.health().get() < 1) {
                    player.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, player.location.position, 1.0)
                } else {
                    player.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, player.location.position, 1.0, 0.5)
                }
            }
        }

        showParticle(
                world,
                source,
                when {
                    wall.isPresent -> wall.get().position.sub(source)
                    target != null -> target.intersection.sub(source)
                    else -> Quaterniond.fromAxesAnglesDeg(player.rotation.x, -player.rotation.y, player.rotation.z).direction.mul(gun.range)
                }
        )

        world.playSound(SoundTypes.ENTITY_PLAYER_SMALL_FALL, SoundCategories.PLAYER, player.location.position, 1.0, 0.5)
    }

    private fun showParticle(world: World, start: Vector3d, target: Vector3d) {
        launch {
            var pos = start
            val interval = when (target.abs().maxAxis) {
                0 -> target.abs().x.div(0.3)
                1 -> target.abs().y.div(0.3)
                2 -> target.abs().z.div(0.3)
                else -> 10.0
            }

            for (i in 0..interval.toInt()) {
                world.spawnParticles(
                        ParticleEffect.builder()
                                .type(ParticleTypes.MAGIC_CRITICAL_HIT)
                                .build(),
                        pos
                )
                pos = pos.add(target.div(interval))
            }
        }
    }
}