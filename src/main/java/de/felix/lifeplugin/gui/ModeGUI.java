package de.felix.lifeplugin.gui;

import de.felix.lifeplugin.Main;
import com.google.gson.JsonObject;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class ModeGUI {

    public static String getTitle(Player p) {
        return "§6Mode Selector";
    }

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 45, getTitle(p));

        String current = Main.getInstance().getConfig().getString("mode", "lifesteal");

        int slot = 10;

        // 🔹 1. CONFIG MODES
        if (Main.getInstance().getConfig().getConfigurationSection("modes") != null) {

            for (String mode : Main.getInstance().getConfig().getConfigurationSection("modes").getKeys(false)) {

                inv.setItem(slot++, createModeItem(mode, current.equalsIgnoreCase(mode), "§7Config Mode"));
            }
        }

        // 🔹 2. LOCAL FILE MODES (/modes/)
        File folder = new File(Main.getInstance().getDataFolder(), "modes");

        if (folder.exists()) {
            for (File file : Objects.requireNonNull(folder.listFiles())) {

                if (!file.getName().endsWith(".yml")) continue;

                String name = file.getName().replace(".yml", "");

                inv.setItem(slot++, createModeItem(name, current.equalsIgnoreCase(name), "§7Local Mode"));
            }
        }

        // 🔹 3. MARKETPLACE MODES (optional anzeigen)
        for (String name : MarketplaceGUI.getAllModes()) {

            JsonObject obj = MarketplaceGUI.get(name);

            inv.setItem(slot++, createModeItem(
                    name,
                    current.equalsIgnoreCase(name),
                    "§7Marketplace",
                    obj.get("description").getAsString()
            ));
        }

        p.openInventory(inv);
    }

    // ---------------- ITEM ----------------

    private static ItemStack createModeItem(String name, boolean selected, String... loreLines) {

        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();

        if (meta == null) return item;

        meta.setDisplayName(selected ? "§6★ §e" + name : "§e" + name);

        List<String> lore = new ArrayList<>();
        lore.addAll(Arrays.asList(loreLines));
        lore.add("");
        lore.add(selected ? "§aSelected" : "§7Click to select");

        meta.setLore(lore);

        if (selected) {
            meta.addEnchant(org.bukkit.enchantments.Enchantment.getByName("UNBREAKING"), 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);
        return item;
    }
}
