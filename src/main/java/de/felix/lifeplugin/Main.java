package de.felix.lifeplugin;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {

    private HashMap<UUID, Integer> lives = new HashMap<>();
    private String mode = "hardcore";
    private boolean useMySQL = false;

    private Connection connection;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        useMySQL = getConfig().getString("storage").equalsIgnoreCase("MYSQL");

        if (useMySQL) connect();

        mode = getConfig().getString("mode", "hardcore");

        Bukkit.getPluginManager().registerEvents(this, this);

        // ActionBar updater
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                int l = lives.getOrDefault(p.getUniqueId(), getConfig().getInt("defaultLives"));
                p.sendActionBar("§cLeben: §f" + l);
            }
        }, 0L, 40L);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        int l = loadPlayer(p.getUniqueId());
        lives.put(p.getUniqueId(), l);

        p.sendTitle("§cDu hast noch", "§f" + l + " Leben", 10, 70, 20);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Player killer = player.getKiller();

        if (killer == null) return;

        UUID uuid = player.getUniqueId();
        int currentLives = lives.getOrDefault(uuid, getConfig().getInt("defaultLives"));

        if (mode.equalsIgnoreCase("hardcore")) {
            currentLives--;

            if (currentLives <= 0) {
                Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), "Keine Leben mehr!", null, null);
                player.kickPlayer("§4Du hast keine Leben mehr!");
            }

        } else if (mode.equalsIgnoreCase("lifesteal")) {

            double victimHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
            victimHealth = Math.max(2, victimHealth - 2);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(victimHealth);

            double killerHealth = killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
            if (killerHealth < 20) killerHealth += 2;
            killer.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(killerHealth);
        }

        int maxLives = getConfig().getInt("maxLives");
        currentLives = Math.min(currentLives, maxLives);

        lives.put(uuid, currentLives);
        savePlayer(uuid, currentLives);
    }

    public void connect() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + getConfig().getString("mysql.host") + "/" + getConfig().getString("mysql.database"),
                    getConfig().getString("mysql.user"),
                    getConfig().getString("mysql.password")
            );

            connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS lives (uuid VARCHAR(36), lives INT)"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void savePlayer(UUID uuid, int l) {
        if (useMySQL) {
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "REPLACE INTO lives (uuid, lives) VALUES (?, ?)"
                );
                ps.setString(1, uuid.toString());
                ps.setInt(2, l);
                ps.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            getConfig().set("players." + uuid.toString(), l);
            saveConfig();
        }
    }

    public int loadPlayer(UUID uuid) {
        if (useMySQL) {
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT lives FROM lives WHERE uuid=?"
                );
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) return rs.getInt("lives");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return getConfig().getInt("players." + uuid.toString(), getConfig().getInt("defaultLives"));
        }
        return getConfig().getInt("defaultLives");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (cmd.getName().equalsIgnoreCase("setmode")) {
            mode = args[0];
            sender.sendMessage("§aModus gesetzt: " + mode);
            return true;
        }

        return false;
    }
}
