package me.lukas.MultipleSurvivalIslands;

import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerListener;

public class PlayerUseBucketListener extends PlayerListener {
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event){
		if(event.getPlayer().getLocation().getBlockX() >= -20 && event.getPlayer().getLocation().getBlockX() <= 20){
			if(event.getPlayer().getLocation().getBlockZ() >= -20 && event.getPlayer().getLocation().getBlockZ() <= 20 ){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You can not use a bucket here!");
				return;
			}
		}
	}
}
