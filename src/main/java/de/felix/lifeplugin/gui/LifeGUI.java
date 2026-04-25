package de.felix.lifeplugin.gui;

import de.felix.lifeplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class LifeGUI {

    // 🔥 Titel jetzt dynamisch
    public static String getTitle(Player p) {
        return Main.getInstance()
                .getLanguageManager()
                .get(p.getUniqueId(), "gui_life_title");
    }

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, getTitle(p));

        int lives = Main.getInstance().getLives(p.getUniqueId());

        ItemStack heart = new ItemStack(Material.RED_DYE);
        ItemMeta meta = heart.getItemMeta();

        if (meta == null) return;

        // 🌍 Name übersetzt
        meta.setDisplayName(
                Main.getInstance()
                        .getLanguageManager()
                        .get(p.getUniqueId(), "gui_lives_title")
        );

        // 🌍 Lore übersetzt
        meta.setLore(List.of(
                Main.getInstance()
                        .getLanguageManager()
                        .get(p.getUniqueId(), "gui_current"),
                "§a" + lives
        ));

        heart.setItemMeta(meta);

        inv.setItem(13, heart);

        p.openInventory(inv);
    }
}
