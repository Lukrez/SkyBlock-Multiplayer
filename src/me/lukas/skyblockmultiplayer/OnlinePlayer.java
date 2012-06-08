package me.lukas.skyblockmultiplayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import sun.security.krb5.Config;

public class OnlinePlayer {

	private String playerName;

	private int hasIslandNr;
	private boolean isDead;
	private boolean isOnIsland;

	private int livesLeft;
	private int islandsLeft;

	private Location islandLocation;
	private Location homeLocation;
	private Location oldLocation;

	
	private ItemStack[] islandInventory;
	private ItemStack[] islandArmor;

	private ItemStack[] oldInventory;
	private ItemStack[] oldArmor;

	private int islandFood;
	private int oldFood;

	private int islandHealth;
	private int oldHealth;

	private float islandExp;
	private float oldExp;

	private int islandLevel;
	private int oldLevel;

	
	public OnlinePlayer(Player player) throws SQLException{
		ResultSet rs = SQLInstructions.loadOnlinePlayerData(player.getName());
		if (rs.next()){ // Player data found, load data
			this.playerName = player.getName();
			this.isOnIsland = rs.getBoolean("isOnIsland");
			this.isDead = rs.getBoolean("isDead");
			this.livesLeft = rs.getInt("livesLeft");
			this.islandsLeft = rs.getInt("islandsLeft");
			this.homeLocation = SkyBlockMultiplayer.getInstance().getLocationString(rs.getString("homeLocation"));
			this.hasIslandNr = rs.getInt("islandNumber");
			this.islandLocation = SkyBlockMultiplayer.getInstance().getLocationString(rs.getString("islandLocation")); 
						
			// Get old Data
			this.oldLocation = SkyBlockMultiplayer.getInstance().getLocationString(rs.getString("oldWorld.location"));
			this.oldInventory = ItemParser.StringToInventory(rs.getString("oldWorld.inventory"), 36);
			this.oldArmor = ItemParser.StringToInventory(rs.getString("oldWorld.armor"), 4);
			this.oldHealth = rs.getInt("oldWorld.health");
			this.oldFood = rs.getInt("oldWorld.food");
			this.oldExp = rs.getInt("oldWorld.exp");
			this.oldLevel = rs.getInt("oldWorld.level");
			
			// Get skyblock Data
			this.islandLocation = SkyBlockMultiplayer.getInstance().getLocationString(rs.getString("skyblockWorld.location"));
			this.islandInventory = ItemParser.StringToInventory(rs.getString("skyblockWorld.inventory"), 36);
			this.islandArmor = ItemParser.StringToInventory(rs.getString("skyblockWorld.armor"), 4);
			this.islandHealth = rs.getInt("skyblockWorld.health");
			this.islandFood = rs.getInt("skyblockWorld.food");
			this.islandExp = rs.getInt("skyblockWorld.exp");
			this.islandLevel = rs.getInt("skyblockWorld.level");
			
			// Add user to the Settingslist
			Settings.onlinePlayers.put(this.playerName, this);
	
		} 
	}
	
	 
	public void setPlayerName(String s) {
		this.playerName = s;
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(this.playerName);
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

	public void setLivesLeft(int i) {
		this.livesLeft = i;
	}

	public int getLivesLeft() {
		return this.livesLeft;
	}

	public void setIslandsLeft(int i) {
		this.islandsLeft = i;
	}

	public int getIslandsLeft() {
		return this.islandsLeft;
	}

	public void setIslandLocation(Location l) {
		this.islandLocation = l;
	}

	public Location getIslandLocation() {
		return this.islandLocation;
	}

	public void setHomeLocation(Location l) {
		this.homeLocation = l;
	}

	public Location getHomeLocation() {
		return this.homeLocation;
	}

	public void setOldLocation(Location l) {
		if (!l.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			this.oldLocation = l;
		}
	}

	public Location getOldLocation() {
		return this.oldLocation;
	}

	public void setIslandExp(float i) {
		this.islandExp = i;
	}

	public float getIslandExp() {
		return this.islandExp;
	}

	public void setOldExp(float i) {
		this.oldExp = i;
	}

	public float getOldExp() {
		return this.oldExp;
	}

	public void setIslandLevel(int i) {
		this.islandLevel = i;
	}

	public int getIslandLevel() {
		return this.islandLevel;
	}

	public void setOldLevel(int i) {
		this.oldLevel = i;
	}

	public int getOldLevel() {
		return this.oldLevel;
	}

	public void setIslandFood(int i) {
		this.islandFood = i;
	}

	public int getIslandFood() {
		return this.islandFood;
	}

	public void setOldFood(int i) {
		this.oldFood = i;
	}

	public int getOldFood() {
		return this.oldFood;
	}

	public void setIslandHealth(int i) {
		this.islandHealth = i;
	}

	public int getIslandHealth() {
		return this.islandHealth;
	}

	public void setOldHealth(int i) {
		this.oldHealth = i;
	}

	public int getOldHealth() {
		return this.oldHealth;
	}

	private ItemStack parseStringToItemStack(String s) {
		if (s.equalsIgnoreCase("")) {
			return null;
		}

		String[] values = s.split(":");
		try {
			int id = Integer.parseInt(values[0]);
			int amount = Integer.parseInt(values[1]);
			short dmg = Short.parseShort(values[2]);
			byte data = Byte.parseByte(values[3]);
			return new ItemStack(id, amount, dmg, data);
		} catch (Exception e) {
		}
		return null;
	}

	/*private String parseItemStackToString(ItemStack i) {
		if (i == null) {
			return "";
		}
		return i.getTypeId() + ":" + i.getAmount() + ":" + i.getDurability() + ":" + i.getData().getData();
	}*/

	private Location getLocationString(String s) {
		if (s == null || s.trim() == "") {
			return null;
		}
		String[] parts = s.split(":");
		if (parts.length == 4) {
			World w = Bukkit.getServer().getWorld(parts[0]);
			int x = Integer.parseInt(parts[1]);
			int y = Integer.parseInt(parts[2]);
			int z = Integer.parseInt(parts[3]);
			return new Location(w, x, y, z);
		}
		return null;
	}

	private String getStringLocation(Location l) {
		if (l == null) {
			return "";
		}
		return l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
	}
}
