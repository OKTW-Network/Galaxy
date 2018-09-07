package one.oktw.galaxy.translation

import one.oktw.galaxy.Main
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text

class Util {
    companion object {
        fun Text.toLegacyText(player: Player): Text{
            return Text.of(Main.translationService.toLegacy(player, this))
        }
    }
}
