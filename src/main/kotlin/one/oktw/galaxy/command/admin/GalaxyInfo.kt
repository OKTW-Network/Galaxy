package one.oktw.galaxy.command.admin

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.command.CommandBase
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.service.pagination.PaginationList
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
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
            var galaxy = galaxyManager.get(uuid)
            //If galaxy(uuid) is null then get player galaxy
            if (galaxy == null && src is Player) galaxy = galaxyManager.get(src.world)
            //If it is still null then return
            if (galaxy == null) {
                src.sendMessage(
                    Text.of(
                        TextColors.RED,
                        "Not enough arguments!\n",
                        Sponge.getCommandManager().getUsage(src)
                    )
                )
                return@launch
            }

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
