package one.oktw.galaxy.gui

import one.oktw.galaxy.types.Galaxy
import org.spongepowered.api.item.inventory.Inventory

class GalaxyInventory(val galaxy: Galaxy) : GUI() {
    override val token = "GalaxyInventory-${galaxy.uuid}"
    override val inventory: Inventory = TODO()
}