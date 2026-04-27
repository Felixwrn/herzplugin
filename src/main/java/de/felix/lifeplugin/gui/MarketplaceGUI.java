package de.felix.lifeplugin.gui;

import com.google.gson.JsonObject;
import de.felix.lifeplugin.market.MarketplaceManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

            JsonObject obj = cache.get(name);

            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();

            if (meta == null) continue;

            String desc = obj.has("description") ? obj.get("description").getAsString() : "No description";
            String author = obj.has("author") ? obj.get("author").getAsString() : "Unknown";

            meta.setDisplayName("§e" + name);
            meta.setLore(List.of(
                    "§7" + desc,
                    "§7by " + author,
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

    public static Set<String> getAllModes() {
        return cache.keySet();
    }
}
