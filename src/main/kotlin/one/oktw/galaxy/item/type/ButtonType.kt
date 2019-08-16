/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2019
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

package one.oktw.galaxy.item.type

enum class ButtonType(val customModelData: Int) {
    BLANK(2001000),
    ARROW_UP(2002000),
    ARROW_DOWN(2002001),
    ARROW_LEFT(2002002),
    ARROW_RIGHT(2002003),
    LONG_ARROW_UP(2003000),
    LONG_ARROW_DOWN(2003001),
    LONG_ARROW_LEFT(2003002),
    LONG_ARROW_RIGHT(2003003),
    09(2004000),
    AZ(2004001),
    NO(2005000),
    YES(2005001),
    OK(2005002),
    MINUS(2006000),
    PLUS(2006001),
    POWER_OFF(2007000),
    pOWER_ON(2007001),
    STARS(2008000),
    ECO(2008001),
    UPGRADE(2008002),
    WARNING(2008003),
    SETTING(2008004),
    LIST(2008005),
    WRITE(2008006),
    LEAVE(2008007),
    ALL(2008008),
    UNCLICKABLE_ARROW_UP(2009000),
    UNCLICKABLE_ARROW_DOWN(2009001),
    UNCLICKABLE_ARROW_LEFT(2009002),
    UNCLICKABLE_ARROW_RIGHT(2009003),
    UNCLICKABLE_OK(2009004),
    MEMBERS(2010000),
    GUEST(2010001),
    MEMBER(2010002),
    MANAGER(2010003),
    GALAXY_SETTING(2011000),
    GALAXY_JOINED(2011001),
    GALAXY(2012000),
    PLANET_O(2012001),
    PLANET_N(2012002),
    PLANET_E(2012003),
    PLANET_LIST(2013000),
    PLANET_ADD(2013001),
    MEMBER_REMOVE(2014000),
    MEMBER_ADD(2014001),
    MEMBER_CHANGE(2014002),
    MEMBER_ASK(2014003),
    MEMBER_SETTING(2014004)
}
