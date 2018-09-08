package one.oktw.galaxy.command.admin.galaxyManage

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main
import one.oktw.galaxy.command.CommandBase
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors

class CreateGalaxy : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .executor(this)
            .permission("oktw.command.admin.galaxyManage.createGalaxy")
            .arguments(
                GenericArguments.string(Text.of("name")),
                GenericArguments.playerOrSource(Text.of("player"))
            )
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val player = args.getOne<Player>("player").get()
        launch {
            val galaxy = Main.galaxyManager.createGalaxy(args.getOne<String>("name").get(), player)
            src.sendMessage(Text.of(TextColors.GREEN, "Galaxy ${galaxy.name} created: ${galaxy.uuid}"))
        }
        return CommandResult.success()
    }
}
