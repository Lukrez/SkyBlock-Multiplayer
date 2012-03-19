package me.lukas.skyblockmultiplayer;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	private boolean hasIsland;
	private boolean isDead;
	private String playerName;

	private String worldIsland;
	private int islandX, islandY, islandZ;

	private String worldOld;
	private int oldX, oldY, oldZ;
	private ArrayList<String> friends;

	private ArrayList<String> contentsInventory;
	private ArrayList<String> contentsArmor;

	public PlayerInfo(String playerName) {
		this.playerName = playerName;
		this.hasIsland = false;
		this.setDead(false);
		this.worldIsland = "";
		this.worldOld = "";
		this.friends = new ArrayList<String>();
		this.contentsArmor = new ArrayList<String>();
		this.contentsInventory = new ArrayList<String>();
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

		if (this.worldOld.equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			this.setOldPlayerLocation(SkyBlockMultiplayer.instance.getServer().getWorlds().get(0).getSpawnLocation());
		}

		SkyBlockMultiplayer.instance.writePlayerFile(this.playerName);
	}

	public Location getOldPlayerLocation() {
		if (this.worldIsland.equalsIgnoreCase("")) {
			return null;
		}
		World w = Bukkit.getWorld(this.worldOld);
		if (w == null) {
			return null;
		}
		return new Location(w, this.oldX, this.oldY, this.oldZ);
	}

	public void setDead(boolean b) {
		this.isDead = b;
		SkyBlockMultiplayer.instance.writePlayerFile(this.playerName);
	}

	public boolean isDead() {
		return this.isDead;
	}

	public void setIslandLocation(Location l) {
		if (l == null) {
			this.worldIsland = "";
			this.islandX = 0;
			this.islandY = 0;
			this.islandZ = 0;
			return;
		}

		this.worldIsland = l.getWorld().getName();
		this.islandX = l.getBlockX();
		this.islandY = l.getBlockY();
		this.islandZ = l.getBlockZ();
		SkyBlockMultiplayer.instance.writePlayerFile(this.playerName);
	}

	public Location getIslandLocation() {
		if (this.worldIsland.trim().equalsIgnoreCase("")) {
			return null;
		}
		World w = Bukkit.getServer().getWorld(this.worldIsland);
		if (w == null) {
			return null;
		}
		return new Location(w, this.islandX, this.islandY, this.islandZ);
	}

	public boolean addFriend(String playerName) {
		boolean ret = this.friends.add(playerName);
		SkyBlockMultiplayer.instance.writePlayerFile(this.playerName);
		return ret;
	}

	public boolean removeFriend(String playerName) {
		boolean ret = this.friends.remove(playerName);
		SkyBlockMultiplayer.instance.writePlayerFile(this.playerName);
		return ret;
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

	public void setContentsInventory(ItemStack[] items) {
		this.contentsInventory = new ArrayList<String>();
		for (ItemStack i : items) {
			this.contentsInventory.add(this.parseItemStackToString(i));
		}
		SkyBlockMultiplayer.instance.writePlayerFile(this.playerName);
	}

	public ItemStack[] getContentsInventory() {
		ItemStack[] items = new ItemStack[this.contentsInventory.size()];
		for (int i = 0; i < items.length; i++) {
			String s = this.contentsInventory.get(i);
			if (!s.equalsIgnoreCase("")) {
				items[i] = this.parseStringToItemStack(this.contentsInventory.get(i));
			}
		}
		return items;
	}

	public void setContentsArmor(ItemStack[] items) {
		this.contentsArmor = new ArrayList<String>();
		for (ItemStack i : items) {
			this.contentsArmor.add(this.parseItemStackToString(i));
		}
		SkyBlockMultiplayer.instance.writePlayerFile(this.playerName);
	}

	public ItemStack[] getContentsArmor() {
		ItemStack[] items = new ItemStack[this.contentsArmor.size()];
		for (int i = 0; i < items.length; i++) {
			String s = this.contentsInventory.get(i);
			if (!s.equalsIgnoreCase("")) {
				items[i] = this.parseStringToItemStack(this.contentsArmor.get(i));
			}
		}
		return items;
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

	private String parseItemStackToString(ItemStack i) {
		if (i == null) {
			return "";
		}
		return i.getTypeId() + ":" + i.getAmount() + ":" + i.getDurability() + ":" + i.getData().getData();
	}
}
