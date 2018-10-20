package one.oktw.galaxy.gui

import kotlinx.coroutines.experimental.channels.any
import kotlinx.coroutines.experimental.channels.toList
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.reactive.openSubscription
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.Main.Companion.translationService
import one.oktw.galaxy.data.DataItemType
import one.oktw.galaxy.galaxy.data.extensions.getGroup
import one.oktw.galaxy.galaxy.enums.Group.OWNER
import one.oktw.galaxy.gui.BrowserGalaxy.Companion.Function.*
import one.oktw.galaxy.gui.PageGUI.Companion.Slot.FUNCTION
import one.oktw.galaxy.gui.PageGUI.Companion.Slot.ITEMS
import one.oktw.galaxy.item.enums.ButtonType
import one.oktw.galaxy.item.enums.ItemType.BUTTON
import one.oktw.galaxy.item.type.Button
import one.oktw.galaxy.translation.extensions.toLegacyText
import one.oktw.galaxy.util.Chat
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
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextColors.*
import org.spongepowered.api.text.format.TextStyles.BOLD
import org.spongepowered.api.text.format.TextStyles.UNDERLINE
import java.util.*
import java.util.Arrays.asList
import kotlin.streams.toList

class BrowserGalaxy(private val player: Player) : PageGUI<BrowserGalaxy.Companion.Wrapper>() {
    companion object {
        enum class Function(val icon: ButtonType, val tips: String) {
            SORT_NUMBER(ButtonType.SORT_NUMBER, "WIP"),
            SORT_NAME(ButtonType.SORT_NAME, "WIP"),
            LIST_ALL(ButtonType.GALAXY, "UI.Button.ListAllGalaxy"),
            LIST_JOIN(ButtonType.GALAXY_JOINED, "UI.Button.ListJoinedGalaxy"),
            NEW_GALAXY(ButtonType.PLUS, "UI.Button.CreateGalaxy")
        }

        private enum class Sort { NAME, NUMBER }

        data class Wrapper(
            val uuid: UUID? = null,
            val function: Function? = null
        )

        private fun UUID.toWrapper(): Wrapper {
            return Wrapper(uuid = this)
        }

        private val PageGUI.Companion.Operation<BrowserGalaxy.Companion.Wrapper>.uuid: UUID?
            get() {
                return this.data?.uuid
            }

        private val PageGUI.Companion.Operation<BrowserGalaxy.Companion.Wrapper>.function: Function?
            get() {
                return this.data?.function
            }
    }

    private val lang = translationService
    private val userStorage = Sponge.getServiceManager().provide(UserStorageService::class.java).get()
    private var list = galaxyManager.get(player)
    private var listAll = false
    private var sort = Sort.NAME
    override val token = "BrowserGalaxy-${player.uniqueId}"
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.DOUBLE_CHEST)
        .property(InventoryTitle.of(lang.ofPlaceHolder("UI.Title.GalaxyList")))
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
                        .add(DISPLAY_NAME, Text.of(YELLOW, BOLD, galaxy.name))
                        .add(SKULL_TYPE, SkullTypes.PLAYER)
                        .add(REPRESENTED_PLAYER, owner.profile)
                        .add(
                            ITEM_LORE,
                            asList(
                                lang.ofPlaceHolder(
                                    GREEN,
                                    lang.of("UI.Tip.Info"),
                                    ": ",
                                    TextColors.RESET,
                                    galaxy.info
                                ),
                                lang.ofPlaceHolder(
                                    GREEN,
                                    lang.of("UI.Tip.Owner"),
                                    ": ",
                                    TextColors.RESET,
                                    owner.name
                                ),
                                lang.ofPlaceHolder(
                                    GREEN,
                                    lang.of("UI.Tip.MemberCount"),
                                    ": ",
                                    TextColors.RESET,
                                    galaxy.members.size
                                ),
                                lang.ofPlaceHolder(
                                    GREEN,
                                    lang.of("UI.Tip.PlanetCount"),
                                    ": ",
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
        val list = arrayListOf<Function?>(
            if (listAll) LIST_ALL else LIST_JOIN,
            when (sort) {
                Sort.NAME -> SORT_NAME
                Sort.NUMBER -> SORT_NUMBER
            },
            NEW_GALAXY
        )

        list.addAll((0 until count - list.size).map { null })

        return list.map {
            if (it != null) {
                Pair(Button(it.icon).createItemStack().apply { offer(DISPLAY_NAME, lang.ofPlaceHolder(BOLD, it.tips)) }, Wrapper(function = it))
            } else {
                Pair(Button(ButtonType.GUI_CENTER).createItemStack(), null)
            }
        }
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        if (view.disabled) return

        val detail = view.getDetail(event)

        // ignore gui elements, because they are handled by the PageGUI
        if (isControl(detail) && detail.primary?.type != FUNCTION) {
            return
        }

        if (!isControl(detail) && detail.affectGUI) {
            event.isCancelled = true
        }

        when (detail.primary?.type) {
            ITEMS -> {
                val player = event.source as Player
                val uuid = detail.primary.data?.uuid ?: return

                launch {
                    galaxyManager.get(uuid)?.let {
                        GUIHelper.open(player) { GalaxyInfo(it, player) }
                    }
                }
            }

            FUNCTION -> {
                val function = detail.primary.data?.function ?: return

                when (function) {
                    SORT_NUMBER, SORT_NAME -> {
                        when (function) {
                            SORT_NUMBER -> sort = Sort.NUMBER
                            SORT_NAME -> sort = Sort.NAME
                            else -> Unit
                        }
                    }
                    LIST_ALL, LIST_JOIN -> {
                        list = if (listAll) galaxyManager.get(player) else galaxyManager.listGalaxy()

                        listAll = !listAll

                        offerPage(0)
                    }
                    NEW_GALAXY -> launch {
                        // TODO clean up
                        if (galaxyManager.get(player).openSubscription().any { it.getGroup(player) == OWNER }) {
                            player.sendMessage(Text.of(RED, lang.of("Respond.maximumOneGalaxyAllowed")).toLegacyText(player))
                            return@launch
                        }

                        val name = Chat.input(player, Text.of(AQUA, lang.of("Respond.inputGalaxyName")).toLegacyText(player))?.toPlain()

                        if (name == null) {
                            player.sendMessage(Text.of(RED, lang.of("Respond.createGalaxyCancel")).toLegacyText(player))
                            return@launch
                        }

                        if (
                            Chat.confirm(
                                player,
                                Text.of(AQUA, lang.of("Respond.createGalaxyConfirmName", Text.of(WHITE, BOLD, name))).toLegacyText(player)
                            ) == true
                        ) {
                            if (galaxyManager.get(player).openSubscription().any { it.getGroup(player) == OWNER }) {
                                player.sendMessage(Text.of(RED, lang.of("Respond.maximumOneGalaxyAllowed")).toLegacyText(player))
                                return@launch
                            }

                            val galaxy = galaxyManager.createGalaxy(name, player)
                            player.sendMessage(Text.of(YELLOW, lang.of("Respond.createGalaxySuccess")).toLegacyText(player))
                            player.sendMessage(
                                Text.of(
                                    UNDERLINE,
                                    AQUA,
                                    TextActions.executeCallback { GUIHelper.open(player) { GalaxyManagement(galaxy) } },
                                    lang.of("Respond.openGalaxyConsole").toLegacyText(player)
                                )
                            )
                        } else {
                            player.sendMessage(Text.of(RED, lang.of("Respond.createGalaxyCancel")).toLegacyText(player))
                        }
                    }
                }
            }

            else -> Unit
        }
    }
}
