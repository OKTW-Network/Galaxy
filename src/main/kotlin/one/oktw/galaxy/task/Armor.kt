package one.oktw.galaxy.task

import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.effect.potion.PotionEffect
import org.spongepowered.api.effect.potion.PotionEffectTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.scheduler.Task
import java.util.function.Consumer

class Armor : Consumer<Task> {
    private val nightVision = ArrayList<Player>()
    private val adapt = ArrayList<Player>()
    private val protect = ArrayList<Player>()

    override fun accept(task: Task) {
        val players = Sponge.getServer().onlinePlayers

        nightVision.removeIf { !players.contains(it) }
        adapt.removeIf { !players.contains(it) }
        protect.removeIf { !players.contains(it) }

        players.forEach {
            if (nightVision.contains(it)) {
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

            if (adapt.contains(it)) {
                it.transform(Keys.POTION_EFFECTS) {
                    it.apply {
                        this += PotionEffect.builder()
                            .potionType(PotionEffectTypes.FIRE_RESISTANCE)
                            .duration(30)
                            .amplifier(1)
                            .particles(false)
                            .build()

                        this += PotionEffect.builder()
                            .potionType(PotionEffectTypes.WATER_BREATHING)
                            .duration(30)
                            .amplifier(1)
                            .particles(false)
                            .build()
                    }
                }
            }

            if (protect.contains(it)) {
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
        }
    }

    fun addNightVision(player: Player) {
        nightVision += player
    }

    fun removeNightVision(player: Player) {
        nightVision -= player
    }
}