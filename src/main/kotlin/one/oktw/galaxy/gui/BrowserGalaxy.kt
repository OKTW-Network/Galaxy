package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataType
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.enums.Group.OWNER
import one.oktw.galaxy.enums.ItemType.BUTTON
import one.oktw.galaxy.helper.GUIHelper
import one.oktw.galaxy.types.Traveler
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys.*
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
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*
import java.util.Arrays.asList

class BrowserGalaxy(traveler: Traveler? = null) : PageGUI() {
    override val token = "BrowserGalaxy-${UUID.randomUUID()}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.DOUBLE_CHEST)
        .property(InventoryTitle.of(Text.of("瀏覽星系")))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(main)
    override val gridInventory: GridInventory = inventory.query(INVENTORY_TYPE.of(GridInventory::class.java))
    private val userStorage = Sponge.getServiceManager().provide(UserStorageService::class.java).get()
    override lateinit var pages: Sequence<List<List<ItemStack>>>

    init {
        launch {
            pages = galaxyManager.run { traveler?.let { listGalaxy(it) } ?: listGalaxy() }.await()
                .map {
                    val owner = userStorage.get(it.members.first { it.group == OWNER }.uuid).get()

                    ItemStack.builder()
                        .itemType(ItemTypes.SKULL)
                        .itemData(DataType(BUTTON))
                        .itemData(DataUUID(it.uuid))
                        .add(DISPLAY_NAME, Text.of(TextColors.YELLOW, TextStyles.BOLD, it.name))
                        .add(SKULL_TYPE, SkullTypes.PLAYER)
                        .add(REPRESENTED_PLAYER, owner.profile)
                        .add(
                            ITEM_LORE,
                            asList(
                                Text.of(TextColors.GREEN, "Owner: ", TextColors.RESET, owner.name),
                                Text.of(TextColors.GREEN, "Members: ", TextColors.RESET, it.members.size),
                                Text.of(TextColors.GREEN, "Planets: ", TextColors.RESET, it.planets.size)
                            )
                        )
                        .build()
                }
                .chunked(9)
                .chunked(5)

            offerPage(0)
        }

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        val player = event.source as? Player ?: return
        val item = event.cursorTransaction.default
        val uuid = item[DataUUID.key].orElse(null) ?: return

        if (item[DataType.key].orElse(null) == BUTTON && !isButton(uuid)) {
            event.isCancelled = true

            launch {
                galaxyManager.getGalaxy(uuid).await()?.let {
                    Task.builder().execute { _ -> GUIHelper.open(player) { GalaxyInfo(it, player) } }.submit(main)
                }
            }
        }
    }
}
