package one.oktw.galaxy.internal.galaxy

enum class Groups(val index: Int) {
    VISITOR(0),
    MEMBER(1),
    ADMIN(2);

    companion object {
        fun fromInt(index: Int): Groups = Groups.values().first { it.index == index }
    }
}
