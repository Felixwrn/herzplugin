package de.felix.lifeplugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {

    private final HashMap<UUID, Integer> lives = new HashMap<>();

    private static Main instance;

    private GameModeType mode;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        String m = getConfig().getString("mode", "LIFESTEAL");
        mode = GameModeType.valueOf(m.toUpperCase());

        Bukkit.getPluginManager().registerEvents(this, this);

        getLogger().info("LifePlugin gestartet! Mode: " + mode);
    }

    public static Main getInstance() {
        return instance;
    }

    public int getLives(UUID uuid) {
        return lives.getOrDefault(uuid, getConfig().getInt("start-lives", 10));
    }

    public GameModeType getMode() {
        return mode;
    }

    // 🧍 Join
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player p = e.getPlayer();

        lives.putIfAbsent(p.getUniqueId(), getConfig().getInt("start-lives", 10));

        updateActionBar(p);
    }

    // 💀 Death
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {

        Player p = e.getEntity();

        if (mode == GameModeType.LIFESTEAL) {
            handleLifeSteal(p, e.getEntity().getKiller());
        }

        int current = lives.getOrDefault(p.getUniqueId(), 10);
        current--;

        if (current <= 0) {

            if (mode == GameModeType.HARDCORE) {
                p.kickPlayer("§cHardcore: keine Leben mehr!");
            } else {
                p.sendMessage("§cDu hast keine Leben mehr!");
            }

            lives.remove(p.getUniqueId());
            return;
        }

        lives.put(p.getUniqueId(), current);

        Bukkit.getScheduler().runTaskLater(this, () -> updateActionBar(p), 10L);
    }

    // 🧛 Lifesteal
    private void handleLifeSteal(Player dead, Player killer) {

        if (killer == null) return;

        int steal = getConfig().getInt("lifesteal.steal-amount", 1);
        int max = getConfig().getInt("lifesteal.max-lives", 20);

        UUID uuid = killer.getUniqueId();

        int current = lives.getOrDefault(uuid, 10);

        if (current >= max) return;

        current += steal;

        lives.put(uuid, current);

        killer.sendMessage("§a+1 Leben durch Kill!");
    }

    // 📊 ActionBar
    private void updateActionBar(Player p) {

        int current = getLives(p.getUniqueId());

        ActionBarUtil.send(p, "§cLeben: §f" + current + " §7| Mode: " + mode);
    }

    // ⌨️ Commands
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) return true;

        if (cmd.getName().equalsIgnoreCase("livesgui")) {
            LifeGUI.open(p);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("mode")) {

            if (!p.isOp()) return true;

            if (args.length == 0) return true;

            mode = GameModeType.valueOf(args[0].toUpperCase());

            p.sendMessage("§aMode gesetzt: " + mode);

            return true;
        }

        return false;
    }
}
