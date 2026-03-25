package lol.oddballs.EssentialEconomy.commands.money;

import lol.oddballs.EssentialEconomy.EconomyMain;
import lol.oddballs.EssentialEconomy.runnables.BalanceTopRunnable;
import lol.oddballs.EssentialEconomy.utils.ChatManager;
import org.bukkit.command.CommandSender;

public class MoneyReloadCommand {

    private final EconomyMain plugin;
    private final ChatManager chatManager;

    public MoneyReloadCommand(EconomyMain plugin) {
        this.plugin = plugin;
        this.chatManager = plugin.getChatManager();
    }

    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            chatManager.sendConfigMessage(sender, "money.reload.usage");
            return;
        }

        plugin.getConfigHandler().loadConfig();
        chatManager.reloadPrefix(plugin.getConfigHandler().getPrefix());

        plugin.getBalanceTopRunnable().cancel();
        plugin.setBalanceTopRunnable(new BalanceTopRunnable(plugin));
        plugin.getBalanceTopRunnable().start(plugin.getConfigHandler().getBalanceTopInterval());

        chatManager.sendConfigMessage(sender, "money.reload.reloaded");
    }
}
