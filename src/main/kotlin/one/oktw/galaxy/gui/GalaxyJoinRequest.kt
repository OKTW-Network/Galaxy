package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.data.extensions.addMember
import one.oktw.galaxy.galaxy.data.extensions.refresh
import one.oktw.galaxy.galaxy.data.extensions.removeJoinRequest
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
import kotlin.streams.toList

class GalaxyJoinRequest(private val galaxy: Galaxy) : PageGUI() {
    private val userStorage = Sponge.getServiceManager().provide(UserStorageService::class.java).get()
    private val lang = languageService.getDefaultLanguage() // Todo get player lang
    override val token = "InviteManagement-${galaxy.uuid}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.DOUBLE_CHEST)
        .property(InventoryTitle.of(Text.of(lang["UI.Title.JoinRequestList"])))
        .listener(InteractInventoryEvent::class.java, ::eventProcess)
        .build(Main.main)

    init {
        offerPage(0)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    override suspend fun get(number: Int, skip: Int): List<ItemStack> {
        return galaxy.refresh().joinRequest
            .parallelStream()
            .skip(skip.toLong())
            .limit(number.toLong())
            .map {
                val user = userStorage.get(it).get()
                ItemStack.builder()
                    .itemType(ItemTypes.SKULL)
                    .itemData(DataItemType(BUTTON))
                    .itemData(DataUUID(it))
                    .add(Keys.DISPLAY_NAME, Text.of(TextColors.YELLOW, TextStyles.BOLD, user.name))
                    .add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
                    .add(Keys.REPRESENTED_PLAYER, user.profile)
                    .build()
            }
            .toList()
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        if (view.disabled) return

        // ignore gui elements, because they are handled by the PageGUI
        if (!isControl(event)) {
            event.isCancelled = true
        }

        val item = event.cursorTransaction.default
        val uuid = item[DataUUID.key].orElse(null) ?: return

        if (view.getNameOf(event)?.first == Companion.Slot.ITEMS) {
            GUIHelper.open(event.source as Player) {
                Confirm(Text.of(lang["UI.Title.ConfirmJoinRequest"])) {
                    launch {
                        if (it) galaxy.addMember(uuid).join()

                        galaxy.removeJoinRequest(uuid).join()

                        offerPage(0)
                    }
                }
            }
        }
    }
}
