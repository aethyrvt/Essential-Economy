package lol.oddballs.EssentialEconomy.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import lol.oddballs.EssentialEconomy.EconomyMain;

public class PlayerJoinListener implements Listener {

	private final EconomyMain plugin;

	public PlayerJoinListener(EconomyMain plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		Player player = event.getPlayer();
		
		if (!plugin.getEco().hasAccount(player.getUniqueId())) {
			new BukkitRunnable() {

				@Override
				public void run() {
					plugin.getEco().createAccount(player.getUniqueId());
				}
			}.runTaskAsynchronously(plugin);
		}
		
	}
	
}
