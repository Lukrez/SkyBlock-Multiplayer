package me.lukas.skyblockmultiplayer;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerBreackBlockListener implements Listener {

	SkyBlockMultiplayer plugin;

	public PlayerBreackBlockListener(SkyBlockMultiplayer instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block b = event.getBlock();

		if (!Data.SKYBLOCK_ONLINE) {
			return;
		}

		if (!player.getWorld().equals(SkyBlockMultiplayer.getSkyBlockWorld())) { // Check if player is in world SkyBlockMultiplayer
			return;
		}

		if (Permissions.SKYBLOCK_BUILD.has(player)) {
			return;
		}

		if (b.getLocation().getBlockX() >= -20 && b.getLocation().getBlockX() <= 20) {
			if (b.getLocation().getBlockZ() >= -20 && b.getLocation().getBlockZ() <= 20) {
				event.setCancelled(true);
				return;
			}
		}

		if (Data.GAMEMODE_SELECTED == Data.GAMEMODE.PVP) {
			return;
		}

		if (Data.GAMEMODE_SELECTED == Data.GAMEMODE.BUILD) {
			if (Data.BUILD_WITHPROTECTEDAREA) {
				PlayerInfo pi = Data.PLAYERS.get(player.getName());
				if (pi == null) {
					return;
				}

				PlayerInfo pOwner = this.getOwner(b.getLocation());
				if (pOwner == null) {
					if (this.canPlayerDoThat(pi, b.getLocation())) {
						return;
					}
					event.setCancelled(true);
					return;
				}
				if (this.canPlayerDoThat(pi, b.getLocation()) || pOwner.getFriends().contains(player.getName())) {
					return;
				}
				event.setCancelled(true);
				return;
			}
		}
	}

	private boolean canPlayerDoThat(PlayerInfo pi, Location l) {
		int islandX = pi.getIslandLocation().getBlockX();
		int islandZ = pi.getIslandLocation().getBlockZ();

		int blockX = l.getBlockX();
		int blockZ = l.getBlockZ();

		int dist = Data.ISLAND_DISTANCE / 2 - 3;

		if (islandX + dist >= blockX && islandX - dist <= blockX) {
			if (islandZ + dist >= blockZ && islandZ - dist <= blockZ) {
				return true;
			}
		}
		return false;
	}

	private PlayerInfo getOwner(Location l) {
		for (PlayerInfo pi : Data.PLAYERS.values()) {
			int islandX = pi.getIslandLocation().getBlockX();
			int islandZ = pi.getIslandLocation().getBlockZ();

			int blockX = l.getBlockX();
			int blockZ = l.getBlockZ();

			int dist = (Data.ISLAND_DISTANCE / 2) - 3;

			if (islandX + dist >= blockX && islandX - dist <= blockX) {
				if (islandZ + dist >= blockZ && islandZ - dist <= blockZ) {
					return pi;
				}
			}
		}
		return null;
	}
}
