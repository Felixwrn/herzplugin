package de.felix.lifeplugin.gui;

import de.felix.lifeplugin.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public class LanguageGUI {

    public static String getTitle(Player p) {
        return "§6Language";
    }

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, getTitle(p));

        String current = "en"; // fallback

        ItemStack filler = createItem(Material.GRAY_STAINED_GLASS_PANE, " ");

        for (int i = 0; i < 27; i++) {
            if (i < 9 || i > 17 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, filler);
            }
        }

        List<String> langs = getLanguages();

        int slot = 10;

        for (String lang : langs) {

            boolean isCurrent = lang.equalsIgnoreCase(current);

            ItemStack item = createItem(Material.BOOK, "§f" + lang.toUpperCase());

            ItemMeta meta = item.getItemMeta();

            if (meta != null && isCurrent) {
                meta.addEnchant(org.bukkit.enchantments.Enchantment.getByName("UNBREAKING"), 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                item.setItemMeta(meta);
            }

            inv.setItem(slot++, item);
        }

        p.openInventory(inv);
    }

    private static List<String> getLanguages() {

        List<String> list = new ArrayList<>();

        list.add("de");
        list.add("en");

        File folder = new File(Main.getInstance().getDataFolder(), "lang");

        if (folder.exists()) {
            for (File f : Objects.requireNonNull(folder.listFiles())) {
                if (f.getName().endsWith(".json")) {
                    list.add(f.getName().replace(".json", ""));
                }
            }
        }

        return list;
    }

    private static ItemStack createItem(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }

        return item;
    }
}
