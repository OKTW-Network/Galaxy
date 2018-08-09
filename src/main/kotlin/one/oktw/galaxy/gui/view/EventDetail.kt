package one.oktw.galaxy.gui.view

import org.spongepowered.api.data.Transaction
import org.spongepowered.api.item.inventory.ItemStackSnapshot

data class EventDetail<EnumValue, Data>(
    val affectGUI: Boolean = false,
    val primary: SlotAffected<EnumValue, Data>? = null,
    val cursorTransaction: Transaction<ItemStackSnapshot>? = null,
    val affectedSlots: ArrayList<SlotAffected<EnumValue, Data>> = ArrayList()
)
