package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.data.extensions.refresh
import one.oktw.galaxy.galaxy.enums.Group.OWNER
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
import java.util.*
import java.util.Arrays.asList
import kotlin.streams.toList

class BrowserMember(private val galaxy: Galaxy, private val manage: Boolean = false) : PageGUI<UUID>() {
    private val lang = languageService.getDefaultLanguage()
    override val token = "BrowserMember-${galaxy.uuid}${if (manage) "-manage" else ""}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.DOUBLE_CHEST)
        .property(InventoryTitle.of(Text.of(lang["UI.Title.MemberList"])))
        .listener(InteractInventoryEvent::class.java, ::eventProcess)
        .build(Main.main)

    init {
        offerPage(0)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    override suspend fun get(number: Int, skip: Int): List<Pair<ItemStack, UUID>> {
        return galaxy.refresh().members
            .parallelStream()
            .skip(skip.toLong())
            .limit(number.toLong())
            .filter { !manage || it.group != OWNER }
            .map {
                val user = Sponge.getServiceManager().provide(UserStorageService::class.java).get().get(it.uuid).get()
                val status = if (user.isOnline) {
                    Text.of(GREEN, lang["UI.Tip.Online"])
                } else {
                    Text.of(RED, lang["UI.Tip.Offline"])
                }
                val location = user.player.orElse(null)?.run {
                    // output: (planeName x,y,z)
                    Text.of(
                        RESET,
                        "(",
                        GOLD,
                        TextStyles.BOLD,
                        "${runBlocking { galaxyManager.get(world)?.getPlanet(world)?.name ?: world.name }} ",
                        TextStyles.RESET,
                        GRAY,
                        position.toInt(),
                        RESET,
                        ")"
                    )
                }
                Pair(
                ItemStack.builder()
                    .itemType(ItemTypes.SKULL)
                    .itemData(DataItemType(BUTTON))
                    .add(Keys.DISPLAY_NAME, Text.of(AQUA, TextStyles.BOLD, user.name))
                    .add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
                    .add(Keys.REPRESENTED_PLAYER, user.profile)
                    .add(
                        Keys.ITEM_LORE,
                        asList(
                            Text.of(
                                YELLOW,
                                "${lang["UI.Tip.Status"]}: ",
                                if (location != null) status.concat(location) else status
                            ),
                            Text.of(YELLOW, "${lang["UI.Tip.PermissionGroup"]}: ", RESET, it.group.toString())
                        )
                    )
                    .build(), it.uuid
                )
            }
            .toList()
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        if (view.disabled) return

        val detail = view.getDetail(event)

        // ignore gui elements, because they are handled by the PageGUI
        if (isControl(event)) {
            return
        }

        if (detail.affectGUI) {
            event.isCancelled = true
        }

        if (detail.primary?.type == Companion.Slot.ITEMS) {
            if (!manage) return
            val uuid = detail.primary.data?.data?: return
            launch {
                GUIHelper.openAsync(event.source as Player) { ManageMember(galaxy.refresh(), uuid) }.await()
                    .registerEvent(InteractInventoryEvent.Close::class.java) { offerPage(0) }
            }
        }
    }
}
