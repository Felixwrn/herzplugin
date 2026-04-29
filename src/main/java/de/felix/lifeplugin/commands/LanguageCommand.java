package de.felix.lifeplugin.commands;

import de.felix.lifeplugin.lang.LanguageManager;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class LanguageCommand implements CommandExecutor {

    private final LanguageManager lang;

    public LanguageCommand(LanguageManager lang) {
        this.lang = lang;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;
        p.sendMessage("§aUse /langgui");
        return true;
    }
}
