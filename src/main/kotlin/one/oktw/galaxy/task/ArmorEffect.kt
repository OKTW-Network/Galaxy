package one.oktw.galaxy.task

import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.effect.potion.PotionEffect
import org.spongepowered.api.effect.potion.PotionEffectTypes
import org.spongepowered.api.scheduler.Task
import java.util.*
import java.util.function.Consumer
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
            if (nightVision.contains(uuid)) {
                it.transform(Keys.POTION_EFFECTS) {
                    it.apply {
                        this += PotionEffect.builder()
                            .potionType(PotionEffectTypes.NIGHT_VISION)
                            .duration(30)
                            .amplifier(1)
                            .particles(false)
                            .build()
                    }
                }
            }

            if (fire.contains(uuid)) {
                it.transform(Keys.POTION_EFFECTS) {
                    it.apply {
                        this += PotionEffect.builder()
                            .potionType(PotionEffectTypes.FIRE_RESISTANCE)
                            .duration(30)
                            .amplifier(1)
                            .particles(false)
                            .build()
                    }
                }
            }

            if (water.contains(uuid)) {
                it.transform(Keys.POTION_EFFECTS) {
                    it.apply {
                        this += PotionEffect.builder()
                            .potionType(PotionEffectTypes.WATER_BREATHING)
                            .duration(30)
                            .amplifier(1)
                            .particles(false)
                            .build()
                    }
                }
            }

            if (protect.contains(uuid)) {
                it.transform(Keys.POTION_EFFECTS) {
                    it.apply {
                        this += PotionEffect.builder()
                            .potionType(PotionEffectTypes.RESISTANCE)
                            .duration(30)
                            .amplifier(3)
                            .particles(false)
                            .build()
                    }
                }
            }

            if (jump.contains(uuid)) {
                it.transform(Keys.POTION_EFFECTS) {
                    it.apply {
                        this += PotionEffect.builder()
                            .potionType(PotionEffectTypes.JUMP_BOOST)
                            .duration(30)
                            .amplifier(jump[uuid]!!)
                            .particles(false)
                            .build()
                    }
                }
            }
        }
    }
}