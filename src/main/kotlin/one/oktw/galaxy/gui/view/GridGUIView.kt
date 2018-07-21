package one.oktw.galaxy.gui.view

import one.oktw.galaxy.data.DataUUID
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.type.GridInventory

open class GridGUIView<EnumValue, Data>(
    override val inventory: Inventory,
    override val layout: ArrayList<EnumValue>,
    private val dimension: Pair<Int, Int>
) : GUIView<EnumValue, Data>(inventory, layout) {

    private val map = HashMap<Int, Data>()
    private val cache = HashMap<Int, ItemStack?>() // workaround for the messy sponge api

    private fun getOffset(x: Int, y: Int): Int {
        return y * dimension.first + x
    }

    private val grid: GridInventory by lazy {
        inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))
    }

    private fun setSlot(x: Int, y: Int, item: ItemStack?) {
        if (item != null) {
            grid.set(x, y, item)
            cache[getOffset(x, y)] = item
        } else {
            grid.poll(x, y)
            cache.remove(getOffset(x, y))
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

                    if (data!= null) {
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
                    return cache[getOffset(x, y)]
                }
            }
        }

        return null
    }

    override fun getSlots(name: EnumValue): ArrayList<ItemStack> {
        val listToReturn = ArrayList<ItemStack>()
        for (y in 0 until dimension.second) {
            for (x in 0 until dimension.first) {
                if (layout[getOffset(x, y)] == name) {
                    cache[getOffset(x, y)]?.let {
                        listToReturn.add(it)
                    }
                }
            }
        }

        return listToReturn
    }

    override fun setSlots(name: EnumValue, listToAdd: ArrayList<ItemStack?>) {
        val iterator = listToAdd.iterator()
        for (y in 0 until dimension.second) {
            for (x in 0 until dimension.first) {
                if (layout[getOffset(x, y)] == name) {
                    if (iterator.hasNext()) {
                        val item = iterator.next()
                        setSlot(x, y, item)
                    } else {
                        setSlot(x, y, null)
                    }
                }
            }
        }
    }

    override fun setSlots(name: EnumValue, listToAdd: ArrayList<ItemStack?>, data: Data?) {
        val iterator = listToAdd.iterator()
        for (y in 0 until dimension.second) {
            for (x in 0 until dimension.first) {
                if (layout[getOffset(x, y)] == name) {
                    if (iterator.hasNext()) {
                        val item = iterator.next()
                        setSlot(x, y, item)
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

    override fun getNameOf(event: ClickInventoryEvent): Pair<EnumValue, Int>? {
        val itemToFind = event.cursorTransaction.default.createStack()
        return  getNameOf(itemToFind)
    }

    override fun getNameOf(stack: ItemStack): Pair<EnumValue, Int>? {
        val hashMap = HashMap<EnumValue, Int>()
        val idOfItemToFind = stack[DataUUID.key].orElse(null) ?: return null

        for (y in 0 until dimension.second) {
            for (x in 0 until dimension.first) {
                val name = layout[getOffset(x, y)]

                hashMap[name] = (hashMap[name] ?: -1) + 1

                val item = cache[getOffset(x, y)] ?: continue

                val idOfSlot = item[DataUUID.key].orElse(null) ?: continue

                if (idOfItemToFind == idOfSlot) {
                    return (hashMap[name] ?: return null).let { Pair(name, it) }
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
                val item = cache[getOffset(x, y)] ?: continue

                val idOfSlot = item[DataUUID.key].orElse(null) ?: continue

                if (idOfItemToFind == idOfSlot) {
                    return map[getOffset(x, y)]
                }
            }
        }

        return null
    }
}
