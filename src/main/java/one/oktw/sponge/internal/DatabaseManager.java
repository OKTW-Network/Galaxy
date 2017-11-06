package one.oktw.sponge.internal;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.slf4j.Logger;

import java.util.Collections;

import static one.oktw.sponge.Main.getMain;

public class DatabaseManager {
    private MongoDatabase database;

    public DatabaseManager() {
        ConfigManager configManager = getMain().getConfigManager();
        CommentedConfigurationNode config = configManager.getConfigNode().getNode("database");

        Logger logger = getMain().getLogger();
        logger.info("Loading Database...");

        if (config.isVirtual()) {
            config.setComment("Mongodb connect setting");
            config.getNode("host").setValue("localhost");
            config.getNode("port").setValue(27017);
            config.getNode("name").setValue("oktw");
            config.getNode("name").setComment("Database name");
            config.getNode("Username").setValue("");
            config.getNode("Password").setValue("");
            configManager.save();
        }

        if (config.getNode("Username").getString().isEmpty()) {
            database = new MongoClient(config.getNode("host").getString(), config.getNode("port").getInt()).getDatabase(config.getNode("name").getString());
        } else {
            MongoCredential credential = MongoCredential.createCredential(
                    config.getNode("Username").getString(),
                    config.getNode("name").getString(),
                    config.getNode("Password").getString().toCharArray()
            );
            database = new MongoClient(
                    new ServerAddress(config.getNode("host").getString(), config.getNode("port").getInt()),
                    Collections.singletonList(credential)
            ).getDatabase(config.getNode("name").getString());
        }
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
