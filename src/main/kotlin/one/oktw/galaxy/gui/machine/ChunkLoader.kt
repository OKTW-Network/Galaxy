package one.oktw.galaxy.gui.machine

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUIHelper
import one.oktw.galaxy.gui.UpgradeSlot
import one.oktw.galaxy.internal.LanguageService
import one.oktw.galaxy.item.enums.ButtonType.UPGRADE
import one.oktw.galaxy.item.enums.ButtonType.X
import one.oktw.galaxy.item.enums.UpgradeType
import one.oktw.galaxy.item.type.Button
import one.oktw.galaxy.item.type.Upgrade
import one.oktw.galaxy.machine.chunkloader.ChunkLoader.Companion.chunkLoaderManager
import one.oktw.galaxy.machine.chunkloader.data.ChunkLoader
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
    private val uuid = entity[DataUUID.key].orElse(null)
    private lateinit var chunkLoader: ChunkLoader
    private lateinit var upgradeGUI: GUI
    private val buttonID = Array(2) { UUID.randomUUID() }
    override val token = "ChunkLoader-$uuid"
    //Todo check player lang
    val lang = LanguageService()
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.HOPPER)
        .property(InventoryTitle.of(Text.of(lang.getString("UI.ChunkLoader.Title"))))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(main)

    init {
        launch { chunkLoader = chunkLoaderManager.get(uuid).await() ?: return@launch }

        // fill inventory
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        Button(UPGRADE).createItemStack()
            .apply {
                offer(DataUUID(buttonID[0]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, lang.getString("UI.ChunkLoader.Upgrade")))
            }
            .let { inventory.set(1, 0, it) }

        Button(X).createItemStack()
            .apply {
                offer(DataUUID(buttonID[1]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.RED, TextStyles.BOLD, lang.getString("UI.ChunkLoader.Remove")))
            }
            .let { inventory.set(3, 0, it) }

        // register event
        registerEvent(InteractInventoryEvent.Close::class.java, this::closeEventListener)
        registerEvent(ClickInventoryEvent::class.java, this::clickEventListener)
    }

    private fun closeEventListener(event: InteractInventoryEvent.Close) {
        event.cursorTransaction.setCustom(ItemStackSnapshot.NONE)
        event.cursorTransaction.isValid = true
    }

    private fun clickEventListener(event: ClickInventoryEvent) {
        event.isCancelled = true

        val itemUUID = event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return

        when (itemUUID) {
            buttonID[0] -> clickUpgrade(event.source as Player)
            buttonID[1] -> clickRemove()
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

        // spawn drop item
        val location = entity.location
        val itemEntities = arrayListOf(
            location.createEntity(EntityTypes.ITEM)
                .apply { offer(Keys.REPRESENTED_ITEM, ItemStack.of(ItemTypes.END_CRYSTAL, 1).createSnapshot()) }
        )

        chunkLoader.upgrade.forEach {
            val upgrade = it.createItemStack()

            itemEntities += location.createEntity(EntityTypes.ITEM)
                .apply { offer(Keys.REPRESENTED_ITEM, upgrade.createSnapshot()) }
        }

        location.spawnEntities(itemEntities)

        // remove entity
        entity.remove()

        // close all GUI
        GUIHelper.close(token)
        if (this@ChunkLoader::upgradeGUI.isInitialized) {
            GUIHelper.close(upgradeGUI.token)
        }

        launch { chunkLoaderManager.delete(uuid) }
    }
}
