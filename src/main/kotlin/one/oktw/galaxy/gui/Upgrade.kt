package one.oktw.galaxy.gui

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUpgrade
import one.oktw.galaxy.enums.UpgradeType
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.property.InventoryDimension
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.type.OrderedInventory
import org.spongepowered.api.text.Text

class Upgrade(vararg acceptUpgradeType: UpgradeType) {
    protected lateinit var closeListener: (HashMap<UpgradeType, Int>) -> Unit
    private var upgrade = HashMap<UpgradeType, Int>()
    private val inventory: Inventory = Inventory.builder()
            .of(InventoryArchetypes.HOPPER)
            .property(InventoryTitle.of(Text.of("Upgrade")))
            .property(InventoryDimension.of(5, 1))
            .listener(InteractInventoryEvent::class.java) {
                val item = it.targetInventory.first<Inventory>().first<OrderedInventory>()
                        .mapNotNull { it.peek().orElse(null) }
                        .mapNotNull { it[DataUpgrade::class.java].orElse(null) }
                val upgrade = HashMap<UpgradeType, Int>()

                val filter = item.sortedBy { it.level }.all {
                    if (it.type !in acceptUpgradeType) return@all false

                    if (upgrade.getOrDefault(it.type, 0) == it.level - 1) {
                        upgrade[it.type] = it.level
                        return@all true
                    } else {
                        return@all false
                    }
                }

                if (filter) {
                    this@Upgrade.upgrade = upgrade
                } else {
                    it.isCancelled = true
                }

                if (it is InteractInventoryEvent.Close && this::closeListener.isInitialized) closeListener(upgrade)
            }
            .build(main)

    fun offer(itemStack: ItemStack) {
        inventory.offer(itemStack)
    }

    fun onClose(listener: (HashMap<UpgradeType, Int>) -> Unit): Upgrade {
        closeListener = listener
        return this
    }

    fun open(player: Player): Upgrade {
        player.openInventory(inventory)
        return this
    }
}