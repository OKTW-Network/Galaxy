package one.oktw.galaxy.traveler.service

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.traveler.data.ActionBarData
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.title.Title

class ActionBar {
    companion object {
        private val actionBar = HashMap<Player, ActionBarData>()

        init {
            Task.builder()
                .name("ActionBar")
                .intervalTicks(0)
                .execute(::tick)
                .submit(main)
        }

        fun setActionBar(player: Player, data: ActionBarData) {
            if (player in actionBar && actionBar[player]!!.priority > data.priority) return

            // show one time
            player.sendTitle(buildTitle(data.text))

            if (data.time > 0) actionBar[player] = data
        }

        private fun tick() {
            actionBar.forEach { player, data ->
                if (data.time == 0) {
                    actionBar -= player
                    return@forEach
                }

                data.time--
                player.sendTitle(buildTitle(data.text))
            }
        }

        private fun buildTitle(text: Text) = Title.builder().actionBar(text).build()
    }
}
