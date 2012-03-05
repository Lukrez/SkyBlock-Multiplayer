package me.lukas.skyblockmultiplayer;

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
		if (!Data.SKYBLOCK_ONLINE) {
			return;
		}

		if (event.getPlayer().getWorld().equals(SkyBlockMultiplayer.getSkyblockIslands())) { // Prüfe ob der Spieler in SkyblockMultiplayer ist
			if (event.getBlock().getLocation().getBlockX() >= -20 && event.getBlock().getLocation().getBlockX() <= 20) {
				if (event.getBlock().getLocation().getBlockZ() >= -20 && event.getBlock().getLocation().getBlockZ() <= 20) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}
