/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2021
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
import net.minecraft.text.LiteralText
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class Button private constructor(id: String, modelData: Int) : CustomItem(Identifier("galaxy", "item/gui/button/$id"), DIAMOND_HOE, modelData) {
    companion object {
        val BLANK = registry.register(Button("blank", 2001000))
        val ARROW_UP = registry.register(Button("arrow_up", 2002000))
        val ARROW_DOWN = registry.register(Button("arrow_down", 2002001))
        val ARROW_LEFT = registry.register(Button("arrow_left", 2002002))
        val ARROW_RIGHT = registry.register(Button("arrow_right", 2002003))
        val LONG_ARROW_UP = registry.register(Button("long_arrow_up", 2003000))
        val LONG_ARROW_DOWN = registry.register(Button("long_arrow_down", 2003001))
        val LONG_ARROW_LEFT = registry.register(Button("long_arrow_left", 2003002))
        val LONG_ARROW_RIGHT = registry.register(Button("long_arrow_right", 2003003))
        val SORT_09 = registry.register(Button("sort_09", 2004000))
        val SORT_AZ = registry.register(Button("sort_az", 2004001))
        val NO = registry.register(Button("no", 2005000))
        val YES = registry.register(Button("yes", 2005001))
        val OK = registry.register(Button("ok", 2005002))
        val MINUS = registry.register(Button("minus", 2006000))
        val PLUS = registry.register(Button("plus", 2006001))
        val POWER_OFF = registry.register(Button("power_off", 2007000))
        val POWER_ON = registry.register(Button("power_on", 2007001))
        val STARS = registry.register(Button("stars", 2008000))
        val ECO = registry.register(Button("eco", 2008001))
        val UPGRADE = registry.register(Button("upgrade", 2008002))
        val WARNING = registry.register(Button("warning", 2008003))
        val SETTING = registry.register(Button("setting", 2008004))
        val LIST = registry.register(Button("list", 2008005))
        val WRITE = registry.register(Button("write", 2008006))
        val LEAVE = registry.register(Button("leave", 2008007))
        val ALL = registry.register(Button("all", 2008008))
        val MESSAGE = registry.register(Button("message", 2008009))
        val UNCLICKABLE_ARROW_UP = registry.register(Button("unclickable_arrow_up", 2009000))
        val UNCLICKABLE_ARROW_DOWN = registry.register(Button("unclickable_arrow_down", 2009001))
        val UNCLICKABLE_ARROW_LEFT = registry.register(Button("unclickable_arrow_left", 2009002))
        val UNCLICKABLE_ARROW_RIGHT = registry.register(Button("unclickable_arrow_right", 2009003))
        val UNCLICKABLE_OK = registry.register(Button("unclickable_ok", 2009004))
        val MEMBERS = registry.register(Button("members", 2010000))
        val GUEST = registry.register(Button("guest", 2010001))
        val MEMBER = registry.register(Button("member", 2010002))
        val MANAGER = registry.register(Button("manager", 2010003))
        val GALAXY_SETTING = registry.register(Button("galaxy_setting", 2011000))
        val GALAXY_JOINED = registry.register(Button("galaxy_joined", 2011001))
        val GALAXY_ADD = registry.register(Button("galaxy_add", 2011002))
        val GALAXY = registry.register(Button("galaxy", 2012000))
        val PLANET_O = registry.register(Button("planet_o", 2012001))
        val PLANET_N = registry.register(Button("planet_n", 2012002))
        val PLANET_E = registry.register(Button("planet_e", 2012003))
        val PLANET_LIST = registry.register(Button("planet_list", 2013000))
        val PLANET_ADD = registry.register(Button("planet_add", 2013001))
        val MEMBER_REMOVE = registry.register(Button("member_remove", 2014000))
        val MEMBER_ADD = registry.register(Button("member_add", 2014001))
        val MEMBER_CHANGE = registry.register(Button("member_change", 2014002))
        val MEMBER_ASK = registry.register(Button("member_ask", 2014003))
        val MEMBER_SETTING = registry.register(Button("member_setting", 2014004))
    }

    override fun getName(): Text = LiteralText("").styled { it.withItalic(false) }
}
