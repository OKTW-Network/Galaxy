package one.oktw.galaxy.command.admin

import kotlinx.coroutines.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.command.CommandBase
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.service.pagination.PaginationList
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors.RED
import java.util.*

class GalaxyInfo : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
        .permission("oktw.command.admin.galaxyInfo")
        .arguments(GenericArguments.optional(GenericArguments.uuid(Text.of("galaxy"))))
        .executor(this)
        .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        var galaxyUUID: UUID? = args.getOne<UUID>("galaxy").orElse(null)
        launch {
            val galaxy = galaxyUUID?.let { galaxyManager.get(it) } ?: (src as? Player)?.world?.let { galaxyManager.get(it) }

            if (galaxyUUID == null) {
                galaxyUUID = galaxy?.uuid
            }

            if (galaxyUUID != null) {
                PaginationList.builder()
                    .contents(
                        Text.of("Name: ", galaxy!!.name),
                        Text.of("UUID: ", galaxy.uuid),
                        Text.of("Info: ", galaxy.info),
                        Text.of("Notice: ", galaxy.notice),
                        Text.of("Star dust: ", galaxy.starDust),
                        Text.of("Planet: ", galaxy.planets),
                        Text.of("Member: ", galaxy.members)
                    )
                    .title(Text.of("Galaxy Info"))
                    .sendTo(src)
            } else {
                src.sendMessage(Text.of(RED, "Not enough argument: galaxy not found or missing."))
            }
        }
        return CommandResult.success()
    }
}
