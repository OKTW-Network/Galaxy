package one.oktw.galaxy.command

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import one.oktw.galaxy.Main.Companion.serverThread
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec

interface CommandBase : CommandExecutor, CoroutineScope {
    override val coroutineContext
        get() = Job() + serverThread
    val spec: CommandSpec
}
