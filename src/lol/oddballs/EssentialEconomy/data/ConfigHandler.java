package lol.oddballs.EssentialEconomy.data;

import lol.oddballs.EssentialEconomy.EconomyMain;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConfigHandler {

    private final EconomyMain plugin;
    private FileConfiguration config;

    private int balanceTopInterval;
    private double startingBalance;
    private Locale locale;
    private boolean customSymbolEnabled;
    private String customSymbol;
    private boolean useMysql;
    private String mysqlHost;
    private int mysqlPort;
    private String mysqlDatabase;
    private String mysqlUsername;
    private String mysqlPassword;
    private Map<String, Integer> suffixes;
    private String prefix;
    private double minimumPayAmount;

    public ConfigHandler(EconomyMain plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        loadConfig();
    }

    public void loadConfig() {
        plugin.reloadConfig(); // Make sure we have the latest version
        this.config = plugin.getConfig();

        balanceTopInterval = config.getInt("BalanceTopTimerInterval", 1200);
        startingBalance = config.getDouble("startingBalance", 100.00);
        
        String localeTag = config.getString("locale", "en-US");
        locale = Locale.forLanguageTag(localeTag);
        if (locale.getLanguage().isEmpty()) {
            plugin.getLogger().warning("Invalid locale in config.yml: " + localeTag);
            locale = null;
        }

        customSymbolEnabled = config.getBoolean("customSymbolEnabled", false);
        customSymbol = config.getString("customSymbol", "");
        
        useMysql = config.getBoolean("mysql.use-mysql", false);
        mysqlHost = config.getString("mysql.host", "localhost");
        mysqlPort = config.getInt("mysql.port", 3306);
        mysqlDatabase = config.getString("mysql.database", "database");
        mysqlUsername = config.getString("mysql.username", "username");
        mysqlPassword = config.getString("mysql.password", "password");

        suffixes = new HashMap<>();
        if (config.isConfigurationSection("suffixes")) {
            for (String key : config.getConfigurationSection("suffixes").getKeys(false)) {
                suffixes.put(key, config.getInt("suffixes." + key));
            }
        }
        
        prefix = config.getString("messages.prefix", "<gray>[<green>EssentialEconomy</green>]</gray> ");
        minimumPayAmount = config.getDouble("minimumPayAmount", 0.01);
    }

    public int getBalanceTopInterval() {
        return balanceTopInterval;
    }

    public double getStartingBalance() {
        return startingBalance;
    }

    public Locale getLocale() {
        return locale;
    }

    public boolean isCustomSymbolEnabled() {
        return customSymbolEnabled;
    }

    public String getCustomSymbol() {
        return customSymbol;
    }

    public boolean isSql() {
        return useMysql;
    }

    public String getMysqlHost() {
        return mysqlHost;
    }

    public int getMysqlPort() {
        return mysqlPort;
    }

    public String getMysqlDatabase() {
        return mysqlDatabase;
    }

    public String getMysqlUsername() {
        return mysqlUsername;
    }

    public String getMysqlPassword() {
        return mysqlPassword;
    }

    public Map<String, Integer> getSuffixes() {
        return suffixes;
    }
    
    public String getPrefix() {
        return prefix;
    }

    public double getMinimumPayAmount() {
        return minimumPayAmount;
    }

    public String getMessage(String path) {
        return config.getString("messages." + path, "Message not found: " + path);
    }

    public List<String> getMessageList(String path) {
        String fullPath = "messages." + path;
        if (config.isList(fullPath)) {
            return config.getStringList(fullPath);
        }
        String single = config.getString(fullPath);
        if (single != null) {
            return Collections.singletonList(single);
        }
        return Collections.singletonList("&cMessage not found: " + path);
    }
}
