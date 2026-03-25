package lol.oddballs.EssentialEconomy;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import lol.oddballs.EssentialEconomy.utils.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import lol.oddballs.EssentialEconomy.commands.BalanceCommand;
import lol.oddballs.EssentialEconomy.commands.BalanceTopCommand;
import lol.oddballs.EssentialEconomy.commands.PayCommand;
import lol.oddballs.EssentialEconomy.commands.money.MoneyCommandHandler;
import lol.oddballs.EssentialEconomy.data.ConfigHandler;
import lol.oddballs.EssentialEconomy.eco.Economy;
import lol.oddballs.EssentialEconomy.eco.SQLEconomy;
import lol.oddballs.EssentialEconomy.eco.VaultImpl;
import lol.oddballs.EssentialEconomy.eco.YamlEconomy;
import lol.oddballs.EssentialEconomy.listeners.PlayerJoinListener;
import lol.oddballs.EssentialEconomy.runnables.BalanceTopRunnable;

public class EconomyMain extends JavaPlugin {

	private MoneyCommandHandler moneyCommandHandler;
	private BalanceTopRunnable balanceTopRunnable;
	private VaultImpl vaultImpl;
	private Economy eco;
	private ChatManager chatManager;
	private ConfigHandler configHandler;
	private Map<String, String> sqlColumns = new HashMap<>();

	private static EconomyMain instance;

	@Override
	public void onEnable() {

		instance = this;
		saveDefaultConfig();
		
		configHandler = new ConfigHandler(this);
		chatManager = new ChatManager(this);

		addSqlColumns();

		if (configHandler.isSql()) {
			eco = new SQLEconomy(this);
		}
		else {
			eco = new YamlEconomy(this);
		}

		vaultImpl = new VaultImpl(this);
		
		balanceTopRunnable = new BalanceTopRunnable(this);
		balanceTopRunnable.start(configHandler.getBalanceTopInterval());
		
		if (!setupEconomy()) {
			disable("<red>Economy couldn't be registered, Vault plugin is missing!");
			return;
		}
		this.getLogger().info("Vault found, Economy has been registered.");

		if (configHandler.getLocale() == null) {
			disable("<red>Invalid locale configured! Change it in your config.yml");
			return;
		}

		moneyCommandHandler = new MoneyCommandHandler(this);
		this.getCommand("money").setExecutor(moneyCommandHandler);
		this.getCommand("money").setTabCompleter(moneyCommandHandler);
		
		BalanceCommand balanceCommand = new BalanceCommand(this);
		this.getCommand("balance").setExecutor(balanceCommand);
		this.getCommand("balance").setTabCompleter(balanceCommand);

		PayCommand payCommand = new PayCommand(this);
		this.getCommand("pay").setExecutor(payCommand);
		this.getCommand("pay").setTabCompleter(payCommand);

		BalanceTopCommand balanceTopCommand = new BalanceTopCommand(this);
		this.getCommand("balancetop").setExecutor(balanceTopCommand);
		this.getCommand("balancetop").setTabCompleter(balanceTopCommand);
		
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
		


	}

	@Override
	public void onDisable() {

	}
	
	public static EconomyMain getInstance() {
		return instance;
	}

	private void addSqlColumns() {
		sqlColumns.put("Balance", "DECIMAL(65, 2) NOT NULL DEFAULT " + configHandler.getStartingBalance());
	}

	public ChatManager getChatManager() {
		return chatManager;
	}

	public ConfigHandler getConfigHandler() {
		return configHandler;
	}
	
	public String getPath() {
		return getDataFolder().getAbsolutePath();
	}
	
	public void warn(String message) {
		chatManager.sendMessage(Bukkit.getConsoleSender(), "<yellow>[WARNING] " + message);
	}
	
	public void disable(String message) {
		warn(message);
		Bukkit.getPluginManager().disablePlugin(this);
	}

	private boolean setupEconomy() {
		if (this.getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		this.getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, vaultImpl, this,
				ServicePriority.Highest);
		return true;
	}
	
	public double getAmountFromString(String string) {
		int mult = 0;
		for (Map.Entry<String, Integer> suffix : configHandler.getSuffixes().entrySet()) {
			if (string.endsWith(suffix.getKey())) {
				string = string.substring(0, string.length() - 1);
				mult = suffix.getValue();
			}
		}
		double pow = Math.pow(10, mult);
		return Math.round(Double.valueOf(string) * (100.0 * pow)) / (100.0 * pow) * pow;
	}
	

	@SuppressWarnings("deprecation")
	public ArrayList<OfflinePlayer> getPlayersFromString(CommandSender sender, String name) {

		OfflinePlayer player = Bukkit.getOfflinePlayer(name);
		
		ArrayList<OfflinePlayer> players = new ArrayList<OfflinePlayer>();
		
		if (player == null && !name.equals("@a")) {
			
			return players;
			
		}
		
		if (name.equals("@a")) {
			
			players.addAll(new ArrayList<OfflinePlayer>(Bukkit.getOnlinePlayers()));

			if (sender instanceof OfflinePlayer) {
				
				players.remove((OfflinePlayer) sender);
				
			}
			
			return players;
		}
		
		return new ArrayList<OfflinePlayer>(Arrays.asList(player));
		
	}
	
	public static String format(double amount) {
		Locale locale = getInstance().configHandler.getLocale();
		NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
		String formatted = numberFormat.format(amount).replace("&nbsp", " ").replace("\u00A0", " ");
		if (getInstance().configHandler.isCustomSymbolEnabled()) {
			formatted = formatted.replace(Currency.getInstance(locale).getSymbol(locale), getInstance().configHandler.getCustomSymbol());
		}
		return formatted;
	}

	public Map<String, String> getSQLColumns() {
		return sqlColumns;
	}

	public BalanceTopRunnable getBalanceTopRunnable() {
		return balanceTopRunnable;
	}

	public void setBalanceTopRunnable(BalanceTopRunnable balanceTopRunnable) {
		this.balanceTopRunnable = balanceTopRunnable;
	}

	public MoneyCommandHandler getMoneyCommandHandler() {
		return moneyCommandHandler;
	}

	public Economy getEco() {
		return eco;
	}

}
