package one.oktw.sponge.internal;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.slf4j.Logger;

import static one.oktw.sponge.Main.getMain;

public class DatabaseManager {
    public DatabaseManager() {
        ConfigManager config = getMain().getConfigManager();
        CommentedConfigurationNode configNode = config.getConfigNode();
        CommentedConfigurationNode databaseConfig = configNode.getNode("database");

        Logger logger = getMain().getLogger();
        logger.info("Loading Database...");
        // TODO
    }
}
