package one.oktw.galaxy.command.admin.galaxyManage

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.command.CommandHelper
import one.oktw.galaxy.util.Chat
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.*

class RemoveGalaxy : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .executor(this)
            .permission("oktw.command.admin.galaxyManage.removeGalaxy")
            .arguments(
                GenericArguments.optional(GenericArguments.uuid(Text.of("galaxy")))
            )
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src !is Player) return CommandResult.empty()
        val uuid = args.getOne<UUID>("galaxy").orElse(null)
        try {
            launch {
                val galaxy = CommandHelper.getGalaxy(uuid, src)
                if (Chat.confirm(src, Text.of(TextColors.AQUA, "Are you sure you want to remove the galaxy ${galaxy.name}?")) == true) {
                    Main.galaxyManager.deleteGalaxy(uuid)
                    src.sendMessage(Text.of(TextColors.GREEN, "Galaxy ${galaxy.name} deleted!"))
                }
            }
        } catch (e: IllegalArgumentException) {
            src.sendMessage(Text.of(TextColors.RED, "Error: ", e.message))
            if (e.message == "Not enough arguments!") {
                src.sendMessage(Text.of(TextColors.RED, Sponge.getCommandManager().getUsage(src)))
            }
        }
        return CommandResult.success()
    }
}
