package me.lukas.skyblockmultiplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

import me.lukas.skyblockmultiplayer.listeners.EntityDeath;
import me.lukas.skyblockmultiplayer.listeners.PlayerInteract;
import me.lukas.skyblockmultiplayer.listeners.PlayerPlaceBlockListener;
import me.lukas.skyblockmultiplayer.listeners.PlayerRespawn;
import me.lukas.skyblockmultiplayer.listeners.PlayerTeleport;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyBlockMultiplayer extends JavaPlugin {
	public PluginDescriptionFile pluginFile;
	public Logger log;

	static World skyBlockWorld = null;
	public static SkyBlockMultiplayer instance;

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

		// register command
		this.getCommand("skyblock").setExecutor(new SkyBlockCommand());

		this.log.info("v" + pluginFile.getVersion() + " enabled.");
	}

	/**
	 * Register the events
	 * 
	 */
	public void registerEvents() {
		PluginManager manager = this.getServer().getPluginManager();
		manager.registerEvents(new PlayerPlaceBlockListener(this), this);
		/*manager.registerEvents(new PlayerBreackBlockListener(this), this); // this 2 events will not be removed
		manager.registerEvents(new PlayerUseBucketListener(this), this);*/// until I know that it works fine...
		manager.registerEvents(new EntityDeath(this), this);
		manager.registerEvents(new PlayerRespawn(this), this);
		manager.registerEvents(new PlayerInteract(this), this);
		manager.registerEvents(new PlayerTeleport(this), this);
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
			Settings.gameModeSelected = Settings.GAMEMODE.BUILD;
			Settings.pvp_livesPerIsland = 1;
			Settings.pvp_islandsPerPlayer = 1;
			Settings.build_respawnWithInventory = true;
			Settings.build_withProtectedArea = false;
			Settings.build_allowEnderpearl = false;
			Settings.worldName = this.pluginFile.getName();
			Settings.messagesOutside = false;
			Settings.islandFileName = "";
			Settings.towerFileName = "";
			Settings.towerHeight = 80;

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
				Settings.towerHeight = Integer.parseInt(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_SCHEMATIC_TOWER_HEIGHT.path, 80, true));
				if (Settings.towerHeight < 0) {
					Settings.towerHeight = 80;
				}
			} catch (Exception e) {
				Settings.towerHeight = 80;
			}

			Settings.itemsChest = itemsChest;
			Settings.skyBlockOnline = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_SKYBLOCKONLINE.path, true, true));
			Settings.language = this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_LANGUAGE.path, "english", true);
			Settings.allowContent = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_ALLOWCONTENT.path, false, true));
			Settings.gameModeSelected = Settings.GAMEMODE.valueOf(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_GAMEMODE.path, "build", true).toUpperCase());
			Settings.build_respawnWithInventory = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_BUILD_RESPAWNWITHINVENTORY.path, true, true));
			Settings.build_withProtectedArea = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_BUILD_WITHPROTECTEDAREA.path, true, true));
			Settings.build_allowEnderpearl = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_BUILD_ALLOWENDERPEARL.path, false, true));
			Settings.build_withProtectedBorder = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_BUILD_WITHPROTECTEDBORDER.path, true, true));
			Settings.worldName = this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_WORLDNAME.path, this.pluginFile.getName(), true);
			Settings.closed = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_CLOSED.path, false, true));
			Settings.messagesOutside = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_MESSAGES_OUTSIDE.path, false, true));
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
					if (pi.getIslandLocation() != null) {
						Settings.players.put(f, pi);
					}
				}
			}
		}
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

		if (!this.fileLanguage.exists()) {
			this.fileLanguage.createNewFile(); //create file
			this.writeLanguageConfig(); //write standard language
		} else {
			Scanner scanner = new Scanner(new FileInputStream(this.fileLanguage), "Cp1252");
			String contentToRead = "";
			while (scanner.hasNextLine()) {
				contentToRead += scanner.nextLine() + System.getProperty("line.separator");
			}
			scanner.close();

			// this.configLanguage.load(this.fileLanguage);
			this.configLanguage.loadFromString(contentToRead);
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
				Writer out = new OutputStreamWriter(new FileOutputStream(this.fileLanguage), "Cp1252");
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
		Writer out = new OutputStreamWriter(new FileOutputStream(this.fileLanguage), "Cp1252");
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
				File f = new File(SkyBlockMultiplayer.instance.getDataFolder(), Settings.towerFileName);
				if (f.exists() && f.isFile()) {
					try {
						CreateNewIsland.createStructure(new Location(getSkyBlockWorld(), 0, 80, 0), new File(SkyBlockMultiplayer.instance.getDataFolder(), Settings.towerFileName));
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					SkyBlockMultiplayer.createSpawnTower();
				}
			}
			skyBlockWorld.setSpawnLocation(1, SkyBlockMultiplayer.getSkyBlockWorld().getHighestBlockYAt(1, 1), 1);
		}
		return skyBlockWorld;
	}

	/**
	 * Clear armor content.
	 * 
	 * @param player
	 */
	public void clearArmorContents(Player player) {
		ItemStack[] items = new ItemStack[player.getInventory().getArmorContents().length];
		player.getInventory().setArmorContents(items);
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
	 * Check if player is on tower.
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
		return "-1";
	}

	public void removeIsland(Location l) {
		System.out.println("called");
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

	public static PlayerInfo getOwner(Location l) {
		for (PlayerInfo pi : Settings.players.values()) {
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
		return null;
	}

	public static boolean canPlayerDoThat(PlayerInfo pi, Location l) {
		if (pi == null) {
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
	}
}
