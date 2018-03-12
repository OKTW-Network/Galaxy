package one.oktw.galaxy.enums

import one.oktw.galaxy.enums.ItemType.*

enum class GunType(val type: ItemType, val id: Short) {
    // Pistol
    PISTOL_ORIGIN(GUN,5),
    PISTOL_GRAY(GUN,6),
    PISTOL_RAINBOW(GUN,7),

    // Sniper
    SNIPER_SIGHT(SNIPER,1),
    SNIPER_SIGHT_OPEN(SNIPER,2),
}
