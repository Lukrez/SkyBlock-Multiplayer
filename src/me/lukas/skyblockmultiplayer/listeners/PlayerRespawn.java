package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.Settings;
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

		PlayerInfo pi = Settings.players.get(player.getName());
		if (pi == null) {
			event.setRespawnLocation(player.getWorld().getSpawnLocation());
			return;
		}

		if (pi.getIslandLocation() == null) {
			player.getInventory().setContents(pi.getContentsInventory());
			player.getInventory().setArmorContents(pi.getContentsArmor());
			return;
		}

		if (Settings.gameModeSelected == Settings.GAMEMODE.PVP || this.plugin.playerIsOnTower(player)) {
			event.setRespawnLocation(player.getWorld().getSpawnLocation());
			return;
		}

		if (Settings.gameModeSelected == Settings.GAMEMODE.BUILD && Settings.build_respawnWithInventory) {
			player.getInventory().setContents(pi.getContentsInventory());
			player.getInventory().setArmorContents(pi.getContentsArmor());
		}

		if (player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			if (pi.getIslandLocation() != null) {
				event.setRespawnLocation(pi.getIslandLocation());
			} else {
				event.setRespawnLocation(player.getWorld().getSpawnLocation());
			}
		}
	}
}
