package lol.oddballs.EssentialEconomy.utils;

import lol.oddballs.EssentialEconomy.EconomyMain;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatManager {

    private final MiniMessage miniMessage;
    private final String prefix;

    public ChatManager(EconomyMain plugin) {
        this.miniMessage = MiniMessage.miniMessage();
        this.prefix = plugin.getConfig().getString("prefix", "<gray>[<green>EssentialEconomy</green>]</gray> ");
    }

    public void sendMessage(CommandSender sender, String message) {
        Component parsed = miniMessage.deserialize(prefix + message);
        if (sender instanceof Player) {
            sender.sendMessage(parsed);
        } else {
            sender.sendMessage(parsed);
        }
    }

    public void sendMessage(CommandSender sender, String message, Component... components) {
        Component parsed = miniMessage.deserialize(prefix + message);
        if (sender instanceof Player) {
            sender.sendMessage(parsed.append(Component.join(Component.empty(), components)));
        } else {
            sender.sendMessage(parsed.append(Component.join(Component.empty(), components)));
        }
    }

    public Component parse(String message) {
        return miniMessage.deserialize(message);
    }
}
