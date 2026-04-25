package de.felix.lifeplugin.storage;

import java.util.UUID;

public interface Storage {

    int getLives(UUID uuid);

    void setLives(UUID uuid, int lives);

    void save(UUID uuid);
}
