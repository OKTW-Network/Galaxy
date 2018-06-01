package one.oktw.galaxy.gui

import one.oktw.galaxy.Main
import one.oktw.galaxy.data.DataType
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.data.extensions.addMember
import one.oktw.galaxy.galaxy.data.extensions.removeJoinRequest
import one.oktw.galaxy.internal.LangSys
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
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles

class GalaxyJoinRequest(private val galaxy: Galaxy) : PageGUI() {
    private val userStorage = Sponge.getServiceManager().provide(UserStorageService::class.java).get()
    override val token = "InviteManagement-${galaxy.uuid}"
    //Todo check player lang
    val lang = LangSys().rootNode.getNode("ui","GalaxyJoinRequest")!!
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.DOUBLE_CHEST)
        .property(InventoryTitle.of(Text.of(lang.getNode("Title").string)))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(Main.main)
    override val pages = galaxy.joinRequest.asSequence()
        .map {
            val user = userStorage.get(it).get()
            ItemStack.builder()
                .itemType(ItemTypes.SKULL)
                .itemData(DataType(BUTTON))
                .itemData(DataUUID(it))
                .add(Keys.DISPLAY_NAME, Text.of(TextColors.YELLOW, TextStyles.BOLD, user.name))
                .add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
                .add(Keys.REPRESENTED_PLAYER, user.profile)
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

            GUIHelper.open(event.source as Player) {
                Confirm(Text.of(lang.getNode("Conform_join").string)) {
                    if (it) galaxy.addMember(uuid)

                    galaxy.removeJoinRequest(uuid)

                    offerPage(0)
                }
            }
        }
    }
}
