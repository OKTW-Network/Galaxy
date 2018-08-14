package one.oktw.galaxy.gui.view

import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack

interface GUIView<EnumValue, Data> {

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

    // get data of first slot that have the name
    fun getData(name: EnumValue): Data?

    // override all item of the given names slots
    fun setSlots(name: EnumValue, listToAdd: List<ItemStack?>)

    // override all item of the given names slots
    fun setSlots(name: EnumValue, listToAdd: List<ItemStack?>, data: Data?)

    // override all item of the given names slots
    fun setSlotPairs(name: EnumValue, listToAdd: List<Pair<ItemStack?, Data?>>)

    // retrieve all slots of the given named slots
    fun getSlots(name: EnumValue): List<ItemStack>

    // get data slots that have the name
    fun getDatas(name: EnumValue): List<Data?>

    // retrieve counts of a named slot
    fun countSlots(name: EnumValue): Int

    // clear the whole view
    fun clear()

    // get the name of field from event
    fun getNameOf(event: ClickInventoryEvent): Pair<EnumValue, Int>?

    // get the data of field from event
    fun getDataOf(event: ClickInventoryEvent): Data?

    // get detail of event
    fun getDetail(event: ClickInventoryEvent): EventDetail<EnumValue, Data>
}
