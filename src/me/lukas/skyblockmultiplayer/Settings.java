package me.lukas.skyblockmultiplayer;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

public class Settings {

	/** OPTIONS GLOBAL **/
	public static ItemStack[] itemsChest; // Content of the chest
	public static String language; // Language for the sentences
	public static boolean allowContent; // Allow or disable content 
	public static String worldName; // The world name of the world and the world folder
	public static boolean skyBlockOnline; // SkyBlock online or offline
	public static int distanceIslands; // Distance between the player spawn locations
	public static boolean closed; // Lock SkyBlock to maximize the amount of players
	public static boolean islandsPerPlayer; // islands who every player have, 0 = endless
	public static boolean spawnTowerReCreate;

	/** Needed for plugin **/
	public static Map<String, PlayerInfo> PLAYERS = new HashMap<String, PlayerInfo>(); // Key = player name, value PlayerInfo
	public static int numbersPlayers; // Amount of players in SkyBlock
	public static int numberIslands; // Amount of islands in SkyBlock

	public static enum GAMEMODE { // Two game modes
		BUILD, PVP
	}

	public static GAMEMODE gameModeSelected; // Selected game mode

	/** Gamemode is build **/
	public static boolean build_respawnWithInventory; // If true save contents of player in SkyBlock in Death
	public static boolean build_withProtectedArea; // If true, protected Area around of the island and a player can not do anything on another island
	public static boolean build_allowEnderpearl; // If true, player can use ender pearl
}
