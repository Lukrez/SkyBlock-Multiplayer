package me.lukas.skyblockmultiplayer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class PlayerUseBucketListener implements Listener {

	SkyBlockMultiplayer plugin;

	public PlayerUseBucketListener(SkyBlockMultiplayer instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		if (!Data.SKYBLOCK_ONLINE) {
			return;
		}
		
		if (event.getPlayer().getWorld().equals(SkyBlockMultiplayer.getSkyblockIslands())) { // Prüfe ob der Spieler in der Welt SkyblockMultiplayer ist
			if (event.getPlayer().getLocation().getBlockX() >= -20 && event.getPlayer().getLocation().getBlockX() <= 20) {
				if (event.getPlayer().getLocation().getBlockZ() >= -20 && event.getPlayer().getLocation().getBlockZ() <= 20) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}
