package one.oktw.galaxy.gui.view

import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import java.util.*

interface GUIView<EnumValue, Data> {
    companion object {
        enum class Action {
            PICK,
            DROP,
            DROP_AND_PICK,
            SWAP,
            UNKNOWN
        }
    }

    val inventory: Inventory
    val layout: List<EnumValue>

    // only used to indicate the gui is disabled, has no side effect at all
    var disabled: Boolean

    // override all item of the given names slots
    fun setSlot(name: EnumValue, item: ItemStack?)

    // override all item of the given names slots
    fun setSlot(name: EnumValue, item: ItemStack?, data: Data?)

    // retrieve first slot of the given named slots
    fun getSlot(name: EnumValue): ItemStack?

    // override all item of the given names slots
    fun setSlots(name: EnumValue, listToAdd: List<ItemStack?>)

    // override all item of the given names slots
    fun setSlots(name: EnumValue, listToAdd: List<ItemStack?>, data: Data?)

    // override all item of the given names slots
    fun setSlotPairs(name: EnumValue, listToAdd: List<Pair<ItemStack?, Data?>>)

    // retrieve all slots of the given named slots
    fun getSlots(name: EnumValue): List<ItemStack>

    // retrieve counts of a named slot
    fun countSlots(name: EnumValue): Int

    fun clear()

    // get the name of field from event
    fun getNameOf(event: ClickInventoryEvent): Pair<EnumValue, Int>?

    // get name from a ItemStack
    fun getNameOf(stack: ItemStack): Pair<EnumValue, Int>?

    // get name from a ItemStack
    fun getNameOf(stack: ItemStackSnapshot): Pair<EnumValue, Int>?

    // get name from a ItemStack
    fun getNameOf(id: UUID): Pair<EnumValue, Int>?

    // get the data of field from event
    fun getDataOf(event: ClickInventoryEvent): Data?

    // get name from a ItemStack
    fun getDataOf(stack: ItemStack): Data?

    // get name from a ItemStack
    fun getDataOf(stack: ItemStackSnapshot): Data?

    // get name from a ItemStack
    fun getDataOf(id: UUID): Data?

    // get type of event
    fun getTypeOf(event: ClickInventoryEvent): Action
}
