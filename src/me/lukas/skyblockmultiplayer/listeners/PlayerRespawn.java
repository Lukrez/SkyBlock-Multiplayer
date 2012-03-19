package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.Data;
import me.lukas.skyblockmultiplayer.PlayerInfo;
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

		PlayerInfo pi = Data.PLAYERS.get(player.getName());
		if (pi == null) {
			return;
		}

		if (Data.GAMEMODE_SELECTED == Data.GAMEMODE.PVP || this.plugin.isPlayerOnTower(player)) {
			event.setRespawnLocation(pi.getOldPlayerLocation());
			return;
		}

		if (Data.GAMEMODE_SELECTED == Data.GAMEMODE.BUILD && Data.BUILD_RESPAWNWITHINVENTORY) {
			if (pi.getContentsInventory() != null) {
				player.getInventory().setContents(pi.getContentsInventory());
			}

			if (pi.getContentsArmor() != null) {
				player.getInventory().setArmorContents(pi.getContentsArmor());
			}
		}

		if (player.getWorld().equals(SkyBlockMultiplayer.getSkyBlockWorld())) {
			if (pi.getIslandLocation() != null) {
				event.setRespawnLocation(pi.getIslandLocation());
			} else {
				if (pi.getOldPlayerLocation() != null) {
					event.setRespawnLocation(pi.getOldPlayerLocation());
				} else {
					event.setRespawnLocation(this.plugin.getServer().getWorlds().get(0).getSpawnLocation());
				}
			}
		}
	}
}
