package one.oktw.galaxy.gui

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.data.DataUUID
import one.oktw.galaxy.galaxy.data.Galaxy
import one.oktw.galaxy.galaxy.data.extensions.delMember
import one.oktw.galaxy.galaxy.data.extensions.setGroup
import one.oktw.galaxy.item.enums.ButtonType.MEMBER_CHANGE
import one.oktw.galaxy.item.enums.ButtonType.MEMBER_REMOVE
import one.oktw.galaxy.item.type.Button
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.InventoryArchetypes
import org.spongepowered.api.item.inventory.property.InventoryTitle
import org.spongepowered.api.item.inventory.query.QueryOperationTypes
import org.spongepowered.api.item.inventory.type.GridInventory
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*

class ManageMember(private val galaxy: Galaxy, private val member: UUID) : GUI() {
    override val token = "ManageMember-${galaxy.uuid}-$member"
    private val user = Sponge.getServiceManager().provide(UserStorageService::class.java).get().get(member).get()
    override val inventory: Inventory = Inventory.builder()
        .of(InventoryArchetypes.HOPPER)
        .property(InventoryTitle.of(Text.of(user.name)))
        .listener(InteractInventoryEvent::class.java, this::eventProcess)
        .build(main)
    private val buttonID = Array(3) { UUID.randomUUID() }

    init {
        val gridInventory =
            inventory.query<GridInventory>(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory::class.java))

        // button
        Button(MEMBER_REMOVE).createItemStack()
            .apply {
                offer(DataUUID(buttonID[0]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, "移除成員"))
            }
            .let { gridInventory.set(1, 0, it) }

        Button(MEMBER_CHANGE).createItemStack()
            .apply {
                offer(DataUUID(buttonID[1]))
                offer(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, TextStyles.BOLD, "更改身分組"))
            }
            .let { gridInventory.set(3, 0, it) }

        // register event
        registerEvent(ClickInventoryEvent::class.java, this::clickEvent)
    }

    private fun clickEvent(event: ClickInventoryEvent) {
        event.isCancelled = true

        val player = event.source as Player

        when (event.cursorTransaction.default[DataUUID.key].orElse(null) ?: return) {
            buttonID[0] -> GUIHelper.open(player) {
                Confirm(Text.of("是否移除成員？")) {
                    if (it) {
                        galaxy.delMember(member)
                        GUIHelper.close(token)
                    }
                }
            }
            buttonID[1] -> GUIHelper.open(player) { GroupSelect { galaxy.setGroup(member, it) } }
        }
    }
}
