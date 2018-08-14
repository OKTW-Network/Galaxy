package one.oktw.galaxy.gui.view

import org.spongepowered.api.data.Transaction
import org.spongepowered.api.item.inventory.ItemStackSnapshot

data class EventDetail<EnumValue, Data>(
    // if this event affect gui or not
    val affectGUI: Boolean = false,
    // slot one the gui that player mainly click (may not exist)
    val primary: SlotAffected<EnumValue, Data>? = null,
    // the cursor transaction
    val cursorTransaction: Transaction<ItemStackSnapshot>? = null,
    // all slots affected
    val affectedSlots: ArrayList<SlotAffected<EnumValue, Data>> = ArrayList()
)
