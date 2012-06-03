package me.lukas.skyblockmultiplayer;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

public class PlayerInfo2 {

	private String playername;
	private Location islandLocation;
	private Map<String, PlayerInfo2> canBuildByFriends;
	private Map<String, PlayerInfo2> friendsCanBuildHere;
	private Location homeLocation;

	public PlayerInfo2(String playername, Location ownisland) {
		this.playername = playername;
		this.islandLocation = ownisland;
		this.canBuildByFriends = new HashMap<String, PlayerInfo2>();
		this.friendsCanBuildHere = new HashMap<String, PlayerInfo2>();
	}

	public String getName() {
		return this.playername;
	}

	public Location getIslandLocation() {
		return this.islandLocation;
	}
	
	public Location getHomeLocation() {
		return this.homeLocation;
	}

	public HashMap<String, PlayerInfo2> getFriends() {
		return (HashMap<String, PlayerInfo2>) this.friendsCanBuildHere;
	}

	public void setIslandLocation(Location l) {
		this.islandLocation = l;
	}
	
	public void setHomeLocation(Location l) {
		this.homeLocation = l;
	}

	public void addFriendsToOwnIsland(PlayerInfo2 friend) {
		// check if friend is already added
		if (this.friendsCanBuildHere.containsKey(friend.getName()))
			return;
		this.friendsCanBuildHere.put(friend.getName(), friend);
	}

	public void addOwnBuildPermission(PlayerInfo2 friend) {
		// check if friend is already added
		if (this.canBuildByFriends.containsKey(friend.getName()))
			return;
		this.canBuildByFriends.put(friend.getName(), friend);
	}

	public void removeFriendFromOwnIsland(PlayerInfo2 friend) {
		// check if friend is in list
		if (!this.friendsCanBuildHere.containsKey(friend.getName()))
			return;
		this.friendsCanBuildHere.remove(friend.getName());
	}

	public void removeBuildPermissionByFriends(PlayerInfo2 friend) {
		// check if friend is in list
		if (!this.canBuildByFriends.containsKey(friend.getName()))
			return;
		this.canBuildByFriends.remove(friend.getName());
	}

	private boolean isLocationWithInArea(Location block, Location center) {
		if (block == null || center == null)
			return false;

		int islandX = center.getBlockX();
		int islandZ = center.getBlockZ();

		int blockX = block.getBlockX();
		int blockZ = block.getBlockZ();

		int dist = 0;
		if (Settings.build_withProtectedBorder) {
			dist = (Settings.distanceIslands / 2) - 3;
		} else {
			dist = Settings.distanceIslands / 2;
		}

		if (islandX + dist >= blockX && islandX - dist <= blockX) {
			if (islandZ + dist >= blockZ && islandZ - dist <= blockZ) {
				return true;
			}
		}
		return false;
	}

	public boolean checkBuildPermission(Location block) {
		if (this.islandLocation == null || block == null) {
			return false;
		}

		// check if player is on own island
		if (this.isLocationWithInArea(block, this.islandLocation)) {
			return true;
		}

		// check friends
		for (PlayerInfo2 friend : this.canBuildByFriends.values()) {
			if (this.isLocationWithInArea(block, friend.getIslandLocation())) {
				return true;
			}
		}
		return false;
	}

}
