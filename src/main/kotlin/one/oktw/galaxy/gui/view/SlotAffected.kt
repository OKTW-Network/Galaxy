package one.oktw.galaxy.gui.view

import org.spongepowered.api.item.inventory.transaction.SlotTransaction

data class SlotAffected<EnumValue, Data>(
    val transaction: SlotTransaction,
    val type: EnumValue? = null,
    val index: Int? = 0,
    val data: Data? = null
)
