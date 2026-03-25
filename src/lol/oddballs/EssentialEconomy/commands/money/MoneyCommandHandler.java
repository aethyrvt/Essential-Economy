package lol.oddballs.EssentialEconomy.commands.money;

import lol.oddballs.EssentialEconomy.EconomyMain;
import lol.oddballs.EssentialEconomy.utils.ChatManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MoneyCommandHandler implements CommandExecutor, TabCompleter {

    private final EconomyMain plugin;
    private final ChatManager chatManager;
    private final MoneyGiveCommand moneyGiveCommand;
    private final MoneySetCommand moneySetCommand;
    private final MoneyTakeCommand moneyTakeCommand;
    private final MoneyReloadCommand moneyReloadCommand;
    private final MoneyHelpCommand moneyHelpCommand;

    public MoneyCommandHandler(EconomyMain plugin) {
        this.plugin = plugin;
        this.chatManager = plugin.getChatManager();
        this.moneyGiveCommand = new MoneyGiveCommand(plugin);
        this.moneySetCommand = new MoneySetCommand(plugin);
        this.moneyTakeCommand = new MoneyTakeCommand(plugin);
        this.moneyReloadCommand = new MoneyReloadCommand(plugin);
        this.moneyHelpCommand = new MoneyHelpCommand(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            moneyHelpCommand.execute(sender, args);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        switch (subCommand) {
            case "give":
                if (!sender.hasPermission("economy.command.give")) {
                    chatManager.sendConfigMessage(sender, "nopermission");
                    return true;
                }
                moneyGiveCommand.execute(sender, args);
                break;
            case "set":
                if (!sender.hasPermission("economy.command.set")) {
                    chatManager.sendConfigMessage(sender, "nopermission");
                    return true;
                }
                moneySetCommand.execute(sender, args);
                break;
            case "take":
            case "remove":
                if (!sender.hasPermission("economy.command.take")) {
                    chatManager.sendConfigMessage(sender, "nopermission");
                    return true;
                }
                moneyTakeCommand.execute(sender, args);
                break;
            case "reload":
            case "rl":
                if (!sender.hasPermission("economy.command.reload")) {
                    chatManager.sendConfigMessage(sender, "nopermission");
                    return true;
                }
                moneyReloadCommand.execute(sender, args);
                break;
            case "help":
                if (!sender.hasPermission("economy.command.help")) {
                    chatManager.sendConfigMessage(sender, "nopermission");
                    return true;
                }
                moneyHelpCommand.execute(sender, args);
                break;
            default:
                chatManager.sendConfigMessage(sender, "money.invalidSubCommand");
                break;
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> subcommands = Arrays.asList("give", "set", "take", "remove", "reload", "rl", "help");
            return subcommands.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (Arrays.asList("give", "set", "take", "remove").contains(subCommand)) {
                return org.bukkit.Bukkit.getOnlinePlayers().stream()
                        .map(org.bukkit.entity.Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return new ArrayList<>();
    }
}
