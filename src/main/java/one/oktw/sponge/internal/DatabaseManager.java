package one.oktw.sponge.internal;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.slf4j.Logger;

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
            configManager.save();
        }

        database = new MongoClient(config.getNode("host").getString(), config.getNode("port").getInt()).getDatabase(config.getNode("name").getString());
    }

    MongoDatabase getDatabase() {
        return database;
    }
}
