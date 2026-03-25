package lol.oddballs.EssentialEconomy.commands;

import lol.oddballs.EssentialEconomy.EconomyMain;
import lol.oddballs.EssentialEconomy.utils.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class BalanceCommand implements CommandExecutor {

    private final EconomyMain plugin;
    private final ChatManager chatManager;

    public BalanceCommand(EconomyMain plugin) {
        this.plugin = plugin;
        this.chatManager = plugin.getChatManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("economy.command.balance")) {
            chatManager.sendConfigMessage(sender, "nopermission");
            return true;
        }

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                chatManager.sendConfigMessage(sender, "playersOnly");
                return true;
            }
            Player player = (Player) sender;
            if (!plugin.getEco().hasAccount(player.getUniqueId())) {
                chatManager.sendConfigMessage(sender, "balance.noAccount");
                return true;
            }
            double balance = plugin.getEco().getBalance(player.getUniqueId()).getBalance();
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%balance%", EconomyMain.format(balance));
            chatManager.sendConfigMessage(sender, "balance.balance", placeholders);
            return true;
        }

        @SuppressWarnings("deprecation")
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (!target.hasPlayedBefore()) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%player%", args[0]);
            chatManager.sendConfigMessage(sender, "balance.otherNoAccount", placeholders);
            return true;
        }

        double balance = plugin.getEco().getBalance(target.getUniqueId()).getBalance();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%player%", target.getName());
        placeholders.put("%balance%", EconomyMain.format(balance));
        chatManager.sendConfigMessage(sender, "balance.otherBalance", placeholders);
        return true;
    }
}
