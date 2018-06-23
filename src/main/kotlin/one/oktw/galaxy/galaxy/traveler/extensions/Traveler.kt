package one.oktw.galaxy.galaxy.traveler.extensions

import one.oktw.galaxy.galaxy.traveler.data.Traveler
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player

fun Traveler.getPlayer(): Player? = Sponge.getServer().getPlayer(uuid).orElse(null)
