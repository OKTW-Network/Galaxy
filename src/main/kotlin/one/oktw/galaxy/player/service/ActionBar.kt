package one.oktw.galaxy.player.service

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.player.data.ActionBarData
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
                .intervalTicks(1)
                .execute(Companion::tick)
                .submit(main)
        }

        fun setActionBar(player: Player, data: ActionBarData) {
            if (actionBar[player]?.priority ?: 0 <= data.priority) actionBar[player] = data
        }

        private fun tick() {
            val iterator = actionBar.iterator()

            while (iterator.hasNext()) {
                val (player, data) = iterator.next()

                player.sendTitle(buildTitle(data.text))

                // remove if timeout
                if (data.time > 0) data.time-- else iterator.remove()
            }
        }

        private fun buildTitle(text: Text) = Title.builder().actionBar(text).build()
    }
}
