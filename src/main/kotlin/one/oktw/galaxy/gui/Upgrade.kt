package one.oktw.galaxy.gui

import one.oktw.galaxy.Main.Companion.main
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.property.InventoryDimension
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.type.OrderedInventory
import org.spongepowered.api.text.Text
import java.util.function.Consumer
import java.util.function.Predicate

class Upgrade(filter: Predicate<ItemStack> = Predicate { true }) : Base<Upgrade> {
    private val closeListener = ArrayList<Consumer<List<ItemStack>>>()

    override val inventory: Inventory = Inventory.builder()
            .of(InventoryArchetypes.HOPPER)
            .property(InventoryTitle.of(Text.of("Upgrade")))
            .property(InventoryDimension.of(5, 1))
            .listener(InteractInventoryEvent::class.java) {
                val inv = it.targetInventory.first<Inventory>().first<OrderedInventory>()

                if (!inv.all { if (it.peek().isPresent) filter.test(it.peek().get()) else true })
                    it.isCancelled = true

                if (it is InteractInventoryEvent.Close) {
                    val item = inv.mapNotNull { it.peek().orElse(null) }
                    closeListener.forEach { it.accept(item) }
                }
            }
            .build(main)

    fun onClose(listener: Consumer<List<ItemStack>>): Upgrade {
        closeListener += listener
        return this
    }
}