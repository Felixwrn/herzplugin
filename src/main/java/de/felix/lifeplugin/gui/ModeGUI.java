package de.felix.lifeplugin.gui;

import de.felix.lifeplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ModeGUI {

    private static final String TITLE = "§6§lMode Selector";

    public static String getTitle() {
        return TITLE;
    }

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        // Hardcore
        ItemStack hardcore = new ItemStack(Material.REDSTONE);
        ItemMeta hMeta = hardcore.getItemMeta();

        if (hMeta != null) {
            hMeta.setDisplayName("§c§lHardcore");
            hMeta.setLore(List.of(
                    "§7Lose lives permanently",
                    "§7Kick or ban on 0 lives",
                    "",
                    "§eClick to select"
            ));
            hardcore.setItemMeta(hMeta);
        }

        // Lifesteal
        ItemStack lifesteal = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta lMeta = lifesteal.getItemMeta();

        if (lMeta != null) {
            lMeta.setDisplayName("§a§lLifesteal");
            lMeta.setLore(List.of(
                    "§7Steal lives from players",
                    "",
                    "§eClick to select"
            ));
            lifesteal.setItemMeta(lMeta);
        }

        inv.setItem(11, hardcore);
        inv.setItem(15, lifesteal);

        p.openInventory(inv);
    }
}
