package one.oktw.galaxy.command.admin.galaxyManage

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.command.CommandHelper
import one.oktw.galaxy.galaxy.data.extensions.update
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.*

class Notice : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .executor(this)
            .permission("oktw.command.admin.galaxyManage.notice")
            .arguments(
                GenericArguments.firstParsing(
                    GenericArguments.uuid(Text.of("galaxy")),
                    GenericArguments.string(Text.of("text"))
                ),
                GenericArguments.optional(GenericArguments.string(Text.of("text")))
            )
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val uuid = args.getOne<UUID>("galaxy").orElse(null)
        try {
            launch {
                val galaxy = CommandHelper.getGalaxy(uuid, src)
                galaxy.update {
                    notice = args.getOne<String>("text").get()
                    src.sendMessage(Text.of(TextColors.GREEN, "Notice of ${galaxy.name} was set to ${this.notice}!"))
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