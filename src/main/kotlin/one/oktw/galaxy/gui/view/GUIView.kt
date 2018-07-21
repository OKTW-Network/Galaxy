package one.oktw.galaxy.gui.view

import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack

abstract class GUIView<EnumValue, Data>(inventory: Inventory, layout: ArrayList<EnumValue>, data: Data? = null) {
    abstract val inventory: Inventory
    abstract val layout: ArrayList<EnumValue>

    // override all item of the given names slots
    abstract fun setSlot(name: EnumValue, item: ItemStack?)

    // override all item of the given names slots
    abstract fun setSlot(name: EnumValue, item: ItemStack?, data: Data?)

    // retrieve first slot of the given named slots
    abstract fun getSlot(name: EnumValue): ItemStack?

    // override all item of the given names slots
    abstract fun setSlots(name: EnumValue, listToAdd: ArrayList<ItemStack?>)

    // override all item of the given names slots
    abstract fun setSlots(name: EnumValue, listToAdd: ArrayList<ItemStack?>, data: Data?)

    // retrieve all slots of the given named slots
    abstract fun getSlots(name: EnumValue): ArrayList<ItemStack>

    // retrieve counts of a named slot
    abstract fun countSlots(name: EnumValue): Int

    // get the name of field from event
    abstract fun getNameOf(event: ClickInventoryEvent): Pair<EnumValue, Int>?

    // get name from a ItemStack
    abstract fun getNameOf(stack: ItemStack): Pair<EnumValue, Int>?

    // get the data of field from event
    abstract fun getDataOf(event: ClickInventoryEvent): Data?

    // get name from a ItemStack
    abstract fun getDataOf(stack: ItemStack): Data?
}
