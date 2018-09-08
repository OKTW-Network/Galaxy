package one.oktw.galaxy.command.admin

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.command.CommandHelper
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.service.pagination.PaginationList
import org.spongepowered.api.text.Text
import java.util.*

class GalaxyInfo : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
        .permission("oktw.command.admin.galaxyInfo")
        .arguments(GenericArguments.optional(GenericArguments.uuid(Text.of("galaxy"))))
        .executor(this)
        .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val uuid = args.getOne<UUID>("galaxy").orElse(null)
        launch {
            val galaxy = CommandHelper.getGalaxy(uuid, src)

            PaginationList.builder()
                .contents(
                    Text.of("Name: ", galaxy.name),
                    Text.of("UUID: ", galaxy.uuid),
                    Text.of("Info: ", galaxy.info),
                    Text.of("Notice: ", galaxy.notice),
                    Text.of("Star dust: ", galaxy.starDust),
                    Text.of("Planet: ", galaxy.planets),
                    Text.of("Member: ", galaxy.members)
                )
                .title(Text.of("Galaxy Info"))
                .sendTo(src)
        }
        return CommandResult.success()
    }
}
