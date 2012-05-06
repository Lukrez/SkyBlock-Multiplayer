package me.lukas.skyblockmultiplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class Settings {

	/* OPTIONS GLOBAL */
	public static ItemStack[] itemsChest; // Content of the chest
	public static String language; // Language for the sentences
	public static boolean allowContent; // Allow or disable content 
	public static String worldName; // The world name of the world and the world folder
	public static boolean skyBlockOnline; // SkyBlock online or offline
	public static int distanceIslands; // Distance between the player spawn locations
	public static boolean closed; // Lock SkyBlock to maximize the amount of players
	public static String islandFileName; // schematic file name for island
	public static String towerFileName; // schematic file name for tower
	public static int towerYHeight; // tower height

	/* Needed for plugin */
	public static Map<String, PlayerInfo> players = new HashMap<String, PlayerInfo>(); // Key = player name, value PlayerInfo
	public static int numbersPlayers; // Amount of players in SkyBlock
	public static boolean messagesOutside; // to get messages from SkyBlock.
	public static Map<String, ArrayList<Location>> islandLocations = new HashMap<String, ArrayList<Location>>(); 

	public static enum GameMode { // Two game modes
		BUILD, PVP
	}

	public static GameMode gameModeSelected; // Selected game mode

	/* Gamemode is build */
	public static boolean build_respawnWithInventory; // If true save contents of player in SkyBlock in Death
	public static boolean build_withProtectedArea; // If true, protected Area around of the island and a player can not do anything on another island
	public static boolean build_allowEnderpearl; // If true, player can use ender pearl
	public static boolean build_withProtectedBorder; // if true the border is protected

	/* Gamemode is pvp */
	public static int pvp_livesPerIsland; // lives points for every island
	public static int pvp_islandsPerPlayer; // amount of islands who every player can have
}
