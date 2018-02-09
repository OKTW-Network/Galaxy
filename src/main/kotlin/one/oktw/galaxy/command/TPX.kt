package one.oktw.galaxy.command

import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors

class TPX : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
                .executor(this)
                .child(TpxPlayer().spec, "player")
                .child(TpxWorld().spec, "world")
                .permission("oktw.command.tpx")
                .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        return CommandResult.empty()
    }

    class TpxPlayer : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                    .executor(this)
                    .arguments(GenericArguments.onlyOne(GenericArguments.player(Text.of("Player"))))
                    .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            if (src !is Player) return CommandResult.empty()

            args.getOne<Player>("Player").ifPresent {
                src.location = it.location
            }
            return CommandResult.success()
        }
    }
    class TpxWorld : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                    .executor(this)
                    .arguments(GenericArguments.onlyOne(GenericArguments.string((Text.of("world")))))
                    .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            if (src !is Player) return CommandResult.empty()
            val worldName = args.getOne<String>("world").get()
            if (!Sponge.getServer().getWorldProperties(worldName).isPresent) {
                src.sendMessage(Text.of(TextColors.RED,"世界 $worldName 不存在"))
                return CommandResult.empty()
            }
            val world = Sponge.getServer().getWorld(worldName)
            if (!world.isPresent) {
                src.sendMessage(Text.of(TextColors.RED,"世界 $worldName 尚未被載入"))
                return CommandResult.empty()
            }
            src.location = world.get().spawnLocation
            return CommandResult.success()
        }
    }
}