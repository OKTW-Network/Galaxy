package one.oktw.galaxy.command.admin

import kotlinx.coroutines.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.galaxy.data.extensions.getMember
import one.oktw.galaxy.galaxy.data.extensions.saveMember
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors.GREEN
import org.spongepowered.api.text.format.TextColors.RED

class SetPlayerStarDust : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
        .permission("oktw.command.admin.setPlayerStarDust")
        .arguments(
            GenericArguments.playerOrSource(Text.of("player")),
            GenericArguments.longNum(Text.of("starDust"))
        )
        .executor(this)
        .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val player = args.getOne<Player>("player").get()
        val starDust = args.getOne<Long>("starDust").get()

        launch {
            val galaxy = galaxyManager.get(player.world)
            if (galaxy == null) {
                src.sendMessage(Text.of(RED, "Not enough argument: galaxy not found or missing."))
                return@launch
            }
            val traveler = galaxy.getMember(player.uniqueId)
            if (traveler == null) {
                src.sendMessage(Text.of(RED, "Error: ${player.name} is not a member of the galaxy."))
                return@launch
            }
            val success = traveler.changeStarDust(starDust)
            if (success) {
                src.sendMessage(Text.of(GREEN, "Set ${player.name}'s StarDust(s) to ${traveler.starDust}"))
                galaxy.run {
                    getMember(player.uniqueId)?.also {
                        saveMember(traveler)
                    }
                }
            } else {
                src.sendMessage(Text.of(RED, "Failed to set ${player.name}'s StarDust(s) (Current: ${traveler.starDust})"))
            }
        }

        return CommandResult.success()
    }
}
