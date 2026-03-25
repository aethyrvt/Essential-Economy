package lol.oddballs.EssentialEconomy.eco;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lol.oddballs.EssentialEconomy.EconomyMain;
import lol.oddballs.EssentialEconomy.data.YamlData;

public class YamlEconomy implements Economy {
	
	private final EconomyMain plugin;
	
	public YamlEconomy(EconomyMain plugin) {
		this.plugin = plugin;
		
		Path dataDir = Paths.get(plugin.getPath() + "/data/");
		if (!Files.exists(dataDir)) {
			try {
				Files.createDirectory(dataDir);
			} catch (IOException e) {
				plugin.warn("There was an error creating the data directory.");
	            return;
			}
		}
		
	}

	@Override
	public boolean createAccount(UUID uuid) {
		set(uuid, plugin.getConfigHandler().getStartingBalance());
		return true;
	}

	@Override
	public boolean hasAccount(UUID uuid) {
		File file = new File(plugin.getPath() + "/data/" + uuid.toString() + ".yml");
		return file.exists();
	}

	@Override
	public boolean delete(UUID uuid) {
		File islandFile = new File(plugin.getPath() + "/data/" + uuid.toString() + ".yml");
		islandFile.delete();
		return true;
	}

	@Override
	public boolean withdraw(UUID uuid, double amount) {
		return set(uuid, getBalance(uuid).getBalance() - amount);
	}

	@Override
	public boolean deposit(UUID uuid, double amount) {
		return set(uuid, getBalance(uuid).getBalance() + amount);
	}

	@Override
	public boolean set(UUID uuid, double amount) {
		if (amount < 0)
			return false;
		YamlData data = new YamlData(uuid.toString() + ".yml", plugin.getPath() + "/data");
		data.getConfig().set("UUID", uuid.toString());
		data.getConfig().set("Balance", amount);
		data.saveConfig();
		return true;
	}

	@Override
	public boolean has(UUID uuid, double amount) {
		return getBalance(uuid).getBalance() >= amount;
	}

	@Override
	public PlayerBalance getBalance(UUID uuid) {
		try {
			YamlData data = new YamlData(uuid.toString() + ".yml", plugin.getPath() + "/data");
			double balance = data.getConfig().getDouble("Balance");
			return new PlayerBalance(uuid, balance);
		} catch (Exception e) {
			return new PlayerBalance(uuid, 0);
		}
	}

	@Override
	public List<PlayerBalance> getPlayers() {
		List<PlayerBalance> playerData = new ArrayList<PlayerBalance>();
		File[] files = new File(plugin.getPath() + "/data").listFiles();
		if (files == null) return playerData;
		for (File file : files) {
			playerData.add(getBalance(UUID.fromString(file.getName().replace(".yml", ""))));
		}
		return playerData;
	}

}
