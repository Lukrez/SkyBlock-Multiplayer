package me.lukas.skyblockmultiplayer;

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

		PlayerInfo pi = Data.PLAYERS.get(player.getName());
		if (pi == null) {
			return;
		}

		if (Data.GAMEMODE_SELECTED == Data.GAMEMODE.PVP || this.plugin.isPlayerOnTower(player)) {
			event.setRespawnLocation(pi.getOldPlayerLocation());
			return;
		}

		if (Data.BUILD_RESPAWNWITHINVENTORY) {
			if (Data.PLAYERINVENTORYS.containsKey(player)) {
				player.getInventory().setContents(Data.PLAYERINVENTORYS.get(player));
			}

			if (Data.PLAYEREQUIPMENTS.containsKey(player)) {
				player.getInventory().setArmorContents(Data.PLAYEREQUIPMENTS.get(player));
			}
		}

		if (player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			if (pi.getIslandLocation() != null) {
				event.setRespawnLocation(pi.getIslandLocation());
			} else {
				event.setRespawnLocation(pi.getOldPlayerLocation());
			}
		}
	}
}
