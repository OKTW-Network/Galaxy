package one.oktw.galaxy.gui

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUpgrade
import one.oktw.galaxy.enums.UpgradeType
import one.oktw.galaxy.helper.ItemHelper
import one.oktw.galaxy.types.item.Upgrade
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.type.OrderedInventory
import org.spongepowered.api.text.Text

class UpgradeSlot(private var upgrade: List<Upgrade>, vararg acceptUpgradeType: UpgradeType) {
    private lateinit var closeListener: (List<Upgrade>) -> Unit
    private val inventory: Inventory = Inventory.builder()
            .of(InventoryArchetypes.HOPPER)
            .property(InventoryTitle.of(Text.of("Upgrade")))
            .listener(InteractInventoryEvent::class.java) {
                val item = it.targetInventory.first<Inventory>().first<OrderedInventory>()
                        .mapNotNull { it.peek().orElse(null) }
                        .map { it[DataUpgrade::class.java].orElse(null) }

                if (item.contains(null)) {
                    it.isCancelled = true
                    return@listener
                }

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
                    this@UpgradeSlot.upgrade = item.map { Upgrade(type = it.type, level = it.level) }
                } else {
                    it.isCancelled = true
                }

                if (it is InteractInventoryEvent.Close) close()
            }
            .build(main)

    init {
        upgrade.forEach { ItemHelper.getItem(it).ifPresent { inventory.offer(it) } }
    }

    private fun close() {
        if (this::closeListener.isInitialized) closeListener(upgrade)
    }

    fun onClose(listener: (List<Upgrade>) -> Unit): UpgradeSlot {
        closeListener = listener
        return this
    }

    fun open(player: Player): UpgradeSlot {
        player.openInventory(inventory)
        return this
    }
}