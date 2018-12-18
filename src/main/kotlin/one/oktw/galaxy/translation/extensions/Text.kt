package one.oktw.galaxy.translation.extensions

import one.oktw.galaxy.Main
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text

fun Text.toLegacyText(player: Player): Text {
    return Text.of(Main.translationService.toLegacy(player, this))
}
