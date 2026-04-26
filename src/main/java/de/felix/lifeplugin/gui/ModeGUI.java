package de.felix.lifeplugin.gui;

import de.felix.lifeplugin.Main;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
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

        String current = Main.getInstance().getConfig().getString("mode", "LIFESTEAL");

        // 🟪 Rahmen (moderner Look)
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fMeta = filler.getItemMeta();
        if (fMeta != null) {
            fMeta.setDisplayName(" ");
            filler.setItemMeta(fMeta);
        }

        for (int i = 0; i < 27; i++) {
            if (i < 9 || i > 17 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, filler);
            }
        }

        // ❤️ HARDCORE
        boolean hardcoreCurrent = current.equalsIgnoreCase("HARDCORE");

        ItemStack hardcore = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta hMeta = hardcore.getItemMeta();

        if (hMeta != null) {

            hMeta.setDisplayName(
                    hardcoreCurrent
                            ? "§6★ §c§lHardcore"
                            : "§c§lHardcore"
            );

            if (hardcoreCurrent) {
                hMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
                hMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            hMeta.setLore(List.of(
                    "§7Lose lives permanently",
                    "§7Kick or ban on 0 lives",
                    "",
                    hardcoreCurrent
                            ? "§a✔ Currently active"
                            : "§eClick to activate"
            ));

            hardcore.setItemMeta(hMeta);
        }

        // 💚 LIFESTEAL
        boolean lifestealCurrent = current.equalsIgnoreCase("LIFESTEAL");

        ItemStack lifesteal = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta lMeta = lifesteal.getItemMeta();

        if (lMeta != null) {

            lMeta.setDisplayName(
                    lifestealCurrent
                            ? "§6★ §a§lLifesteal"
                            : "§a§lLifesteal"
            );

            if (lifestealCurrent) {
                lMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
                lMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            lMeta.setLore(List.of(
                    "§7Steal lives from players",
                    "",
                    lifestealCurrent
                            ? "§a✔ Currently active"
                            : "§eClick to activate"
            ));

            lifesteal.setItemMeta(lMeta);
        }

        inv.setItem(11, hardcore);
        inv.setItem(15, lifesteal);

        p.openInventory(inv);
    }
}
