package one.oktw.galaxy.gui.machine

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.data.extensions.*
import one.oktw.galaxy.galaxy.enums.Group.*
import one.oktw.galaxy.galaxy.enums.Group.MEMBER
import one.oktw.galaxy.gui.*
import one.oktw.galaxy.item.enums.ButtonType.*
import one.oktw.galaxy.item.type.Button
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes.CHEST
import org.spongepowered.api.item.inventory.InventoryArchetypes.DOUBLE_CHEST
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.type.GridInventory
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors.*
import org.spongepowered.api.text.format.TextStyles.BOLD
import org.spongepowered.api.text.serializer.TextSerializers
import java.util.*
import java.util.Arrays.asList

class PlanetTerminal(private val galaxy: Galaxy, private val player: Player) : GUI() {
    private val lang = languageService.getDefaultLanguage()
    private val buttonID = Array(7) { UUID.randomUUID() }
    override val token = "PlanetTerminal-${galaxy.uuid}-${player.uniqueId}"
    override val inventory: Inventory = Inventory.builder()
        .of(if (galaxy.getGroup(player) == VISITOR) CHEST else DOUBLE_CHEST)
        .property(InventoryTitle.of(Text.of(lang["UI.Title.PlanetTerminal"])))
        .listener(InteractInventoryEvent::class.java, ::eventProcess)
        .build(main)

