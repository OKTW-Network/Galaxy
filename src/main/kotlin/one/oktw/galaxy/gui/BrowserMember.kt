package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.runBlocking
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.travelerManager
import one.oktw.galaxy.data.DataType
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.enums.Group
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.item.enums.ItemType.BUTTON
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
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors.*
import org.spongepowered.api.text.format.TextStyles
import java.util.Arrays.asList

class BrowserMember(private val galaxy: Galaxy, private val manage: Boolean = false) : PageGUI() {
    override val token = "BrowserMember-${galaxy.uuid}${if (manage) "-manage" else ""}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.DOUBLE_CHEST)
        .property(InventoryTitle.of(Text.of("成員列表")))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(Main.main)
    override val pages = galaxy.members.asSequence()
        .filter {
            if (manage) it.group != Group.OWNER else true
        }
        .map {
            val user = Sponge.getServiceManager().provide(UserStorageService::class.java).get().get(it.uuid).get()
            val status = if (user.isOnline) Text.of(GREEN, "ONLINE") else Text.of(RED, "OFFLINE")
            val location = user.player.orElse(null)
                ?.let { travelerManager.getTraveler(it).position }
                ?.run {
                    // output: (planeName x,y,z)
                    Text.of(RESET, "(${runBlocking { galaxyManager.getPlanet(planet!!).await()!!.name }} $x,$y,$z)")
                }

            ItemStack.builder()
                .itemType(ItemTypes.SKULL)
                .itemData(DataType(BUTTON))
                .itemData(DataUUID(it.uuid))
                .add(Keys.DISPLAY_NAME, Text.of(AQUA, TextStyles.BOLD, user.name))
                .add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
                .add(Keys.REPRESENTED_PLAYER, user.profile)
                .add(
                    Keys.ITEM_LORE,
                    asList(
                        Text.of(YELLOW, "Status: ", if (location != null) status.concat(location) else status),
                        Text.of(YELLOW, "Group: ", RESET, it.group.toString())
                    )
                )
                .build()
        }
        .chunked(9)
        .chunked(5)

    init {
        offerPage(0)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        val item = event.cursorTransaction.default
        val uuid = item[DataUUID.key].orElse(null) ?: return

        if (item[DataType.key].orElse(null) == BUTTON && !isButton(uuid)) {
            event.isCancelled = true

            if (manage) {
                GUIHelper.open(event.source as Player) { ManageMember(galaxy, uuid) }
                    .registerEvent(InteractInventoryEvent.Close::class.java) { offerPage(0) }
            }
        }
    }
}
