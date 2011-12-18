package me.lukas.MultipleSurvivalIslands;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerInfo {
	private boolean hasIsland;
	private Player player;
	private Location oldlocation;
	
	public PlayerInfo(Player p){
		this.player = p;
		this.hasIsland = false;
		this.oldlocation = p.getLocation();
	}
	
	public void setHasIslandToTrue(){
		this.hasIsland = true;
	}
	public boolean getHasIsland(){
		return this.hasIsland;
	}
	
	public Location getOldPlayerLocation(){
		return this.oldlocation;
	}
	
	public void setPlayer(Player p){
		this.player = p;
	}
	
	public String getPlayerName(){
		return this.player.getName();
	}
	
	public Player getPlayer(){
		return this.player;
	}
}
