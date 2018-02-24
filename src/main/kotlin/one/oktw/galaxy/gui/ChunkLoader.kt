package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.chunkLoaderManager
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.data.DataUpgrade
import one.oktw.galaxy.enums.BlockUpgradeType
import one.oktw.galaxy.types.ChunkLoader
import org.spongepowered.api.data.key.Keys
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

class ChunkLoader(uuid: UUID) {
    private lateinit var chunkLoader: ChunkLoader
    private lateinit var player: Player
    private val upgradeButton = UUID.randomUUID()
    private val removeButton = UUID.randomUUID()
    private val inventory: Inventory = Inventory.builder()
            .of(InventoryArchetypes.HOPPER)
            .property(InventoryTitle.of(Text.of("ChunkLoader")))
            .listener(InteractInventoryEvent::class.java) {
                if (it is InteractInventoryEvent.Open || it is InteractInventoryEvent.Close) {
                    it.cursorTransaction.setCustom(ItemStackSnapshot.NONE)
                    it.cursorTransaction.isValid = true
                }
            }
            .listener(ClickInventoryEvent::class.java) {
                it.isCancelled = true

                val itemUUID = it.cursorTransaction.default[DataUUID.key].orElse(null) ?: return@listener

                when (itemUUID) {
                    upgradeButton -> clickUpgrade()
                    removeButton -> clickRemove()
                }
            }
            .build(main)

    init {
        launch { chunkLoader = chunkLoaderManager.getChunkLoader(uuid).await() ?: return@launch }

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

    fun open(player: Player) {
        this@ChunkLoader.player = player

        player.openInventory(this.inventory)
    }

    private fun clickUpgrade() {
        if (!this::player.isInitialized || !this::chunkLoader.isInitialized) return

        Upgrade(BlockUpgradeType.RANGE)
                .apply {
                    for (i in 1..chunkLoader.level / 2) {
                        this.offer(ItemStack.builder()
                                .itemType(ItemTypes.ENCHANTED_BOOK)
                                .itemData(DataUpgrade(BlockUpgradeType.RANGE, i))
                                .quantity(1)
                                .build())
                    }
                }
                .onClose {
                    val level = it.getOrDefault(BlockUpgradeType.RANGE, 0)

                    if (level != chunkLoader.level) launch { chunkLoaderManager.changeRange(chunkLoader.uuid, (level)) }
                }
                .open(player)
    }

    private fun clickRemove() {
        // TODO
    }
}