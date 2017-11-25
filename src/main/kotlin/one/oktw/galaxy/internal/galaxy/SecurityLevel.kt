package one.oktw.galaxy.internal.galaxy

enum class SecurityLevel(val level: Int) {
    MEMBER(0),
    VISIT(1),
    PUBLIC(2);

    companion object {
        fun fromInt(level: Int): SecurityLevel = SecurityLevel.values().first { it.level == level }
    }
}
