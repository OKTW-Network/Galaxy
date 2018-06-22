package one.oktw.galaxy.command.debug

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.player.event.Viewer.Companion.isViewer
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.service.pagination.PaginationList
import org.spongepowered.api.text.Text

class PlayerInfo : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
        .permission("oktw.command.debug.player")
        .arguments(GenericArguments.playerOrSource(Text.of("player")))
        .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val player = args.getOne<Player>("player").get()

        launch {
            PaginationList.builder()
                .contents(
                    Text.of("Name: ", player.name),
                    Text.of("UUID: ", player.uniqueId),
                    Text.of("Viewer mode: ", isViewer(player.uniqueId)),
                    Text.of("Planet: ", galaxyManager.get(player.world).await()?.getPlanet(player.world))
                )
                .title(Text.of("Player Info"))
                .sendTo(src)
        }

        return CommandResult.success()
    }
}