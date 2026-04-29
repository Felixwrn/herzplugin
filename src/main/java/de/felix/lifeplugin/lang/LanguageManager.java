package de.felix.lifeplugin.lang;

import java.io.File;
import java.util.*;

public class LanguageManager {

    private final HashMap<UUID, String> playerLang = new HashMap<>();

    public void load(File folder) {
        if (!folder.exists()) folder.mkdirs();
    }

    public void setLanguage(UUID uuid, String lang) {
        playerLang.put(uuid, lang);
    }

    public String get(UUID uuid, String key) {
        return key;
    }
}
