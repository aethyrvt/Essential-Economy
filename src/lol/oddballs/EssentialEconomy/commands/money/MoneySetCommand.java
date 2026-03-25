package lol.oddballs.EssentialEconomy.commands.money;

import lol.oddballs.EssentialEconomy.EconomyMain;
import lol.oddballs.EssentialEconomy.utils.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MoneySetCommand {

    private final EconomyMain plugin;
    private final ChatManager chatManager;

    public MoneySetCommand(EconomyMain plugin) {
        this.plugin = plugin;
        this.chatManager = plugin.getChatManager();
    }

    @SuppressWarnings("deprecation")
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 3) {
            chatManager.sendConfigMessage(sender, "money.set.usage");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore()) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%player%", args[1]);
            chatManager.sendConfigMessage(sender, "money.set.otherDoesntExist", placeholders);
            return;
        }

        if (!plugin.getEco().hasAccount(target.getUniqueId())) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%player%", target.getName());
            chatManager.sendConfigMessage(sender, "money.set.otherNoAccount", placeholders);
            return;
        }

        double amount;
        try {
            amount = plugin.getAmountFromString(args[2]);
        } catch (NumberFormatException e) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%amount%", args[2]);
            chatManager.sendConfigMessage(sender, "money.set.invalidAmount", placeholders);
            return;
        }

        if (amount < 0) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%amount%", args[2]);
            chatManager.sendConfigMessage(sender, "money.set.invalidAmount", placeholders);
            return;
        }

        plugin.getEco().set(target.getUniqueId(), amount);
        String formattedAmount = EconomyMain.format(amount);

        Map<String, String> senderPlaceholders = new HashMap<>();
        senderPlaceholders.put("%player%", target.getName());
        senderPlaceholders.put("%balance%", formattedAmount);
        chatManager.sendConfigMessage(sender, "money.set.setter", senderPlaceholders);

        if (target.isOnline()) {
            Map<String, String> targetPlaceholders = new HashMap<>();
            targetPlaceholders.put("%amount%", formattedAmount);
            chatManager.sendConfigMessage((Player) target, "money.set.set", targetPlaceholders);
        }
    }
}
