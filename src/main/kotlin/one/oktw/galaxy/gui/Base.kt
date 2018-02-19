package one.oktw.galaxy.gui

import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.item.inventory.Inventory

interface Base<T : Base<T>> {
    val inventory: Inventory

    fun open(player: Player): T {
        player.openInventory(inventory)

        @Suppress("UNCHECKED_CAST")
        return this as T
    }
}