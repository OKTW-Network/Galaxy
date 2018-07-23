package one.oktw.galaxy.gui.view

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.data.DataUUID
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.type.GridInventory
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

open class GridGUIView<EnumValue, Data>(
    override val inventory: Inventory,
    override val layout: List<EnumValue>,
    private val dimension: Pair<Int, Int>
) : GUIView<EnumValue, Data> {

    private val map = HashMap<Int, Data>()
    private val cache = ConcurrentHashMap<Int, UUID>() // workaround for the messy sponge api

    private fun getOffset(x: Int, y: Int): Int {
        return y * dimension.first + x
    }

    private val grid: GridInventory by lazy {
        inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))
    }

    private fun setSlot(x: Int, y: Int, item: ItemStack?) {
        launch {
            if (item != null) {
                grid.set(x, y, item)

                item[DataUUID.key].orElse(null)?.let {
                    cache[getOffset(x, y)] = it
                } ?: let {
                    cache.remove(getOffset(x, y))
                }
            } else {
                grid.poll(x, y)
                cache.remove(getOffset(x, y))
            }
        }
    }

    override fun setSlot(name: EnumValue, item: ItemStack?) {
        for (y in 0 until dimension.second) {
            for (x in 0 until dimension.first) {
                if (layout[getOffset(x, y)] == name) {
                    setSlot(x, y, item)
                }
            }
        }
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

    override fun setSlots(name: EnumValue, listToAdd: List<ItemStack?>) {
        val iterator = listToAdd.iterator()

        for (y in 0 until dimension.second) {
            for (x in 0 until dimension.first) {
                if (layout[getOffset(x, y)] == name) {
                    if (iterator.hasNext()) {
                        iterator.next().let {
                            setSlot(x, y, it)
                        }
                    } else {
                        setSlot(x, y, null)
                    }
                }
            }
        }
    }

    override fun setSlots(name: EnumValue, listToAdd: List<ItemStack?>, data: Data?) {
        val iterator = listToAdd.iterator()

        for (y in 0 until dimension.second) {
            for (x in 0 until dimension.first) {
                if (layout[getOffset(x, y)] == name) {
                    if (iterator.hasNext()) {
                        iterator.next().let {
                            setSlot(x, y, it)
                        }
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
                        iterator.next().let {
                            setSlot(x, y, it.first)

                            it.second.let {
                                if (it != null) {
                                    map[getOffset(x, y)] = it
                                } else {
                                    map.remove(getOffset(x, y))
                                }
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
        inventory.clear()
        cache.clear()
        map.clear()
    }

    override fun getNameOf(event: ClickInventoryEvent): Pair<EnumValue, Int>? {
        val itemToFind = event.cursorTransaction.default.createStack()
        return getNameOf(itemToFind)
    }

    override fun getNameOf(stack: ItemStack): Pair<EnumValue, Int>? {
        val hashMap = HashMap<EnumValue, Int>()
        val idOfItemToFind = stack[DataUUID.key].orElse(null) ?: return null

        for (y in 0 until dimension.second) {
            for (x in 0 until dimension.first) {
                val name = layout[getOffset(x, y)]
                val idOfSlot = cache[getOffset(x, y)] ?: continue
                hashMap[name] = (hashMap[name] ?: -1) + 1

                if (idOfItemToFind == idOfSlot) {
                    return hashMap[name]?.let { Pair(name, it) }
                }
            }
        }

        return null
    }

    override fun getDataOf(event: ClickInventoryEvent): Data? {
        return getDataOf(event.cursorTransaction.default.createStack())
    }

    override fun getDataOf(stack: ItemStack): Data? {
        val idOfItemToFind = stack[DataUUID.key].orElse(null) ?: return null

        for (y in 0 until dimension.second) {
            for (x in 0 until dimension.first) {
                val idOfSlot = cache[getOffset(x, y)] ?: continue

                if (idOfItemToFind == idOfSlot) {
                    return map[getOffset(x, y)]
                }
            }
        }

        return null
    }
}
