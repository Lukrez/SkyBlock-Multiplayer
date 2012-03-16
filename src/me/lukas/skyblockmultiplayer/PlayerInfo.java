package me.lukas.skyblockmultiplayer;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean hasIsland;
	private String worldIsland;
	private String playerName;
	private int islandX, islandY, islandZ;
	private String worldOld;
	private int oldX, oldY, oldZ;
	private boolean isDead;
	private ArrayList<String> friends;

	public PlayerInfo(Player p) {
		this.playerName = p.getName();
		this.hasIsland = false;
		this.friends = new ArrayList<String>();
		if (!p.getWorld().equals(SkyBlockMultiplayer.getSkyBlockWorld())) {
			this.setOldPlayerLocation(p.getLocation());
		} else {
			this.setOldPlayerLocation(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
		}
		this.setDead(false);
		SkyBlockMultiplayer.instance.writePlayerFile(this.playerName);
	}

	public void setHasIsland(boolean b) {
		this.hasIsland = b;
		SkyBlockMultiplayer.instance.writePlayerFile(this.playerName);
	}

	public boolean getHasIsland() {
		return this.hasIsland;
	}

	public void setOldPlayerLocation(Location l) {
		this.worldOld = l.getWorld().getName();
		this.oldX = l.getBlockX();
		this.oldY = l.getBlockY();
		this.oldZ = l.getBlockZ();
		SkyBlockMultiplayer.instance.writePlayerFile(this.playerName);
	}

	public Location getOldPlayerLocation() {
		return new Location(Bukkit.getServer().getWorld(this.worldOld), this.oldX, this.oldY, this.oldZ);
	}

	public void setDead(boolean b) {
		this.isDead = b;
		SkyBlockMultiplayer.instance.writePlayerFile(this.playerName);
	}

	public boolean isDead() {
		return this.isDead;
	}

	public void setIslandLocation(Location l) {
		this.worldIsland = l.getWorld().getName();
		this.islandX = l.getBlockX();
		this.islandY = l.getBlockY();
		this.islandZ = l.getBlockZ();
		SkyBlockMultiplayer.instance.writePlayerFile(this.playerName);
	}

	public Location getIslandLocation() {
		return new Location(Bukkit.getServer().getWorld(this.worldIsland), this.islandX, this.islandY, this.islandZ);
	}

	public void addFriend(String playerName) {
		this.friends.add(playerName);
		SkyBlockMultiplayer.instance.writePlayerFile(this.playerName);
	}

	public void removeFriend(String playerName) {
		this.friends.remove(playerName);
		SkyBlockMultiplayer.instance.writePlayerFile(this.playerName);
	}

	public ArrayList<String> getFriends() {
		return this.friends;
	}

	public Player getPlayer() {
		return Bukkit.getServer().getPlayer(this.playerName);
	}

	public String getPlayerName() {
		return this.playerName;
	}
}
