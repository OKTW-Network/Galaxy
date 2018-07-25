package one.oktw.galaxy.galaxy.planet.data

import one.oktw.galaxy.Main.Companion.dummyUUID
import one.oktw.galaxy.galaxy.planet.enums.PlanetType
import one.oktw.galaxy.galaxy.planet.enums.PlanetType.NORMAL
import org.spongepowered.api.effect.potion.PotionEffect
import java.util.*
import kotlin.collections.ArrayList

data class Planet(
    val uuid: UUID = UUID.randomUUID(),
    var world: UUID = dummyUUID,
    var name: String = "",
    val type: PlanetType = NORMAL,
    var size: Int = 32,
    var level: Short = 1,
    var effect: List<PotionEffect> = ArrayList(),
    var visitable: Boolean = true,
    var lastTime: Date = Date()
)
