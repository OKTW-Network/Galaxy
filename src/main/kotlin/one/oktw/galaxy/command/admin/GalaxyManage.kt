package one.oktw.galaxy.command.admin

import kotlinx.coroutines.experimental.launch
import one.oktw.galaxy.Main.Companion.galaxyManager
import one.oktw.galaxy.command.CommandBase
import one.oktw.galaxy.galaxy.data.extensions.*
import one.oktw.galaxy.galaxy.enums.Group
import one.oktw.galaxy.galaxy.enums.Group.OWNER
import one.oktw.galaxy.galaxy.planet.PlanetHelper
import one.oktw.galaxy.galaxy.planet.enums.PlanetType
import one.oktw.galaxy.galaxy.planet.enums.PlanetType.NORMAL
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
            .child(RemovePlanet().spec, "setPlanet")
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
                    GenericArguments.string(Text.of("name")),
                    GenericArguments.optional(
                        GenericArguments.enumValue(Text.of("Type"), PlanetType::class.java),
                        NORMAL
                    ),
                    GenericArguments.optional(GenericArguments.uuid(Text.of("galaxy")))
                )
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
                try {
                    val planet = galaxy.createPlanet(args.getOne<String>("name").get(), args.getOne<PlanetType>("Type").get())
                    src.sendMessage(Text.of(TextColors.GREEN, planet.uuid))
                } catch (e: IllegalArgumentException) {
                    src.sendMessage(Text.of(TextColors.RED, "Error：", e.message))
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
                    GenericArguments.playerOrSource(Text.of("player")),
                    GenericArguments.optional(GenericArguments.uuid(Text.of("galaxy")))
                )
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            val player = args.getOne<Player>("player").get()
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
                galaxy.addMember(player.uniqueId)
                src.sendMessage(Text.of(TextColors.GREEN, "Member added！"))
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
                    GenericArguments.playerOrSource(Text.of("player")),
                    GenericArguments.enumValue(Text.of("Group"), Group::class.java),
                    GenericArguments.optional(GenericArguments.uuid(Text.of("galaxy")))
                )
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            val player = args.getOne<Player>("player").get()
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
                galaxy.setGroup(player.uniqueId, args.getOne<Group>("Group").get())
                src.sendMessage(Text.of(TextColors.GREEN, "Group set！"))
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
                    GenericArguments.playerOrSource(Text.of("player")),
                    GenericArguments.optional(GenericArguments.uuid(Text.of("galaxy")))
                )
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            val player = args.getOne<Player>("player").get()
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
                if (galaxy.getMember(player.uniqueId)?.group == OWNER) {
                    src.sendMessage(Text.of(TextColors.RED, "You are deleting an owner"))
                    return@launch
                }
                galaxy.delMember(player.uniqueId)
                src.sendMessage(Text.of(TextColors.GREEN, "Member removed！"))
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
                    GenericArguments.string(Text.of("name")),
                    GenericArguments.optional(GenericArguments.uuid(Text.of("galaxy")))
                )
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
                galaxy.update { name = args.getOne<String>("name").get() }
                src.sendMessage(Text.of(TextColors.GREEN, "Galaxy Renamed to ", galaxy.name, "!"))
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
                    GenericArguments.string(Text.of("text")),
                    GenericArguments.optional(GenericArguments.uuid(Text.of("galaxy")))
                )
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
                galaxy.update { info = args.getOne<String>("text").get() }
                src.sendMessage(Text.of(TextColors.GREEN, "Info set to ", galaxy.info, "!"))
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
                    GenericArguments.string(Text.of("text")),
                    GenericArguments.optional(GenericArguments.uuid(Text.of("galaxy")))
                )
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
                galaxy.update { notice = args.getOne<String>("text").get() }
                src.sendMessage(Text.of(TextColors.GREEN, "Notice set to ", galaxy.notice, "!"))
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
                    GenericArguments.integer(Text.of("size")),
                    GenericArguments.optional(GenericArguments.uuid(Text.of("planet")))
                )
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            val maxChunkSize = 375000
            val minChunkSize = 0
            if (args.getOne<Int>("size").get() > maxChunkSize || args.getOne<Int>("size").get() < minChunkSize) {
                src.sendMessage(Text.of(TextColors.RED, "Size must be between $minChunkSize and $maxChunkSize"))
                return CommandResult.empty()
            }
            val uuid = args.getOne<UUID>("planet").orElse(null)
            launch {
                var planet = galaxyManager.get(planet = uuid)?.getPlanet(uuid)
                //If planet(uuid) is null then get player planet
                if (planet == null && src is Player) planet = galaxyManager.get(src.world)?.getPlanet(src.world)
                //If it is still null then return
                if (planet == null) {
                    src.sendMessage(
                        Text.of(
                            TextColors.RED,
                            "Not enough arguments!\n",
                            Sponge.getCommandManager().getUsage(src)
                        )
                    )
                    return@launch
                }
                planet.size = args.getOne<Int>("size").get()
                PlanetHelper.updatePlanet(planet)
                src.sendMessage(Text.of(TextColors.GREEN, "Size set to ", planet.size, "!"))
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
                    GenericArguments.bool(Text.of("visitable")),
                    GenericArguments.optional(GenericArguments.uuid(Text.of("planet")))
                )
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            val uuid = args.getOne<UUID>("planet").orElse(null)
            launch {
                var planet = galaxyManager.get(planet = uuid)?.getPlanet(uuid)
                //If planet(uuid) is null then get player planet
                if (planet == null && src is Player) planet = galaxyManager.get(src.world)?.getPlanet(src.world)
                //If it is still null then return
                if (planet == null) {
                    src.sendMessage(
                        Text.of(
                            TextColors.RED,
                            "Not enough arguments!\n",
                            Sponge.getCommandManager().getUsage(src)
                        )
                    )
                    return@launch
                }
                planet.visitable = args.getOne<Boolean>("visitable").get()
                PlanetHelper.updatePlanet(planet)
                src.sendMessage(Text.of(TextColors.GREEN, "Visibility set to ", planet.visitable, "!"))
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
            var uuid = args.getOne<UUID>("galaxy").orElse(null)
            launch {
                //If uuid is null then get player galaxy uuid
                if (uuid == null && src is Player) uuid = galaxyManager.get(src.world)?.uuid
                //If it is still null then return
                if (uuid == null) {
                    src.sendMessage(
                        Text.of(
                            TextColors.RED,
                            "Not enough arguments!\n",
                            Sponge.getCommandManager().getUsage(src)
                        )
                    )
                    return@launch
                }
                galaxyManager.deleteGalaxy(uuid)
                src.sendMessage(Text.of(TextColors.GREEN, "Galaxy deleted!"))
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
            var uuid = args.getOne<UUID>("planet").orElse(null)
            launch {
                //If uuid is null then get player planet uuid
                if (uuid == null && src is Player) uuid = galaxyManager.get(src.world)?.getPlanet(src.world)?.uuid
                //If it is still null then return
                if (uuid == null) {
                    src.sendMessage(
                        Text.of(
                            TextColors.RED,
                            "Not enough arguments!\n",
                            Sponge.getCommandManager().getUsage(src)
                        )
                    )
                    return@launch
                }
                //fetch galaxy from planet
                val galaxy = galaxyManager.get(planet = uuid)
                if (galaxy == null) {
                    src.sendMessage(
                        Text.of(
                            TextColors.RED,
                            "Galaxy not found!"
                        )
                    )
                    return@launch
                }
                galaxy.removePlanet(uuid)
                src.sendMessage(Text.of(TextColors.GREEN, "Planet deleted!"))
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
                    GenericArguments.longNum(Text.of("number")),
                    GenericArguments.optional(GenericArguments.uuid(Text.of("galaxy")))
                )
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
                galaxy.dividends(args.getOne<Long>("number").get())
                src.sendMessage(Text.of(TextColors.GREEN, "Notice set to ", galaxy.notice, "!"))
            }
            return CommandResult.success()
        }
    }
}
