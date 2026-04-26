package de.felix.lifeplugin.gui;

import de.felix.lifeplugin.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.io.File;
import java.net.URL;
import java.util.*;

public class LanguageGUI {

    private static final String TITLE = "§6§lLanguage Selection";

    public static String getTitle() {
        return TITLE;
    }

    public static void open(Player p) {

        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        String current = Main.getInstance()
                .getLanguageManager()
                .getLanguage(p.getUniqueId());

        // 🔲 Rahmen
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

        // 📍 Slots
        int[] slots = {10,11,12,13,14,15,16};
        int index = 0;

        // 🌍 Flag Textures
        Map<String, String> flags = new HashMap<>();
        flags.put("de", "ddecc08cf7c1b666bc554d7f7325cd890b875b32f046e8994e37ff4fdccd");
        flags.put("en", "ec9991997832ccabe97dc20ecd144eeb69859523dc14a19d2ba947629e7");
        flags.put("fr", "76521a6aeb35737d07c37c28e84698669b4832769af9ccbcc2af36fb");
        flags.put("es", "734698f24581fd33d6728648c6e6d8762bf5724e67f4929966cf4");
        flags.put("it", "c07559ce8625281762f29669e99cc5b63845482fe");

        for (String lang : getAllLanguages()) {

            boolean installed = new File(
                    Main.getInstance().getDataFolder(),
                    "lang/" + lang + ".json"
            ).exists();

            boolean isCurrent = lang.equalsIgnoreCase(current);

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) head.getItemMeta();

            if (meta == null) continue;

            // 🌍 Flag setzen
            if (flags.containsKey(lang)) {
                try {
                    PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());

                    profile.getTextures().setSkin(
                            new URL("http://textures.minecraft.net/texture/" + flags.get(lang))
                    );

                    meta.setOwnerProfile(profile);

                } catch (Exception e) {
                    Bukkit.getLogger().warning("Failed to load flag for " + lang);
                }
            }

            // ⭐ Name + Highlight
            meta.setDisplayName(
                    isCurrent
                            ? "§6★ §f§l" + lang.toUpperCase()
                            : "§f§l" + lang.toUpperCase()
            );

            // ✨ Glint
            if (isCurrent) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            // 📄 Lore
            meta.setLore(List.of(
                    "§7Status:",
                    isCurrent
                            ? "§a✔ Selected"
                            : installed
                                ? "§e✔ Installed"
                                : "§c✖ Not installed",
                    "",
                    installed
                            ? "§eClick to select"
                            : "§cClick to download"
            ));

            head.setItemMeta(meta);

            if (index < slots.length) {
                inv.setItem(slots[index++], head);
            }
        }

        p.openInventory(inv);
    }

    private static List<String> getAllLanguages() {

        List<String> langs = new ArrayList<>();
        langs.add("de");
        langs.add("en");

        if (Main.getInstance().getLangConfig().getConfigurationSection("languages") != null) {
            langs.addAll(
                    Main.getInstance()
                            .getLangConfig()
                            .getConfigurationSection("languages")
                            .getKeys(false)
            );
        }

        return langs;
    }
}
