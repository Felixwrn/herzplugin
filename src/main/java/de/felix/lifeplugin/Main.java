package de.felix.lifeplugin;

import de.felix.lifeplugin.gui.*;
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

        getLogger().info("LifePlugin gestartet!");
    }

    // ================= COMMANDS =================
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

    // ================= GUI CLICK =================
    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player p)) return;

        String title = e.getView().getTitle();

        if (
                title.equals(LanguageGUI.TITLE) ||
                title.equals(LifeGUI.TITLE) ||
                title.equals(ModeGUI.TITLE) ||
                title.equals(MarketplaceGUI.TITLE)
        ) {
            e.setCancelled(true);
        }

        // ===== LANGUAGE =====
        if (title.equals(LanguageGUI.TITLE)) {

            if (e.getCurrentItem() == null) return;

            String name = e.getCurrentItem().getItemMeta().getDisplayName();

            String lang = "de";

            if (name.contains("(en)")) lang = "en";
            if (name.contains("(de)")) lang = "de";

            WRNAPI.setLanguage(p.getUniqueId(), lang);

            p.sendMessage("§aLanguage gesetzt: " + lang);
        }

        // ===== LIVES =====
        if (title.equals(LifeGUI.TITLE)) {

            int lives = getConfig().getInt(
                    "lives." + p.getUniqueId(),
                    getConfig().getInt("default-lives", 3)
            );

            int max = getConfig().getInt("max-lives", 10);

            if (!p.hasPermission("life.admin")) {
                p.sendMessage("§cKeine Rechte!");
                return;
            }

            if (e.getSlot() == 11 && lives < max) lives++;
            if (e.getSlot() == 15 && lives > 0) lives--;

            getConfig().set("lives." + p.getUniqueId(), lives);
            saveConfig();

            LifeGUI.open(p);
        }

        // ===== MODE =====
        if (title.equals(ModeGUI.TITLE)) {

            if (e.getSlot() == 11) {
                getConfig().set("mode.name", "Hardcore");
                getConfig().set("mode.lives", 1);
                getConfig().set("mode.banOnZero", true);
            }

            if (e.getSlot() == 15) {
                getConfig().set("mode.name", "Normal");
                getConfig().set("mode.lives", 3);
                getConfig().set("mode.banOnZero", false);
            }

            saveConfig();

            p.sendMessage("§aMode gesetzt!");
        }

        // ===== MARKETPLACE =====
        if (title.equals(MarketplaceGUI.TITLE)) {

            if (e.getSlot() == 11) loadMode("hardcore", p);
            if (e.getSlot() == 13) loadMode("pro", p);
            if (e.getSlot() == 15) loadMode("vanilla_plus", p);
        }
    }

    // ================= DEATH SYSTEM =================
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        Player p = e.getEntity();

        int lives = getConfig().getInt(
                "lives." + p.getUniqueId(),
                getConfig().getInt("mode.lives", 3)
        );

        lives--;

        getConfig().set("lives." + p.getUniqueId(), lives);
        saveConfig();

        if (lives <= 0) {

            boolean ban = getConfig().getBoolean("mode.banOnZero", false);

            if (ban) {
                Bukkit.getBanList(BanList.Type.NAME)
                        .addBan(p.getName(), "Keine Leben mehr", null, null);
                p.kickPlayer("§cDu hast keine Leben mehr!");
            } else {
                p.setGameMode(GameMode.SPECTATOR);
                p.sendMessage("§cDu hast keine Leben mehr!");
            }
        }

        // Placeholder Beispiel
        HashMap<String, String> ph = new HashMap<>();
        ph.put("lives", String.valueOf(lives));

        p.sendMessage(WRNAPI.text(p.getUniqueId(), "lives_display", ph));
    }

    // ================= LOAD MODE =================
    private void loadMode(String key, Player p) {

        String path = "marketplace." + key;

        if (!getConfig().contains(path)) {
            p.sendMessage("§cMode nicht gefunden!");
            return;
        }

        getConfig().set("mode.name", getConfig().getString(path + ".name"));
        getConfig().set("mode.lives", getConfig().getInt(path + ".lives"));
        getConfig().set("mode.banOnZero", getConfig().getBoolean(path + ".banOnZero"));

        saveConfig();

        p.sendMessage("§aMode geladen: " + getConfig().getString(path + ".name"));
    }
}
