/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2025
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package one.oktw.galaxy.block.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.core.component.DataComponentGetter
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickType
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemContainerContents
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.BlockHitResult
import one.oktw.galaxy.block.listener.CustomBlockClickListener
import one.oktw.galaxy.gui.GUI
import one.oktw.galaxy.gui.GUISBackStackManager
import one.oktw.galaxy.item.Gui
import one.oktw.galaxy.item.Misc
import one.oktw.galaxy.item.gui.GuiButton
import one.oktw.galaxy.item.gui.GuiIcon
import one.oktw.galaxy.item.gui.GuiModelBuilder

class TestGuiBlockEntity(type: BlockEntityType<*>, pos: BlockPos, modelItem: ItemStack) : ModelCustomBlockEntity(type, pos, modelItem),
    CustomBlockClickListener, Container {
    private val checkMarkButton = Gui(
        GuiModelBuilder().withButton(GuiButton.BUTTON).withIcon(GuiIcon.CHECK_MARK).build()
    ).createItemStack()
    private val eraseButton = Gui(
        GuiModelBuilder().withButton(GuiButton.BUTTON).withIcon(GuiIcon.ERASE).build()
    ).createItemStack()
    private val closeAllButton = Gui(
        GuiModelBuilder().withButton(GuiButton.BUTTON).withIcon(GuiIcon.CROSS_MARK).build(),
        Component.nullToEmpty("CLOSE ALL")
    ).createItemStack()
    private val plusButton = Gui(
        GuiModelBuilder().withButton(GuiButton.BUTTON).withIcon(GuiIcon.PLUS_SIGN).build()
    ).createItemStack()


    private val inventory = NonNullList.withSize(3 * 9, ItemStack.EMPTY)

    private val gui = GUI.Builder(MenuType.GENERIC_9x6)
        .setTitle(Component.nullToEmpty("Test GUI"))
        .setBackground("A", ResourceLocation.fromNamespaceAndPath("galaxy", "gui_font/container_layout/test_gui"))
        .blockEntity(this)
        .apply {
            var i = 0
            for (x in 0 until 9) addSlot(x, 0, Slot(this@TestGuiBlockEntity, i++, 0, 0))
            for (y in 4 until 6) for (x in 0 until 9) addSlot(x, y, Slot(this@TestGuiBlockEntity, i++, 0, 0))
        }.build().apply {
            editInventory {
                fill(0 until 9, 1 until 4, Misc.PLACEHOLDER.createItemStack())
                set(4, 2, checkMarkButton)
                set(2, 2, plusButton)
            }
            addBinding(4, 2) {
                cancel = true
                if (action == ClickType.PICKUP) GUISBackStackManager.openGUI(player, gui2)
            }
            addBinding(2, 2) {
                cancel = true
                if (action == ClickType.PICKUP) GUISBackStackManager.openGUI(player, gui3)
            }
        }

    private val gui2 = GUI.Builder(MenuType.GENERIC_9x4)
        .setTitle(Component.nullToEmpty("Test GUI2"))
        .setBackground("B", ResourceLocation.fromNamespaceAndPath("galaxy", "gui_font/container_layout/test_gui"))
        .blockEntity(this)
        .apply {
            var i = 0
            for (y in 0 until 3) for (x in 0 until 9) addSlot(x, y, Slot(this@TestGuiBlockEntity, i++, 0, 0))
        }.build().apply {
            editInventory {
                fill(0 until 9, 3..3, Misc.PLACEHOLDER.createItemStack())
                set(4, 3, closeAllButton)
            }
            addBinding(4, 3) {
                cancel = true
                if (action == ClickType.PICKUP) GUISBackStackManager.closeAll(player)
            }
        }
    private val gui3 = GUI.Builder(MenuType.ANVIL)
        .setTitle(Component.literal("Test GUI3"))
        .setBackground("C", ResourceLocation.fromNamespaceAndPath("galaxy", "gui_font/container_layout/test_gui"))
        .blockEntity(this).build()
        .apply {
            editInventory {
                set(0, eraseButton)
                set(1, Misc.PLACEHOLDER.createItemStack())
                set(2, checkMarkButton)
            }
            addBinding(2) {
                cancel = true
                if (action == ClickType.PICKUP) player.sendSystemMessage(Component.literal(inputText))
            }
        }

    override fun readCopyableData(view: ValueInput) {
        super.readCopyableData(view)
        ContainerHelper.loadAllItems(view, inventory)
    }

    override fun saveAdditional(view: ValueOutput) {
        super.saveAdditional(view)
        ContainerHelper.saveAllItems(view, inventory)
    }

    override fun collectImplicitComponents(builder: DataComponentMap.Builder) {
        super.collectImplicitComponents(builder)
        builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(inventory))
    }

    override fun applyImplicitComponents(components: DataComponentGetter) {
        super.applyImplicitComponents(components)
        components.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(inventory)
    }

    override fun removeComponentsFromTag(view: ValueOutput) {
        super.removeComponentsFromTag(view)
        view.discard("Items")
    }

    override fun onClick(player: Player, hand: InteractionHand, hit: BlockHitResult): InteractionResult {
        GUISBackStackManager.openGUI(player as ServerPlayer, gui)
        return InteractionResult.SUCCESS_SERVER
    }

    override fun clearContent() {
        inventory.clear()
    }

    override fun getContainerSize() = inventory.size

    override fun isEmpty() = inventory.isEmpty()

    override fun getItem(slot: Int) = inventory[slot]

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        val itemStack = ContainerHelper.removeItem(inventory, slot, amount)
        if (!itemStack.isEmpty) {
            this.setChanged()
        }
        return itemStack
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack = ContainerHelper.takeItem(inventory, slot)

    override fun setItem(slot: Int, stack: ItemStack) {
        inventory[slot] = stack
        if (stack.count > this.maxStackSize) {
            stack.count = this.maxStackSize
        }
        this.setChanged()
    }

    override fun stillValid(player: Player): Boolean {
        return Container.stillValidBlockEntity(this, player)
    }
}
