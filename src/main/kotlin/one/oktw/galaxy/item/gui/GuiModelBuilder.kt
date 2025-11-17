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

package one.oktw.galaxy.item.gui

import net.minecraft.world.item.component.CustomModelData

class GuiModelBuilder {
    private var background: GuiBackground = GuiBackground.NONE
    private var guiField: GuiField = GuiField.NONE
    private var pattern: GuiPattern = GuiPattern.NONE
    private var slot: GuiSlot = GuiSlot.NONE
    private var button: GuiButton = GuiButton.NONE
    private var icon: GuiIcon = GuiIcon.NONE
    private var foreground: GuiForeground = GuiForeground.NONE

    fun withBackground(item: GuiBackground): GuiModelBuilder {
        background = item
        return this
    }

    fun withField(item: GuiField): GuiModelBuilder {
        guiField = item
        return this
    }

    fun withPattern(item: GuiPattern): GuiModelBuilder {
        pattern = item
        return this
    }

    fun withSlot(item: GuiSlot): GuiModelBuilder {
        slot = item
        return this
    }

    fun withButton(item: GuiButton): GuiModelBuilder {
        button = item
        return this
    }

    fun withIcon(item: GuiIcon): GuiModelBuilder {
        icon = item
        return this
    }

    fun withForeground(item: GuiForeground): GuiModelBuilder {
        foreground = item
        return this
    }

    fun build(): CustomModelData = CustomModelData(
        emptyList(),
        emptyList(),
        mutableListOf(
            background.value,
            guiField.value,
            pattern.value,
            slot.value,
            button.value,
            icon.value,
            foreground.value
        ),
        emptyList(),
    )
}
