package de.felix.lifeplugin.gui;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class LifeGUI {

    public static String getTitle() {
        return "§cLives";
    }

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, getTitle());

        ItemStack heart = new ItemStack(Material.RED_DYE);
        ItemMeta meta = heart.getItemMeta();

        meta.setDisplayName("§cYour Lives");
        meta.setLore(List.of("§7Example"));

        heart.setItemMeta(meta);

        inv.setItem(13, heart);

        p.openInventory(inv);
    }
}
