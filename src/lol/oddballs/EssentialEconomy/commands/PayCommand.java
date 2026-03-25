package lol.oddballs.EssentialEconomy.commands;

import lol.oddballs.EssentialEconomy.EconomyMain;
import lol.oddballs.EssentialEconomy.utils.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PayCommand implements CommandExecutor, TabCompleter {

    private final EconomyMain plugin;
    private final ChatManager chatManager;

    public PayCommand(EconomyMain plugin) {
        this.plugin = plugin;
        this.chatManager = plugin.getChatManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("economy.command.pay")) {
            chatManager.sendConfigMessage(sender, "nopermission");
            return true;
        }

        if (args.length != 2) {
            chatManager.sendConfigMessage(sender, "pay.usage");
            return true;
        }

        if (!(sender instanceof Player)) {
            chatManager.sendConfigMessage(sender, "playersOnly");
            return true;
        }

        Player player = (Player) sender;

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (!target.hasPlayedBefore()) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%player%", args[0]);
            chatManager.sendConfigMessage(sender, "pay.otherDoesntExist", placeholders);
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            chatManager.sendConfigMessage(sender, "pay.cannotPaySelf");
            return true;
        }

        if (!plugin.getEco().hasAccount(player.getUniqueId())) {
            chatManager.sendConfigMessage(sender, "pay.noAccount");
            return true;
        }

        if (!plugin.getEco().hasAccount(target.getUniqueId())) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%player%", target.getName());
            chatManager.sendConfigMessage(sender, "pay.otherNoAccount", placeholders);
            return true;
        }

        double amount;
        try {
            amount = plugin.getAmountFromString(args[1]);
        } catch (NumberFormatException e) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%amount%", args[1]);
            chatManager.sendConfigMessage(sender, "pay.invalidAmount", placeholders);
            return true;
        }

        double minimum = plugin.getConfigHandler().getMinimumPayAmount();
        if (amount < minimum) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%amount%", args[1]);
            placeholders.put("%minimum%", EconomyMain.format(minimum));
            chatManager.sendConfigMessage(sender, "pay.belowMinimum", placeholders);
            return true;
        }

        if (!plugin.getEco().has(player.getUniqueId(), amount)) {
            chatManager.sendConfigMessage(sender, "pay.insufficientFunds");
            return true;
        }

        plugin.getEco().withdraw(player.getUniqueId(), amount);
        plugin.getEco().deposit(target.getUniqueId(), amount);

        String formattedAmount = EconomyMain.format(amount);

        Map<String, String> senderPlaceholders = new HashMap<>();
        senderPlaceholders.put("%player%", target.getName());
        senderPlaceholders.put("%amount%", formattedAmount);
        chatManager.sendConfigMessage(player, "pay.paid", senderPlaceholders);

        if (target.isOnline()) {
            Player onlineTarget = target.getPlayer();
            if (onlineTarget != null) {
                Map<String, String> targetPlaceholders = new HashMap<>();
                targetPlaceholders.put("%player%", player.getName());
                targetPlaceholders.put("%amount%", formattedAmount);
                chatManager.sendConfigMessage(onlineTarget, "pay.received", targetPlaceholders);
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    .filter(p -> !p.equals(sender))
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
