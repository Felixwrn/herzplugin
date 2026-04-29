package de.felix.lifeplugin.util;

import org.bukkit.entity.Player;

public class ActionBarUtil {

    public static void send(Player p, String msg) {
        try {
            p.sendActionBar(msg);
        } catch (NoSuchMethodError e) {
            p.spigot().sendMessage(
                net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                new net.md_5.bungee.api.chat.TextComponent(msg)
            );
        }
    }
}
