package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.channels.toList
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.reactive.openSubscription
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.galaxy.enums.Group.OWNER
import one.oktw.galaxy.item.enums.ButtonType
import one.oktw.galaxy.item.enums.ItemType.BUTTON
import one.oktw.galaxy.item.type.Button
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
import kotlin.collections.ArrayList
import kotlin.streams.toList

class BrowserGalaxy(player: Player? = null) : PageGUI<BrowserGalaxy.Companion.Wrapper>() {
    companion object {
        enum class Function(val icon: ButtonType) {
            SORT_NUMBER(ButtonType.SORT_NUMBER),
            SORT_NAME(ButtonType.SORT_NAME),
            LIST(ButtonType.WRITE),
        }

        data class Wrapper(
            val uuid: UUID? = null,
            val function: Function? = null
        )

        fun UUID.toWrapper(): Wrapper {
            return Wrapper(uuid = this)
        }

        val PageGUI.Companion.Operation<BrowserGalaxy.Companion.Wrapper>.uuid: UUID?
            get() {
                return this.data?.uuid
            }

        val PageGUI.Companion.Operation<BrowserGalaxy.Companion.Wrapper>.function: Function?
            get() {
                return this.data?.function
            }
    }

    private val lang = languageService.getDefaultLanguage()
    private val userStorage = Sponge.getServiceManager().provide(UserStorageService::class.java).get()
    private val list = galaxyManager.run { player?.let { get(it) } ?: listGalaxy() }
    override val token = "BrowserGalaxy-${UUID.randomUUID()}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.DOUBLE_CHEST)
        .property(InventoryTitle.of(Text.of(lang["UI.Title.GalaxyList"])))
        .listener(InteractInventoryEvent::class.java, ::eventProcess)
        .build(main)
    override val hasFunctionButtons: Boolean = true

    init {
        offerPage(0)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    override suspend fun get(number: Int, skip: Int): List<Pair<ItemStack, Wrapper>> {
        return list
            .skip(skip)
            .limit(number)
            .openSubscription()
            .toList()
            .parallelStream()
            .map { galaxy ->
                val owner = userStorage.get(galaxy.members.first { it.group == OWNER }.uuid).get()

                Pair(
                    ItemStack.builder()
                        .itemType(ItemTypes.SKULL)
                        .itemData(DataItemType(BUTTON))
                        .add(DISPLAY_NAME, Text.of(TextColors.YELLOW, TextStyles.BOLD, galaxy.name))
                        .add(SKULL_TYPE, SkullTypes.PLAYER)
                        .add(REPRESENTED_PLAYER, owner.profile)
                        .add(
                            ITEM_LORE,
                            asList(
                                Text.of(
                                    TextColors.GREEN,
                                    "${lang["UI.Tip.Info"]}: ",
                                    TextColors.RESET,
                                    galaxy.info
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
                                    galaxy.members.size
                                ),
                                Text.of(
                                    TextColors.GREEN,
                                    "${lang["UI.Tip.PlanetCount"]}: ",
                                    TextColors.RESET,
                                    galaxy.planets.size
                                )
                            )
                        )
                        .build(), galaxy.uuid.toWrapper()
                )
            }
            .toList()
    }

    override suspend fun getFunctionButtons(count: Int): List<Pair<ItemStack, Wrapper?>> {
        val list = Function.values().asList()

        return ArrayList<Function?>()
            .apply {
                addAll(list)
                addAll((0 until count).map { null })
            }
            .subList(0, count)
            .map {
                if (it != null) {
                    Pair(Button(it.icon).createItemStack(), Wrapper(function = it))
                } else {
                    Pair(Button(ButtonType.GUI_CENTER).createItemStack(), null)
                }
            }
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        if (view.disabled) return

        val detail = view.getDetail(event)

        // ignore gui elements, because they are handled by the PageGUI
        if (isControl(detail) && detail.primary?.type != PageGUI.Companion.Slot.FUNCTION) {
            return
        }

        if (!isControl(detail) && detail.affectGUI) {
            event.isCancelled = true
        }

        when (detail.primary?.type) {
            PageGUI.Companion.Slot.ITEMS -> {
                val player = event.source as Player
                val uuid = detail.primary.data?.uuid ?: return

                launch {
                    galaxyManager.get(uuid)?.let {
                        GUIHelper.open(player) { GalaxyInfo(it, player) }
                    }
                }
            }

            PageGUI.Companion.Slot.FUNCTION -> {
                val function = detail.primary.data?.function ?: return

                when (function) {
                    Function.SORT_NUMBER -> {

                    }
                    Function.SORT_NAME -> {

                    }
                    Function.LIST -> {

                    }
                }
            }

            else -> Unit
        }
    }
}
