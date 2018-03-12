package one.oktw.galaxy.task

import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.effect.potion.PotionEffect
import org.spongepowered.api.effect.potion.PotionEffectTypes.*
import org.spongepowered.api.scheduler.Task
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

class ArmorEffect : Consumer<Task> {
    val nightVision = HashSet<UUID>()
    val fire = HashSet<UUID>()
    val water = HashSet<UUID>()
    val protect = HashSet<UUID>()
    val jump = HashMap<UUID, Int>()

    override fun accept(task: Task) {
        val players = Sponge.getServer().onlinePlayers

        nightVision.removeIf { !Sponge.getServer().getPlayer(it).isPresent }
        fire.removeIf { !Sponge.getServer().getPlayer(it).isPresent }
        water.removeIf { !Sponge.getServer().getPlayer(it).isPresent }
        protect.removeIf { !Sponge.getServer().getPlayer(it).isPresent }
        jump.keys.filter { !Sponge.getServer().getPlayer(it).isPresent }.forEach { jump.remove(it) }

        players.forEach {
            val uuid = it.uniqueId
            val effect = it[Keys.POTION_EFFECTS].orElse(ArrayList())

            if (nightVision.contains(uuid)) {
                effect.removeIf { it.type == NIGHT_VISION }
                effect += PotionEffect.builder()
                    .potionType(NIGHT_VISION)
                    .duration(300)
                    .amplifier(0)
                    .particles(false)
                    .build()
            }

            if (fire.contains(uuid)) {
                effect.removeIf { it.type == FIRE_RESISTANCE }
                effect += PotionEffect.builder()
                    .potionType(FIRE_RESISTANCE)
                    .duration(30)
                    .amplifier(0)
                    .particles(false)
                    .build()
            }

            if (water.contains(uuid)) {
                effect.removeIf { it.type == WATER_BREATHING }
                effect += PotionEffect.builder()
                    .potionType(WATER_BREATHING)
                    .duration(30)
                    .amplifier(0)
                    .particles(false)
                    .build()
            }

            if (protect.contains(uuid)) {
                effect.removeIf { it.type == RESISTANCE }
                effect += PotionEffect.builder()
                    .potionType(RESISTANCE)
                    .duration(30)
                    .amplifier(2)
                    .particles(false)
                    .build()
            }

            if (jump.contains(uuid)) {
                effect.removeIf { it.type == JUMP_BOOST }
                effect += PotionEffect.builder()
                    .potionType(JUMP_BOOST)
                    .duration(30)
                    .amplifier(jump[uuid]!!)
                    .particles(false)
                    .build()
            }

            it.offer(Keys.POTION_EFFECTS, effect)
        }
    }
}