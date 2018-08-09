package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.channels.toList
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.reactive.openSubscription
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.enums.Group.OWNER
import one.oktw.galaxy.item.enums.ItemType.BUTTON
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
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*
import java.util.Arrays.asList
import kotlin.streams.toList

class BrowserGalaxy(player: Player? = null) : PageGUI() {
    private val lang = languageService.getDefaultLanguage()
    private val userStorage = Sponge.getServiceManager().provide(UserStorageService::class.java).get()
    private val list = galaxyManager.run { player?.let { get(it) } ?: listGalaxy() }
    override val token = "BrowserGalaxy-${UUID.randomUUID()}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.DOUBLE_CHEST)
        .property(InventoryTitle.of(Text.of(lang["UI.Title.GalaxyList"])))
        .listener(InteractInventoryEvent::class.java, ::eventProcess)
        .build(main)

    init {
        offerPage(0)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    override suspend fun get(number: Int, skip: Int): List<ItemStack> {
        return list
            .skip(skip)
            .limit(number)
            .openSubscription()
            .toList()
            .parallelStream()
            .map {
                val owner = userStorage.get(it.members.first { it.group == OWNER }.uuid).get()

                ItemStack.builder()
                    .itemType(ItemTypes.SKULL)
                    .itemData(DataItemType(BUTTON))
                    .itemData(DataUUID(it.uuid))
                    .add(DISPLAY_NAME, Text.of(TextColors.YELLOW, TextStyles.BOLD, it.name))
                    .add(SKULL_TYPE, SkullTypes.PLAYER)
                    .add(REPRESENTED_PLAYER, owner.profile)
                    .add(
                        ITEM_LORE,
                        asList(
                            Text.of(
                                TextColors.GREEN,
                                "${lang["UI.Tip.Info"]}: ",
                                TextColors.RESET,
                                it.info
                            ),
                            Text.of(
                                TextColors.GREEN,
                                "${lang["UI.Tip.Owner"]}: ",
                                TextColors.RESET,
                                owner.name
                            ),
                            Text.of(
                                TextColors.GREEN,
                                "${lang["UI.Tip.MemberCount"]}: ",
                                TextColors.RESET,
                                it.members.size
                            ),
                            Text.of(
                                TextColors.GREEN,
                                "${lang["UI.Tip.PlanetCount"]}: ",
                                TextColors.RESET,
                                it.planets.size
                            )
                        )
                    )
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

        val player = event.source as Player
        val item = event.cursorTransaction.default
        val uuid = item[DataUUID.key].orElse(null) ?: return

        if (view.getNameOf(event)?.first == Companion.Slot.ITEMS) {
            launch {
                galaxyManager.get(uuid)?.let {
                    GUIHelper.open(player) { GalaxyInfo(it, player) }
                }
            }
        }
    }
}
