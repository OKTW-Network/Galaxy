package one.oktw.galaxy.command.admin.galaxyManage

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.command.CommandHelper
import one.oktw.galaxy.galaxy.data.extensions.delMember
import one.oktw.galaxy.galaxy.data.extensions.getMember
import one.oktw.galaxy.galaxy.enums.Group
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

class RemoveMember : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .executor(this)
            .permission("oktw.command.admin.galaxyManage.removeMember")
            .arguments(
                GenericArguments.firstParsing(
                    GenericArguments.uuid(Text.of("galaxy")),
                    GenericArguments.player(Text.of("player")),
                    GenericArguments.string(Text.of("offlinePlayer"))
                ),
                GenericArguments.optional(
                    GenericArguments.firstParsing(
                        GenericArguments.player(Text.of("player")),
                        GenericArguments.string(Text.of("offlinePlayer"))
                    )
                )
            )
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val uuid = args.getOne<UUID>("galaxy").orElse(null)
        try {
            val player = CommandHelper.getPlayer(
                args.getOne<Player>("player").orElse(null),
                args.getOne<String>("offlinePlayer").orElse(null)
            )
            launch {
                val galaxy = CommandHelper.getGalaxy(uuid, src)
                if (galaxy.getMember(player.uniqueId)?.group == Group.OWNER) {
                    src.sendMessage(Text.of(TextColors.RED, "Error: You are removing an owner"))
                    return@launch
                }
                galaxy.delMember(player.uniqueId)
                src.sendMessage(Text.of(TextColors.GREEN, "${player.name} was removed from ${galaxy.name}!"))
            }
        } catch (e: RuntimeException) {
            src.sendMessage(Text.of(TextColors.RED, "Error: Illegal arguments!\n", Sponge.getCommandManager().getUsage(src)))
        } catch (e: IllegalArgumentException) {
            src.sendMessage(Text.of(TextColors.RED, "Error: ", e.message))
            if (e.message == "Not enough arguments!") {
                src.sendMessage(Text.of(TextColors.RED, Sponge.getCommandManager().getUsage(src)))
            }
        }
        return CommandResult.success()
    }
}
