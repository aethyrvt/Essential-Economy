package lol.oddballs.EssentialEconomy.commands.money;

import lol.oddballs.EssentialEconomy.EconomyMain;
import lol.oddballs.EssentialEconomy.utils.ChatManager;
import org.bukkit.command.CommandSender;

public class MoneyHelpCommand {

    private final ChatManager chatManager;

    public MoneyHelpCommand(EconomyMain plugin) {
        this.chatManager = plugin.getChatManager();
    }

    public void execute(CommandSender sender, String[] args) {
        chatManager.sendConfigMessage(sender, "money.help.message");
    }
}
