package code.test.dodgeball.api.io;

import com.google.common.base.Preconditions;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class Configuration {

    private final File folder;
    private final File file;
    private final ConfigurationLoader<?> loader;
    private ConfigurationNode node;

    public Configuration(File folder, File file, ConfigurationLoader<?> loader, ConfigurationNode node) {
        this.folder = folder;
        this.file = file;
        this.loader = loader;
        this.node = node;
    }

    public boolean reload() {
        ConfigurationNode fallback = node;
        try {

            this.node = loader.load();

            return true;
        } catch (IOException exception) {
            exception.printStackTrace();

            node = fallback;

            return false;
        }
    }

    public boolean save() {
        try {
            loader.save(node);

            return true;
        } catch (IOException exception) {
            exception.printStackTrace();

            return false;
        }
    }

    public ConfigurationNode node(Object... values) {
        return node.node(values);
    }

    public void set(ConfigurationNode node) {
        set(node, false);
    }

    public void set(ConfigurationNode node, boolean save) {
        this.node = node;

        if (save) {
            save();
        }
    }

    public File getFolder() {
        return folder;
    }

    public File getFile() {
        return file;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private File folder;
        private String name;

        public Builder folder(File folder) {
            this.folder = folder;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Configuration build() {
            Preconditions.checkNotNull(folder);
            Preconditions.checkNotNull(name);

            try {
                File file = Files.getOrCreate(folder, name);
                ConfigurationLoader<?> loader = YamlConfigurationLoader.builder()
                        .defaultOptions(ConfigurationOptions.defaults()
                                .shouldCopyDefaults(true)).file(file).build();

                ConfigurationNode node = loader.load();

                return new Configuration(folder, file, loader, node);
            } catch (IOException exception) {
                exception.printStackTrace();
            }

            return null;
        }
    }
}