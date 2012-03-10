package me.lukas.skyblockmultiplayer;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerInfo {
	private boolean hasIsland;
	private Location islandLocation;
	private Player player;
	private Location oldlocation;
	private boolean isDead;
	private SkyBlockMultiplayer plugin;

	public PlayerInfo(Player p, SkyBlockMultiplayer instance) {
		this.plugin = instance;
		this.player = p;
		this.hasIsland = false;
		this.oldlocation = null;
		if (!p.getWorld().equals(SkyBlockMultiplayer.getSkyblockIslands())) {
			this.setOldPlayerLocation(p.getLocation());
		} else {
			this.setOldPlayerLocation(this.plugin.getServer().getWorlds().get(0).getSpawnLocation());
		}
		this.setDead(false);
	}

	public void setHasIsland(boolean b) {
		this.hasIsland = b;
		this.plugin.setStringbyPath(this.plugin.configPlayer, this.plugin.filePlayer, "players." + this.getPlayerName() + ".hasIsland", b);
	}

	public boolean getHasIsland() {
		return this.hasIsland;
	}

	public void setOldPlayerLocation(Location l) {
		this.oldlocation = l;
		this.plugin.setStringbyPath(this.plugin.configPlayer, this.plugin.filePlayer, "players." + this.getPlayerName() + ".oldLocation", this.plugin.getStringLocation(l));
	}

	public Location getOldPlayerLocation() {
		return this.oldlocation;
	}

	public Player getPlayer() {
		return this.player;
	}

	public String getPlayerName() {
		return this.player.getName();
	}

	public void setDead(boolean b) {
		this.isDead = b;
		this.plugin.setStringbyPath(this.plugin.configPlayer, this.plugin.filePlayer, "players." + this.getPlayerName() + ".isDead", b);
	}

	public boolean isDead() {
		return this.isDead;
	}

	public void setIslandLocation(Location l) {
		this.islandLocation = l;
		this.plugin.setStringbyPath(this.plugin.configPlayer, this.plugin.filePlayer, "players." + this.getPlayerName() + ".islandLocation", this.plugin.getStringLocation(l));
	}

	public Location getIslandLocation() {
		return this.islandLocation;
	}
}
