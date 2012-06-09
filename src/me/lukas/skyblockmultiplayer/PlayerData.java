package me.lukas.skyblockmultiplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import sun.security.krb5.Config;

public class PlayerData {

	private String playerName;
	private boolean hasIsland;
	private boolean isDead;
	private boolean isOnIsland;
	private int livesLeft;
	private int islandsLeft;
	private Location homeLocation;

	private Map<String, PlayerData> canBuildByFriends;
	private Map<String, PlayerData> friendsCanBuildHere;

	private Location oldLocation;
	private ItemStack[] oldInventory;
	private ItemStack[] oldArmor;
	private int oldFood;
	private int oldHealth;
	private float oldExp;
	private int oldLevel;

	private Location islandLocation;
	private ItemStack[] islandInventory;
	private ItemStack[] islandArmor;
	private int islandFood;
	private int islandHealth;
	private float islandExp;
	private int islandLevel;

	public PlayerData(Player player) {
		// Set Default Values
		this.playerName = player.getName();
		this.hasIsland = false;
		this.isOnIsland = false;
		this.isDead = false;
		this.homeLocation = null;
		this.livesLeft = Settings.pvp_livesPerIsland;
		this.islandsLeft = Settings.pvp_islandsPerPlayer;
		this.oldHealth = player.getMaxHealth();
		this.islandHealth = player.getMaxHealth();
		this.oldFood = 20;
		this.islandFood = 20;
		this.canBuildByFriends = new HashMap<String, PlayerData>();
		this.friendsCanBuildHere = new HashMap<String, PlayerData>();
		this.islandInventory = new ItemStack[36];
		this.oldInventory = new ItemStack[36];
		this.islandArmor = new ItemStack[4];
		this.oldArmor = new ItemStack[4];
	}

	public PlayerData() {
	};

	public void updateSQLPartialData() {
		SQLInstructions.writePartialPlayerData(this);
	}

	public void setOldWorldValues(Player player) {
		if (player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()))
			return;
		this.oldLocation = player.getLocation();
		this.oldInventory = player.getInventory().getContents();
		this.oldArmor = player.getInventory().getArmorContents();
		this.oldFood = player.getFoodLevel();
		this.oldHealth = player.getHealth();
		this.oldExp = player.getExp();
		this.oldLevel = player.getLevel();
		SQLInstructions.writeOldWorldData(this);

	}

	public void setIslandValues(Player player) {
		// this.islandLocation = player.getLocation();
		this.islandInventory = player.getInventory().getContents();
		this.islandArmor = player.getInventory().getArmorContents();
		this.islandFood = player.getFoodLevel();
		this.islandHealth = player.getHealth();
		this.islandExp = player.getExp();
		this.islandLevel = player.getLevel();
		SQLInstructions.writeIslandData(this);
	}

	public void setPlayerName(String name) {
		this.playerName = name;
	}

	public void setHasIsland(boolean b) {
		this.hasIsland = b;
	}

	public void setDeathStatus(boolean isDead) {
		this.isDead = isDead;
	}

	public void setIsOnIslandStatus(boolean isOnIsland) {
		this.isOnIsland = isOnIsland;
	}

	public void setLivesLeft(int livesLeft) {
		this.livesLeft = livesLeft;
	}

	public void setIslandsLeft(int islandsLeft) {
		this.islandsLeft = islandsLeft;
	}

	public void setHomeLocation(Location home) {
		this.homeLocation = home;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(this.playerName);
	}

	public String getPlayerName() {
		return this.playerName;
	}

	public boolean getHasIsland() {
		return this.hasIsland;
	}

	public boolean isOnIsland() {
		return this.isOnIsland;
	}

	public boolean isDead() {
		return this.isDead;
	}

	public int getLivesLeft() {
		return this.livesLeft;
	}

	public int getIslandsLeft() {
		return this.islandsLeft;
	}

	public Location getIslandLocation() {
		return this.islandLocation;
	}

	public Location getHomeLocation() {
		return this.homeLocation;
	}

	public Location getOldLocation() {
		return this.oldLocation;
	}

	public ItemStack[] getOldInventory() {
		return this.oldInventory;
	}

	public ItemStack[] getOldArmor() {
		return this.oldArmor;
	}

	public ItemStack[] getIslandInventory() {
		return this.islandInventory;
	}

	public ItemStack[] getIslandArmor() {
		return this.islandArmor;
	}

	public float getIslandExp() {
		return this.islandExp;
	}

	public float getOldExp() {
		return this.oldExp;
	}

	public int getIslandLevel() {
		return this.islandLevel;
	}

	public int getOldLevel() {
		return this.oldLevel;
	}

	public int getIslandFood() {
		return this.islandFood;
	}

	public int getOldFood() {
		return this.oldFood;
	}

	public int getIslandHealth() {
		return this.islandHealth;
	}

	public int getOldHealth() {
		return this.oldHealth;
	}

	public void setOldLocation(Location l) {
		this.oldLocation = l;
	}

	public void setOldInventory(ItemStack[] i) {
		this.oldInventory = i;
	}

	public void setOldArmor(ItemStack[] i) {
		this.oldArmor = i;
	}

	public void setOldFood(int x) {
		this.oldFood = x;
	}

	public void setOldHealth(int x) {
		this.oldHealth = x;
	}

	public void setOldExp(float x) {
		this.oldExp = x;
	}

	public void setOldLevel(int x) {
		this.oldLevel = x;
	}

	public void setIslandLocation(Location l) {
		this.islandLocation = l;
	}

	public void setIslandInventory(ItemStack[] i) {
		this.islandInventory = i;
	}

	public void setIslandArmor(ItemStack[] i) {
		this.islandArmor = i;
	}

	public void setIslandFood(int x) {
		this.islandFood = x;
	}

	public void setIslandHealth(int x) {
		this.islandHealth = x;
	}

	public void setIslandExp(float x) {
		this.islandExp = x;
	}

	public void setIslandLevel(int x) {
		this.islandLevel = x;
	}

	// ----------- friend methods ----------------------------- //

	public HashMap<String, PlayerData> getFriends() {
		return (HashMap<String, PlayerData>) this.friendsCanBuildHere;
	}

	public void addFriendsToOwnIsland(PlayerData friend) {
		// check if friend is already added
		if (this.friendsCanBuildHere.containsKey(friend.getPlayerName()))
			return;
		this.friendsCanBuildHere.put(friend.getPlayerName(), friend);
	}

	public void addOwnBuildPermission(PlayerData friend) {
		// check if friend is already added
		if (this.canBuildByFriends.containsKey(friend.getPlayerName()))
			return;
		this.canBuildByFriends.put(friend.getPlayerName(), friend);
	}

	public void removeFriendFromOwnIsland(PlayerData friend) {
		// check if friend is in list
		if (!this.friendsCanBuildHere.containsKey(friend.getPlayerName()))
			return;
		this.friendsCanBuildHere.remove(friend.getPlayerName());
	}

	public void removeBuildPermissionByFriends(PlayerData friend) {
		// check if friend is in list
		if (!this.canBuildByFriends.containsKey(friend.getPlayerName()))
			return;
		this.canBuildByFriends.remove(friend.getPlayerName());
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
		for (PlayerData friend : this.canBuildByFriends.values()) {
			if (this.isLocationWithInArea(block, friend.getIslandLocation())) {
				return true;
			}
		}
		return false;
	}

}
