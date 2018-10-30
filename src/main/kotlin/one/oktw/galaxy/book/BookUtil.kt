package one.oktw.galaxy.book

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.loader.ConfigurationLoader
import one.oktw.galaxy.Main.Companion.main
import org.spongepowered.api.item.ItemTypes
import org.spongepowered.api.item.inventory.ItemStack
import java.nio.file.Paths

class BookUtil {
    companion object {
        private val book: ConfigurationLoader<CommentedConfigurationNode> = HoconConfigurationLoader.builder()
            .setPath(Paths.get(main.configDir.toString(), "books.cfg")).build()
        private val rootNode: ConfigurationNode = book.load()


        fun writeBook(item: ItemStack, key: String) {
            if (item.type == ItemTypes.WRITTEN_BOOK) {
                rootNode.getNode(key).setValue(TypeToken.of<ItemStack>(ItemStack::class.java), item)
                book.save(rootNode)
            } else {
                throw IllegalArgumentException("Item provided is not a written book.")
            }
        }

        fun getBook(key: String): ItemStack? {
            return rootNode.getNode(key).getValue(TypeToken.of<ItemStack>(ItemStack::class.java))
        }

    }
}
