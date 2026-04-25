package de.felix.lifeplugin.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileStorage implements Storage {

    private final File file;
    private final FileConfiguration config;

    public FileStorage(File folder) {
        this.file = new File(folder, "lives.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public int getLives(UUID uuid) {
        return config.getInt(uuid.toString(), 10);
    }

    @Override
    public void setLives(UUID uuid, int lives) {
        config.set(uuid.toString(), lives);
    }

    @Override
    public void save(UUID uuid) {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
