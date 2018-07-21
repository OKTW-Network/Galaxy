package one.oktw.galaxy.command

import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.languageService
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.action.TextActions
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextStyles
import java.util.*
import java.util.concurrent.TimeUnit

class UnStuck : CommandBase {
    private val callbackLimit: ArrayList<UUID> = ArrayList()

    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .executor(this)
            .description(Text.of("Unstuck"))
            .permission("oktw.command.unstuck")
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src is Player) {
            val lang = languageService.getDefaultLanguage()// Todo get player lang
            val random = UUID.randomUUID()
            val retryButton = Text.of(
                TextColors.AQUA,
                TextStyles.UNDERLINE,
                TextActions.executeCallback {
                    if (random in callbackLimit) {
                        callbackLimit.remove(random)
                        if (src.setLocationSafely(src.location.add(0.0, 2.0, 0.0))) {
                            src.sendMessage(Text.of(TextColors.GREEN, lang["command.Unstuck.success"]))
                        } else {
                            src.sendMessage(Text.of(TextColors.RED, lang["command.Unstuck.failed"]))
                        }
                    }
                },
                lang["command.Unstuck.get_higher"]
            )

            if (src.setLocationSafely(src.location)) {

                if (src.world == Sponge.getServer().run { getWorld(defaultWorldName).get() }) {
                    src.sendMessage(Text.of(TextColors.GREEN, lang["command.Unstuck.success"]))
                } else {
                    callbackLimit.add(random)
                    src.sendMessage(
                        Text.of(
                            TextColors.GREEN, "${lang["command.Unstuck.success"]}\n",
                            TextColors.GOLD, lang["command.Unstuck.does_not_unstuck"], retryButton
                        )
                    )
                    launch {
                        delay(5, TimeUnit.MINUTES)
                        if (random in callbackLimit) {
                            callbackLimit.remove(random)
                        }
                    }
                }

                return CommandResult.affectedEntities(1)
            } else if (src.setLocationSafely(src.location.add(0.0, 2.0, 0.0))) {
                if (src.world == Sponge.getServer().run { getWorld(defaultWorldName).get() }) {
                    src.sendMessage(Text.of(TextColors.GREEN, lang["command.Unstuck.success"]))
                } else {
                    callbackLimit.add(random)
                    src.sendMessage(
                        Text.of(
                            TextColors.GREEN, "${lang["command.Unstuck.success"]}\n",
                            TextColors.GOLD, lang["command.Unstuck.does_not_unstuck"], retryButton
                        )
                    )
                    launch {
                        delay(5, TimeUnit.MINUTES)
                        if (random in callbackLimit) {
                            callbackLimit.remove(random)
                        }
                    }
                }

                return CommandResult.affectedEntities(1)
            }

            src.sendMessage(Text.of(TextColors.RED, lang["command.Unstuck.failed"]))
        }

        return CommandResult.empty()
    }
}
