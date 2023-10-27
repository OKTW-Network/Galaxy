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
        val ARROWHEAD_UP = registry.register(Button("arrowhead_up", 1300001))
        val ARROWHEAD_DOWN = registry.register(Button("arrowhead_down", 1300002))
        val ARROWHEAD_LEFT = registry.register(Button("arrowhead_left", 1300003))
        val ARROWHEAD_RIGHT = registry.register(Button("arrowhead_right", 1300004))
        val ARROW_UP = registry.register(Button("arrow_up", 1300005))
        val ARROW_DOWN = registry.register(Button("arrow_down", 1300006))
        val ARROW_LEFT = registry.register(Button("arrow_left", 1300007))
        val ARROW_RIGHT = registry.register(Button("arrow_right", 1300008))
        val CROSS_MARK = registry.register(Button("cross_mark", 1300009))
        val CHECK_MARK = registry.register(Button("check_mark", 1300010))
        val MINUS = registry.register(Button("minus", 1300011))
        val PLUS = registry.register(Button("plus", 1300012))
        val ZERO_TO_NINE = registry.register(Button("zero_to_nine", 1300013))
        val A_TO_Z = registry.register(Button("a_to_z", 1300014))
        val POWER_OFF = registry.register(Button("power_off", 1300015))
        val POWER_ON = registry.register(Button("power_on", 1300016))
        val STARS = registry.register(Button("stars", 1300017))
        val UPGRADE = registry.register(Button("upgrade", 1300018))
        val EXCLAMATION_MARK = registry.register(Button("exclamation_mark", 1300019))
        val QUESTION_MARK = registry.register(Button("question_mark", 1300020))
        val COG = registry.register(Button("cog", 1300021))
        val LIST = registry.register(Button("list", 1300022))
        val LEAVE = registry.register(Button("leave", 1300023))
        val ENTER = registry.register(Button("enter", 1300024))
        val SUQARES = registry.register(Button("suqares", 1300025))
        val WRITE = registry.register(Button("write", 1300026))
        val MESSAGE = registry.register(Button("message", 1300027))
        val ANONYMOUS = registry.register(Button("anonymous", 1300028))
        val PERSON = registry.register(Button("person", 1300029))
        val MEMBER = registry.register(Button("member", 1300030))
        val ADMINISTRATOR = registry.register(Button("administrator", 1300031))
        val PEOPLE = registry.register(Button("people", 1300032))
        val GALAXY = registry.register(Button("galaxy", 1300033))
        val GALAXY_WITH_MINUS_SIGN = registry.register(Button("galaxy_with_minus_sign", 1300034))
        val GALAXY_WITH_PLUS_SIGN = registry.register(Button("galaxy_with_plus_sign", 1300035))
        val GALAXY_WITH_EXCLAMATION_MARK = registry.register(Button("galaxy_with_exclamation_mark", 1300036))
        val GALAXY_WITH_QUESTION_MARK = registry.register(Button("galaxy_with_question_mark", 1300037))
        val GALAXY_WITH_COG = registry.register(Button("galaxy_with_cog", 1300038))
        val GALAXY_WITH_CHECK_MARK = registry.register(Button("galaxy_with_check_mark", 1300039))
        val PLANET_NEUTRAL = registry.register(Button("planet_neutral", 1300040))
        val PLANET_HOT = registry.register(Button("planet_hot", 1300041))
        val PLANET_DARK = registry.register(Button("planet_dark", 1300042))
        val PLANET_WITH_LIST = registry.register(Button("planet_with_list", 1300043))
        val UNKNOWN_PLANET = registry.register(Button("unknown_planet", 1300044))
        val UNKNOWN_PLANET_WITH_PLUS_SIGN = registry.register(Button("unknown_planet_with_plus_sign", 1300045))
        val PERSON_WITH_MINUS_SIGN = registry.register(Button("person_with_minus_sign", 1300046))
        val PERSON_WITH_PLUS_SIGN = registry.register(Button("person_with_plus_sign", 1300047))
        val PERSON_WITH_EXCLAMATION_MARK = registry.register(Button("person_with_exclamation_mark", 1300048))
        val PERSON_WITH_QUESTION_MARK = registry.register(Button("person_with_question_mark", 1300049))
        val PERSON_WITH_COG = registry.register(Button("person_with_cog", 1300050))
        val NUMBER_0 = registry.register(Button("number_0", 1300051))
        val NUMBER_1 = registry.register(Button("number_1", 1300052))
        val NUMBER_2 = registry.register(Button("number_2", 1300053))
        val NUMBER_3 = registry.register(Button("number_3", 1300054))
        val NUMBER_4 = registry.register(Button("number_4", 1300055))
        val NUMBER_5 = registry.register(Button("number_5", 1300056))
        val NUMBER_6 = registry.register(Button("number_6", 1300057))
        val NUMBER_7 = registry.register(Button("number_7", 1300058))
        val NUMBER_8 = registry.register(Button("number_8", 1300059))
        val NUMBER_9 = registry.register(Button("number_9", 1300060))
        val UNDERSCORE = registry.register(Button("underscore", 1300061))
        val INFO = registry.register(Button("info", 1300062))
        val MAGNIFIER = registry.register(Button("magnifier", 1300063))
        val STARDUST_JAR = registry.register(Button("stardust_jar", 1300064))
        val DISABLED_BLANK = registry.register(Button("disabled_blank", 1310000))
        val DISABLED_ARROWHEAD_UP = registry.register(Button("disabled_arrowhead_up", 1310001))
        val DISABLED_ARROWHEAD_DOWN = registry.register(Button("disabled_arrowhead_down", 1310002))
        val DISABLED_ARROWHEAD_LEFT = registry.register(Button("disabled_arrowhead_left", 1310003))
        val DISABLED_ARROWHEAD_RIGHT = registry.register(Button("disabled_arrowhead_right", 1310004))
        val DISABLED_ARROW_UP = registry.register(Button("disabled_arrow_up", 1310005))
        val DISABLED_ARROW_DOWN = registry.register(Button("disabled_arrow_down", 1310006))
        val DISABLED_ARROW_LEFT = registry.register(Button("disabled_arrow_left", 1310007))
        val DISABLED_ARROW_RIGHT = registry.register(Button("disabled_arrow_right", 1310008))
        val DISABLED_CROSS_MARK = registry.register(Button("disabled_cross_mark", 1310009))
        val DISABLED_CHECK_MARK = registry.register(Button("disabled_check_mark", 1310010))
    }

    override fun getName(): Text = Text.literal("").styled { it.withItalic(false) }
}
