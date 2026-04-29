package de.felix.lifeplugin.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class ModeGUI {
    public static final String TITLE = "§bMode";

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);
        p.openInventory(inv);
    }
}
