package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.chunkLoaderManager
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.enums.UpgradeType
import one.oktw.galaxy.helper.GUIHelper
import one.oktw.galaxy.helper.ItemHelper
import one.oktw.galaxy.types.ChunkLoader
import one.oktw.galaxy.types.item.Upgrade
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.type.GridInventory
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*

class ChunkLoader(val entity: Entity) : GUI() {
    override val inventory: Inventory =
        Inventory.builder().of(InventoryArchetypes.HOPPER).property(InventoryTitle.of(Text.of("ChunkLoader")))
            .listener(InteractInventoryEvent.Close::class.java, this::closeEventListener)
            .listener(ClickInventoryEvent::class.java, this::clickEventListener)
            .build(main)
    private val uuid = entity[DataUUID.key].orElse(null)
    private lateinit var chunkLoader: ChunkLoader
    private lateinit var upgradeGUI: GUI
    private val upgradeButton = UUID.randomUUID()
    private val removeButton = UUID.randomUUID()

    init {
        launch { chunkLoader = chunkLoaderManager.get(uuid).await() ?: return@launch }

        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))
        val upgradeItem = ItemStack.builder()
            .itemType(ItemTypes.ENCHANTED_BOOK)
            .itemData(DataUUID(upgradeButton))
            .add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, "Upgrade"))
            .build()
        val removeItem = ItemStack.builder()
            .itemType(ItemTypes.ENCHANTED_BOOK)
            .itemData(DataUUID(removeButton))
            .add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, TextStyles.BOLD, "Remove"))
            .build()

        inventory.set(1, 0, upgradeItem)
        inventory.set(3, 0, removeItem)
    }

    override fun getToken() = uuid.toString()

    private fun closeEventListener(event: InteractInventoryEvent.Close) {
        event.cursorTransaction.setCustom(ItemStackSnapshot.NONE)
        event.cursorTransaction.isValid = true
    }

    private fun clickEventListener(event: ClickInventoryEvent) {
        event.isCancelled = true

        val itemUUID = event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return

        when (itemUUID) {
            upgradeButton -> clickUpgrade(event.source as Player)
            removeButton -> clickRemove()
        }
    }

    private fun clickUpgrade(player: Player) {
        if (!this::chunkLoader.isInitialized) return

        upgradeGUI = GUIHelper.open(player) {
            UpgradeSlot(this, chunkLoader.upgrade, UpgradeType.RANGE)
                .onClose {
                    val originLevel = chunkLoader.upgrade.maxBy { it.level }?.level ?: 0
                    val newLevel = it.maxBy { it.level }?.level ?: 0

                    chunkLoader.upgrade = it as ArrayList<Upgrade>

                    if (newLevel != originLevel) {
                        launch { chunkLoaderManager.updateChunkLoader(chunkLoader, true) }
                    } else {
                        launch { chunkLoaderManager.updateChunkLoader(chunkLoader) }
                    }
                }
        }
    }

    private fun clickRemove() {
        if (!this::chunkLoader.isInitialized) return

        val location = entity.location
        val itemEntities = arrayListOf(
            location.createEntity(EntityTypes.ITEM).also {
                it.offer(Keys.REPRESENTED_ITEM, ItemStack.of(ItemTypes.END_CRYSTAL, 1).createSnapshot())
            }
        )

        launch { chunkLoaderManager.delete(uuid) }

        chunkLoader.upgrade.forEach {
            val entity = location.createEntity(EntityTypes.ITEM)
            val upgrade = ItemHelper.getItem(it) ?: return@forEach

            entity.offer(Keys.REPRESENTED_ITEM, upgrade.createSnapshot())
            itemEntities += entity
        }

        location.spawnEntities(itemEntities)
        entity.remove()
        GUIHelper.apply {
            closeAll(getToken())
            if (this@ChunkLoader::upgradeGUI.isInitialized) {
                // TODO check why didn't work
                closeAll(upgradeGUI.getToken())
            }
        }
    }
}