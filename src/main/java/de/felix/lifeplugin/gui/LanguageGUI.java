package de.felix.lifeplugin.gui;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class LanguageGUI {

    public static String getTitle() {
        return "§6Language";
    }

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, getTitle());

        inv.setItem(11, item("§fEN"));
        inv.setItem(13, item("§fDE"));
        inv.setItem(15, item("§fFR"));

        p.openInventory(inv);
    }

    private static ItemStack item(String name) {
        ItemStack i = new ItemStack(Material.BOOK);
        ItemMeta m = i.getItemMeta();
        m.setDisplayName(name);
        m.setLore(List.of("§7Click to select"));
        i.setItemMeta(m);
        return i;
    }
}
