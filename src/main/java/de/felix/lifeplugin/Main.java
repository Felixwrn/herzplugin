package de.felix.lifeplugin;

import de.felix.lifeplugin.gui.*;
import de.felix.lifeplugin.util.ActionBarUtil;
import de.wrn.api.api.WRNAPI;
import de.wrn.api.api.PlaceholderAPI;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Main extends JavaPlugin implements Listener {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        Bukkit.getPluginManager().registerEvents(this, this);

        // ActionBar Loop
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {

                int lives = getConfig().getInt(
                        "lives." + p.getUniqueId(),
                        getConfig().getInt("default-lives", 3)
                );

                ActionBarUtil.send(p, "§cLives: " + lives);
            }
        }, 0L, 40L);

        getLogger().info("LifePlugin gestartet!");
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

        if (cmd.getName().equalsIgnoreCase("modegui")) {
            ModeGUI.open(p);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("market")) {
            MarketplaceGUI.open(p);
            return true;
        }

        return false;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player p)) return;

        String title = e.getView().getTitle();

        if (title.equals(LanguageGUI.TITLE) ||
            title.equals(LifeGUI.TITLE) ||
            title.equals(ModeGUI.TITLE) ||
            title.equals(MarketplaceGUI.TITLE)) {
            e.setCancelled(true);
        }

        if (title.equals(LanguageGUI.TITLE)) {
            if (e.getCurrentItem() == null) return;

            String name = e.getCurrentItem().getItemMeta().getDisplayName();
            String lang = name.contains("(en)") ? "en" : "de";

            WRNAPI.setLanguage(p.getUniqueId(), lang);
            p.sendMessage("§aLanguage set: " + lang);
        }

        if (title.equals(LifeGUI.TITLE)) {

            int lives = getConfig().getInt(
                    "lives." + p.getUniqueId(),
                    getConfig().getInt("default-lives", 3)
            );

            if (e.getSlot() == 11) lives++;
            if (e.getSlot() == 15) lives--;

            getConfig().set("lives." + p.getUniqueId(), lives);
            saveConfig();

            LifeGUI.open(p);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        Player p = e.getEntity();

        int lives = getConfig().getInt(
                "lives." + p.getUniqueId(),
                getConfig().getInt("default-lives", 3)
        );

        lives--;

        getConfig().set("lives." + p.getUniqueId(), lives);
        saveConfig();

        HashMap<String, String> ph = new HashMap<>();
        ph.put("lives", String.valueOf(lives));

        p.sendMessage(WRNAPI.text(p.getUniqueId(), "lives_display", ph));
    }
}
