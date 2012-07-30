package me.lukas.skyblockmultiplayer;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerInfo{


	private String playerName;

	private boolean hasIsland;
	private boolean isDead;

	private int livesLeft;
	private int islandsLeft;

	private String islandLocation;
	private String homeLocation;
	private String oldLocation;

	private ArrayList<String> islandInventory;
	private ArrayList<String> islandArmor;

	private ArrayList<String> oldInventory;
	private ArrayList<String> oldArmor;

	private ArrayList<String> friends;

	private int islandFood;
	private int oldFood;

	private int islandHealth;
	private int oldHealth;

	private float islandExp;
	private float oldExp;

	private int islandLevel;
	private int oldLevel;

	public PlayerInfo(String playerName) {
		
		// checke, ob playerfile existiert, ansonsten neu erstellen
		
		this.playerName = playerName;

		this.hasIsland = false;
		this.isDead = false;

		this.livesLeft = Settings.pvp_livesPerIsland;
		this.islandsLeft = Settings.pvp_islandsPerPlayer;

		this.islandLocation = null;
		this.homeLocation = null;
		this.oldLocation = null;

		this.islandInventory = new ArrayList<String>();
		this.islandArmor = new ArrayList<String>();

		this.oldInventory = new ArrayList<String>();
		this.oldArmor = new ArrayList<String>();

		this.friends = new ArrayList<String>();

		this.islandFood = 0;
		this.oldFood = 0;

		this.islandHealth = 0;
		this.oldHealth = 0;

		this.islandExp = 0;
		this.oldExp = 0;

		this.islandLevel = 0;
		this.oldLevel = 0;
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

	public void setHasIsland(boolean b) {
		this.hasIsland = b;
	}

	public boolean getHasIsland() {
		return this.hasIsland;
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
		this.islandLocation = this.getStringLocation(l);
	}

	public Location getIslandLocation() {
		return this.getLocationString(this.islandLocation);
	}

	public void setHomeLocation(Location l) {
		this.homeLocation = this.getStringLocation(l);
	}

	public Location getHomeLocation() {
		return this.getLocationString(this.homeLocation);
	}

	public void setOldLocation(Location l) {
		if (!l.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			this.oldLocation = this.getStringLocation(l);
		}
	}

	public Location getOldLocation() {
		return this.getLocationString(this.oldLocation);
	}

	public void setIslandInventory(ItemStack[] items) {
		this.islandInventory = new ArrayList<String>();
		if (items == null) {
			return;
		}
		for (ItemStack i : items) {
			this.islandInventory.add(ItemParser.parseItemStackToString(i));
		}
	}

	public ItemStack[] getIslandInventory() {
		ItemStack[] items = new ItemStack[this.islandInventory.size()];
		for (int i = 0; i < items.length; i++) {
			String s = this.islandInventory.get(i);
			if (!s.equalsIgnoreCase("")) {
				if (s.contains("amount")) {
					items[i] = ItemParser.getItemStackfromString(s);
				} else {
					items[i] = this.parseStringToItemStack(this.islandInventory.get(i));
				}
			}
		}
		return items;
	}

	public void setIslandArmor(ItemStack[] items) {
		this.islandArmor = new ArrayList<String>();
		if (items == null) {
			return;
		}
		for (ItemStack i : items) {
			this.islandArmor.add(ItemParser.parseItemStackToString(i));
		}
	}

	public ItemStack[] getIslandArmor() {
		ItemStack[] items = new ItemStack[this.islandArmor.size()];
		for (int i = 0; i < items.length; i++) {
			String s = this.islandArmor.get(i);
			if (!s.equalsIgnoreCase("")) {
				if (s.contains("amount")) {
					items[i] = ItemParser.getItemStackfromString(s);
				} else {
					items[i] = this.parseStringToItemStack(this.islandArmor.get(i));
				}
			}
		}
		return items;
	}

	public void setOldInventory(ItemStack[] items) {
		this.oldInventory = new ArrayList<String>();
		if (items == null) {
			return;
		}
		for (ItemStack i : items) {
			this.oldInventory.add(ItemParser.parseItemStackToString(i));
		}
	}

	public ItemStack[] getOldInventory() {
		ItemStack[] items = new ItemStack[this.oldInventory.size()];
		for (int i = 0; i < items.length; i++) {
			String s = this.oldInventory.get(i);
			if (!s.equalsIgnoreCase("")) {
				if (s.contains("amount")) {
					items[i] = ItemParser.getItemStackfromString(s);
				} else {
					items[i] = this.parseStringToItemStack(this.oldInventory.get(i));
				}
			}
		}
		return items;
	}

	public void setOldArmor(ItemStack[] items) {
		this.oldArmor = new ArrayList<String>();
		if (items == null) {
			return;
		}
		for (ItemStack i : items) {
			this.oldArmor.add(ItemParser.parseItemStackToString(i));
		}
	}

	public ItemStack[] getOldArmor() {
		ItemStack[] items = new ItemStack[this.oldArmor.size()];
		for (int i = 0; i < items.length; i++) {
			String s = this.oldArmor.get(i);
			if (!s.equalsIgnoreCase("")) {
				if (s.contains("amount")) {
					items[i] = ItemParser.getItemStackfromString(s);
				} else {
					items[i] = this.parseStringToItemStack(this.oldArmor.get(i));
				}
			}
		}
		return items;
	}

	public void addFriend(String s) {
		this.friends.add(s);
	}

	public void removeFriend(String s) {
		this.friends.remove(s);
	}

	public ArrayList<String> getFriends() {
		return this.friends;
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
