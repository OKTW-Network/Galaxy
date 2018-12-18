package one.oktw.galaxy.block.enums

enum class CustomBlocks(val id: Int? = null, val hasGUI: Boolean = false) {
    DUMMY,
    CONTROL_PANEL(null, true),
    PLANET_TERMINAL(null, true),
    HT_CRAFTING_TABLE(1, true),
    ELEVATOR(2),
    TELEPORTER(3, true),
    TELEPORTER_ADVANCED(4, true),
    TELEPORTER_FRAME(5)
}
