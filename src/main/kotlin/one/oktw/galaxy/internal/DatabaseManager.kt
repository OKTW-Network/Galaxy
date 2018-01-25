package one.oktw.galaxy.internal

import com.mongodb.MongoClient
import com.mongodb.MongoClientOptions
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoDatabase
import one.oktw.galaxy.Main.Companion.configManager
import one.oktw.galaxy.Main.Companion.main
import one.oktw.galaxy.internal.types.Galaxy
import one.oktw.galaxy.internal.types.Member
import one.oktw.galaxy.internal.types.Planet
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.pojo.PojoCodecProvider

class DatabaseManager {
    val database: MongoDatabase

    init {
        val config = configManager.configNode.getNode("database")

        main.logger.info("Loading Database...")

        // Init Config
        if (config.isVirtual) {
            config.setComment("Mongodb connect setting")
            config.getNode("host").value = "localhost"
            config.getNode("port").value = 27017
            config.getNode("name").value = "oktw"
            config.getNode("name").setComment("Database name")
            config.getNode("Username").value = ""
            config.getNode("Password").value = ""
            configManager.save()
        }

        // Init Database connect
        val serverAddress = ServerAddress(
                config.getNode("host").string,
                config.getNode("port").int
        )

        val pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().register(
                        Galaxy::class.java,
                        Planet::class.java,
                        Member::class.java
                ).build()))

        database = if (config.getNode("Username").string.isEmpty()) {
            MongoClient(serverAddress)
                    .getDatabase(config.getNode("name").string)
                    .withCodecRegistry(pojoCodecRegistry)
        } else {
            val credential = MongoCredential.createCredential(
                    config.getNode("Username").string,
                    config.getNode("name").string,
                    config.getNode("Password").string.toCharArray()
            )

            MongoClient(serverAddress, credential, MongoClientOptions.builder().build())
                    .getDatabase(config.getNode("name").string)
                    .withCodecRegistry(pojoCodecRegistry)
        }
    }
}
