package one.oktw.sponge.internal;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;

import static one.oktw.sponge.Main.getMain;

public class DatabaseManager {
    public DatabaseManager() {
        ConfigManager config = getMain().getConfigManager();
        CommentedConfigurationNode configNode = config.getConfigNode();
//        CommentedConfigurationNode mongodbConfig = configNode.getNode("mongodb");
//        if (mongodbConfig.isVirtual()) {
//            mongodbConfig.getNode("connect").setValue("mongodb://localhost");
//            mongodbConfig.getNode("connect").setComment("ConnectionString for MongoDB");
//            mongodbConfig.getNode("database").setValue("oktw");
//            mongodbConfig.getNode("database").setComment("Datebase name");
//            config.save();
//        }
//
//        Logger logger = getCore().getLogger();
//        logger.info("Loading Database...");
//        MongoClient mongoClient = MongoClients.create(mongodbConfig.getNode("connect").getString());
    }
}
