package one.oktw.galaxy.command

import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text

class TPX : CommandBase {

    override val spec: CommandSpec
        get() = CommandSpec.builder()
                .executor(this)
                .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("Player"))))
                .permission("oktw.command.teleport.x")
                .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src is Player) {
            if (args.getOne<Player>("Player").isPresent) {
                src.setLocation(args.getOne<Player>("Player").get().location)
                return CommandResult.success()
            } else {
                return CommandResult.empty()
            }
        }

        return CommandResult.empty()
    }

}
