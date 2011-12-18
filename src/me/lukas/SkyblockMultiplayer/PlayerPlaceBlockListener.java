package me.lukas.SkyblockMultiplayer;

import org.bukkit.ChatColor;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerPlaceBlockListener extends BlockListener {
	
	public void onBlockPlace(BlockPlaceEvent event){
		if(event.getBlock().getLocation().getBlockX() >= -20 && event.getBlock().getLocation().getBlockX() <= 20){
			if(event.getBlock().getLocation().getBlockZ() >= -20 && event.getBlock().getLocation().getBlockZ() <= 20 ){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You can not place a block here!");
				return;
			}
		}
	}
}
