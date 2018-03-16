package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.data.DataUUID
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.data.type.SkullTypes
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.query.QueryOperationTypes.INVENTORY_TYPE
import org.spongepowered.api.item.inventory.type.GridInventory
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*

class InviteManagement(uuid: UUID) : GUI() {
    override val token = "InviteManagement-$uuid"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.DOUBLE_CHEST)
        .property(InventoryTitle.of(Text.of("審核加入申請")))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(Main.main)
    private val buttonID = Array(2) { UUID.randomUUID() }
    private val userStorage = Sponge.getServiceManager().provide(UserStorageService::class.java).get()

    init {
        val inventory = inventory.query<GridInventory>(INVENTORY_TYPE.of(GridInventory::class.java))

        launch {
            val galaxy = galaxyManager.getGalaxy(uuid).await() ?: return@launch

            // member
            val players: ArrayList<UUID> = galaxy.invite
            var (x, y) = Pair(0, 0)

            for (player in players) {
                if (y == 5) {
                    break
                }

                val user = userStorage.get(player).get()
                val item = ItemStack.builder()
                    .itemType(ItemTypes.SKULL)
                    .itemData(DataUUID.Immutable(player))
                    .add(Keys.DISPLAY_NAME, Text.of(TextColors.YELLOW, TextStyles.BOLD, user.name))
                    .add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
                    .add(Keys.REPRESENTED_PLAYER, user.profile)
                    .build()

                inventory.set(x, y, item)
                if (x++ == 9) {
                    y++
                    x = 0
                }
            }
        }

        // button
        val nextButton = ItemStack.builder()
            .itemType(ItemTypes.BARRIER)
            .itemData(DataUUID(buttonID[0]))
            .add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, "Next"))
            .build()
        val previousButton = ItemStack.builder()
            .itemType(ItemTypes.BARRIER)
            .itemData(DataUUID(buttonID[1]))
            .add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, "Previous"))
            .build()

        inventory.set(0, 5, previousButton)
        inventory.set(8, 5, nextButton)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true

        val player = event.source as? Player ?: return
        val itemUUID = event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return

        when (itemUUID) {
            buttonID[0] -> TODO()
            buttonID[1] -> TODO()
            else -> TODO()
        }
    }
}
