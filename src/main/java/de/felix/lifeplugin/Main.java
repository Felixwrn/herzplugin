
package de.felix.lifeplugin;

import de.felix.lifeplugin.ai.OpenAIService;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private OpenAIService ai;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        String key = getConfig().getString("openai.api-key");
        String model = getConfig().getString("openai.model");

        ai = new OpenAIService(key, model);

        getLogger().info("LifePlugin with AI enabled!");
    }

    public OpenAIService getAI() {
        return ai;
    }
}
