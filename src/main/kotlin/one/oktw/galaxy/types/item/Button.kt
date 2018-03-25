package one.oktw.galaxy.types.item

import one.oktw.galaxy.enums.ButtonType
import one.oktw.galaxy.enums.ItemType.BUTTON

data class Button(val type: ButtonType) : Item {
    override val itemType = BUTTON
}
