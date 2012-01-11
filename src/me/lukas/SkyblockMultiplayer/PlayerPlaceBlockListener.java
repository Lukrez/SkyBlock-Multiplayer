package me.lukas.skyblockmultiplayer;

import org.bukkit.ChatColor;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerPlaceBlockListener extends BlockListener {

	SkyblockMultiplayer plugin;

	public PlayerPlaceBlockListener(SkyblockMultiplayer instance) {
		this.plugin = instance;
	}

	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().getWorld().equals(SkyblockMultiplayer.getSkyblockIslands())) { // Prüfe ob der Spieler in der Welt SkyblockMultiplayer ist
			if (event.getBlock().getLocation().getBlockX() >= -20 && event.getBlock().getLocation().getBlockX() <= 20) {
				if (event.getBlock().getLocation().getBlockZ() >= -20 && event.getBlock().getLocation().getBlockZ() <= 20) {
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + "You can not place a block here!");
					return;
				}
			}
		}
	}
}
