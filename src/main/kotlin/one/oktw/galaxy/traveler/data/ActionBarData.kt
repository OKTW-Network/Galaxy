package one.oktw.galaxy.traveler.data

import org.spongepowered.api.text.Text

data class ActionBarData(
    val text: Text,
    val priority: Int = 0,
    var time: Int = 0
)