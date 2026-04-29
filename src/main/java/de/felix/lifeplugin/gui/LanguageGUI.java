package de.felix.lifeplugin.gui;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class LanguageGUI {
    public static final String TITLE = "§6Language";

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9, TITLE);
        inv.setItem(3, item(Material.PAPER, "Deutsch"));
        inv.setItem(5, item(Material.BOOK, "English"));
        p.openInventory(inv);
    }

    private static ItemStack item(Material m, String name) {
        ItemStack i = new ItemStack(m);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(name);
        i.setItemMeta(meta);
        return i;
    }
}
