package de.felix.lifeplugin.gui;

import de.felix.lifeplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.List;
import java.util.Map;

public class LanguageGUI {

    private static final String TITLE = "§6Language Selection";

    public static String getTitle() {
        return TITLE;
    }

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        if (Main.getInstance().getLangConfig().getConfigurationSection("languages") == null) {
            p.sendMessage("§cNo languages configured!");
            return;
        }

        Map<String, Object> langs = Main.getInstance()
                .getLangConfig()
                .getConfigurationSection("languages")
                .getValues(false);

        int slot = 0;

        for (String lang : langs.keySet()) {

            boolean installed = new File(
                    Main.getInstance().getDataFolder(),
                    "lang/" + lang + ".json"
            ).exists();

            ItemStack item = new ItemStack(installed ? Material.LIME_DYE : Material.GRAY_DYE);
            ItemMeta meta = item.getItemMeta();

            if (meta == null) continue;

            meta.setDisplayName("§e" + lang.toUpperCase());

            meta.setLore(List.of(
                    installed
                            ? "§aInstalled (Click to use)"
                            : "§cNot installed (Click to download)"
            ));

            item.setItemMeta(meta);

            inv.setItem(slot++, item);
        }

        p.openInventory(inv);
    }
}
