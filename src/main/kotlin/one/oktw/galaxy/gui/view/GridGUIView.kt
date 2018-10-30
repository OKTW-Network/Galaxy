package one.oktw.galaxy.gui.view

import kotlinx.coroutines.experimental.Job
import one.oktw.galaxy.Main
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.property.SlotIndex
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.type.GridInventory
import java.util.concurrent.ConcurrentLinkedQueue

open class GridGUIView<EnumValue, Data>(
    override val inventory: Inventory,
    final override val layout: List<EnumValue>,
    private val dimension: Pair<Int, Int>
) : GUIView<EnumValue, Data> {

    override var disabled = false

    private val map = HashMap<Int, Data>()

    private val nameIndex = HashMap<Int, Pair<EnumValue, Int>>()

    private var scheduled: Job? = null
    private val pendingTasks = ConcurrentLinkedQueue<() -> Unit>()
    private val lock = object {}

    init {
        val map = HashMap<EnumValue, Int>()

        layout.mapIndexed { index, item ->
            map[item] = (map[item] ?: -1) + 1

            nameIndex[index] = Pair(item, map[item]!!)
        }
    }

    private fun queueAndRun(op: () -> Unit) {
        pendingTasks.add(op)

        synchronized(lock) {
            if (scheduled == null) {
                scheduled = Main.delay.launch {
                    synchronized(lock) {
                        while (pendingTasks.size > 0) {
                            val task = pendingTasks.poll()
                            task.invoke()
                        }

                        scheduled = null
                    }
                }

                if (scheduled?.isCompleted == true) {
                    scheduled = null
                }
            }
        }
    }

    private fun getOffset(x: Int, y: Int): Int {
        return y * dimension.first + x
    }

    private val grid: GridInventory by lazy {
        inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))
    }

    private fun setSlot(x: Int, y: Int, item: ItemStack?) {
        if (item != null) {
            queueAndRun {
                grid.set(x, y, item)
            }
        } else {
            queueAndRun {
                grid.poll(x, y)
            }
        }
    }

    override fun setSlot(name: EnumValue, item: ItemStack?) {
        setSlot(name, item, null)
    }

    override fun setSlot(name: EnumValue, item: ItemStack?, data: Data?) {
        for (y in 0 until dimension.second) {
            for (x in 0 until dimension.first) {
                if (layout[getOffset(x, y)] == name) {
                    setSlot(x, y, item)

                    if (data != null) {
                        map[getOffset(x, y)] = data
                    } else {
                        map.remove(getOffset(x, y))
                    }
                }
            }
        }
    }

    override fun getSlot(name: EnumValue): ItemStack? {
        for (y in 0 until dimension.second) {
            for (x in 0 until dimension.first) {
                if (layout[getOffset(x, y)] == name) {
                    return grid.getSlot(x, y).orElse(null)?.peek()?.orElse(null)
                }
            }
        }

        return null
    }

    override fun getData(name: EnumValue): Data? {
        layout.mapIndexed { index, enumValue ->
            if (enumValue == name) {
                return map[index]
            }
        }

        return null
    }

    override fun getSlots(name: EnumValue): List<ItemStack> {
        val listToReturn = ArrayList<ItemStack>()

        for (y in 0 until dimension.second) {
            for (x in 0 until dimension.first) {
                if (layout[getOffset(x, y)] == name) {
                    grid.getSlot(x, y).orElse(null)?.peek()?.orElse(null)?.let {
                        listToReturn.add(it)
                    }
                }
            }
        }

        return listToReturn
    }

    override fun getDatas(name: EnumValue): List<Data?> {
        val list: ArrayList<Data?> = ArrayList()

        layout.mapIndexed { index, enumValue ->
            if (enumValue == name) {
                list.add(map[index])
            }
        }

        return list
    }

    override fun setSlots(name: EnumValue, listToAdd: List<ItemStack?>) {
        setSlots(name, listToAdd, null)
    }

    override fun setSlots(name: EnumValue, listToAdd: List<ItemStack?>, data: Data?) {
        val iterator = listToAdd.iterator()

        for (y in 0 until dimension.second) {
            for (x in 0 until dimension.first) {
                if (layout[getOffset(x, y)] == name) {
                    if (iterator.hasNext()) {
                        setSlot(x, y, iterator.next())
                    } else {
                        setSlot(x, y, null)
                    }

                    if (data != null) {
                        map[getOffset(x, y)] = data
                    } else {
                        map.remove(getOffset(x, y))
                    }
                }
            }
        }
    }

    override fun setSlotPairs(name: EnumValue, listToAdd: List<Pair<ItemStack?, Data?>>) {
        val iterator = listToAdd.iterator()

        for (y in 0 until dimension.second) {
            for (x in 0 until dimension.first) {
                if (layout[getOffset(x, y)] == name) {
                    if (iterator.hasNext()) {
                        iterator.next().let { (item, data) ->
                            setSlot(x, y, item)

                            if (data != null) {
                                map[getOffset(x, y)] = data
                            } else {
                                map.remove(getOffset(x, y))
                            }
                        }
                    } else {
                        setSlot(x, y, null)
                        map.remove(getOffset(x, y))
                    }
                }
            }
        }
    }

    override fun countSlots(name: EnumValue): Int {
        var count = 0

        for (y in 0 until dimension.second) {
            for (x in 0 until dimension.first) {
                if (layout[getOffset(x, y)] == name) {
                    count++
                }
            }
        }

        return count
    }

    override fun clear() {
        map.clear()

        queueAndRun {
            inventory.clear()
        }
    }

    override fun getNameOf(event: ClickInventoryEvent): Pair<EnumValue, Int>? {
        if (event.transactions.size != 1) {
            return null
        } else {
            val slotIndex = event.transactions[0]!!.slot.getProperty(SlotIndex::class.java, "slotindex").orElse(null) ?: return null
            val index = slotIndex.value ?: return null

            return nameIndex[index]
        }
    }

    override fun getDataOf(event: ClickInventoryEvent): Data? {
        if (event.transactions.size != 1) {
            return null
        } else {
            val slotIndex = event.transactions[0]!!.slot.getProperty(SlotIndex::class.java, "slotindex").orElse(null) ?: return null
            val index = slotIndex.value ?: return null

            return map[index]
        }
    }

    override fun getDetail(event: ClickInventoryEvent): EventDetail<EnumValue, Data> {
        val list = ArrayList<SlotAffected<EnumValue, Data>>()
        var touchedGUI = false
        var primary: SlotAffected<EnumValue, Data>? = null

        event.transactions.forEach {
            val slotIndex = it.slot.getProperty(SlotIndex::class.java, "slotindex").orElse(null)
            var position: Pair<EnumValue, Int>? = null
            var data: Data? = null

            if (slotIndex?.value != null && (slotIndex.value!! < dimension.first * dimension.second)) {
                position = nameIndex[slotIndex.value!!]
                data = map[slotIndex.value!!]
                touchedGUI = true
            }

            list.add(SlotAffected(it, position?.first, position?.second, data))
        }

        // search for the main clicked slot, which is the user trying to click
        if (event.cursorTransaction.original.isEmpty && !event.cursorTransaction.default.isEmpty) {
            // ok, it seems the player is try to pick something.

            val picking = event.cursorTransaction.default
            list.forEach {
                if (!it.transaction.original.isEmpty && it.transaction.default.isEmpty) {
                    if (it.transaction.original == picking) {
                        primary = it
                    }
                }
            }
        }

        return EventDetail(touchedGUI, primary, event.cursorTransaction, list)
    }
}
