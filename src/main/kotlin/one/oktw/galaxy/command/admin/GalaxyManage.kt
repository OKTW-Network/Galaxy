package one.oktw.galaxy.command.admin

import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.Main.Companion.serverThread
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.command.CommandHelper
import one.oktw.galaxy.galaxy.data.extensions.*
import one.oktw.galaxy.galaxy.enums.Group
import one.oktw.galaxy.galaxy.enums.Group.OWNER
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import one.oktw.galaxy.galaxy.planet.enums.PlanetType
import one.oktw.galaxy.galaxy.planet.enums.PlanetType.NORMAL
import one.oktw.galaxy.util.Chat.Companion.confirm
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

class GalaxyManage : CommandBase {
    override val spec: CommandSpec
        get() = CommandSpec.builder()
            .permission("oktw.command.admin.galaxyManage")
            .child(CreateGalaxy().spec, "createGalaxy")
            .child(CreatePlanet().spec, "createPlanet")
            .child(AddMember().spec, "addMember")
            .child(SetGroup().spec, "setGroup")
            .child(RemoveMember().spec, "removeMember")
            .child(Rename().spec, "rename")
            .child(Info().spec, "info")
            .child(Notice().spec, "notice")
            .child(SetSize().spec, "setSize")
            .child(SetVisit().spec, "setVisit")
            .child(RemoveGalaxy().spec, "removeGalaxy")
            .child(RemovePlanet().spec, "removePlanet")
            .child(Dividends().spec, "dividends")
            .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        src.sendMessage(Sponge.getCommandManager().getUsage(src))

