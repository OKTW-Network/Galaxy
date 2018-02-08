package one.oktw.galaxy.event

import com.flowpowered.math.imaginary.Quaterniond
import kotlinx.coroutines.experimental.launch
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EntityDamageSource
import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.enums.UpgradeType.*
import one.oktw.galaxy.helper.CoolDownHelper
import one.oktw.galaxy.types.item.Gun
import org.spongepowered.api.block.BlockTypes.*
import org.spongepowered.api.data.property.entity.EyeLocationProperty
import org.spongepowered.api.effect.particle.ParticleEffect
import org.spongepowered.api.effect.particle.ParticleTypes
import org.spongepowered.api.effect.sound.SoundCategories
import org.spongepowered.api.effect.sound.SoundType
import org.spongepowered.api.effect.sound.SoundTypes
import org.spongepowered.api.entity.living.ArmorStand
import org.spongepowered.api.entity.living.Living
import org.spongepowered.api.entity.living.monster.Boss
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.event.item.inventory.InteractItemEvent
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.util.blockray.BlockRay
import java.lang.Math.random
import java.util.Arrays.asList
import kotlin.math.roundToInt

class Gun {
    @Listener
    @Suppress("unused")
    fun onInteractItem(event: InteractItemEvent.Secondary.MainHand, @Getter("getSource") player: Player) {
        val itemStack = event.itemStack
        if (itemStack.type !in asList(ItemTypes.WOODEN_SWORD, ItemTypes.IRON_SWORD) || !itemStack[DataUUID.key].isPresent) return

        val world = player.world
        val gun = travelerManager.getTraveler(player).item
                .filter { it is Gun }
                .find { it.uuid == itemStack[DataUUID.key].get() } as Gun? ?: return
        val source = player.getProperty(EyeLocationProperty::class.java)
                .map(EyeLocationProperty::getValue).orElse(null) ?: return

        var cooling = gun.cooling
        var range = gun.range
        var damage = gun.damage
        var through = gun.through
        var maxTemp = gun.maxTemp

        // TODO
        gun.upgrade.forEach {
            when (it.type) {
                DAMAGE -> damage += it.level * 3
                RANGE -> range += it.level * 5
                COOLING -> cooling += it.level * 2
                THROUGH -> through += it.level
                HEAT -> maxTemp += it.level * 5
            }
        }

        var heatStatus = CoolDownHelper.getCoolDown(gun.uuid)
        if (heatStatus == null) {
            heatStatus = CoolDownHelper.HeatStatus(gun.uuid, max = maxTemp, cooling = cooling)
            CoolDownHelper.addCoolDown(heatStatus)
        }

        if (heatStatus.isOverheat()) return

        if (heatStatus.addHeat(gun.heat)) {
            world.playSound(SoundType.of("gun.overheat"), SoundCategories.PLAYER, source, 1.0)
        }

        val target = world.getIntersectingEntities(
                player,
                range,
                { it.entity is Living && it.entity !is Player && it.entity !is ArmorStand && (it.entity as EntityLivingBase).isEntityAlive }
        )
        val wall = BlockRay.from(player)
                .distanceLimit(if (!target.isEmpty()) target.first().intersection.distance(source) else range)
                .skipFilter {
                    when (it.location.blockType) {
                        AIR,
                        GLASS, STAINED_GLASS,
                        GLASS_PANE, STAINED_GLASS_PANE,
                        IRON_BARS,
                        TALLGRASS, DOUBLE_PLANT,
                        TORCH, REDSTONE_TORCH, UNLIT_REDSTONE_TORCH,
                        REEDS,
                        LEAVES, LEAVES2 -> false

                        else -> true
                    }
                }.build()

        if (!target.isEmpty() && !wall.hasNext()) target.stream()
                .filter { it.entity !is Boss }
                .sorted { hit1, hit2 -> ((hit1.intersection.distance(source) - hit2.intersection.distance(source)) * 10).roundToInt() }
                .limit(through.toLong())
                .forEach {
                    val entity = it.entity as Living

                    val damageSource = EntityDamageSource("player", player as EntityPlayer).setProjectile() as DamageSource
                    (entity as EntityLivingBase).hurtResistantTime = 0
                    entity.damage(damage, damageSource)
                    damage *= 0.9

                    if ((entity as EntityLivingBase).isEntityAlive) {
                        player.playSound(
                                SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP,
                                SoundCategories.PLAYER,
                                player.location.position,
                                1.0
                        )
                    } else {
                        player.playSound(
                                SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP,
                                SoundCategories.PLAYER,
                                player.location.position,
                                1.0,
                                0.5
                        )
                    }
                }

        // Show trajectory
        launch {
            val line = when {
                wall.hasNext() -> wall.next().position.sub(source)
                !target.isEmpty() -> target.first().intersection.sub(source)
                else -> Quaterniond.fromAxesAnglesDeg(player.rotation.x, -player.rotation.y, player.rotation.z).direction.mul(range)
            }
            val interval = when (line.abs().maxAxis) {
                0 -> line.abs().x.div(0.3)
                1 -> line.abs().y.div(0.3)
                2 -> line.abs().z.div(0.3)
                else -> 10.0
            }
            var pos = source.add(line.div(interval / 4))

            for (i in 4..interval.roundToInt()) {
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
        world.playSound(
                SoundType.of("gun.shot"),
                SoundCategories.PLAYER,
                source,
                1.0,
                1 + random() / 10 - random() / 10
        )
    }
}