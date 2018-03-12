package one.oktw.galaxy.task

import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.effect.potion.PotionEffect
import org.spongepowered.api.effect.potion.PotionEffectType
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.scheduler.Task
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PlayerEffect : Consumer<Task> {
    private val server = Sponge.getServer()
    private val effect = HashMap<UUID, HashMap<PotionEffectType, Int>>()

    fun addEffect(player: Player, type: PotionEffectType, level: Int = 0) {
        effect.getOrPut(player.uniqueId) { HashMap() }[type] = level

        player.transform(Keys.POTION_EFFECTS) {
            val effectList = it ?: ArrayList()

            effectList += PotionEffect.builder()
                .potionType(type)
                .amplifier(level)
                .duration(Int.MAX_VALUE)
                .particles(false)
                .build()

            effectList
        }
    }

    fun removeEffect(player: Player, type: PotionEffectType) {
        effect[player.uniqueId]?.remove(type)

        player.transform(Keys.POTION_EFFECTS) { it?.apply { removeIf { it.type == type } } }
    }

    fun removeAllEffect(player: Player) {
        effect[player.uniqueId]?.forEach { map ->
            player.transform(Keys.POTION_EFFECTS) { it?.apply { removeIf { it.type == map.key } } }
        }
        effect -= player.uniqueId
    }

    fun hasEffect(player: Player, type: PotionEffectType): Boolean {
        return effect[player.uniqueId]?.get(type) != null
    }

    override fun accept(task: Task) {
        effect.forEach { eff ->
            val player = server.getPlayer(eff.key).orElse(null)
            if (player == null) {
                effect -= eff.key
                return@forEach
            }

            player.transform(Keys.POTION_EFFECTS) {
                val effectList = it ?: ArrayList()

                eff.value.forEach {
                    effectList += PotionEffect.builder()
                        .potionType(it.key)
                        .amplifier(it.value)
                        .duration(Int.MAX_VALUE)
                        .particles(false)
                        .build()
                }

                effectList
            }
        }
    }
}