package de.felix.lifeplugin;

import de.felix.lifeplugin.gui.*;
import de.felix.lifeplugin.lang.LanguageManager;
import de.felix.lifeplugin.util.ActionBarUtil;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin implements Listener {

    private static Main instance;
    private LanguageManager langManager;

    public static Main getInstance() { return instance; }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        langManager = new LanguageManager();
        langManager.load(new File(getDataFolder(), "lang"));

        Bukkit.getPluginManager().registerEvents(this, this);

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                int lives = getConfig().getInt("lives." + p.getUniqueId(), 3);
                ActionBarUtil.send(p, "§cLives: " + lives);
            }
        }, 0, 40);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        String title = e.getView().getTitle();

        if (title.equals(LanguageGUI.TITLE) || title.equals(LifeGUI.TITLE)) e.setCancelled(true);

        if (title.equals(LanguageGUI.TITLE)) {
            if (e.getCurrentItem() == null) return;
            String lang = e.getCurrentItem().getItemMeta().getDisplayName().toLowerCase();
            langManager.setLanguage(p.getUniqueId(), lang);
            p.sendMessage("§aLanguage gesetzt: " + lang);
        }

        if (title.equals(LifeGUI.TITLE)) {
            int lives = getConfig().getInt("lives." + p.getUniqueId(), 3);

            if (!p.hasPermission("life.admin")) {
                p.sendMessage("§cKeine Rechte!");
                return;
            }

            if (e.getSlot() == 11 && lives < 10) lives++;
            if (e.getSlot() == 15 && lives > 0) lives--;

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
