package lol.oddballs.EssentialEconomy.commands;

import lol.oddballs.EssentialEconomy.EconomyMain;
import lol.oddballs.EssentialEconomy.eco.PlayerBalance;
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
import java.util.stream.IntStream;

public class BalanceTopCommand implements CommandExecutor, TabCompleter {

    private final EconomyMain plugin;
    private final ChatManager chatManager;

    public BalanceTopCommand(EconomyMain plugin) {
        this.plugin = plugin;
        this.chatManager = plugin.getChatManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("economy.command.balancetop")) {
            chatManager.sendConfigMessage(sender, "nopermission");
            return true;
        }

        List<PlayerBalance> balances = plugin.getBalanceTopRunnable().getBalanceTop();
        if (balances.isEmpty()) {
            chatManager.sendConfigMessage(sender, "top.noAccounts");
            return true;
        }

        int page = 1;
        if (args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("%top%", args[0]);
                chatManager.sendConfigMessage(sender, "top.invalidTop", placeholders);
                return true;
            }
        }

        if (page <= 0) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%top%", String.valueOf(page));
            chatManager.sendConfigMessage(sender, "top.invalidTop", placeholders);
            return true;
        }

        int pageSize = 10;
        int startIndex = (page - 1) * pageSize;
        int maxPages = (int) Math.ceil((double) balances.size() / pageSize);

        if (page > maxPages) {
            chatManager.sendConfigMessage(sender, "top.notEnoughPlayers");
            return true;
        }

        for (int i = 0; i < pageSize; i++) {
            int currentIndex = startIndex + i;
            if (currentIndex >= balances.size()) {
                break;
            }

            PlayerBalance playerBalance = balances.get(currentIndex);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerBalance.getUUID());
            String name = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown";
            String formattedBalance = EconomyMain.format(playerBalance.getBalance());

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%rank%", String.valueOf(currentIndex + 1));
            placeholders.put("%player%", name);
            placeholders.put("%balance%", formattedBalance);
            chatManager.sendConfigMessage(sender, "top.message", placeholders);
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            int playerRank = -1;
            for (int i = 0; i < balances.size(); i++) {
                if (balances.get(i).getUUID().equals(player.getUniqueId())) {
                    playerRank = i + 1;
                    break;
                }
            }

            if (playerRank != -1 && (playerRank <= startIndex || playerRank > startIndex + pageSize)) {
                double playerBalance = plugin.getEco().getBalance(player.getUniqueId()).getBalance();
                Map<String, String> placeholders = new HashMap<>();
                placeholders.put("%rank%", String.valueOf(playerRank));
                placeholders.put("%player%", player.getName());
                placeholders.put("%balance%", EconomyMain.format(playerBalance));
                chatManager.sendConfigMessage(sender, "top.self", placeholders);
            }
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<PlayerBalance> balances = plugin.getBalanceTopRunnable().getBalanceTop();
            int maxPages = balances.isEmpty() ? 1 : (int) Math.ceil((double) balances.size() / 10);
            String partial = args[0];
            return IntStream.rangeClosed(1, maxPages)
                    .mapToObj(Integer::toString)
                    .filter(s -> s.startsWith(partial))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
