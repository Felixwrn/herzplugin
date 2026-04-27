package de.felix.lifeplugin;

import com.google.gson.JsonObject;
import de.felix.lifeplugin.gui.*;
import de.felix.lifeplugin.util.ChatInput;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;

public class Main extends JavaPlugin implements Listener, CommandExecutor {

    private static Main instance;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new ChatInput(), this);

        // Commands
        register("mode");
        register("market");
        register("modes");
        register("language");
        register("lifecore");
        register("livesgui");
        register("langgui");

        // Daily Marketplace Reload
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            MarketplaceGUI.reload();
        }, getTicksUntilMidnight(), 20L * 60 * 60 * 24);

        getLogger().info("LifePlugin enabled!");
    }

    private void register(String cmd) {
        if (getCommand(cmd) != null) getCommand(cmd).setExecutor(this);
    }

    // ---------------- CLICK ----------------
    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (!(e.getWhoClicked() instanceof Player p)) return;

        String title = e.getView().getTitle();

        // Mode Builder
        if (title.contains("Mode Builder")) {
            e.setCancelled(true);
            ModeBuilderGUI.click(p, e.getSlot());
            return;
        }

        // Marketplace
        if (title.contains("Marketplace")) {
            e.setCancelled(true);

            if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

            String name = e.getCurrentItem().getItemMeta().getDisplayName().replace("§e", "");

            JsonObject obj = MarketplaceGUI.get(name);
            if (obj == null) return;

            downloadMode(name, obj.get("url").getAsString(), p);
            return;
        }

        // Mode GUI
        if (title.contains("Mode")) {
            e.setCancelled(true);

            if (e.getCurrentItem() == null || e.getCurrentItem().getItemMeta() == null) return;

            String name = e.getCurrentItem().getItemMeta().getDisplayName()
                    .replace("§6★ ", "")
                    .replace("§e", "")
                    .toLowerCase();

            getConfig().set("mode", name);
            saveConfig();

            p.sendMessage("§aMode set to " + name);
            ModeGUI.open(p);
            return;
        }
    }

    // ---------------- COMMANDS ----------------
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) return true;

        switch (cmd.getName().toLowerCase()) {

            case "mode":
                if (args.length == 2 && args[0].equalsIgnoreCase("create")) {
                    ModeBuilderGUI.open(p, args[1]);
                } else {
                    ModeGUI.open(p);
                }
                return true;

            case "market":
                MarketplaceGUI.open(p);
                return true;

            case "modes":
                ModeGUI.open(p);
                return true;

            case "livesgui":
                LifeGUI.open(p);
                return true;

            case "langgui":
                LanguageGUI.open(p);
                return true;

            case "language":
                if (args.length == 1) {
                    p.sendMessage("§aLanguage set to " + args[0]);
                }
                return true;

            case "lifecore":
                if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                    reloadConfig();
                    p.sendMessage("§aReloaded!");
                }
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
}
