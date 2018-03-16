package one.oktw.galaxy.gui

import one.oktw.galaxy.Main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.enums.ButtonType.ARROW_LEFT
import one.oktw.galaxy.enums.ButtonType.ARROW_RIGHT
import one.oktw.galaxy.helper.ItemHelper
import one.oktw.galaxy.types.Galaxy
import one.oktw.galaxy.types.item.Button
import org.spongepowered.api.data.key.Keys
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
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*
import java.util.Arrays.asList

class BrowserPlanet(uuid: UUID) : GUI() {
    override val token = "BrowserPlanet-$uuid"
    val galaxy: Galaxy = TODO("Galaxy")
    override val inventory: Inventory = Inventory.builder()
            .of(InventoryArchetypes.DOUBLE_CHEST)
            .property(InventoryTitle.of(Text.of("星球列表")))
            .listener(InteractInventoryEvent::class.java, this::eventProcess)
            .build(Main.main)
    private val buttonID = Array(2) { UUID.randomUUID() }

    init {
        val inventory = inventory.query<GridInventory>(INVENTORY_TYPE.of(GridInventory::class.java))

        // member
        val planets = galaxy.planets
        var (x, y) = Pair(0, 0)

        for (planet in planets) {
            if (y == 5) {
                break
            }
            val item = ItemStack.builder()
                    .itemType(ItemTypes.BARRIER)
                    .itemData(DataUUID.Immutable(planet.uuid))
                    .add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, planet.name))
                    .add(
                        Keys.ITEM_LORE, asList(
                            Text.of(TextColors.AQUA, "Players: ", TextColors.RESET, TODO("Player Count")),
                            Text.of(TextColors.AQUA, "Security: ", TextColors.RESET, planet.security.toString())
                        )
                    )
                    .build()

            inventory.set(x, y, item)
            if (x++ == 9) {
                y++
                x = 0
            }
        }

        // button
        ItemHelper.getItem(Button(ARROW_RIGHT))?.apply {
            offer(DataUUID(buttonID[0]))
            offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, "Next"))
        }?.let { inventory.set(8, 5, it) }

        ItemHelper.getItem(Button(ARROW_LEFT))?.apply {
            offer(DataUUID(buttonID[1]))
            offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, "Previous"))
        }?.let { inventory.set(0, 5, it) }

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
            else -> TODO("Join Planet")
        }
    }
}
