package one.oktw.galaxy.internal

enum class Groups(val group: Int) {
    VISITOR(0),
    MEMBER(1),
    ADMIN(2);

    companion object {
        fun fromInt(group: Int): Groups = Groups.values().first { it.group == group }
    }
}
