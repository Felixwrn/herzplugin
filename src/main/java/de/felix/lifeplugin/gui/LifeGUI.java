package de.felix.lifeplugin.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class LifeGUI {

    private static final String TITLE = "§cDeine Leben";

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        int lives = Main.getInstance().getLives(p.getUniqueId());

        ItemStack heart = new ItemStack(Material.RED_DYE);
        ItemMeta meta = heart.getItemMeta();

        if (meta == null) return; // safety fix

        meta.setDisplayName("§cLeben");

        meta.setLore(List.of(
                "§7Aktuell:",
                "§a" + lives
        ));

        heart.setItemMeta(meta);

        inv.setItem(13, heart);

        p.openInventory(inv);
    }

    // 🔒 Optional: für GUI-Schutz in Main.java wichtig
    public static String getTitle() {
        return TITLE;
    }
}
