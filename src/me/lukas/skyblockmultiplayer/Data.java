package me.lukas.skyblockmultiplayer;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

public class Data {

	/** OPTIONS GLOBAL **/
	public static ItemStack[] ITEMSCHEST; // Content of the chest
	public static String LANGUAGE; // Language for the sentences
	public static boolean ALLOWCONTENT; // Allow or disable content 
	public static String WORLDNAME; // The world name of the world and the world folder
	public static boolean SKYBLOCK_ONLINE; // SkyBlock online or offline
	public static int ISLAND_DISTANCE; // Distance between the player spawn locations
	public static boolean CLOSED;

	/** Needed for plugin **/
	public static Map<String, PlayerInfo> PLAYERS = new HashMap<String, PlayerInfo>(); // Key = Player, value PlayerInfo
	public static int PLAYERS_NUMBER; // Amount of players in SkyBlock
	public static int ISLAND_NUMBER; // Amount of islands in SkyBlock

	public static enum GAMEMODE { // Two game modes
		BUILD, PVP
	}

	public static GAMEMODE GAMEMODE_SELECTED; // Selected game mode

	/** Gamemode is build **/
	public static boolean BUILD_RESPAWNWITHINVENTORY; // If true save contents of player in SkyBlock in Death
	public static boolean BUILD_WITHPROTECTEDAREA; // If true, protected Area around of the island and a player can not do anything on another island
	public static boolean BUILD_ALLOW_ENDERPEARL; // If true, player can use ender pearl
}
