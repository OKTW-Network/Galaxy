/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2022
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

package one.oktw.galaxy.item

import net.minecraft.item.Items.DIAMOND_HOE
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class Button private constructor(id: String, modelData: Int) : CustomItem(Identifier("galaxy", "item/gui/button/$id"), DIAMOND_HOE, modelData) {
    companion object {
        val BLANK = registry.register(Button("blank", 1300000))
        val ARROW_UP = registry.register(Button("arrow_up", 1300001))
        val ARROW_DOWN = registry.register(Button("arrow_down", 1300002))
        val ARROW_LEFT = registry.register(Button("arrow_left", 1300003))
        val ARROW_RIGHT = registry.register(Button("arrow_right", 1300004))
        val LONG_ARROW_UP = registry.register(Button("long_arrow_up", 1300005))
        val LONG_ARROW_DOWN = registry.register(Button("long_arrow_down", 1300006))
        val LONG_ARROW_LEFT = registry.register(Button("long_arrow_left", 1300007))
        val LONG_ARROW_RIGHT = registry.register(Button("long_arrow_right", 1300008))
        val NO = registry.register(Button("no", 1300009))
        val OK = registry.register(Button("ok", 1300010))
        val MINUS = registry.register(Button("minus", 1300011))
        val PLUS = registry.register(Button("plus", 1300012))
        val SORT_09 = registry.register(Button("sort_09", 1300013))
        val SORT_AZ = registry.register(Button("sort_az", 1300014))
        val POWER_OFF = registry.register(Button("power_off", 1300015))
        val POWER_ON = registry.register(Button("power_on", 1300016))
        val STARS = registry.register(Button("stars", 1300017))
        val UPGRADE = registry.register(Button("upgrade", 1300018))
        val WARNING = registry.register(Button("warning", 1300019))
        val SETTING = registry.register(Button("setting", 1300021))
        val LIST = registry.register(Button("list", 1300022))
        val LEAVE = registry.register(Button("leave", 1300023))
        val ALL = registry.register(Button("all", 1300025))
        val WRITE = registry.register(Button("write", 1300026))
        val MESSAGE = registry.register(Button("message", 1300027))
        val GUEST = registry.register(Button("guest", 1300028))
        val MEMBER = registry.register(Button("member", 1300030))
        val MANAGER = registry.register(Button("manager", 1300031))
        val MEMBERS = registry.register(Button("members", 1300032))
        val GALAXY = registry.register(Button("galaxy", 1300033))
        val GALAXY_ADD = registry.register(Button("galaxy_add", 1300035))
        val GALAXY_SETTING = registry.register(Button("galaxy_setting", 1300038))
        val GALAXY_JOINED = registry.register(Button("galaxy_joined", 1300039))
        val PLANET_O = registry.register(Button("planet_o", 1300040))
        val PLANET_N = registry.register(Button("planet_n", 1300041))
        val PLANET_E = registry.register(Button("planet_e", 1300042))
        val PLANET_LIST = registry.register(Button("planet_list", 1300043))
        val PLANET_ADD = registry.register(Button("planet_add", 1300045))
        val MEMBER_REMOVE = registry.register(Button("member_remove", 1300046))
        val MEMBER_ADD = registry.register(Button("member_add", 1300047))
        val MEMBER_CHANGE = registry.register(Button("member_change", 1300048))
        val MEMBER_ASK = registry.register(Button("member_ask", 1300049))
        val MEMBER_SETTING = registry.register(Button("member_setting", 1300050))
        val UNCLICKABLE_ARROW_UP = registry.register(Button("unclickable_arrow_up", 1310001))
        val UNCLICKABLE_ARROW_DOWN = registry.register(Button("unclickable_arrow_down", 1310002))
        val UNCLICKABLE_ARROW_LEFT = registry.register(Button("unclickable_arrow_left", 1310003))
        val UNCLICKABLE_ARROW_RIGHT = registry.register(Button("unclickable_arrow_right", 1310004))
        val UNCLICKABLE_OK = registry.register(Button("unclickable_ok", 1310010))
        val YES = registry.register(Button("yes", 9999999))
        val ECO = registry.register(Button("eco", 9999999))
    }

    override fun getName(): Text = Text.literal("").styled { it.withItalic(false) }
}
