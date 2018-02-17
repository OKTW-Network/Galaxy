package one.oktw.galaxy.enums

import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.ItemTypes.IRON_SWORD
import org.spongepowered.api.item.ItemTypes.WOODEN_SWORD

enum class GunType(val item: ItemType, val id: Short) {
    // Pistol
    PISTOL_ORIGIN(WOODEN_SWORD, 1),
    PISTOL_GRAY(WOODEN_SWORD, 2),
    PISTOL_RAINBOW(WOODEN_SWORD, 3),
    PISTOL_GRAY_ARROW(WOODEN_SWORD, 4),
    PISTOL_RAINBOW_ARROW(WOODEN_SWORD, 5),
    PISTOL_GRAY_SLASH(WOODEN_SWORD, 6),
    PISTOL_RAINBOW_SLASH(WOODEN_SWORD, 7),
    PISTOL_GRAY_DASH(WOODEN_SWORD, 8),
    PISTOL_RAINBOW_DASH(WOODEN_SWORD, 9),

    // Sniper
    SNIPER_ORIGIN(IRON_SWORD, 1),
    SNIPER_ORIGIN_OPEN(IRON_SWORD, 2),
    SNIPER_SIGHT(IRON_SWORD, 3),
    SNIPER_SIGHT_OPEN(IRON_SWORD, 4),
}
