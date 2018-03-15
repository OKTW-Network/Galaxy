package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.enums.Group.OWNER
import one.oktw.galaxy.helper.GUIHelper
import one.oktw.galaxy.types.Galaxy
import one.oktw.galaxy.types.Traveler
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
import java.util.Arrays.asList

class BrowserGalaxy(traveler: Traveler? = null) : GUI() {
    override val token = "BrowserGalaxy-${UUID.randomUUID()}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.DOUBLE_CHEST)
        .property(InventoryTitle.of(Text.of("瀏覽星系")))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(Main.main)
    private val buttonID = Array(2) { UUID.randomUUID() }
    private val userStorage = Sponge.getServiceManager().provide(UserStorageService::class.java).get()

    init {
        val inventory = inventory.query<GridInventory>(INVENTORY_TYPE.of(GridInventory::class.java))

        // galaxy
        launch {
            val galaxies = galaxyManager.run { traveler?.let { listGalaxy(it) } ?: listGalaxy() }.await()
            var (x, y) = Pair(0, 0)

            while (galaxies.hasNext()) {
                if (y == 5) {
                    break
                }
                val galaxy: Galaxy = galaxies.next()
                val owner = userStorage.get(galaxy.members.first { it.group == OWNER }.uuid).get()
                val item = ItemStack.builder()
                    .itemType(ItemTypes.SKULL)
                    .itemData(DataUUID.Immutable(galaxy.uuid))
                    .add(Keys.DISPLAY_NAME, Text.of(TextColors.YELLOW, TextStyles.BOLD, galaxy.name))
                    .add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
                    .add(Keys.REPRESENTED_PLAYER, owner.profile)
                    .add(
                        Keys.ITEM_LORE, asList(
                            Text.of(TextColors.GREEN, "Owner: ", TextColors.RESET, owner.name),
                            Text.of(TextColors.GREEN, "Members: ", TextColors.RESET, galaxy.members.size),
                            Text.of(TextColors.GREEN, "Planets: ", TextColors.RESET, galaxy.planets.size)
                        )
                    )
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
            else -> GUIHelper.open(player) { GalaxyInfo(itemUUID) }
        }
    }
}