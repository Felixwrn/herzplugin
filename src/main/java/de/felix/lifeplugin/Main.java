package de.felix.lifeplugin;

import de.felix.lifeplugin.gui.LanguageGUI;
import de.felix.lifeplugin.gui.LifeGUI;
import de.felix.lifeplugin.gui.ModeBuilderGUI;
import de.felix.lifeplugin.gui.ModeGUI;
import de.felix.lifeplugin.gui.MarketplaceGUI;
import de.felix.lifeplugin.lang.LanguageManager;
import de.felix.lifeplugin.storage.*;
import de.felix.lifeplugin.util.ChatInput;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Main extends JavaPlugin implements Listener, TabExecutor {

    private static Main instance;

    private LanguageManager languageManager;
    private Storage storage;

    private final HashMap<UUID, Integer> lives = new HashMap<>();

    private String mode;

    private File langConfigFile;
    private YamlConfiguration langConfig;

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();
        loadLangConfig();
        copyDefaultLanguages();

        languageManager = new LanguageManager();
        languageManager.load(new File(getDataFolder(), "lang"));

        mode = getConfig().getString("mode", "LIFESTEAL");

        String type = getConfig().getString("storage.type");

        if ("MYSQL".equalsIgnoreCase(type)) {
            storage = new MySQLStorage(
                    getConfig().getString("mysql.host"),
                    getConfig().getInt("mysql.port"),
                    getConfig().getString("mysql.database"),
                    getConfig().getString("mysql.user"),
                    getConfig().getString("mysql.password")
            );
        } else {
            storage = new FileStorage(getDataFolder());
        }

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ChatInput(), this);

        getCommand("mode").setExecutor(this);
        getCommand("mode").setTabCompleter(this);

        // 🔥 Daily Marketplace Update
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            MarketplaceGUI.reload();
        }, getTicksUntilMidnight(), 20L * 60 * 60 * 24);

        getLogger().info("LifePlugin enabled!");
    }

    // ---------------- LANGUAGE ----------------

    private void loadLangConfig() {
        langConfigFile = new File(getDataFolder(), "languages.yml");

        if (!langConfigFile.exists()) {
            saveResource("languages.yml", false);
        }

        langConfig = YamlConfiguration.loadConfiguration(langConfigFile);
    }

    private void copyDefaultLanguages() {
        File langFolder = new File(getDataFolder(), "lang");

        if (!langFolder.exists()) langFolder.mkdirs();

        String[] defaults = {"de.json", "en.json"};

        for (String file : defaults) {
            File target = new File(langFolder, file);
            if (!target.exists()) {
                saveResource("lang/" + file, false);
            }
        }
    }

    public static Main getInstance() {
        return instance;
    }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public YamlConfiguration getLangConfig() {
        return langConfig;
    }

    public int getLives(UUID uuid) {
        return lives.getOrDefault(uuid, getConfig().getInt("start-lives", 10));
    }

    // ---------------- EVENTS ----------------

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        int loaded = storage.getLives(p.getUniqueId());
        lives.put(p.getUniqueId(), loaded);

        updateActionBar(p);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        Player p = e.getEntity();

        int current = getLives(p.getUniqueId()) - 1;

        lives.put(p.getUniqueId(), current);
        storage.setLives(p.getUniqueId(), current);
        storage.save(p.getUniqueId());

        if (current <= 0) {
            p.kickPlayer(languageManager.get(p.getUniqueId(), "no_lives"));
            return;
        }

        Player killer = p.getKiller();

        if (killer != null && mode.equalsIgnoreCase("LIFESTEAL")) {

            int steal = getConfig().getInt("lifesteal.steal-amount", 1);
            int max = getConfig().getInt("lifesteal.max-lives", 20);

            int newLives = Math.min(getLives(killer.getUniqueId()) + steal, max);

            lives.put(killer.getUniqueId(), newLives);
            storage.setLives(killer.getUniqueId(), newLives);
            storage.save(killer.getUniqueId());
        }
    }

    private void updateActionBar(Player p) {
        String msg = languageManager.format(
                p.getUniqueId(),
                "lives",
                "lives", String.valueOf(getLives(p.getUniqueId()))
        );
        p.sendActionBar(msg);
    }

    // ---------------- GUI ----------------

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player p)) return;

        // Mode Builder
        if (e.getView().getTitle().startsWith("§6Mode Builder:")) {
            e.setCancelled(true);
            ModeBuilderGUI.click(p, e.getSlot());
            return;
        }

        // Marketplace
        if (e.getView().getTitle().equals("§6Marketplace")) {

            e.setCancelled(true);

            if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

            String name = e.getCurrentItem().getItemMeta().getDisplayName().replace("§e", "");

            var obj = MarketplaceGUI.get(name);
            if (obj == null) return;

            downloadMode(name, obj.getString("url"), p);
        }

        // Life GUI
        if (e.getView().getTitle().equals(LifeGUI.getTitle(p))) {
            e.setCancelled(true);
        }

        // Language GUI
        if (e.getView().getTitle().equals(LanguageGUI.getTitle(p))) {
            e.setCancelled(true);
        }

        // Mode GUI
        if (e.getView().getTitle().equals(ModeGUI.getTitle(p))) {
            e.setCancelled(true);

            if (e.getCurrentItem() == null) return;

            Material mat = e.getCurrentItem().getType();

            if (mat == Material.REDSTONE_BLOCK) {
                mode = "HARDCORE";
            } else if (mat == Material.HEART_OF_THE_SEA) {
                mode = "LIFESTEAL";
            } else return;

            getConfig().set("mode", mode);
            saveConfig();

            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
            p.sendMessage("§aMode set to " + mode);
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {

        if (!(e.getWhoClicked() instanceof Player p)) return;

        if (e.getView().getTitle().equals(LifeGUI.getTitle(p))) e.setCancelled(true);
        if (e.getView().getTitle().equals(LanguageGUI.getTitle(p))) e.setCancelled(true);
        if (e.getView().getTitle().equals(ModeGUI.getTitle(p))) e.setCancelled(true);
    }

    // ---------------- COMMANDS ----------------

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) return true;

        // Mode Builder
        if (cmd.getName().equalsIgnoreCase("mode") && args.length == 2 && args[0].equalsIgnoreCase("create")) {
            ModeBuilderGUI.open(p, args[1]);
            return true;
        }

        // Marketplace
        if (cmd.getName().equalsIgnoreCase("market")) {
            MarketplaceGUI.open(p);
            return true;
        }

        // GUIs
        if (cmd.getName().equalsIgnoreCase("livesgui")) {
            LifeGUI.open(p);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("langgui")) {
            LanguageGUI.open(p);
            return true;
        }

        return false;
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
                e.printStackTrace();
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

    @Override
    public void onDisable() {

        for (UUID uuid : lives.keySet()) {
            storage.setLives(uuid, lives.get(uuid));
            storage.save(uuid);
        }

        getLogger().info("LifePlugin disabled!");
    }
}
