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

class Button private constructor(id: String) : CustomItem(Identifier.of("galaxy", "item/gui/button/$id"), DIAMOND_HOE) {
    companion object {
        val BLANK = registry.register(Button("blank"))
        val ARROWHEAD_UP = registry.register(Button("arrowhead_up"))
        val ARROWHEAD_DOWN = registry.register(Button("arrowhead_down"))
        val ARROWHEAD_LEFT = registry.register(Button("arrowhead_left"))
        val ARROWHEAD_RIGHT = registry.register(Button("arrowhead_right"))
        val ARROW_UP = registry.register(Button("arrow_up"))
        val ARROW_DOWN = registry.register(Button("arrow_down"))
        val ARROW_LEFT = registry.register(Button("arrow_left"))
        val ARROW_RIGHT = registry.register(Button("arrow_right"))
        val CROSS_MARK = registry.register(Button("cross_mark"))
        val CHECK_MARK = registry.register(Button("check_mark"))
        val MINUS = registry.register(Button("minus"))
        val PLUS = registry.register(Button("plus"))
        val ZERO_TO_NINE = registry.register(Button("zero_to_nine"))
        val A_TO_Z = registry.register(Button("a_to_z"))
        val POWER_OFF = registry.register(Button("power_off"))
        val POWER_ON = registry.register(Button("power_on"))
        val STARS = registry.register(Button("stars"))
        val UPGRADE = registry.register(Button("upgrade"))
        val EXCLAMATION_MARK = registry.register(Button("exclamation_mark"))
        val QUESTION_MARK = registry.register(Button("question_mark"))
        val COG = registry.register(Button("cog"))
        val LIST = registry.register(Button("list"))
        val LEAVE = registry.register(Button("leave"))
        val ENTER = registry.register(Button("enter"))
        val SUQARES = registry.register(Button("suqares"))
        val WRITE = registry.register(Button("write"))
        val MESSAGE = registry.register(Button("message"))
        val ANONYMOUS = registry.register(Button("anonymous"))
        val PERSON = registry.register(Button("person"))
        val MEMBER = registry.register(Button("member"))
        val ADMINISTRATOR = registry.register(Button("administrator"))
        val PEOPLE = registry.register(Button("people"))
        val GALAXY = registry.register(Button("galaxy"))
        val GALAXY_WITH_MINUS_SIGN = registry.register(Button("galaxy_with_minus_sign"))
        val GALAXY_WITH_PLUS_SIGN = registry.register(Button("galaxy_with_plus_sign"))
        val GALAXY_WITH_EXCLAMATION_MARK = registry.register(Button("galaxy_with_exclamation_mark"))
        val GALAXY_WITH_QUESTION_MARK = registry.register(Button("galaxy_with_question_mark"))
        val GALAXY_WITH_COG = registry.register(Button("galaxy_with_cog"))
        val GALAXY_WITH_CHECK_MARK = registry.register(Button("galaxy_with_check_mark"))
        val PLANET_NEUTRAL = registry.register(Button("planet_neutral"))
        val PLANET_HOT = registry.register(Button("planet_hot"))
        val PLANET_DARK = registry.register(Button("planet_dark"))
        val PLANET_WITH_LIST = registry.register(Button("planet_with_list"))
        val UNKNOWN_PLANET = registry.register(Button("unknown_planet"))
        val UNKNOWN_PLANET_WITH_PLUS_SIGN = registry.register(Button("unknown_planet_with_plus_sign"))
        val PERSON_WITH_MINUS_SIGN = registry.register(Button("person_with_minus_sign"))
        val PERSON_WITH_PLUS_SIGN = registry.register(Button("person_with_plus_sign"))
        val PERSON_WITH_EXCLAMATION_MARK = registry.register(Button("person_with_exclamation_mark"))
        val PERSON_WITH_QUESTION_MARK = registry.register(Button("person_with_question_mark"))
        val PERSON_WITH_COG = registry.register(Button("person_with_cog"))
        val NUMBER_0 = registry.register(Button("number_0"))
        val NUMBER_1 = registry.register(Button("number_1"))
        val NUMBER_2 = registry.register(Button("number_2"))
        val NUMBER_3 = registry.register(Button("number_3"))
        val NUMBER_4 = registry.register(Button("number_4"))
        val NUMBER_5 = registry.register(Button("number_5"))
        val NUMBER_6 = registry.register(Button("number_6"))
        val NUMBER_7 = registry.register(Button("number_7"))
        val NUMBER_8 = registry.register(Button("number_8"))
        val NUMBER_9 = registry.register(Button("number_9"))
        val UNDERSCORE = registry.register(Button("underscore"))
        val INFO = registry.register(Button("info"))
        val MAGNIFIER = registry.register(Button("magnifier"))
        val STARDUST_JAR = registry.register(Button("stardust_jar"))
        val DISABLED_BLANK = registry.register(Button("disabled_blank"))
        val DISABLED_ARROWHEAD_UP = registry.register(Button("disabled_arrowhead_up"))
        val DISABLED_ARROWHEAD_DOWN = registry.register(Button("disabled_arrowhead_down"))
        val DISABLED_ARROWHEAD_LEFT = registry.register(Button("disabled_arrowhead_left"))
        val DISABLED_ARROWHEAD_RIGHT = registry.register(Button("disabled_arrowhead_right"))
        val DISABLED_ARROW_UP = registry.register(Button("disabled_arrow_up"))
        val DISABLED_ARROW_DOWN = registry.register(Button("disabled_arrow_down"))
        val DISABLED_ARROW_LEFT = registry.register(Button("disabled_arrow_left"))
        val DISABLED_ARROW_RIGHT = registry.register(Button("disabled_arrow_right"))
        val DISABLED_CROSS_MARK = registry.register(Button("disabled_cross_mark"))
        val DISABLED_CHECK_MARK = registry.register(Button("disabled_check_mark"))
    }

    override fun getName(): Text = Text.literal("")
}