        return CommandResult.success()
    }

    class CreateGalaxy : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.admin.galaxyManage.createGalaxy")
                .arguments(
                    GenericArguments.string(Text.of("name")),
                    GenericArguments.playerOrSource(Text.of("player"))
                )
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            val player = args.getOne<Player>("player").get()
            launch {
                val galaxy = galaxyManager.createGalaxy(args.getOne<String>("name").get(), player)
                src.sendMessage(Text.of(TextColors.GREEN, galaxy.uuid))
            }
            return CommandResult.success()
        }
    }

    class CreatePlanet : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.admin.galaxyManage.createPlanet")
                .arguments(
                    GenericArguments.optional(GenericArguments.uuid(Text.of("galaxy"))),
                    GenericArguments.string(Text.of("name")),
                    GenericArguments.optional(GenericArguments.enumValue(Text.of("Type"), PlanetType::class.java), NORMAL)
                )
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            val uuid = args.getOne<UUID>("galaxy").orElse(null)
            launch {
                try {
                    val galaxy = CommandHelper.getGalaxy(uuid, src)
                    val planet = galaxy.createPlanet(args.getOne<String>("name").get(), args.getOne<PlanetType>("Type").get())
                    src.sendMessage(Text.of(TextColors.GREEN, planet.uuid))
                } catch (e: IllegalArgumentException) {
                    src.sendMessage(Text.of(TextColors.RED, "Error：", e.message))
                    if (e.message == "Not enough arguments!") {
                        src.sendMessage(Text.of(TextColors.RED, Sponge.getCommandManager().getUsage(src)))
                    }
                } catch (e: NotImplementedError) {
                    src.sendMessage(Text.of(TextColors.RED, "Error：", e.message))
                }
            }
            return CommandResult.success()
        }
    }

    class AddMember : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.admin.galaxyManage.addMember")
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
                //Fetch Galaxy
                launch {
                    val galaxy = CommandHelper.getGalaxy(uuid, src)
                    galaxy.addMember(player.uniqueId)
                    src.sendMessage(Text.of(TextColors.GREEN, "Member added！"))
                }
            } catch (e: RuntimeException) {
                src.sendMessage(Text.of(TextColors.RED, "Illegal arguments!\n", Sponge.getCommandManager().getUsage(src)))
            } catch (e: IllegalArgumentException) {
                src.sendMessage(Text.of(TextColors.RED, e.message))
                if (e.message == "Not enough arguments!") {
                    src.sendMessage(Text.of(TextColors.RED, Sponge.getCommandManager().getUsage(src)))
                }
            }
            return CommandResult.success()
        }
    }

    class SetGroup : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.admin.galaxyManage.SetGroup")
                .arguments(
                    GenericArguments.optional(GenericArguments.uuid(Text.of("galaxy"))),
                    GenericArguments.firstParsing(
                        GenericArguments.player(Text.of("player")),
                        GenericArguments.string(Text.of("offlinePlayer"))
                    ),
                    GenericArguments.enumValue(Text.of("Group"), Group::class.java)
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
                    galaxy.setGroup(player.uniqueId, args.getOne<Group>("Group").get())
                    src.sendMessage(Text.of(TextColors.GREEN, "Group set！"))
                }
            } catch (e: IllegalArgumentException) {
                src.sendMessage(Text.of(TextColors.RED, e.message))
                if (e.message == "Not enough arguments!") {
                    src.sendMessage(Text.of(TextColors.RED, Sponge.getCommandManager().getUsage(src)))
                }
            }
            return CommandResult.success()
        }
    }

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
                    if (galaxy.getMember(player.uniqueId)?.group == OWNER) {
                        src.sendMessage(Text.of(TextColors.RED, "You are deleting an owner"))
                        return@launch
                    }
                    galaxy.delMember(player.uniqueId)
                    src.sendMessage(Text.of(TextColors.GREEN, "Member removed！"))
                }
            } catch (e: IllegalArgumentException) {
                src.sendMessage(Text.of(TextColors.RED, e.message))
                if (e.message == "Not enough arguments!") {
                    src.sendMessage(Text.of(TextColors.RED, Sponge.getCommandManager().getUsage(src)))
                }
            }
            return CommandResult.success()
        }
    }

    class Rename : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.admin.galaxyManage.rename")
                .arguments(
                    GenericArguments.firstParsing(
                        GenericArguments.uuid(Text.of("galaxy")),
                        GenericArguments.string(Text.of("name"))
                    ),
                    GenericArguments.optional(GenericArguments.string(Text.of("name")))
                )
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            val uuid = args.getOne<UUID>("galaxy").orElse(null)
            try {
                launch {
                    val galaxy = CommandHelper.getGalaxy(uuid, src)
                    galaxy.update { name = args.getOne<String>("name").get() }
                    src.sendMessage(Text.of(TextColors.GREEN, "Galaxy Renamed to ", galaxy.name, "!"))
                }
            } catch (e: IllegalArgumentException) {
                src.sendMessage(Text.of(TextColors.RED, e.message))
                if (e.message == "Not enough arguments!") {
                    src.sendMessage(Text.of(TextColors.RED, Sponge.getCommandManager().getUsage(src)))
                }
            }
            return CommandResult.success()
        }
    }

    class Info : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.admin.galaxyManage.info")
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
                    galaxy.update { info = args.getOne<String>("text").get() }
                    src.sendMessage(Text.of(TextColors.GREEN, "Info set to ", galaxy.info, "!"))
                }

            } catch (e: IllegalArgumentException) {
                src.sendMessage(Text.of(TextColors.RED, e.message))
                if (e.message == "Not enough arguments!") {
                    src.sendMessage(Text.of(TextColors.RED, Sponge.getCommandManager().getUsage(src)))
                }
            }
            return CommandResult.success()
        }
    }

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
                    galaxy.update { notice = args.getOne<String>("text").get() }
                    src.sendMessage(Text.of(TextColors.GREEN, "Notice set to ", galaxy.notice, "!"))
                }

            } catch (e: IllegalArgumentException) {
                src.sendMessage(Text.of(TextColors.RED, e.message))
                if (e.message == "Not enough arguments!") {
                    src.sendMessage(Text.of(TextColors.RED, Sponge.getCommandManager().getUsage(src)))
                }
            }
            return CommandResult.success()
        }
    }

    class SetSize : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.admin.galaxyManage.setSize")
                .arguments(
                    GenericArguments.firstParsing(
                        GenericArguments.uuid(Text.of("planet")),
                        GenericArguments.integer(Text.of("size"))
                    ),
                    GenericArguments.optional(GenericArguments.integer(Text.of("size")))
                )
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            val maxChunkSize = 375000
            val minChunkSize = 0
            val size = args.getOne<Int>("size").get()
            if (size > maxChunkSize || size < minChunkSize) {
                src.sendMessage(Text.of(TextColors.RED, "Size must be between $minChunkSize and $maxChunkSize"))
                return CommandResult.empty()
            }
            val uuid = args.getOne<UUID>("planet").orElse(null)
            try {
                launch {
                    val planet = CommandHelper.getGalaxyAndPlanet(uuid, src).planet
                    planet.size = size
                    PlanetHelper.updatePlanet(planet)
                    src.sendMessage(Text.of(TextColors.GREEN, "Size set to ", planet.size, "!"))
                }
            } catch (e: IllegalArgumentException) {
                src.sendMessage(Text.of(TextColors.RED, e.message))
                if (e.message == "Not enough arguments!") {
                    src.sendMessage(Text.of(TextColors.RED, Sponge.getCommandManager().getUsage(src)))
                }
            }
            return CommandResult.success()
        }
    }

    class SetVisit : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.admin.galaxyManage.setVisit")
                .arguments(
                    GenericArguments.firstParsing(
                        GenericArguments.uuid(Text.of("planet")),
                        GenericArguments.bool(Text.of("visitable"))
                    ),
                    GenericArguments.optional(GenericArguments.bool(Text.of("visitable")))
                )
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            try {
                launch {
                    val (galaxy, _, uuid) = CommandHelper.getGalaxyAndPlanet(
                        args.getOne<UUID>("planet").orElse(null), src
                    )
                    galaxy.update {
                        this.getPlanet(uuid)!!.let {
                            it.visitable = args.getOne<Boolean>("visitable").get()
                            src.sendMessage(Text.of(TextColors.GREEN, "Visibility set to ", it.visitable, "!"))
                        }
                    }
                }
            } catch (e: IllegalArgumentException) {
                src.sendMessage(Text.of(TextColors.RED, e.message))
                if (e.message == "Not enough arguments!") {
                    src.sendMessage(Text.of(TextColors.RED, Sponge.getCommandManager().getUsage(src)))
                }
            }
            return CommandResult.success()
        }
    }

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
                    if (confirm(src, Text.of(TextColors.AQUA, "Are you sure you want to remove the galaxy ${galaxy.name}?")) == true) {
                        galaxyManager.deleteGalaxy(uuid)
                        src.sendMessage(Text.of(TextColors.GREEN, "Galaxy deleted!"))
                    }
                }
            } catch (e: IllegalArgumentException) {
                src.sendMessage(Text.of(TextColors.RED, e.message))
                if (e.message == "Not enough arguments!") {
                    src.sendMessage(Text.of(TextColors.RED, Sponge.getCommandManager().getUsage(src)))
                }
            }
            return CommandResult.success()
        }
    }

    class RemovePlanet : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.admin.galaxyManage.removePlanet")
                .arguments(
                    GenericArguments.optional(GenericArguments.uuid(Text.of("planet")))
                )
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            if (src !is Player) return CommandResult.empty()
            try {
                launch {
                    val (galaxy, planet, uuid) = CommandHelper.getGalaxyAndPlanet(
                        args.getOne<UUID>("planet").orElse(null), src
                    )

                    if (confirm(src, Text.of(TextColors.AQUA, "Are you sure you want to remove the planet ${planet.name}?")) == true) {
                        withContext(serverThread) { galaxy.removePlanet(uuid) }
                        src.sendMessage(Text.of(TextColors.GREEN, "Planet on ${galaxy.name} (${galaxy.uuid}) deleted!"))
                    }
                }

            } catch (e: IllegalArgumentException) {
                src.sendMessage(Text.of(TextColors.RED, e.message))
                if (e.message == "Not enough arguments!") {
                    src.sendMessage(Text.of(TextColors.RED, Sponge.getCommandManager().getUsage(src)))
                }
            }
            return CommandResult.success()
        }
    }

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
                        src.sendMessage(Text.of(TextColors.GREEN, "Dividend successful!"))
                    } else {
                        src.sendMessage(Text.of(TextColors.RED, "Dividend failed!"))
                    }
                }
            } catch (e: IllegalArgumentException) {
                src.sendMessage(Text.of(TextColors.RED, e.message))
                if (e.message == "Not enough arguments!") {
                    src.sendMessage(Text.of(TextColors.RED, Sponge.getCommandManager().getUsage(src)))
                }
            }
            return CommandResult.success()
        }
    }
}
