package de.felix.lifeplugin.gui;

import de.felix.lifeplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

public class LifeGUI {

    // 📌 GUI TITLE
    public static String getTitle(Player p) {
        return Main.getInstance()
                .getLanguageManager()
                .get(p.getUniqueId(), "gui_title");
    }

    // 📌 OPEN GUI
    public static void open(Player p) {

        UUID uuid = p.getUniqueId();

        Inventory inv = Bukkit.createInventory(
                null,
                27,
                getTitle(p)
        );

        int lives = Main.getInstance().getLives(uuid);

        ItemStack heart = new ItemStack(Material.RED_DYE);
        ItemMeta meta = heart.getItemMeta();

        if (meta == null) return;

        // 🧠 Title inside item
        meta.setDisplayName(
                Main.getInstance()
                        .getLanguageManager()
                        .get(uuid, "gui_lives_title")
        );

        // 📜 Lore
        meta.setLore(List.of(
                Main.getInstance()
                        .getLanguageManager()
                        .get(uuid, "gui_current"),
                "§a" + lives
        ));

        heart.setItemMeta(meta);

        inv.setItem(13, heart);

        p.openInventory(inv);
    }
}
