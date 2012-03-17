package me.lukas.skyblockmultiplayer;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Data {

	public static ItemStack[] ITEMSCHEST;
	public static Map<String, PlayerInfo> PLAYERS = new HashMap<String, PlayerInfo>();
	public static int PLAYERS_NUMBER;
	public static boolean SKYBLOCK_ONLINE;
	public static int ISLAND_DISTANCE;
	public static int ISLAND_NUMBER;
	public static String LANGUAGE;
	public static boolean ALLOWCONTENT;

	public static enum GAMEMODE {
		BUILD, PVP
	}

	public static GAMEMODE GAMEMODE_SELECTED;
	public static Map<Player, ItemStack[]> PLAYERINVENTORYS = new HashMap<Player, ItemStack[]>();
	public static Map<Player, ItemStack[]> PLAYEREQUIPMENTS = new HashMap<Player, ItemStack[]>();
	public static boolean BUILD_RESPAWNWITHINVENTORY;
	public static boolean BUILD_WITHPROTECTEDAREA;
}