    init {
        val inventory = inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        val buttonExit = Button(EXIT).createItemStack()
            .apply {
                offer(DataUUID(buttonID[0]))
                offer(Keys.DISPLAY_NAME, Text.of(GREEN, BOLD, lang["UI.Button.BackToSpaceShip"]))
            }
        val buttonListPlanet = Button(PLANET_O).createItemStack()
            .apply {
                offer(DataUUID(buttonID[1]))
                offer(Keys.DISPLAY_NAME, Text.of(GREEN, lang["UI.Button.PlanetList"]))
            }
        val buttonStarDust by lazy {
            Button(STARS).createItemStack()
                .apply {
                    offer(DataUUID(buttonID[2]))
                    offer(Keys.DISPLAY_NAME, Text.of(GREEN, BOLD, lang["UI.Button.StarDust"]))
                }
        }
        val buttonListMember by lazy {
            Button(MEMBERS).createItemStack()
                .apply {
                    offer(DataUUID(buttonID[3]))
                    offer(Keys.DISPLAY_NAME, Text.of(GREEN, lang["UI.Button.MemberList"]))
                }
        }
        val buttonECS by lazy {
            Button(ECS).createItemStack()
                .apply {
                    offer(DataUUID(buttonID[4]))
                    offer(Keys.DISPLAY_NAME, Text.of(GREEN, BOLD, lang["UI.Button.ECS"]))
                }
        }
        val buttonManageGalaxy by lazy {
            Button(LIST).createItemStack()
                .apply {
                    offer(DataUUID(buttonID[5]))
                    offer(Keys.DISPLAY_NAME, Text.of(GREEN, lang["UI.Button.ManageGalaxy"]))
                }
        }
        val buttonRequestJoin by lazy {
            Button(PLUS).createItemStack()
                .apply {
                    if (player.uniqueId in galaxy.joinRequest) {
                        offer(Keys.DISPLAY_NAME, Text.of(GRAY, lang["UI.Button.JoinRequestSent"]))
                    } else {
                        offer(DataUUID(buttonID[6]))
                        offer(Keys.DISPLAY_NAME, Text.of(GREEN, lang["UI.Button.JoinRequest"]))
                    }
                }
        }
        val buttonNotify by lazy {
            Button(WARNING).createItemStack()
                .apply {
                    offer(Keys.DISPLAY_NAME, Text.of(YELLOW, lang["UI.Button.GalaxyNotice"]))
                    offer(
                        Keys.ITEM_LORE,
                        galaxy.notice.split('\n').map(TextSerializers.FORMATTING_CODE::deserialize)
                    )
                }
        }

        // Info
        Button(GUI_INFO).createItemStack()
            .apply {
                val planet = galaxy.getPlanet(player.world)!!

                offer(Keys.DISPLAY_NAME, Text.of(GREEN, BOLD, lang["UI.Button.Info"]))
                offer(
                    Keys.ITEM_LORE, asList(
                        Text.of(BOLD, YELLOW, lang["UI.Tip.PlanetLevel"], RESET, ":", planet.level),
                        Text.of(BOLD, YELLOW, lang["UI.Tip.PlanetRange"], RESET, ":", planet.size),
                        Text.of(BOLD, YELLOW, lang["UI.Tip.PlanetEffect"], RESET, ":")
                    ).apply { addAll(planet.effect.map { Text.of(BOLD, it.type.potionTranslation) }) }
                )
            }
            .let { inventory.set(4, 0, it) }

        when (galaxy.getGroup(player)) {
            OWNER, ADMIN -> {
                inventory.set(2, 2, buttonListMember)
                inventory.set(4, 2, buttonECS)
                inventory.set(6, 2, buttonManageGalaxy)

                inventory.set(2, 4, buttonListPlanet)
                inventory.set(4, 4, buttonStarDust)
                inventory.set(6, 4, buttonExit)
            }
            MEMBER -> {
                inventory.set(2, 2, buttonListMember)
                inventory.set(6, 2, buttonNotify)

                inventory.set(2, 4, buttonListPlanet)
                inventory.set(4, 4, buttonStarDust)
                inventory.set(6, 4, buttonExit)
            }
            VISITOR -> {
                inventory.set(2, 2, buttonRequestJoin)
                inventory.set(4, 2, buttonListPlanet)
                inventory.set(6, 2, buttonExit)
            }
        }

        GUIHelper.fillEmptySlot(inventory)

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true

        when (event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return) {
            buttonID[0] -> player.transferToWorld(Sponge.getServer().run { getWorld(defaultWorldName).get() })
            buttonID[1] -> GUIHelper.openAsync(player) { BrowserPlanet(galaxy.refresh()) }
            buttonID[2] -> GUIHelper.openAsync(player) {
                NumberSelect(
                    Text.of(lang["UI.Title.SelectStarDustCount"]),
                    asList(
                        Text.of(
                            GREEN,
                            lang["UI.Tip.StarDustCount"].format(galaxy.refresh().getMember(player.uniqueId)?.starDust)
                        )
                    )
                ) {
                    if (it <= 0) return@NumberSelect

                    launch {
                        val galaxy = galaxy.refresh()
                        val member = galaxy.getMember(player.uniqueId) ?: return@launch

                        if (member.takeStarDust(it)) {
                            galaxy.saveMember(member).join()
                            galaxy.update { giveStarDust(it) }.join()

                            player.sendMessage(Text.of(AQUA, lang["Respond.DonateStarDustTip"].format(it)))
                        } else {
                            player.sendMessage(Text.of(RED, lang["Respond.StarDustNotEnough"]))
                        }
                    }
                }
            }
            buttonID[3] -> GUIHelper.openAsync(player) { BrowserMember(galaxy.refresh()) }
            buttonID[4] -> Unit // TODO ECS
            buttonID[5] -> GUIHelper.openAsync(player) { GalaxyManagement(galaxy.refresh()) }
            buttonID[6] -> {
                galaxy.requestJoin(player.uniqueId)

                event.isCancelled = false
                event.cursorTransaction.setCustom(ItemStackSnapshot.NONE)

                inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java)).apply {
                    Button(PLUS).createItemStack()
                        .apply {
                            offer(Keys.DISPLAY_NAME, Text.of(GRAY, lang["UI.Button.JoinRequestSent"]))
                        }
                        .let { set(2, 2, it) }
                }
            }
        }
    }
}
