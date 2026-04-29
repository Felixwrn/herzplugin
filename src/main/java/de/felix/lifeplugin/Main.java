package de.felix.lifeplugin;

import de.felix.lifeplugin.commands.LanguageCommand;
import de.felix.lifeplugin.gui.*;
import de.felix.lifeplugin.lang.LanguageManager;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin implements Listener {

    private static Main instance;
    private LanguageManager langManager;

    public static Main getInstance() { return instance; }
    public LanguageManager getLangManager() { return langManager; }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        langManager = new LanguageManager();
        langManager.load(new File(getDataFolder(), "lang"));

        Bukkit.getPluginManager().registerEvents(this, this);

        getCommand("language").setExecutor(new LanguageCommand(langManager));
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        String title = e.getView().getTitle();

        if (title.equals(LanguageGUI.TITLE) || title.equals(LifeGUI.TITLE)) e.setCancelled(true);

        if (title.equals(LanguageGUI.TITLE)) {
            if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;
            String lang = e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase();
            langManager.setLanguage(p.getUniqueId(), lang);
            p.sendMessage("§aLanguage set to " + lang);
            LanguageGUI.open(p);
        }

        if (title.equals(LifeGUI.TITLE)) {
            int lives = getConfig().getInt("lives." + p.getUniqueId(), 3);
            if (e.getSlot() == 11) lives++;
            if (e.getSlot() == 15) lives--;
            if (lives < 0) lives = 0;

            getConfig().set("lives." + p.getUniqueId(), lives);
            saveConfig();
            LifeGUI.open(p);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        int lives = getConfig().getInt("lives." + p.getUniqueId(), 3);
        lives--;

        getConfig().set("lives." + p.getUniqueId(), lives);
        saveConfig();

        if (lives <= 0) {
            p.setGameMode(GameMode.SPECTATOR);
            p.sendMessage("§cKeine Leben mehr!");
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;

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
}
