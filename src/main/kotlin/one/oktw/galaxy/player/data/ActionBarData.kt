package one.oktw.galaxy.player.data

import org.spongepowered.api.text.Text

data class ActionBarData(
    val text: Text,
    val priority: Int = 0,
    var time: Int = 0
)