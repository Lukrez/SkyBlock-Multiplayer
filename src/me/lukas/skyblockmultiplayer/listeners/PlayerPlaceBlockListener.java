package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.Settings;
import me.lukas.skyblockmultiplayer.Permissions;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerPlaceBlockListener implements Listener {

	SkyBlockMultiplayer plugin;

	public PlayerPlaceBlockListener(SkyBlockMultiplayer instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		//Block b = event.getBlockPlaced();

		if (!Settings.skyBlockOnline) {
			return;
		}

		if (!player.getWorld().equals(SkyBlockMultiplayer.getSkyBlockWorld())) { // Check if player is in world SkyBlockMultiplayer
			return;
		}

		if (Permissions.SKYBLOCK_BUILD.has(player)) {
			event.setBuild(true);
			return;
		}

		/*if (b.getLocation().getBlockX() >= -20 && b.getLocation().getBlockX() <= 20) {
			if (b.getLocation().getBlockZ() >= -20 && b.getLocation().getBlockZ() <= 20) {
				event.setCancelled(true);
				return;
			}
		}

		if (Settings.gameModeSelected == Settings.GAMEMODE.PVP) {
			return;
		}

		if (Settings.gameModeSelected == Settings.GAMEMODE.BUILD) {
			if (Settings.build_withProtectedArea) {
				PlayerInfo pi = Settings.PLAYERS.get(player.getName());
				if (pi == null) {
					event.setCancelled(true);
					return;
				}

				PlayerInfo owner = this.getOwner(b.getLocation());
				if (owner == null) {
					if (this.canPlayerDoThat(pi, b.getLocation())) {
						return;
					}
					event.setCancelled(true);
					return;
				}

				if (owner.getPlayerName().equalsIgnoreCase(player.getName())) {
					return;
				}

				if (owner.getFriends().contains(player.getName())) {
					return;
				}
				event.setCancelled(true);
				return;
			}
		}*/
	}

	/*private boolean canPlayerDoThat(PlayerInfo pi, Location l) {
		int islandX = pi.getIslandLocation().getBlockX();
		int islandZ = pi.getIslandLocation().getBlockZ();

		int blockX = l.getBlockX();
		int blockZ = l.getBlockZ();

		int dist = (Settings.distanceIslands / 2) - 3;

		if (islandX + dist >= blockX && islandX - dist <= blockX) {
			if (islandZ + dist >= blockZ && islandZ - dist <= blockZ) {
				return true;
			}
		}
		return false;
	}

	private PlayerInfo getOwner(Location l) {
		for (PlayerInfo pi : Settings.PLAYERS.values()) {
			int islandX = pi.getIslandLocation().getBlockX();
			int islandZ = pi.getIslandLocation().getBlockZ();

			int blockX = l.getBlockX();
			int blockZ = l.getBlockZ();

			int dist = (Settings.distanceIslands / 2) - 3;

			if (islandX + dist >= blockX && islandX - dist <= blockX) {
				if (islandZ + dist >= blockZ && islandZ - dist <= blockZ) {
					return pi;
				}
			}
		}
		return null;
	}*/
}
