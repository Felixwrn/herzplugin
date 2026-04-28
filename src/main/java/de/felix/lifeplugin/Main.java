package de.felix.lifeplugin;

import com.google.gson.JsonObject;
import de.felix.lifeplugin.ai.OpenAIService;
import de.felix.lifeplugin.commands.ModeAICommand;
import de.felix.lifeplugin.gui.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;

public class Main extends JavaPlugin implements Listener {

    private static Main instance;
    private OpenAIService ai;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        // 🔑 AI Setup
        String key = getConfig().getString("openai.api-key");
        String model = getConfig().getString("openai.model");

        ai = new OpenAIService(key, model);

        // 🎮 Events
        getServer().getPluginManager().registerEvents(this, this);

        // 🎮 Commands
        if (getCommand("mode") != null) {
            getCommand("mode").setExecutor(new ModeAICommand());
        }

        // 🔄 Marketplace Reload täglich
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            MarketplaceGUI.reload();
        }, getTicksUntilMidnight(), 20L * 60 * 60 * 24);

        getLogger().info("WRN LifePlugin FULL enabled!");
    }

    // ---------------- AI ----------------

    public OpenAIService getAI() {
        return ai;
    }

    // ---------------- LANGUAGE ----------------

    public void setLang(Player p, String lang) {
        getConfig().set("player-lang." + p.getUniqueId(), lang);
        saveConfig();
    }

    public String getLang(Player p) {
        return getConfig().getString("player-lang." + p.getUniqueId(), "en");
    }

    // ---------------- MODE SAVE ----------------

    public void saveMode(String name, int lives, boolean regen, boolean ban, boolean lifesteal) {

        name = name.toLowerCase();

        getConfig().set("modes." + name + ".lives", lives);
        getConfig().set("modes." + name + ".regen", regen);
        getConfig().set("modes." + name + ".ban", ban);
        getConfig().set("modes." + name + ".lifesteal", lifesteal);

        saveConfig();
    }

    // ---------------- CLICK HANDLING ----------------

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player p)) return;

        String title = e.getView().getTitle();

        // 🔒 GUI sichern
        if (
                title.contains("Mode") ||
                title.contains("Marketplace") ||
                title.contains("Language") ||
                title.contains("Lives")
        ) {
            e.setCancelled(true);
        }

        // 🌍 Language GUI
        if (title.contains("Language")) {

            if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

            String lang = e.getCurrentItem().getItemMeta().getDisplayName()
                    .replace("§f", "")
                    .toLowerCase();

            setLang(p, lang);

            p.sendMessage("§aLanguage set to " + lang);
            LanguageGUI.open(p);
            return;
        }

        // 📦 Marketplace
        if (title.contains("Marketplace")) {

            if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

            String name = e.getCurrentItem().getItemMeta().getDisplayName().replace("§e", "");

            JsonObject obj = MarketplaceGUI.get(name);
            if (obj == null) return;

            downloadMode(name, obj.get("url").getAsString(), p);
            return;
        }

        // ⚙ Mode GUI
        if (title.contains("Mode")) {

            if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

            String name = e.getCurrentItem().getItemMeta().getDisplayName()
                    .replace("§6★ ", "")
                    .replace("§e", "")
                    .toLowerCase();

            getConfig().set("mode", name);
            saveConfig();

            p.sendMessage("§aMode set to " + name);

            ModeGUI.open(p);
        }
    }

    // ---------------- DOWNLOAD ----------------

    private void downloadMode(String name, String urlStr, Player p) {

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {

                URL url = new URL(urlStr);

                File folder = new File(getDataFolder(), "modes");
                if (!folder.exists()) folder.mkdirs();

                File file = new File(folder, name + ".yml");

                Files.copy(url.openStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

                p.sendMessage("§aDownloaded mode: " + name);

            } catch (Exception e) {
                p.sendMessage("§cDownload failed!");
            }
        });
    }

    // ---------------- TIMER ----------------

    private long getTicksUntilMidnight() {

        long now = System.currentTimeMillis();
        Calendar next = Calendar.getInstance();

        next.set(Calendar.HOUR_OF_DAY, 0);
        next.set(Calendar.MINUTE, 0);
        next.set(Calendar.SECOND, 0);
        next.set(Calendar.MILLISECOND, 0);
        next.add(Calendar.DAY_OF_MONTH, 1);

        return (next.getTimeInMillis() - now) / 50;
    }
}
