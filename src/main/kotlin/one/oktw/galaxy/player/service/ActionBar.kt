package one.oktw.galaxy.player.service

import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.player.data.ActionBarData
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.title.Title
import java.util.concurrent.ConcurrentHashMap

class ActionBar {
    companion object {
        private val actionBar = ConcurrentHashMap<Player, ActionBarData>()

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
            for ((player, data) in actionBar) {
                player.sendTitle(buildTitle(data.text))

                // remove if timeout
                if (data.time > 0) data.time-- else actionBar -= player
            }
        }

        private fun buildTitle(text: Text) = Title.builder().actionBar(text).build()
    }
}
