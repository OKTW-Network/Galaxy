/*
 * OKTW Galaxy Project
 * Copyright (C) 2018-2018
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

package one.oktw.galaxy.economy.event

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import one.oktw.galaxy.Main
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.economy.service.EconomyService
import one.oktw.galaxy.galaxy.data.extensions.getMember
import one.oktw.galaxy.galaxy.data.extensions.saveMember
import one.oktw.galaxy.player.data.ActionBarData
import one.oktw.galaxy.player.service.ActionBar
import one.oktw.galaxy.translation.extensions.toLegacyText
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors.AQUA

class EconomyEvent {
    init {
        EconomyService
    }

    @SubscribeEvent
    fun onPickupExp(event: PlayerPickupXpEvent) {
        val player = event.entityPlayer as Player
        val lang = Main.translationService

        if (event.orb.xpValue == 0) return

        GlobalScope.launch {
            galaxyManager.get(player.world)?.run {
                getMember(player.uniqueId)
                    ?.apply {
                        giveStarDust(event.orb.xpValue)

                        Text.of(AQUA, lang.of("traveler.event.get_dust", event.orb.xpValue, starDust)).toLegacyText(player)
                            .let { ActionBar.setActionBar(player, ActionBarData(it, 2, 10)) }
                    }
                    ?.let { saveMember(it) }
            }
        }

    }
}
