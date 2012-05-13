package me.lukas.skyblockmultiplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;

public class PlayerInfo2 {

	private String playername;
	private Location ownIslandLocation;
	private Map<String, PlayerInfo2> canBuildByFriends;
	private Map<String, PlayerInfo2> friendsCanBuildHere;

	public PlayerInfo2(String playername, Location ownisland){
		this.playername = playername;
		this.ownIslandLocation = ownisland;
		this.canBuildByFriends = new HashMap<String, PlayerInfo2>();
		this.friendsCanBuildHere = new HashMap<String, PlayerInfo2>();
	}
	
	public String getName(){
		return this.playername;
	}
	
	public Location getLocation() {
		return this.ownIslandLocation;
	}
	
	
	
	public void addFriendToOwnIsland(PlayerInfo2 friend){
		// check if friend is already added
		if (this.friendsCanBuildHere.containsKey(friend.getName())) return;
		this.friendsCanBuildHere.put(friend.getName(), friend);
	}
	
	
	public void addNewBuildPermissionFromFriend(PlayerInfo2 friend){
		// check if friend is already added
		if (this.canBuildByFriends.containsKey(friend.getName())) return;
		this.canBuildByFriends.put(friend.getName(), friend);
	}
	
	public void removeFriendFromOwnIsland(PlayerInfo2 friend){
		// check if friend is in list
		if (!this.friendsCanBuildHere.containsKey(friend.getName())) return;
		this.friendsCanBuildHere.remove(friend.getName());
	}
	
	
	public void removewBuildPermission(PlayerInfo2 friend){
		// check if friend is in list
		if (!this.canBuildByFriends.containsKey(friend.getName())) return;
		this.canBuildByFriends.remove(friend.getName());
	}
	


	private boolean isLocationWithinArea(Location block, Location center) {

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

		if (this.ownIslandLocation == null || block == null) {
			return false;
		}

		// check if player is on own island
		if (this.isLocationWithinArea(block, this.ownIslandLocation)) {
			return true;
		}

		// check friends
		for (PlayerInfo2 friend : this.canBuildByFriends.values()) {
			if (this.isLocationWithinArea(block, friend.getLocation())) {
				return true;
			}
		}
		return false;
	}

}
