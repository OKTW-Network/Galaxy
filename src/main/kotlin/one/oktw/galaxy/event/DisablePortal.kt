package one.oktw.galaxy.event

import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.entity.MoveEntityEvent
import org.spongepowered.api.event.filter.Getter
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors

class DisablePortal {
    private val server = Sponge.getServer()

    @Listener
    fun onMoveEntityEvent(portal: MoveEntityEvent.Teleport.Portal, @Getter("getTargetEntity") player: Player) {
        if (portal.fromTransform.extent.properties.worldName != server.defaultWorldName) {
            player.sendMessages(Text.of(TextColors.RED, "此路不通！ "))
            portal.toTransform = portal.fromTransform
        }
    }
}
