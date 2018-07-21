package one.oktw.galaxy.gui.view

import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack

interface IGUIView<EnumValue, Data> {
    val inventory: Inventory
    val layout: ArrayList<EnumValue>
    // override all item of the given names slots
    fun setSlot(name: EnumValue, item: ItemStack?)

    // override all item of the given names slots
    fun setSlot(name: EnumValue, item: ItemStack?, data: Data?)

    // retrieve first slot of the given named slots
    fun getSlot(name: EnumValue): ItemStack?

    // override all item of the given names slots
    fun setSlots(name: EnumValue, listToAdd: ArrayList<ItemStack?>)

    // override all item of the given names slots
    fun setSlots(name: EnumValue, listToAdd: ArrayList<ItemStack?>, data: Data?)

    // override all item of the given names slots
    fun setSlotPairs(name: EnumValue, listToAdd: ArrayList<Pair<ItemStack?, Data?>>)

    // retrieve all slots of the given named slots
    fun getSlots(name: EnumValue): ArrayList<ItemStack>

    // retrieve counts of a named slot
    fun countSlots(name: EnumValue): Int

    // get the name of field from event
    fun getNameOf(event: ClickInventoryEvent): Pair<EnumValue, Int>?

    // get name from a ItemStack
    fun getNameOf(stack: ItemStack): Pair<EnumValue, Int>?

    // get the data of field from event
    fun getDataOf(event: ClickInventoryEvent): Data?

    // get name from a ItemStack
    fun getDataOf(stack: ItemStack): Data?
}
