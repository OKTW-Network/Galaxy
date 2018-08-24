package one.oktw.galaxy.command.admin

import one.oktw.galaxy.book.BookUtil
import one.oktw.galaxy.book.enums.BooksInSpawn
import one.oktw.galaxy.command.CommandBase
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.data.type.HandTypes.MAIN_HAND
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors

class Book : CommandBase {
    override val spec: CommandSpec = CommandSpec.builder()
        .permission("oktw.command.admin.book")
        .child(SaveBook().spec, "saveBook")
        .child(GetBook().spec, "getBook")
        .executor(this)
        .build()

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        src.sendMessage(Sponge.getCommandManager().getUsage(src))

        return CommandResult.success()
    }

    class SaveBook : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.admin.book.saveBook")
                .arguments(
                    GenericArguments.firstParsing(
                        GenericArguments.enumValue(Text.of("Type"), BooksInSpawn::class.java),
                        GenericArguments.string(Text.of("key"))
                    )
                )
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            if (src !is Player) return CommandResult.empty()

            val bookUtil = BookUtil()
            val key = when {
                args.hasAny("Type") -> args.getOne<BooksInSpawn>("Type").get().key
                args.hasAny("key") -> args.getOne<String>("key").get()
                else -> ""
            }
            val item = src.getItemInHand(MAIN_HAND).get()
            try {
                bookUtil.writeBook(item, key)
                src.sendMessage(Text.of(TextColors.GREEN, "Book successfully saved as $key."))
            } catch (e: IllegalArgumentException) {
                src.sendMessage(Text.of(TextColors.RED, "Error： ", e.message))
            }
            return CommandResult.success()
        }
    }

    class GetBook : CommandBase {
        override val spec: CommandSpec
            get() = CommandSpec.builder()
                .executor(this)
                .permission("oktw.command.admin.book.getBook")
                .arguments(
                    GenericArguments.firstParsing(
                        GenericArguments.enumValue(Text.of("Type"), BooksInSpawn::class.java),
                        GenericArguments.string(Text.of("key"))
                    )
                )
                .build()

        override fun execute(src: CommandSource, args: CommandContext): CommandResult {
            if (src !is Player) return CommandResult.empty()

            val bookUtil = BookUtil()
            val key = when {
                args.hasAny("Type") -> args.getOne<BooksInSpawn>("Type").get().key
                args.hasAny("key") -> args.getOne<String>("key").get()
                else -> ""
            }
            val backBook = bookUtil.getBook(key)
            if (backBook == null) {
                src.sendMessage(Text.of(TextColors.RED, "Error： Book $key not found."))
                return CommandResult.success()
            }
            src.setItemInHand(MAIN_HAND, backBook)

            return CommandResult.success()
        }
    }
}
