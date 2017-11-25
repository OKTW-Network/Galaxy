package one.oktw.galaxy.internal

import com.mongodb.MongoClient
import com.mongodb.MongoCredential
import com.mongodb.ServerAddress
import com.mongodb.client.MongoDatabase
import one.oktw.galaxy.Main.Companion.configManager
import one.oktw.galaxy.Main.Companion.main

class DatabaseManager {
    val database: MongoDatabase

    init {
        val config = configManager.configNode.getNode("database")

        main.logger.info("Loading Database...")

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

        if (config.getNode("Username").string.isEmpty()) {
            database = MongoClient(config.getNode("host").string, config.getNode("port").int).getDatabase(config.getNode("name").string)
        } else {
            val credential = MongoCredential.createCredential(
                    config.getNode("Username").string,
                    config.getNode("name").string,
                    config.getNode("Password").string.toCharArray()
            )
            database = MongoClient(
                    ServerAddress(config.getNode("host").string, config.getNode("port").int),
                    listOf(credential)
            ).getDatabase(config.getNode("name").string)
        }
    }
}
