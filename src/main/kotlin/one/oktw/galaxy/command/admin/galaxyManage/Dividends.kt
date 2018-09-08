package one.oktw.galaxy.command.admin.galaxyManage

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.command.CommandHelper
import one.oktw.galaxy.galaxy.data.extensions.dividends
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.*

class Dividends : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .executor(this)
            .permission("oktw.command.admin.galaxyManage.dividends")
            .arguments(
                GenericArguments.firstParsing(
                    GenericArguments.uuid(Text.of("galaxy")),
                    GenericArguments.longNum(Text.of("number"))
                ),
                GenericArguments.optional(GenericArguments.longNum(Text.of("number")))
            )
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val uuid = args.getOne<UUID>("galaxy").orElse(null)
        try {
            launch {
                val galaxy = CommandHelper.getGalaxy(uuid, src)
                if (galaxy.dividends(args.getOne<Long>("number").get())) {
                    src.sendMessage(Text.of(TextColors.GREEN, "Dividend to ${galaxy.name} successful!"))
                } else {
                    src.sendMessage(Text.of(TextColors.RED, "Dividend to ${galaxy.name} failed!"))
                }
            }
        } catch (e: IllegalArgumentException) {
            src.sendMessage(Text.of(TextColors.RED, "Error: ", e.message))
            if (e.message == "Not enough arguments!") {
                src.sendMessage(Text.of(TextColors.RED, spec.getUsage(src)))
            }
        }
        return CommandResult.success()
    }
}
