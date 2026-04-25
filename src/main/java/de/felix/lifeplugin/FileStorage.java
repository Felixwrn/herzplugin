package de.felix.lifeplugin;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.UUID;

public class FileStorage implements Storage {

    private final Main plugin;
    private final File file;
    private final YamlConfiguration cfg;

    public FileStorage(Main plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "data.yml");
        this.cfg = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public void loadPlayer(UUID uuid) {
        if (!cfg.contains(uuid.toString())) {
            cfg.set(uuid.toString(), 10);
            saveFile();
        }
    }

    @Override
    public void savePlayer(UUID uuid, int lives) {
        cfg.set(uuid.toString(), lives);
        saveFile();
    }

    @Override
    public int getLives(UUID uuid) {
        return cfg.getInt(uuid.toString(), 10);
    }

    private void saveFile() {
        try {
            cfg.save(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
