package me.lukas.skyblockmultiplayer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerPlaceBlockListener implements Listener {

	SkyblockMultiplayer plugin;

	public PlayerPlaceBlockListener(SkyblockMultiplayer instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!Data.SKYBLOCK_ONLINE) {
			return;
		}

		if (event.getPlayer().getWorld().equals(SkyblockMultiplayer.getSkyblockIslands())) { // Prüfe ob der Spieler in der Welt SkyblockMultiplayer ist
			if (event.getBlock().getLocation().getBlockX() >= -20 && event.getBlock().getLocation().getBlockX() <= 20) {
				if (event.getBlock().getLocation().getBlockZ() >= -20 && event.getBlock().getLocation().getBlockZ() <= 20) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}
