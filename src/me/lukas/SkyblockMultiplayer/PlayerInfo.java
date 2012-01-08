package me.lukas.skyblockmultiplayer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerInfo {
	private boolean hasIsland;
	private Location islandLocation;
	private Player player;
	private Location oldlocation;
	private boolean isDead;

	public PlayerInfo(Player p) {
		this.player = p;
		this.hasIsland = false;
		this.oldlocation = p.getLocation();
		this.isDead = false;
	}

	public void setHasIslandToTrue() {
		this.hasIsland = true;
	}

	public void setHasIslandToFalse() {
		this.hasIsland = false;
	}

	public boolean getHasIsland() {
		return this.hasIsland;
	}

	public Location getOldPlayerLocation() {
		return this.oldlocation;
	}

	public void setOldPlayerLocation(Location l) {
		this.oldlocation = l;
	}

	public Player getPlayer() {
		return this.player;
	}

	public void setPlayer(Player p) {
		this.player = p;
	}

	public String getPlayerName() {
		return this.player.getName();
	}

	public void setDeadToTrue() {
		this.isDead = true;
	}

	public void setDeadToFalse() {
		this.isDead = false;
	}

	public boolean getDead() {
		return this.isDead;
	}

	public Location getIslandLocation() {
		return this.islandLocation;
	}

	public void setIslandLocation(Location l) {
		this.islandLocation = l;
	}
}
