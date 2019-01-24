package one.oktw.galaxy.item.enums

enum class UpgradeType(val id: Int) {
    BASE(16),

    // Machine
    RANGE(999),
    SPEED(998),

    // Weapon or Tool
    DAMAGE(997),
    COOLING(9999),
    COOLING_LV5(21),
    COOLING_LV4(20),
    COOLING_LV3(19),
    COOLING_LV2(18),
    COOLING_LV1(17),
    HEAT(996),
    THROUGH(995),

    // Armor
    SHIELD(994),
    FLEXIBLE(993),
    ADAPT(992),
    FLY(991),
    NIGHT_VISION(990),
    GPS(989),
    DETECTOR(988)
}
