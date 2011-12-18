package me.lukas.MultipleSurvivalIslands;

import org.bukkit.ChatColor;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

public class PlayerBreackBlockListener extends BlockListener {

	public void	onBlockBreak(BlockBreakEvent event){
		if(event.getBlock().getLocation().getBlockX() >= -20 && event.getBlock().getLocation().getBlockX() <= 20){
			if(event.getBlock().getLocation().getBlockZ() >= -20 && event.getBlock().getLocation().getBlockZ() <= 20 ){
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "You can not break that block!");
				return;
			}
		}
	}
}
