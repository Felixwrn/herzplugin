package de.felix.lifeplugin;

import de.wrn.api.api.WRNAPI;
import de.wrn.api.api.PlaceholderAPI;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player p)) return true;

        if (cmd.getName().equalsIgnoreCase("test")) {

            var ph = PlaceholderAPI.create();
            PlaceholderAPI.add(ph, "player", p.getName());

            p.sendMessage(WRNAPI.text(p.getUniqueId(), "Hallo {player}", ph));
            return true;
        }

        return false;
    }
}
