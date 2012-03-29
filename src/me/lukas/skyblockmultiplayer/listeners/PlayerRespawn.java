package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.Settings;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawn implements Listener {

	private SkyBlockMultiplayer plugin;

	public PlayerRespawn(SkyBlockMultiplayer instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();

		if (!player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			return;
		}

		PlayerInfo pi = Settings.players.get(player.getName());
		if (pi == null) {
			event.setRespawnLocation(player.getWorld().getSpawnLocation());
			return;
		}

		if (Settings.gameModeSelected == Settings.GAMEMODE.PVP || this.plugin.playerIsOnTower(player) || pi.getIslandLocation() == null) {
			/*player.getInventory().setContents(pi.getOldInventory());
			player.getInventory().setArmorContents(pi.getOldArmor());
			player.setExp(pi.getOldExp());
			player.setLevel(pi.getOldLevel());
			player.setFoodLevel(pi.getOldFood());
			player.setHealth(player.getMaxHealth());
			this.plugin.writePlayerFile(player.getName(), pi);*/

			event.setRespawnLocation(player.getWorld().getSpawnLocation());
			return;
		}

		if (Settings.gameModeSelected == Settings.GAMEMODE.BUILD && Settings.build_respawnWithInventory) {
			if (!this.plugin.playerIsOnTower(player) && pi.getIslandLocation() != null) {
				player.getInventory().setContents(pi.getIslandInventory());
				player.getInventory().setArmorContents(pi.getIslandArmor());
				player.setExp(pi.getIslandExp());
				player.setLevel(pi.getIslandLevel());
				player.setFoodLevel(20);
				player.setHealth(player.getMaxHealth());

				this.plugin.writePlayerFile(player.getName(), pi);

				if (pi.getHomeLocation() == null) {
					event.setRespawnLocation(pi.getIslandLocation());
				} else {
					event.setRespawnLocation(pi.getHomeLocation());
				}
				return;
			}
		}

		if (Settings.gameModeSelected == Settings.GAMEMODE.BUILD) {
			if (!this.plugin.playerIsOnTower(player)) {
				if (pi.getHomeLocation() == null) {
					event.setRespawnLocation(pi.getIslandLocation());
				} else {
					event.setRespawnLocation(pi.getHomeLocation());
				}
			}
		}
	}
}
