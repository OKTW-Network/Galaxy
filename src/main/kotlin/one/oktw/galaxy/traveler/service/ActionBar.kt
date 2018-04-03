package one.oktw.galaxy.traveler.service

import one.oktw.galaxy.Main.Companion.main
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.title.Title
import java.util.*
import kotlin.collections.HashMap

class ActionBar {
    companion object {
        private val actionBar = HashMap<Player, Stack<() -> Text?>>()
        private val lastText = HashMap<Player, Text>()

        init {
            Task.builder()
                .intervalTicks(1)
                .execute(::doTick)
                .submit(main)
        }

        fun add(player: Player, title: () -> Text?) {
            actionBar.getOrPut(player) { Stack() } += title
        }

        private fun doTick() {
            actionBar.forEach {
                val player = it.key
                val title = it.value
                while (true) {
                    if (title.empty()) {
                        actionBar -= player
                        break
                    }

                    val text = title.peek()()
                    if (text == null) {
                        title.pop()
                        continue
                    } else {
                        player.sendTitle(Title.builder().actionBar(text).build())
                        break
                    }
                }
            }
        }
    }
}
