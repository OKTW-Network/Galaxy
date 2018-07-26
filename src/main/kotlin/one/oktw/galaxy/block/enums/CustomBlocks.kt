package one.oktw.galaxy.block.enums

enum class CustomBlocks(val id: Int? = null, val hasGUI: Boolean = false) {
    DUMMY,
    CONTROL_PANEL(null, true),
    PLANET_TERMINAL(null, true),
    HT_CRAFTING_TABLE(1, true),
    ELEVATOR(2),
    PORTAL(3),
    ADVANCED_PORTAL(4),
    PORTAL_FRAME(5)
}
