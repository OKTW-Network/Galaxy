package one.oktw.galaxy.command.admin.galaxyManage

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.extensions.serialize
import one.oktw.galaxy.galaxy.data.extensions.update
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors.*
import org.spongepowered.api.text.serializer.TextSerializers.FORMATTING_CODE
import java.util.*

class Info : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .executor(this)
            .permission("oktw.command.admin.galaxyManage.info")
            .arguments(
                GenericArguments.optionalWeak(GenericArguments.uuid(Text.of("galaxy"))),
                GenericArguments.text(Text.of("text"), FORMATTING_CODE, true)
            )
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        val text = args.getOne<Text>("text").get()
        var galaxyUUID: UUID? = args.getOne<UUID>("galaxy").orElse(null)
        launch {
            val galaxy = galaxyUUID?.let { galaxyManager.get(it) } ?: (src as? Player)?.world?.let { galaxyManager.get(it) }

            if (galaxyUUID == null) {
                galaxyUUID = galaxy?.uuid
            }

            if (galaxyUUID != null) {
                galaxy!!.update { info = text.serialize() }
                src.sendMessage(Text.of(GREEN, "Info of ${galaxy.name} was set to:\n", WHITE, text))

            } else {
                src.sendMessage(Text.of(RED, "Not enough argument: galaxy not found or missing."))
            }
        }
        return CommandResult.success()
    }
}
