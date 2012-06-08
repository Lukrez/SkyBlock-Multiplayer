package me.lukas.skyblockmultiplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

import me.lukas.skyblockmultiplayer.listeners.EntityDeath;
import me.lukas.skyblockmultiplayer.listeners.PlayerBreackBlockListener;
import me.lukas.skyblockmultiplayer.listeners.PlayerInteract;
import me.lukas.skyblockmultiplayer.listeners.PlayerPlaceBlockListener;
import me.lukas.skyblockmultiplayer.listeners.PlayerRespawn;
import me.lukas.skyblockmultiplayer.listeners.PlayerTeleport;
import me.lukas.skyblockmultiplayer.listeners.PlayerUseBucketListener;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyBlockMultiplayer extends JavaPlugin {
	public PluginDescriptionFile pluginFile;
	public Logger log;

	public static World skyBlockWorld = null;
	private static SkyBlockMultiplayer instance;
	public String fileSQLite;

	public FileConfiguration configPlugin;
	public File filePlugin;

	public FileConfiguration configLanguage;
	public File fileLanguage;

	private File directoryPlayers;

	public String pName;

	@Override
	public void onDisable() {
		this.log.info("v" + pluginFile.getVersion() + " disabled.");
	}

	@Override
	public void onEnable() {
		SkyBlockMultiplayer.instance = this;

		this.pluginFile = this.getDescription();
		this.log = this.getLogger();

		this.fileSQLite = this.getDataFolder() + File.separator + "Skylock.db";

		this.pName = ChatColor.WHITE + "[" + ChatColor.GREEN + this.pluginFile.getName() + ChatColor.WHITE + "] ";

		// register events
		this.registerEvents();

		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdir();
		}

		this.configPlugin = this.getConfig();
		this.filePlugin = new File(this.getDataFolder(), "config.yml");
		this.loadPluginConfig();

		this.configLanguage = new YamlConfiguration();
		this.fileLanguage = new File(this.getDataFolder() + File.separator + "language", Settings.language + ".yml");
		try {
			this.loadLanguageConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.directoryPlayers = new File(this.getDataFolder() + File.separator + "players");
		if (!this.directoryPlayers.exists()) {
			this.directoryPlayers.mkdir();
		} else {
			this.loadPlayerFiles();
		}
		
		// load SQL
		try {
			SQLInstructions.createTables();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// register command
		this.getCommand("skyblock").setExecutor(new SkyBlockCommand());

		this.log.info("v" + pluginFile.getVersion() + " enabled.");
	}

	public static SkyBlockMultiplayer getInstance() {
		return instance;
	}

	/**
	 * Register the events
	 * 
	 */
	public void registerEvents() {
		PluginManager manager = this.getServer().getPluginManager();
		manager.registerEvents(new PlayerPlaceBlockListener(), this);
		manager.registerEvents(new PlayerBreackBlockListener(), this);
		manager.registerEvents(new PlayerUseBucketListener(), this);
		manager.registerEvents(new EntityDeath(), this);
		manager.registerEvents(new PlayerRespawn(), this);
		manager.registerEvents(new PlayerInteract(), this);
		manager.registerEvents(new PlayerTeleport(), this);
	}

	/**
	 * Creates or loads the config file.
	 * 
	 */
	public void loadPluginConfig() {
		ItemStack[] itemsChest = { new ItemStack(Material.ICE, 2), new ItemStack(Material.SAPLING, 5), new ItemStack(Material.MELON, 3), new ItemStack(Material.CACTUS, 1), new ItemStack(Material.LAVA_BUCKET, 1), new ItemStack(Material.PUMPKIN, 1) };

		if (!this.filePlugin.exists()) {
			Settings.distanceIslands = 50;
			Settings.itemsChest = itemsChest;
			Settings.skyBlockOnline = true;
			Settings.allowContent = false;
			Settings.language = "english";
			Settings.gameModeSelected = Settings.GameMode.BUILD;
			Settings.pvp_livesPerIsland = 1;
			Settings.pvp_islandsPerPlayer = 1;
			Settings.build_respawnWithInventory = true;
			Settings.build_withProtectedArea = false;
			Settings.build_allowEnderpearl = false;
			Settings.worldName = this.pluginFile.getName();
			Settings.messagesOutside = false;
			Settings.removeCreaturesByTeleport = true;
			Settings.islandFileName = "";
			Settings.islandYHeight = 64;
			Settings.towerFileName = "";
			Settings.towerYHeight = 80;

			for (ConfigPlugin c : ConfigPlugin.values()) {
				this.setStringbyPath(this.configPlugin, this.filePlugin, c.path, c.value);
			}
		} else {
			try {
				this.configPlugin.load(this.filePlugin);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				Settings.distanceIslands = Integer.parseInt(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_ISLANDDISTANCE.path, 50, true));
				if (Settings.distanceIslands < 50) {
					Settings.distanceIslands = 50;
				}
			} catch (Exception e) {
				Settings.distanceIslands = 50;
			}

			String[] dataItems = this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_CHESTITEMS.path, "79:2 6:5 360:3 81:1 327:1 86:1", true).split(" ");
			ArrayList<ItemStack> alitemsChest = new ArrayList<ItemStack>();

			for (String s : dataItems) {
				if (s.trim() != "") {
					String[] dataValues = s.split(":");
					try {
						int id = Integer.parseInt(dataValues[0]);
						int amount = Integer.parseInt(dataValues[1]);

						Material m = Material.matchMaterial("" + dataValues[0]);
						if (m != null) {
							if (dataValues.length == 2) {
								alitemsChest.add(new ItemStack(id, amount));
							} else if (dataValues.length == 3) {
								alitemsChest.add(new ItemStack(id, amount, (short) 0, Byte.parseByte(dataValues[2])));
							}
						}

					} catch (Exception ex) {
					}
				}
			}

			itemsChest = new ItemStack[alitemsChest.size()];
			for (int i = 0; i < itemsChest.length; i++) {
				itemsChest[i] = alitemsChest.get(i);
			}

			try {
				Settings.pvp_livesPerIsland = Integer.parseInt(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_PVP_LIVESPERISLAND.path, 1, true));
				if (Settings.pvp_livesPerIsland <= 0) {
					Settings.pvp_livesPerIsland = 1;
				}
			} catch (Exception e) {
				Settings.pvp_livesPerIsland = 1;
			}

			try {
				Settings.pvp_islandsPerPlayer = Integer.parseInt(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_PVP_ISLANDSPERPLAYER.path, 1, true));
				if (Settings.pvp_islandsPerPlayer <= 0) {
					Settings.pvp_islandsPerPlayer = 1;
				}
			} catch (Exception e) {
				Settings.pvp_islandsPerPlayer = 1;
			}

			try {
				Settings.towerYHeight = Integer.parseInt(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_SCHEMATIC_TOWER_YHEIGHT.path, 80, true));
				if (Settings.towerYHeight < 0) {
					Settings.towerYHeight = 80;
				}
			} catch (Exception e) {
				Settings.towerYHeight = 80;
			}

			/*try {
				Settings.islandYHeight = Integer.parseInt(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_SCHEMATIC_ISLAND_YHEIGHT.path, 64, true));
				if (Settings.islandYHeight < 0) {
					Settings.islandYHeight = 64;
				}
			} catch (Exception e) {
				Settings.islandYHeight = 64;
			}*/

			Settings.islandYHeight = 64;

			Settings.itemsChest = itemsChest;
			Settings.skyBlockOnline = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_SKYBLOCKONLINE.path, true, true));
			Settings.language = this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_LANGUAGE.path, "english", true);
			Settings.allowContent = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_ALLOWCONTENT.path, false, true));
			Settings.gameModeSelected = Settings.GameMode.valueOf(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_GAMEMODE.path, "build", true).toUpperCase());
			Settings.build_respawnWithInventory = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_BUILD_RESPAWNWITHINVENTORY.path, true, true));
			Settings.build_withProtectedArea = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_BUILD_WITHPROTECTEDAREA.path, true, true));
			Settings.build_allowEnderpearl = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_BUILD_ALLOWENDERPEARL.path, false, true));
			Settings.build_withProtectedBorder = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_BUILD_WITHPROTECTEDBORDER.path, true, true));
			Settings.worldName = this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_WORLDNAME.path, this.pluginFile.getName(), true);
			Settings.closed = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_CLOSED.path, false, true));
			Settings.messagesOutside = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_MESSAGES_OUTSIDE.path, false, true));
			Settings.removeCreaturesByTeleport = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_REMOVECREATURESBYTELEPORT.path, true, true));
			Settings.islandFileName = this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_SCHEMATIC_ISLAND_FILENAME.path, "", true);
			Settings.towerFileName = this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_SCHEMATIC_TOWER_FILENAME.path, "", true);
		}
	}

	/**
	 * Load all informations from player who exists in folder players and who have a island.
	 * 
	 */
	public void loadPlayerFiles() {
		for (String f : new File(this.directoryPlayers.toString()).list()) {
			if (new File(this.directoryPlayers, f).isFile()) {
				PlayerInfo pi = this.readPlayerFile(f);
				if (pi != null) {
					// add player, if missing
					String playerName = pi.getPlayerName();

					Player playerOnline = this.getServer().getPlayer(playerName);
					if (pi.getIslandLocation() == null) {
						continue;
					}

					if (!Settings.lstPlayerInfo2.containsKey(playerName)) {
						Settings.lstPlayerInfo2.put(playerName, new PlayerInfo2(playerName, pi.getIslandLocation()));
						Settings.lstPlayerInfo2.get(playerName).setHomeLocation(pi.getHomeLocation());
					} else {
						Settings.lstPlayerInfo2.get(playerName).setIslandLocation(pi.getIslandLocation());
						Settings.lstPlayerInfo2.get(playerName).setHomeLocation(pi.getHomeLocation());
					}
					PlayerInfo2 player = Settings.lstPlayerInfo2.get(playerName);

					// Add friends
					for (String friendName : pi.getFriends()) {
						if (!Settings.lstPlayerInfo2.containsKey(friendName)) {
							Settings.lstPlayerInfo2.put(friendName, new PlayerInfo2(friendName, null));
						}
						PlayerInfo2 friend = Settings.lstPlayerInfo2.get(friendName);
						// Add Permissions
						player.addFriendsToOwnIsland(friend);
						friend.addOwnBuildPermission(player);
					}

					Settings.islandsAndOwners.put(pi.getPlayerName(), pi.getIslandLocation());
					if (playerOnline == null || !playerOnline.getWorld().getName().equals(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
						continue;
					}
					Settings.players.put(playerName, pi);
				}
			}
		}

		/*System.out.println("Alle: " + all);
		System.out.println("Not online: " + notOnline);
		System.out.println("islandsAndOwners: " + Settings.islandsAndOwners.size());*/

		/*// print friendslist
		for (PlayerInfo2 player : Settings.lstPlayerInfo2.values()) {
			System.out.println("Player: " + player.getName());
			for (PlayerInfo2 friend : player.getFriends().values()) {
				System.out.println("\t -" + friend.getName());
			}
		}*/
	}

	public PlayerInfo readPlayerFile(String playerName) {
		File f = new File(this.directoryPlayers, playerName);
		if (!f.exists()) {
			return null;
		}

		try {
			FileInputStream fileIn = new FileInputStream(f);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			PlayerInfo p = (PlayerInfo) in.readObject();
			in.close();
			fileIn.close();
			return p;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void writePlayerFile(String playerName, PlayerInfo pi) {
		File f = new File(this.directoryPlayers, playerName);

		try {
			FileOutputStream fileOut = new FileOutputStream(f);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(pi);
			out.flush();
			out.close();
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the language file, that is setted in conig.yml
	 * 
	 */
	public void loadLanguageConfig() throws Exception {
		if (!new File(this.getDataFolder() + File.separator + "language").exists()) {
			new File(this.getDataFolder() + File.separator + "language").mkdirs();
		}

		String encoding = "UTF-8";
		if (!this.fileLanguage.exists()) {
			this.fileLanguage.createNewFile(); //create file
			this.writeLanguageConfig(); //write standard language
		} else {
			Scanner scanner = new Scanner(new FileInputStream(this.fileLanguage), encoding);
			String contentToRead = "";
			while (scanner.hasNextLine()) {
				contentToRead += scanner.nextLine() + System.getProperty("line.separator");
			}
			scanner.close();

			try {
				this.configLanguage.loadFromString(contentToRead);
			} catch (InvalidConfigurationException e) {
				encoding = "Cp1252";
				scanner = new Scanner(new FileInputStream(this.fileLanguage), encoding);
				contentToRead = "";
				while (scanner.hasNextLine()) {
					contentToRead += scanner.nextLine() + System.getProperty("line.separator");
				}
				scanner.close();
				this.configLanguage.loadFromString(contentToRead);
			}

			boolean missingSentences = false;
			for (Language g : Language.values()) {
				String path = g.path;
				if (!this.configLanguage.contains(path)) {
					this.configLanguage.set(path, g.sentence);
					missingSentences = true;
				} else {
					g.sentence = this.replaceColor(this.configLanguage.getString(path));
				}
			}

			if (missingSentences) {
				String contentToSave = this.configLanguage.saveToString();
				Writer out = new OutputStreamWriter(new FileOutputStream(this.fileLanguage), encoding);
				out.write(contentToSave);
				out.flush();
				out.close();
			}
		}
		SkyBlockMultiplayer.getSkyBlockWorld();
	}

	/**
	 * This replace §0-§f with ChatColor.
	 * 
	 * @param s the given String
	 * @return string with ChatColor.
	 */
	private String replaceColor(String s) {
		for (ChatColor c : ChatColor.values()) {
			s = s.replaceAll("§" + c.getChar(), "" + ChatColor.getByChar(c.getChar()));
		}
		return s;
	}

	/**
	 * Parse a String to a location.
	 * 
	 * @param s 
	 * @return location or null
	 */
	public Location getLocationString(String s) {
		if (s == null || s.trim() == "") {
			return null;
		}
		String[] parts = s.split(":");
		if (parts.length == 4) {
			World w = this.getServer().getWorld(parts[0]);
			int x = Integer.parseInt(parts[1]);
			int y = Integer.parseInt(parts[2]);
			int z = Integer.parseInt(parts[3]);
			return new Location(w, x, y, z);
		}
		return null;
	}

	/**
	 * Returns a string of the given location, can be empty.
	 * 
	 * @param l get a string of it.
	 * @return string.
	 */
	public String getStringLocation(Location l) {
		if (l == null) {
			return "";
		}
		return l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
	}

	/**
	 * This writes the the language yml file.
	 * @throws IOException 
	 * 
	 */
	private void writeLanguageConfig() throws IOException {
		for (Language g : Language.values()) {
			String path = g.path;
			this.configLanguage.set(path, g.sentence);
		}

		String contentToSave = this.configLanguage.saveToString();
		Writer out = new OutputStreamWriter(new FileOutputStream(this.fileLanguage), "UTF-8");
		out.write(contentToSave);
		out.flush();
		out.close();
	}

	/**
	 * Creates the path and set the value.
	 * 
	 * @param fc a instance of FileConfiguration.
	 * @param f a instance of File.
	 * @param path the path to be created.
	 * @param value the given value to be included.
	 */
	public void setStringbyPath(FileConfiguration fc, File f, String path, Object value) {
		fc.set(path, value.toString());
		try {
			fc.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *  Get the value from the config by path, if path not exists, it will be created with the given standard value.
	 * 
	 * @param fc a instance of FileConfiguration.
	 * @param file a instance of File.
	 * @param path the path to the value in the file.
	 * @param stdValue the standard content, will be return if the path not exists.
	 * @return object.
	 */
	public String getStringbyPath(FileConfiguration fc, File file, String path, Object stdValue, boolean addMissing) {
		if (!fc.contains(path)) {
			if (addMissing) {
				this.setStringbyPath(fc, file, path, stdValue);
			}
			return stdValue.toString();
		}
		return fc.getString(path);
	}

	/**
	 * Creates the world and the tower of SkyBlock.
	 * 
	 * @return world instance of SkyBlock
	 */
	public static World getSkyBlockWorld() {
		if (skyBlockWorld == null) {
			boolean folderExists = new File(Settings.worldName).exists();
			skyBlockWorld = WorldCreator.name(Settings.worldName).type(WorldType.FLAT).environment(Environment.NORMAL).generator(new SkyBlockChunkGenerator()).createWorld();
			if (!folderExists) {
				File f = new File(Settings.towerFileName);
				if (f.exists() && f.isFile()) {
					try {
						CreateNewIsland.createStructure(new Location(getSkyBlockWorld(), 0, Settings.towerYHeight, 0), f);
						skyBlockWorld.setSpawnLocation(0, skyBlockWorld.getHighestBlockYAt(0, 0), 0);
						return skyBlockWorld;
					} catch (Exception e) {
						e.printStackTrace();
						SkyBlockMultiplayer.createSpawnTower();
						skyBlockWorld.setSpawnLocation(1, skyBlockWorld.getHighestBlockYAt(1, 1), 1);
						return skyBlockWorld;
					}
				}

				f = new File(SkyBlockMultiplayer.getInstance().getDataFolder(), Settings.towerFileName);
				if (f.exists() && f.isFile()) {
					try {
						CreateNewIsland.createStructure(new Location(getSkyBlockWorld(), 0, Settings.towerYHeight, 0), f);
						skyBlockWorld.setSpawnLocation(0, skyBlockWorld.getHighestBlockYAt(0, 0), 0);
						// System.out.println("Spawn set: " + skyBlockWorld.setSpawnLocation(0, skyBlockWorld.getHighestBlockYAt(0, 0), 0) + " location = " + SkyBlockMultiplayer.getInstance().getStringLocation(skyBlockWorld.getSpawnLocation()));
						return skyBlockWorld;
					} catch (Exception e) {
						e.printStackTrace();
						SkyBlockMultiplayer.createSpawnTower();
						skyBlockWorld.setSpawnLocation(1, skyBlockWorld.getHighestBlockYAt(1, 1), 1);
						return skyBlockWorld;
					}
				}

				SkyBlockMultiplayer.createSpawnTower();
				skyBlockWorld.setSpawnLocation(1, skyBlockWorld.getHighestBlockYAt(1, 1), 1);
				return skyBlockWorld;
			}
		}
		return skyBlockWorld;
	}

	/**
	 * Clear armor content.
	 * 
	 * @param player
	 */
	public void clearArmorContents(Player player) {
		player.getInventory().setArmorContents(new ItemStack[player.getInventory().getArmorContents().length]);
	}

	private ArrayList<File> sfiles;

	/**
	 * Get all files, directories inside of the given path.
	 * 
	 * @param path the directory. 
	 */
	public void getAllFiles(String path) {

		File dirpath = new File(path);
		if (!dirpath.exists()) {
			return;
		}

		for (File f : dirpath.listFiles()) {
			try {
				if (!f.isDirectory()) {
					this.sfiles.add(f);
				} else {
					this.getAllFiles(f.getAbsolutePath());
				}
			} catch (Exception ex) {
				this.log.warning(ex.getMessage());
			}
		}
	}

	/**
	 * Check if player is on tower.
	 * 
	 * @param player
	 * @return boolean true if player is on tower, false if not
	 */
	public boolean playerIsOnTower(Player player) {
		int px = player.getLocation().getBlockX();
		int pz = player.getLocation().getBlockZ();

		if (px >= -20 && px <= 20 && pz >= -20 && pz <= 20) {
			return true;
		}
		return false;
	}

	/**
	 * Check if a location is on tower.
	 * 
	 * @param player
	 * @return boolean true if player is on tower, false if not
	 */
	public boolean locationIsOnTower(Location l) {
		int px = l.getBlockX();
		int pz = l.getBlockZ();

		if (px >= -20 && px <= 20 && pz >= -20 && pz <= 20) {
			return true;
		}
		return false;
	}

	/**
	 * Comparing part of a player name with all players
	 * 
	 * @param partName
	 * @return
	 */
	public String getFullPlayerName(String partName) {
		int amount = 0;
		String pName = "";
		for (Player p : this.getServer().getOnlinePlayers()) {
			if (p.getName().toLowerCase().startsWith(partName.toLowerCase())) {
				amount++;
				pName = p.getName();
			}
		}
		if (amount == 1)
			return pName;
		else if (amount > 1)
			return "0";

		// check PlayerInfo2, also with offline players
		amount = 0;
		pName = "";
		for (String playerName : Settings.lstPlayerInfo2.keySet()) {
			if (playerName.toLowerCase().startsWith(partName.toLowerCase())) {
				amount++;
				pName = playerName;
			}
		}
		if (amount == 1)
			return pName;
		else if (amount > 1)
			return "0";
		return "-1";
	}

	/**
	 * Returns a location who 2 blocks are air.
	 * 
	 * @param l
	 * @return
	 */
	public Location getYLocation(Location l) {
		for (int y = 0; y < 254; y++) {
			int px = l.getBlockX();
			int py = y;
			int pz = l.getBlockZ();
			Block b1 = new Location(l.getWorld(), px, py, pz).getBlock();
			Block b2 = new Location(l.getWorld(), px, py + 1, pz).getBlock();
			Block b3 = new Location(l.getWorld(), px, py + 2, pz).getBlock();
			if (!b1.getType().equals(Material.AIR) && b2.getType().equals(Material.AIR) && b3.getType().equals(Material.AIR)) {
				return b2.getLocation();
			}
		}
		return l;
	}

	public Location getSafeHomeLocation(PlayerInfo p) {
		// a) check original location
		Location home = null;
		if (p.getHomeLocation() == null) {
			home = p.getIslandLocation();
		} else {
			home = p.getHomeLocation();
		}

		if (this.isSafeLocation(home)) {
			return home;
		}
		// b) check if a suitable y exists on this x and z
		for (int y = home.getBlockY(); y > 0; y--) {
			Location n = new Location(home.getWorld(), home.getBlockX(), y, home.getBlockZ());
			if (this.isSafeLocation(n)) {
				return n;
			}
		}
		for (int y = home.getBlockY(); y < 255; y++) {
			Location n = new Location(home.getWorld(), home.getBlockX(), y, home.getBlockZ());
			if (this.isSafeLocation(n)) {
				return n;
			}
		}

		// c) check island Location
		Location island = p.getIslandLocation();
		if (this.isSafeLocation(island)) {
			return island;
		}

		for (int y = island.getBlockY(); y > 0; y--) {
			Location n = new Location(island.getWorld(), island.getBlockX(), y, island.getBlockZ());
			if (this.isSafeLocation(n)) {
				return n;
			}
		}
		for (int y = island.getBlockY(); y < 255; y++) {
			Location n = new Location(island.getWorld(), island.getBlockX(), y, island.getBlockZ());
			if (this.isSafeLocation(n)) {
				return n;
			}
		}
		return null;
	}

	public Location getSafeHomeLocation(PlayerInfo2 p) {
		// a) check original location
		Location home = null;
		if (p.getIslandLocation() == null) {
			home = p.getIslandLocation();
		} else {
			home = p.getHomeLocation();
		}

		if (this.isSafeLocation(home)) {
			return home;
		}
		// b) check if a suitable y exists on this x and z
		for (int y = home.getBlockY(); y > 0; y--) {
			Location n = new Location(home.getWorld(), home.getBlockX(), y, home.getBlockZ());
			if (this.isSafeLocation(n)) {
				return n;
			}
		}
		for (int y = home.getBlockY(); y < 255; y++) {
			Location n = new Location(home.getWorld(), home.getBlockX(), y, home.getBlockZ());
			if (this.isSafeLocation(n)) {
				return n;
			}
		}

		// c) check island Location
		Location island = p.getIslandLocation();
		if (this.isSafeLocation(island)) {
			return island;
		}

		for (int y = island.getBlockY(); y > 0; y--) {
			Location n = new Location(island.getWorld(), island.getBlockX(), y, island.getBlockZ());
			if (this.isSafeLocation(n)) {
				return n;
			}
		}
		for (int y = island.getBlockY(); y < 255; y++) {
			Location n = new Location(island.getWorld(), island.getBlockX(), y, island.getBlockZ());
			if (this.isSafeLocation(n)) {
				return n;
			}
		}
		return null;
	}

	public boolean isSafeLocation(Location l) {
		if (l == null) {
			return false;
		}

		Block ground = l.getBlock().getRelative(BlockFace.DOWN);
		Block air1 = l.getBlock();
		Block air2 = l.getBlock().getRelative(BlockFace.UP);

		if (ground.getType().equals(Material.AIR))
			return false;
		if (ground.getType().equals(Material.LAVA))
			return false;
		if (ground.getType().equals(Material.STATIONARY_LAVA))
			return false;
		if (ground.getType().equals(Material.WATER))
			return false;
		if (ground.getType().equals(Material.STATIONARY_WATER))
			return false;
		if (air1.getType().equals(Material.AIR) && air2.getType().equals(Material.AIR))
			return true;
		return false;
	}

	/**
	 * Remove creatures from chunk at and around the given location.
	 * 
	 * @param l
	 */
	public void removeCreatures(Location l) {
		if (!Settings.removeCreaturesByTeleport || l == null) {
			return;
		}

		int px = l.getBlockX();
		int py = l.getBlockY();
		int pz = l.getBlockZ();
		for (int x = -1; x <= 1; x++) {
			for (int z = -1; z <= 1; z++) {
				Chunk c = l.getWorld().getChunkAt(new Location(l.getWorld(), px + x * 16, py, pz + z * 16));
				for (Entity e : c.getEntities()) {
					if (e.getType() == EntityType.SPIDER || e.getType() == EntityType.CREEPER || e.getType() == EntityType.ENDERMAN || e.getType() == EntityType.SKELETON || e.getType() == EntityType.ZOMBIE) {
						e.remove();
					}
				}
			}
		}
	}

	/**
	 * Remove a island from SkyBlock.
	 * 
	 * @param l given location
	 */
	public void removeIsland(Location l) {
		if (l != null) {
			int px = l.getBlockX();
			int py = l.getBlockY();
			int pz = l.getBlockZ();
			for (int x = -15; x <= 15; x++) {
				for (int y = -15; y <= 15; y++) {
					for (int z = -15; z <= 15; z++) {
						Block b = new Location(l.getWorld(), px + x, py + y, pz + z).getBlock();
						if (!b.getType().equals(Material.AIR)) {
							if (b.getType().equals(Material.CHEST)) {
								Chest c = (Chest) b.getState();
								ItemStack[] items = new ItemStack[c.getInventory().getContents().length];
								c.getInventory().setContents(items);
							} else if (b.getType().equals(Material.FURNACE)) {
								Furnace f = (Furnace) b.getState();
								ItemStack[] items = new ItemStack[f.getInventory().getContents().length];
								f.getInventory().setContents(items);
							} else if (b.getType().equals(Material.DISPENSER)) {
								Dispenser d = (Dispenser) b.getState();
								ItemStack[] items = new ItemStack[d.getInventory().getContents().length];
								d.getInventory().setContents(items);
							}
							b.setType(Material.AIR);
						}
					}
				}
			}
		}
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new SkyBlockChunkGenerator();
	}

	private static void makeBlock(int x, int y, int z, Material m) {
		if (!SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(x, y, z).getType().equals(m)) {
			SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(x, y, z).setType(m);
		}
	}

	private static void quader(int x, int y, int z, Material m) {
		if (y < 0)
			return;
		SkyBlockMultiplayer.makeBlock(x, y, z, m);
		SkyBlockMultiplayer.makeBlock(x, y, z + 1, m);
		SkyBlockMultiplayer.makeBlock(x + 1, y, z, m);
		SkyBlockMultiplayer.makeBlock(x + 1, y, z + 1, m);
	}

	public static void createSpawnTower() {
		int yStart = 2;
		int yEnde = 90;
		int[][] lavatreppe = { { 2, 0 }, { 2, 0 }, { 0, 2 }, { 0, 2 }, { -2, 0 }, { -2, 0 }, { 0, -2 }, { 0, -2 } };
		int i = 0;
		int x = -2;
		int z = -2;
		for (int y = yStart; y < yEnde - 2; y++) {
			// create obsidan tower
			SkyBlockMultiplayer.quader(0, y, 0, Material.OBSIDIAN);
			// create wall inside
			for (int xw = -2; xw < 4; xw++) {
				SkyBlockMultiplayer.makeBlock(xw, y, -2, Material.GLASS);
				SkyBlockMultiplayer.makeBlock(xw, y, 3, Material.GLASS);
			}
			for (int zw = -2; zw < 4; zw++) {
				SkyBlockMultiplayer.makeBlock(-2, y, zw, Material.GLASS);
				SkyBlockMultiplayer.makeBlock(3, y, zw, Material.GLASS);
			}
			// create lava steps
			SkyBlockMultiplayer.quader(x, y, z, Material.getMaterial(43));
			x += lavatreppe[i][0];
			z += lavatreppe[i][1];
			i++;
			if (i == lavatreppe.length)
				i = 0;

		}
		// water steps
		i = 0;
		x = -2;
		z = -2;
		for (int y = yStart; y <= yEnde; y++) {
			SkyBlockMultiplayer.quader(x, y - 3, z, Material.GLASS);
			x += lavatreppe[i][0];
			z += lavatreppe[i][1];
			i++;
			if (i == lavatreppe.length)
				i = 0;
		}

		// place the full stepes
		i = 0;
		x = -2;
		z = -4;
		int[][] stairsWhole = { { 4, 0 }, { 2, 2 }, { 0, 4 }, { -2, 2 }, { -4, 0 }, { -2, -2 }, { 0, -4 }, { 2, -2 } };
		for (int y = yStart + 1; y < yEnde - 1; y++) {
			SkyBlockMultiplayer.quader(x, y, z, Material.getMaterial(43));
			x += stairsWhole[i][0];
			z += stairsWhole[i][1];
			i++;
			if (i == stairsWhole.length)
				i = 0;
		}
		// place the half steps
		i = 0;
		x = -4;
		z = -4;
		int[][] stairsHalf = { { 4, 0 }, { 4, 0 }, { 0, 4 }, { 0, 4 }, { -4, 0 }, { -4, 0 }, { 0, -4 }, { 0, -4 } };
		for (int y = yStart + 1; y < yEnde - 1; y++) {
			SkyBlockMultiplayer.quader(x, y, z, Material.getMaterial(44));
			x += stairsHalf[i][0];
			z += stairsHalf[i][1];
			i++;
			if (i == stairsHalf.length)
				i = 0;
		}

		// place lava
		SkyBlockMultiplayer.makeBlock(2, yEnde - 3, 2, Material.LAVA);
		// place water
		SkyBlockMultiplayer.makeBlock(-1, yEnde - 3, 0, Material.WATER);

		// create roof
		for (x = -2; x < 4; x++) {
			for (z = -2; z < 4; z++) {
				SkyBlockMultiplayer.makeBlock(x, yEnde - 2, z, Material.getMaterial(43));
			}
		}

		// fence
		for (x = -2; x < 4; x++) {
			SkyBlockMultiplayer.makeBlock(x, yEnde - 1, -2, Material.FENCE);
		}
		for (z = -2; z < 4; z++) {
			SkyBlockMultiplayer.makeBlock(-2, yEnde - 1, z, Material.FENCE);
			SkyBlockMultiplayer.makeBlock(3, yEnde - 1, z, Material.FENCE);
		}

		// torches
		SkyBlockMultiplayer.makeBlock(-2, yEnde, -2, Material.TORCH);
		SkyBlockMultiplayer.makeBlock(-2, yEnde, 3, Material.TORCH);
		SkyBlockMultiplayer.makeBlock(3, yEnde, -2, Material.TORCH);
		SkyBlockMultiplayer.makeBlock(3, yEnde, 3, Material.TORCH);

		// create the tower floor
		for (x = -2; x < 4; x++) {
			for (z = -2; z < 4; z++) {
				for (int y = 0; y < yStart; y++) {
					SkyBlockMultiplayer.makeBlock(x, y, z, Material.AIR);
				}
				SkyBlockMultiplayer.makeBlock(x, yStart, z, Material.getMaterial(43));
			}
		}

		//create signs
		SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(1, yEnde - 1, 2).setType(Material.SIGN_POST);
		Sign s1 = (Sign) SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(1, yEnde - 1, 2).getState();
		s1.setLine(0, Language.MSGS_SIGN1LINE1.sentence);
		s1.setLine(1, Language.MSGS_SIGN1LINE2.sentence);
		s1.setLine(2, Language.MSGS_SIGN1LINE3.sentence);
		s1.update();
		s1.getBlock().setData((byte) 8);
		SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(0, yEnde - 1, 2).setType(Material.SIGN_POST);
		Sign s2 = (Sign) SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(0, yEnde - 1, 2).getState();
		s2.setLine(0, Language.MSGS_SIGN2LINE1.sentence);
		s2.setLine(1, Language.MSGS_SIGN2LINE2.sentence);
		s2.setLine(2, Language.MSGS_SIGN2LINE3.sentence);
		s2.setLine(3, Language.MSGS_SIGN2LINE4.sentence);
		s2.update();
		s2.getBlock().setData((byte) 8);
	}

	public static String getOwner(Location l) {
		/*for (PlayerInfo pi : Settings.players.values()) {
			if (pi != null) {
				if (pi.getIslandLocation() != null) {
					int islandX = pi.getIslandLocation().getBlockX();
					int islandZ = pi.getIslandLocation().getBlockZ();

					int blockX = l.getBlockX();
					int blockZ = l.getBlockZ();

					int dist = 0;
					if (Settings.build_withProtectedBorder) {
						dist = (Settings.distanceIslands / 2) - 3;
					} else {
						dist = Settings.distanceIslands / 2;
					}

					if (islandX + dist >= blockX && islandX - dist <= blockX) {
						if (islandZ + dist >= blockZ && islandZ - dist <= blockZ) {
							return pi;
						}
					}
				}
			}
		}
		return null;*/

		for (String owner : Settings.islandsAndOwners.keySet()) {
			Location locIsland = Settings.islandsAndOwners.get(owner);
			if (locIsland != null) {
				int islandX = locIsland.getBlockX();
				int islandZ = locIsland.getBlockZ();

				int blockX = l.getBlockX();
				int blockZ = l.getBlockZ();

				int dist = 0;
				if (Settings.build_withProtectedBorder) {
					dist = (Settings.distanceIslands / 2) - 3;
				} else {
					dist = Settings.distanceIslands / 2;
				}

				if (islandX + dist >= blockX && islandX - dist <= blockX) {
					if (islandZ + dist >= blockZ && islandZ - dist <= blockZ) {
						return owner;
					}
				}
			}
		}
		return null;
	}

	public static boolean checkBuildPermission(PlayerInfo pi, Location l) {

		if (l == null || pi == null) {
			return false;
		}

		// get PlayerInfo2
		if (!Settings.lstPlayerInfo2.containsKey(pi.getPlayerName()))
			return false;
		PlayerInfo2 player = Settings.lstPlayerInfo2.get(pi.getPlayerName());

		return player.checkBuildPermission(l);
	}

	/*public static boolean canPlayerDoThat(PlayerInfo pi, Location l) {
		if (pi == null || pi.getIslandLocation() == null) {
			return false;
		}
		int islandX = pi.getIslandLocation().getBlockX();
		int islandZ = pi.getIslandLocation().getBlockZ();

		int blockX = l.getBlockX();
		int blockZ = l.getBlockZ();

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
	}*/
}
