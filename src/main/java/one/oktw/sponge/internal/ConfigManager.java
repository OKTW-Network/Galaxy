package one.oktw.sponge.internal;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.IOException;

public class ConfigManager {
    private ConfigurationLoader configLoader;
    private CommentedConfigurationNode configNode;

    public ConfigManager(ConfigurationLoader<CommentedConfigurationNode> configLoader) {
        this.configLoader = configLoader;
        try {
            configNode = configLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void save() {
        try {
            configLoader.save(configNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    CommentedConfigurationNode getConfigNode() {
        return configNode;
    }
}
