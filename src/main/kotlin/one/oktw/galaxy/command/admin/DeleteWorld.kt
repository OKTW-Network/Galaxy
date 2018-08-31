package one.oktw.galaxy.command.admin

import kotlinx.coroutines.experimental.future.await
import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.galaxy.data.extensions.getPlanet
import one.oktw.galaxy.galaxy.data.extensions.removePlanet
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import one.oktw.galaxy.util.Chat.Companion.confirm
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.world.storage.WorldProperties

class DeleteWorld : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
        .permission("oktw.command.admin.deleteWorld")
        .arguments(GenericArguments.world(Text.of("World")))
        .executor(this)
        .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src !is Player) return CommandResult.empty()

        launch {
            val world = args.getOne<WorldProperties>("World").get()
            if (confirm(src, Text.of(TextColors.AQUA, "Are you sure you want to remove the world ${world.worldName}?")) == true) {
                val galaxy = galaxyManager.get(world)
                if (galaxy != null) {
                    //src.sendMessage(Text.of(TextColors.GREEN, "Planet found!Removing planet instead."))
                    val planet = galaxy.getPlanet(world)?.uuid
                    if (planet != null) {
                        galaxy.removePlanet(planet)
                        src.sendMessage(Text.of(TextColors.GREEN, "Planet on ${galaxy.name} (${galaxy.uuid}) deleted!"))
                        return@launch
                    }
                }
                if (PlanetHelper.removePlanet(world.uniqueId).await()) {
                    src.sendMessage(Text.of(TextColors.RED, "World deleted!"))
                } else {
                    src.sendMessage(Text.of(TextColors.RED, "Failed!"))
                }
            }
        }

        return CommandResult.success()
    }
}
