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

package one.oktw.galaxy.item

import net.minecraft.item.Items.DIAMOND_HOE
import net.minecraft.text.Text
import net.minecraft.util.Identifier

class Gui private constructor(id: String) : CustomItem(Identifier.of("galaxy", "gui/$id"), DIAMOND_HOE) {
    companion object {
        val BLANK = registry.register(Gui("blank"))
        val ARROWHEAD_UP = registry.register(Gui("arrowhead_up"))
        val ARROWHEAD_DOWN = registry.register(Gui("arrowhead_down"))
        val ARROWHEAD_LEFT = registry.register(Gui("arrowhead_left"))
        val ARROWHEAD_RIGHT = registry.register(Gui("arrowhead_right"))
        val ARROW_UP = registry.register(Gui("arrow_up"))
        val ARROW_DOWN = registry.register(Gui("arrow_down"))
        val ARROW_LEFT = registry.register(Gui("arrow_left"))
        val ARROW_RIGHT = registry.register(Gui("arrow_right"))
        val CROSS_MARK = registry.register(Gui("cross_mark"))
        val CHECK_MARK = registry.register(Gui("check_mark"))
        val MINUS = registry.register(Gui("minus"))
        val PLUS = registry.register(Gui("plus"))
        val ZERO_TO_NINE = registry.register(Gui("zero_to_nine"))
        val A_TO_Z = registry.register(Gui("a_to_z"))
        val POWER_OFF = registry.register(Gui("power_off"))
        val POWER_ON = registry.register(Gui("power_on"))
        val STARS = registry.register(Gui("stars"))
        val UPGRADE = registry.register(Gui("upgrade"))
        val EXCLAMATION_MARK = registry.register(Gui("exclamation_mark"))
        val QUESTION_MARK = registry.register(Gui("question_mark"))
        val COG = registry.register(Gui("cog"))
        val LIST = registry.register(Gui("list"))
        val LEAVE = registry.register(Gui("leave"))
        val ENTER = registry.register(Gui("enter"))
        val SUQARES = registry.register(Gui("suqares"))
        val WRITE = registry.register(Gui("write"))
        val MESSAGE = registry.register(Gui("message"))
        val ANONYMOUS = registry.register(Gui("anonymous"))
        val PERSON = registry.register(Gui("person"))
        val MEMBER = registry.register(Gui("member"))
        val ADMINISTRATOR = registry.register(Gui("administrator"))
        val PEOPLE = registry.register(Gui("people"))
        val GALAXY = registry.register(Gui("galaxy"))
        val GALAXY_WITH_MINUS_SIGN = registry.register(Gui("galaxy_with_minus_sign"))
        val GALAXY_WITH_PLUS_SIGN = registry.register(Gui("galaxy_with_plus_sign"))
        val GALAXY_WITH_EXCLAMATION_MARK = registry.register(Gui("galaxy_with_exclamation_mark"))
        val GALAXY_WITH_QUESTION_MARK = registry.register(Gui("galaxy_with_question_mark"))
        val GALAXY_WITH_COG = registry.register(Gui("galaxy_with_cog"))
        val GALAXY_WITH_CHECK_MARK = registry.register(Gui("galaxy_with_check_mark"))
        val PLANET_NEUTRAL = registry.register(Gui("planet_neutral"))
        val PLANET_HOT = registry.register(Gui("planet_hot"))
        val PLANET_DARK = registry.register(Gui("planet_dark"))
        val PLANET_WITH_LIST = registry.register(Gui("planet_with_list"))
        val UNKNOWN_PLANET = registry.register(Gui("unknown_planet"))
        val UNKNOWN_PLANET_WITH_PLUS_SIGN = registry.register(Gui("unknown_planet_with_plus_sign"))
        val PERSON_WITH_MINUS_SIGN = registry.register(Gui("person_with_minus_sign"))
        val PERSON_WITH_PLUS_SIGN = registry.register(Gui("person_with_plus_sign"))
        val PERSON_WITH_EXCLAMATION_MARK = registry.register(Gui("person_with_exclamation_mark"))
        val PERSON_WITH_QUESTION_MARK = registry.register(Gui("person_with_question_mark"))
        val PERSON_WITH_COG = registry.register(Gui("person_with_cog"))
        val NUMBER_0 = registry.register(Gui("number_0"))
        val NUMBER_1 = registry.register(Gui("number_1"))
        val NUMBER_2 = registry.register(Gui("number_2"))
        val NUMBER_3 = registry.register(Gui("number_3"))
        val NUMBER_4 = registry.register(Gui("number_4"))
        val NUMBER_5 = registry.register(Gui("number_5"))
        val NUMBER_6 = registry.register(Gui("number_6"))
        val NUMBER_7 = registry.register(Gui("number_7"))
        val NUMBER_8 = registry.register(Gui("number_8"))
        val NUMBER_9 = registry.register(Gui("number_9"))
        val UNDERSCORE = registry.register(Gui("underscore"))
        val INFO = registry.register(Gui("info"))
        val MAGNIFIER = registry.register(Gui("magnifier"))
        val STARDUST_JAR = registry.register(Gui("stardust_jar"))
        val DISABLED_BLANK = registry.register(Gui("disabled_blank"))
        val DISABLED_ARROWHEAD_UP = registry.register(Gui("disabled_arrowhead_up"))
        val DISABLED_ARROWHEAD_DOWN = registry.register(Gui("disabled_arrowhead_down"))
        val DISABLED_ARROWHEAD_LEFT = registry.register(Gui("disabled_arrowhead_left"))
        val DISABLED_ARROWHEAD_RIGHT = registry.register(Gui("disabled_arrowhead_right"))
        val DISABLED_ARROW_UP = registry.register(Gui("disabled_arrow_up"))
        val DISABLED_ARROW_DOWN = registry.register(Gui("disabled_arrow_down"))
        val DISABLED_ARROW_LEFT = registry.register(Gui("disabled_arrow_left"))
        val DISABLED_ARROW_RIGHT = registry.register(Gui("disabled_arrow_right"))
        val DISABLED_CROSS_MARK = registry.register(Gui("disabled_cross_mark"))
        val DISABLED_CHECK_MARK = registry.register(Gui("disabled_check_mark"))
    }

    override fun getName(): Text = Text.literal("")
}
