package de.felix.lifeplugin.gui;

import de.felix.lifeplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ModeGUI {

    public static String getTitle(Player p) {
        return Main.getInstance().getLanguageManager()
                .get(p.getUniqueId(), "modegui_title");
    }

    public static void open(Player p) {

        var lm = Main.getInstance().getLanguageManager();
        var uuid = p.getUniqueId();

        Inventory inv = Bukkit.createInventory(null, 27, getTitle(p));

        String current = Main.getInstance().getConfig().getString("mode", "LIFESTEAL");

        // Hardcore
        boolean hc = current.equalsIgnoreCase("HARDCORE");

        ItemStack hardcore = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta h = hardcore.getItemMeta();

        if (h != null) {

            h.setDisplayName(
                    hc
                            ? "§6★ " + lm.get(uuid, "mode_hardcore")
                            : lm.get(uuid, "mode_hardcore")
            );

            if (hc) {
                h.addEnchant(Enchantment.UNBREAKING, 1, true);
                h.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            h.setLore(List.of(
                    hc
                            ? lm.get(uuid, "mode_selected")
                            : lm.get(uuid, "mode_click")
            ));

            hardcore.setItemMeta(h);
        }

        // Lifesteal
        boolean ls = current.equalsIgnoreCase("LIFESTEAL");

        ItemStack lifesteal = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta l = lifesteal.getItemMeta();

        if (l != null) {

            l.setDisplayName(
                    ls
                            ? "§6★ " + lm.get(uuid, "mode_lifesteal")
                            : lm.get(uuid, "mode_lifesteal")
            );

            if (ls) {
                l.addEnchant(Enchantment.UNBREAKING, 1, true);
                l.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            l.setLore(List.of(
                    ls
                            ? lm.get(uuid, "mode_selected")
                            : lm.get(uuid, "mode_click")
            ));

            lifesteal.setItemMeta(l);
        }

        inv.setItem(11, hardcore);
        inv.setItem(15, lifesteal);

        p.openInventory(inv);
    }
}
