package de.felix.lifeplugin.gui;

import com.google.gson.JsonObject;
import de.felix.lifeplugin.market.MarketplaceManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MarketplaceGUI {

    private static Map<String, JsonObject> cache = new HashMap<>();

    public static void reload() {
        cache = MarketplaceManager.load();
    }

    public static void open(Player p) {

        reload();

        Inventory inv = Bukkit.createInventory(null, 45, "§6Marketplace");

        int slot = 10;

        for (String name : cache.keySet()) {

            JsonObject o = cache.get(name);

            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();

            if (meta == null) continue;

            meta.setDisplayName("§e" + name);
            meta.setLore(List.of(
                    "§7" + o.get("description").getAsString(),
                    "§7by " + o.get("author").getAsString(),
                    "",
                    "§aClick to download"
            ));

            item.setItemMeta(meta);
            inv.setItem(slot++, item);
        }

        p.openInventory(inv);
    }

    public static JsonObject get(String name) {
        return cache.get(name);
    }
}
