package one.oktw.galaxy.event

import com.flowpowered.math.imaginary.Quaterniond
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
                .distanceLimit(if (target != null) target.intersection.distance(source) else gun.range)
                .stopFilter(BlockRay.continueAfterFilter(BlockRay.onlyAirFilter(), 1))
                .end().filter { it.location.block.type != BlockTypes.AIR }

        if (target != null) {
            val entity = target.entity as Living

            if (!wall.isPresent) {
                entity.transform(Keys.HEALTH) { it - gun.damage }
                entity.damage(0.0, DamageSources.MAGIC)
                if (entity.health().get() < 1) {
                    player.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, player.location.position, 1.0)
                } else {
                    player.playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, player.location.position, 1.0, 0.5)
                }
            }
        }

        // Show trajectory
        launch {
            var pos = source
            val line = when {
                wall.isPresent -> wall.get().position.sub(source)
                target != null -> target.intersection.sub(source)
                else -> Quaterniond.fromAxesAnglesDeg(player.rotation.x, -player.rotation.y, player.rotation.z).direction.mul(gun.range)
            }
            val interval = when (line.abs().maxAxis) {
                0 -> line.abs().x.div(0.3)
                1 -> line.abs().y.div(0.3)
                2 -> line.abs().z.div(0.3)
                else -> 10.0
            }

            for (i in 0..interval.toInt()) {
                world.spawnParticles(
                        ParticleEffect.builder()
                                .type(ParticleTypes.MAGIC_CRITICAL_HIT)
                                .build(),
                        pos
                )
                pos = pos.add(line.div(interval))
            }
        }

        // Play gun sound
        world.playSound(SoundTypes.ENTITY_BLAZE_HURT, SoundCategories.PLAYER, source, 1.0, 2.0, 1.0)
        world.playSound(SoundTypes.ENTITY_FIREWORK_BLAST, SoundCategories.PLAYER, source, 1.0, 0.0, 1.0)
        world.playSound(SoundTypes.BLOCK_PISTON_EXTEND, SoundCategories.PLAYER, source, 1.0, 2.0, 1.0)
    }
}