package lol.oddballs.EssentialEconomy.utils;

import lol.oddballs.EssentialEconomy.EconomyMain;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatManager {

    private final EconomyMain plugin;
    private final MiniMessage miniMessage;
    private String prefix;

    private static final Map<Character, String> LEGACY_COLOR_MAP = new HashMap<>();
    static {
        LEGACY_COLOR_MAP.put('0', "black");
        LEGACY_COLOR_MAP.put('1', "dark_blue");
        LEGACY_COLOR_MAP.put('2', "dark_green");
        LEGACY_COLOR_MAP.put('3', "dark_aqua");
        LEGACY_COLOR_MAP.put('4', "dark_red");
        LEGACY_COLOR_MAP.put('5', "dark_purple");
        LEGACY_COLOR_MAP.put('6', "gold");
        LEGACY_COLOR_MAP.put('7', "gray");
        LEGACY_COLOR_MAP.put('8', "dark_gray");
        LEGACY_COLOR_MAP.put('9', "blue");
        LEGACY_COLOR_MAP.put('a', "green");
        LEGACY_COLOR_MAP.put('b', "aqua");
        LEGACY_COLOR_MAP.put('c', "red");
        LEGACY_COLOR_MAP.put('d', "light_purple");
        LEGACY_COLOR_MAP.put('e', "yellow");
        LEGACY_COLOR_MAP.put('f', "white");
        LEGACY_COLOR_MAP.put('l', "bold");
        LEGACY_COLOR_MAP.put('m', "strikethrough");
        LEGACY_COLOR_MAP.put('n', "underlined");
        LEGACY_COLOR_MAP.put('o', "italic");
        LEGACY_COLOR_MAP.put('r', "reset");
    }

    public ChatManager(EconomyMain plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
        this.prefix = plugin.getConfigHandler().getPrefix();
    }

    public void reloadPrefix(String newPrefix) {
        this.prefix = newPrefix;
    }

    /**
     * Send a MiniMessage-formatted string (no prefix). Used for
     * internal/programmatic messages.
     */
    public void sendMessage(CommandSender sender, String message) {
        Component parsed = miniMessage.deserialize(message);
        sender.sendMessage(parsed);
    }

    /**
     * Send config messages (legacy &-codes) with placeholder replacement.
     */
    public void sendConfigMessage(CommandSender sender, String configPath, Map<String, String> placeholders) {
        List<String> messages = plugin.getConfigHandler().getMessageList(configPath);
        for (String msg : messages) {
            msg = msg.replace("%prefix%", prefix);
            if (placeholders != null) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    msg = msg.replace(entry.getKey(), entry.getValue());
                }
            }
            String translated = translateLegacy(msg);
            Component parsed = miniMessage.deserialize(translated);
            sender.sendMessage(parsed);
        }
    }

    /**
     * Send config messages with no placeholders.
     */
    public void sendConfigMessage(CommandSender sender, String configPath) {
        sendConfigMessage(sender, configPath, null);
    }

    public Component parse(String message) {
        return miniMessage.deserialize(message);
    }

    /**
     * Translate legacy &-codes to MiniMessage format.
     */
    public static String translateLegacy(String input) {
        if (input == null)
            return "";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if ((c == '&' || c == '\u00A7') && i + 1 < input.length()) {
                char code = Character.toLowerCase(input.charAt(i + 1));
                String tag = LEGACY_COLOR_MAP.get(code);
                if (tag != null) {
                    result.append('<').append(tag).append('>');
                    i++;
                    continue;
                }
            }
            result.append(c);
        }
        return result.toString();
    }
}
