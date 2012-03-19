package me.lukas.skyblockmultiplayer;

import org.bukkit.Location;

public class Old_PlayerInfo {
	private boolean hasIsland;
	private Location islandLocation;
	private Location oldlocation;
	private boolean isDead;
	private String playerName;

	public Old_PlayerInfo(String playerName) {
		this.playerName = playerName;
		this.hasIsland = false;
		this.oldlocation = null;
		this.setDead(false);
	}

	public void setHasIsland(boolean b) {
		this.hasIsland = b;
	}

	public boolean getHasIsland() {
		return this.hasIsland;
	}

	public void setOldPlayerLocation(Location l) {
		this.oldlocation = l;
	}

	public Location getOldPlayerLocation() {
		return this.oldlocation;
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public void setDead(boolean b) {
		this.isDead = b;
	}

	public boolean isDead() {
		return this.isDead;
	}

	public void setIslandLocation(Location l) {
		this.islandLocation = l;
	}

	public Location getIslandLocation() {
		return this.islandLocation;
	}
}
