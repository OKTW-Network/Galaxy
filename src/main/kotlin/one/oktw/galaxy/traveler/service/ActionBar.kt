package one.oktw.galaxy.traveler.service

import one.oktw.galaxy.Main.Companion.main
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.Text
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque

private typealias title = () -> Text

class ActionBar {
    private val actionBar = ConcurrentHashMap<Player, ConcurrentLinkedDeque<title>>()

    init {
        Task.builder()
            .async()
            .intervalTicks(1)
            .execute(::doTick)
            .submit(main)
    }

    fun add(player: Player, title: title) {
        actionBar.getOrPut(player) { ConcurrentLinkedDeque() } += title
    }

    private fun doTick() {
        // TODO
    }
}