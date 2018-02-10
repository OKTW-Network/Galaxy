package one.oktw.galaxy.command

import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.world.storage.WorldProperties

class TPX : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
                .permission("oktw.command.tpx")
                .executor(this)
                .arguments(GenericArguments.firstParsing(
                        GenericArguments.world(Text.of("World")),
                        GenericArguments.player(Text.of("Player"))
                ))
                .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src !is Player) return CommandResult.empty()

        if (args.hasAny("World")) {
            Sponge.getServer()
                    .loadWorld(args.getOne<WorldProperties>("World").get())
                    .ifPresent { src.transferToWorld(it) }
        } else if (args.hasAny("Player")) {
            src.setLocationSafely(args.getOne<Player>("Player").get().location)
        }

        return CommandResult.success()
    }
}
