package one.oktw.galaxy.gui.view

import org.spongepowered.api.item.inventory.transaction.SlotTransaction

data class SlotAffected<EnumValue, Data>(
    // slot transaction
    val transaction: SlotTransaction,
    // type of the slot (only exist on gui slot)
    val type: EnumValue? = null,
    // index in the type (only exist on gui slot)
    val index: Int? = 0,
    // data associated with this slot (only may exist on gui slot)
    val data: Data? = null
)
