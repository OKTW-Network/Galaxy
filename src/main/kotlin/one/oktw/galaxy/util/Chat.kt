package one.oktw.galaxy.util

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.languageService
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.gui.GUIHelper
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.EventListener
import org.spongepowered.api.event.message.MessageChannelEvent
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions.executeCallback
import org.spongepowered.api.text.action.TextActions.showText
import org.spongepowered.api.text.format.TextColors.GREEN
import org.spongepowered.api.text.format.TextColors.RED
import org.spongepowered.api.text.format.TextStyles
import org.spongepowered.api.text.format.TextStyles.UNDERLINE
import org.spongepowered.api.text.serializer.TextSerializers.FORMATTING_CODE
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit.MINUTES
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.suspendCoroutine

class Chat {
    companion object {
        private val inputStatus = ConcurrentHashMap<Player, Pair<EventListener<MessageChannelEvent.Chat>, Continuation<Text?>>>()

        suspend fun input(player: Player, message: Text) = suspendCoroutine<Text?> { continuation ->
            val eventManager = Sponge.getEventManager()
            lateinit var listener: EventListener<MessageChannelEvent.Chat>

            GUIHelper.closeAll(player)

            listener = EventListener {
                if (it.source != player) return@EventListener

                it.isCancelled = true
                eventManager.unregisterListeners(listener)
                inputStatus -= player

                continuation.resume(FORMATTING_CODE.deserialize(it.rawMessage.toPlain()))
            }

            // unregister old listener and callback
            inputStatus[player]?.let {
                eventManager.unregisterListeners(it.first)
                it.second.resume(null)
            }

            // send message to player
            player.sendMessage(message)

            // register new listener anc callback
            eventManager.registerListener(main, MessageChannelEvent.Chat::class.java, listener)
            inputStatus[player] = Pair(listener, continuation)

            // Timeout 5 min
            launch {
                delay(5, MINUTES)

                if (inputStatus[player]?.first === listener) {
                    eventManager.unregisterListeners(listener)
                    inputStatus -= player
                    continuation.resume(null)
                }
            }
        }

        suspend fun confirm(player: Player, message: Text) = suspendCoroutine<Boolean?> { continuation ->
            var lock = false
            val lang = languageService.getDefaultLanguage()
            val confirm = executeCallback {
                if (lock) return@executeCallback else lock = true

                continuation.resume(true)
            }
            val cancel = executeCallback {
                if (lock) return@executeCallback else lock = true

                continuation.resume(false)
            }

            // send message to player
            player.sendMessage(message)
            player.sendMessage(
                Text.of(
                    GREEN, UNDERLINE, showText(Text.of(GREEN, lang["Respond.Confirm"])), confirm, lang["Respond.Confirm"],
                    TextStyles.RESET, " ",
                    RED, UNDERLINE, showText(Text.of(RED, lang["Respond.Cancel"])), cancel, lang["Respond.Cancel"]
                )
            )

            // Timeout 5 min
            launch {
                delay(5, MINUTES)

                if (!lock) {
                    lock = true
                    continuation.resume(null)
                }
            }
        }
    }
}
