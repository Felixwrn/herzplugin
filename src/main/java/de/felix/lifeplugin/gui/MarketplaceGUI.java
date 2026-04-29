package de.felix.lifeplugin.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MarketplaceGUI {
    public static final String TITLE = "§5Marketplace";

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);
        p.openInventory(inv);
    }
}
