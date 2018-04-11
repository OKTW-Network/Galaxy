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
                .intervalTicks(1)
                .execute(::tick)
                .submit(main)
        }

        fun setActionBar(player: Player, data: ActionBarData) {
            if (actionBar[player]?.priority ?: 0 <= data.priority) actionBar[player] = data
        }

        private fun tick() {
            actionBar.forEach {
                it.key.sendTitle(buildTitle(it.value.text))

                if (it.value.time == 0) {
                    actionBar -= it.key
                    return@forEach
                }

                it.value.time--
            }
        }

        private fun buildTitle(text: Text) = Title.builder().actionBar(text).build()
    }
}
