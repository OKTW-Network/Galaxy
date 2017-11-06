package one.oktw.galaxy.command

import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec

interface CommandBase : CommandExecutor {
    val spec: CommandSpec
}
