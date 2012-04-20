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
import java.util.HashMap;
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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyBlockMultiplayer extends JavaPlugin {
	private PluginDescriptionFile pluginFile;
	public Logger log;

	private static World skyBlockWorld = null;
	public static SkyBlockMultiplayer instance;

	private FileConfiguration configPlugin;
	private File filePlugin;

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
	private void loadLanguageConfig() throws Exception {
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
						new CreateNewIsland().createStructure(new Location(getSkyBlockWorld(), 0, 80, 0), new File(SkyBlockMultiplayer.instance.getDataFolder(), Settings.towerFileName));
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
	 * If a command is called, this code will be running.
	 * 	
	 * @param sender that types the com mand.
	 * @param cmd the typed command.
	 * @param label 
	 * @param  args array that includes all given arguments.
	 * @return boolean command was successfull or not
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("skyblock")) {
			if (args.length == 0) {
				sender.sendMessage(this.pluginFile.getName() + " v" + this.pluginFile.getVersion());
				sender.sendMessage(Language.MSGS_SKYBLOCK.sentence);
				return true;
			}

			if (args[0].equalsIgnoreCase("show")) {
				sender.sendMessage("" + Settings.messagesOutside);
				return true;
			}

			// only for testing
			/*if (args[0].equalsIgnoreCase("create")) {
				new CreateNewIsland(Integer.parseInt(args[1]));
				return true;
			}

			if (args[0].equalsIgnoreCase("amount")) {
				sender.sendMessage("" + new CreateNewIsland().getAmountOfIslands());
				return true;
			}*/

			if (args[0].equalsIgnoreCase("tower")) {
				if (args.length == 2) {
					if (args[1].equalsIgnoreCase("recreate")) {
						if (!Permissions.SKYBLOCK_BUILD.has(sender)) {
							return this.notAuthorized(sender);
						}
						SkyBlockMultiplayer.createSpawnTower();
						sender.sendMessage(this.pName + Language.MSGS_SPAWN_TOWER_RECREATED.sentence);
						return true;
					}
				}
			}

			if (args[0].equalsIgnoreCase("set")) {
				if (!Permissions.SKYBLOCK_SET.has(sender)) {
					return this.notAuthorized(sender);
				}

				if (args.length < 2) {
					sender.sendMessage(this.pName + Language.MSGS_WRONG_ARGS.sentence);
					return true;
				}
				if (args[1].equalsIgnoreCase("offline")) {
					return this.setSkyBlockOffline(sender);
				}
				if (args[1].equalsIgnoreCase("online")) {
					return this.setSkyBlockOnline(sender);
				}
				if (args[1].equalsIgnoreCase("closed")) {
					return this.setClosed(sender);
				}
				if (args[1].equalsIgnoreCase("opened")) {
					return this.setOpened(sender);
				}
				if (args.length < 3) {
					sender.sendMessage(this.pName + Language.MSGS_WRONG_ARGS.sentence);
					return true;
				}
				if (args[1].equalsIgnoreCase("language")) {
					return this.setLanguage(sender, args[2]);
				}
				if (args[1].equalsIgnoreCase("gamemode") || args[1].equalsIgnoreCase("gm")) {
					return this.setGameMode(sender, args[2]);
				}
			}
			if (args[0].equalsIgnoreCase("reset")) {
				return this.resetSkyBlock(sender);
			}
			if (args[0].equalsIgnoreCase("reload")) {
				if (args.length < 2) {
					sender.sendMessage(this.pName + Language.MSGS_WRONG_ARGS.sentence);
					return true;
				}
				if (args[1].equalsIgnoreCase("config")) {
					return this.reloadConfig(sender);
				}
				if (args[1].equalsIgnoreCase("language")) {
					return this.reloadLanguage(sender);
				}
			}
			if (args[0].equalsIgnoreCase("status")) {
				return this.getStatus(sender);
			}
			if (args[0].equalsIgnoreCase("help")) {
				if (args.length == 1) {
					return this.getListCommands(sender, "1");
				} else {
					return this.getListCommands(sender, args[1]);
				}
			}

			if (args[0].equalsIgnoreCase("remove")) {
				if (!Permissions.SKYBLOCK_REMOVE_ISLAND.has(sender)) {
					return this.notAuthorized(sender);
				}

				if (args.length == 0) {
					sender.sendMessage(this.pName + Language.MSGS_WRONG_ARGS.sentence);
					return true;
				}

				int islandNumber = 0;
				try {
					islandNumber = Integer.parseInt(args[1]);
				} catch (Exception e) {
					sender.sendMessage(this.pName + Language.MSGS_INVALID_ISLAND_NUMBER.sentence);
					return true;
				}

				if (islandNumber <= 0 || islandNumber > CreateNewIsland.getAmountOfIslands()) {
					sender.sendMessage(this.pName + Language.MSGS_INVALID_ISLAND_NUMBER.sentence);
					return true;
				}

				this.removeIsland(new CreateNewIsland().getIslandLocation(islandNumber));
				sender.sendMessage(this.pName + "Island removed!");
				return true;
			}

			if (!(sender instanceof Player)) {
				return true;
			}

			Player player = (Player) sender;

			if (args[0].equalsIgnoreCase("setowner")) {
				if (args.length == 1) {
					player.sendMessage(this.pName + Language.MSGS_WRONG_ARGS.sentence);
					return true;
				}

				if (!Permissions.SKYBLOCK_OWNER_SET.has(sender)) {
					return this.notAuthorized(sender);
				}

				int islandNumber = -1;
				try {
					islandNumber = Integer.parseInt(args[1]);
					if (islandNumber > CreateNewIsland.getAmountOfIslands() || islandNumber <= 0) {
						player.sendMessage(this.pName + Language.MSGS_INVALID_ISLAND_NUMBER.sentence);
						return true;
					}
				} catch (Exception e) {
					player.sendMessage(this.pName + Language.MSGS_INVALID_ISLAND_NUMBER.sentence);
					return true;
				}

				String res = this.getFullPlayerName(args[2]);
				if (res.equalsIgnoreCase("-1")) {
					player.sendMessage(this.pName + Language.MSGS_WRONGE_PLAYER_NAME.sentence);
					return true;
				}
				if (res.equalsIgnoreCase("0")) {
					player.sendMessage(this.pName + Language.MSGS_BETTER_SPECIFY.sentence);
					return true;
				}

				PlayerInfo pi = new PlayerInfo(res);
				if (Settings.players.containsKey(res)) {
					PlayerInfo oldPi = Settings.players.get(res);
					pi.setOldLocation(oldPi.getOldLocation());
					pi.setOldInventory(oldPi.getOldInventory());
					pi.setOldArmor(pi.getOldArmor());
					pi.setOldExp(oldPi.getOldExp());
					pi.setOldLevel(oldPi.getOldLevel());
					pi.setOldFood(oldPi.getOldFood());
					pi.setOldHealth(oldPi.getOldHealth());
					Settings.players.remove(res);
				}

				pi.setIslandLocation(new CreateNewIsland().getIslandLocation(islandNumber));
				pi.setHasIsland(true);

				this.writePlayerFile(res, pi);

				Settings.players.put(res, pi);
				player.sendMessage(this.pName + Language.MSGS_CHANGED_OWNER_TO.sentence + res);
				return true;
			}

			if (args[0].equalsIgnoreCase("tower")) {
				if (!player.getWorld().getName().equals(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
					player.sendMessage(this.pName + Language.MSGS_ONLY_INSIDE_OF_SB.sentence);
					return true;
				}

				if (this.playerIsOnTower(player)) {
					return true;
				}

				PlayerInfo pi = Settings.players.get(player.getName());
				if (pi == null) {
					pi = this.readPlayerFile(player.getName());
					if (pi == null) {
						pi = new PlayerInfo(player.getName());
						pi.setOldLocation(player.getLocation());

						if (!Settings.allowContent) {
							pi.setIslandInventory(player.getInventory().getContents());
							pi.setIslandArmor(player.getInventory().getArmorContents());
							pi.setIslandExp(player.getExp());
							pi.setIslandLevel(player.getLevel());
							pi.setIslandFood(player.getFoodLevel());
							pi.setIslandHealth(player.getHealth());

							player.getInventory().setContents(pi.getOldInventory());
							player.getInventory().setArmorContents(pi.getOldArmor());
							player.setExp(pi.getOldExp());
							player.setLevel(pi.getOldLevel());
							player.setFoodLevel(20);
							player.setHealth(player.getMaxHealth());
						}

						Settings.players.put(player.getName(), pi);
						this.writePlayerFile(player.getName(), pi);
						player.teleport(player.getWorld().getSpawnLocation());
						player.sendMessage(this.pName + Language.MSGS_BACK_ON_TOWER.sentence);
						return true;

					}
					Settings.players.put(player.getName(), pi);
				}

				if (!Settings.allowContent) {
					pi.setIslandInventory(player.getInventory().getContents());
					pi.setIslandArmor(player.getInventory().getArmorContents());
					pi.setIslandExp(player.getExp());
					pi.setIslandLevel(player.getLevel());
					pi.setIslandFood(player.getFoodLevel());
					pi.setIslandHealth(player.getHealth());

					player.getInventory().setContents(pi.getOldInventory());
					player.getInventory().setArmorContents(pi.getOldArmor());
					player.setExp(pi.getOldExp());
					player.setLevel(pi.getOldLevel());

					// check food od player
					if (pi.getOldFood() <= 0) {
						player.setFoodLevel(20);
						pi.setOldFood(20);
					} else {
						player.setFoodLevel(pi.getOldFood());
					}

					// check hp of player
					if (pi.getOldHealth() <= 0) {
						player.setHealth(player.getMaxHealth());
						pi.setOldHealth(player.getMaxHealth());
					} else {
						player.setHealth(pi.getOldHealth());
					}
				}

				this.writePlayerFile(player.getName(), pi);

				player.teleport(player.getWorld().getSpawnLocation());
				player.sendMessage(this.pName + Language.MSGS_BACK_ON_TOWER.sentence);
				return true;
			}

			if (args[0].equalsIgnoreCase("join")) {
				return this.playerJoin(player);
			}
			if (args[0].equalsIgnoreCase("start")) {
				return this.playerStart(player);
			}
			if (args[0].equalsIgnoreCase("leave")) {
				return this.playerLeave(player);
			}
			if (args[0].equalsIgnoreCase("newIsland")) {
				String s = "";
				if (args.length < 2) {
					s = "";
				} else {
					s = args[1];
				}
				return this.playerNewIsland(player, s);
			}

			if (args[0].equalsIgnoreCase("home")) {
				if (args.length == 1) {
					PlayerInfo pi = Settings.players.get(player.getName());
					if (pi == null) {
						pi = this.readPlayerFile(player.getName());
						if (pi == null) {
							return true;
						}
						Settings.players.put(player.getName(), pi);
					}
					if (pi.getHomeLocation() == null) {
						player.teleport(pi.getIslandLocation());
					} else {
						Location l = pi.getHomeLocation();
						if (l.getBlockY() == 0 && l.getBlock().getType().equals(Material.AIR)) {
							player.teleport(pi.getIslandLocation());
						} else {
							player.teleport(pi.getHomeLocation());
						}
					}
					return true;
				}

				if (args[1].equalsIgnoreCase("set")) {
					if (!player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
						return true;
					}

					PlayerInfo pi = Settings.players.get(player.getName());
					if (pi == null) {
						pi = this.readPlayerFile(player.getName());
						if (pi == null) {
							return true;
						}
						Settings.players.put(player.getName(), pi);
					}

					if (SkyBlockMultiplayer.canPlayerDoThat(pi, player.getLocation())) {
						pi.setHomeLocation(player.getLocation());
						this.writePlayerFile(player.getName(), pi);
						player.sendMessage(this.pName + Language.MSGS_SPAWN_LOCATION_CHANGED.sentence);
						return true;
					} else {
						player.sendMessage(this.pName + Language.MSGS_HOME_CHANGE_ONYL_INOWN_AREA.sentence);
						return true;
					}
				}

				if (Settings.gameModeSelected == Settings.GAMEMODE.PVP) {
					player.sendMessage(this.pName + Language.MSGS_ONLY_INBUILD_MODE.sentence);
					return true;
				}

				if (args[1].equalsIgnoreCase("list")) {
					PlayerInfo pi = Settings.players.get(player.getName());
					if (pi == null) {
						pi = this.readPlayerFile(player.getName());
						if (pi == null) {
							return true;
						}
						Settings.players.put(player.getName(), pi);
					}

					String list = "";
					for (int i = 0; i < pi.getFriends().size(); i++) {
						if (i != 0) {
							list += ", ";
						}
						list += pi.getFriends().get(i);
					}
					player.sendMessage(list);
					return true;
				}

				if (args[1].equalsIgnoreCase("join")) {
					if (args.length == 2) {
						player.sendMessage(this.pName + Language.MSGS_WRONG_ARGS.sentence);
						return true;
					}

					PlayerInfo pi = Settings.players.get(player.getName());
					if (pi == null) {
						pi = this.readPlayerFile(player.getName());
						if (pi == null) {
							player.sendMessage(this.pName + Language.MSGS_NO_ISLAND_TELEPORT_IMPOSSIBLE.sentence);
							return true;
						}
						Settings.players.put(player.getName(), pi);
					}

					if (pi.getIslandLocation() == null) {
						player.sendMessage(this.pName + Language.MSGS_NO_ISLAND_TELEPORT_IMPOSSIBLE.sentence);
						return true;
					}

					String res = this.getFullPlayerName(args[2]);
					if (res.equalsIgnoreCase("-1")) {
						player.sendMessage(this.pName + Language.MSGS_WRONGE_PLAYER_NAME.sentence);
						return true;
					}
					if (res.equalsIgnoreCase("0")) {
						player.sendMessage(this.pName + Language.MSGS_BETTER_SPECIFY.sentence);
						return true;
					}

					if (player.getName().equalsIgnoreCase(res)) {
						return true;
					}

					PlayerInfo pTarget = Settings.players.get(res);
					if (pTarget == null) {
						return true;
					}

					if (!pTarget.getFriends().contains(player.getName())) {
						player.sendMessage(this.pName + Language.MSGS_NOT_FRIEND_FROM_YOU.sentence);
						return true;
					}

					if (pTarget.getHomeLocation() == null) {
						player.teleport(pTarget.getIslandLocation());
					} else {
						player.teleport(pTarget.getHomeLocation());
					}
					return true;
				}

				if (args[1].equalsIgnoreCase("add")) {
					if (args.length == 2) {
						player.sendMessage(this.pName + Language.MSGS_WRONG_ARGS.sentence);
						return true;
					}

					String res = this.getFullPlayerName(args[2]);
					if (res.equalsIgnoreCase("-1")) {
						player.sendMessage(this.pName + Language.MSGS_WRONGE_PLAYER_NAME.sentence);
						return true;
					}
					if (res.equalsIgnoreCase("0")) {
						player.sendMessage(this.pName + Language.MSGS_BETTER_SPECIFY.sentence);
						return true;
					}

					if (res.equalsIgnoreCase(player.getName())) {
						return true;
					}

					Player toAdd = this.getServer().getPlayer(res);
					if (toAdd != null) {
						toAdd.sendMessage(player.getName() + Language.MSGS_SOMEONE_ADDED_YOU.sentence);
					}

					PlayerInfo pi = Settings.players.get(player.getName());
					if (pi == null) {
						pi = this.readPlayerFile(player.getName());
						if (pi == null) {
							return true;
						}
					}

					pi.addFriend(res);

					this.writePlayerFile(player.getName(), pi);

					player.sendMessage(this.pName + Language.MSGS_FRIEND_ADDED.sentence);
					return true;
				}

				if (args[1].equalsIgnoreCase("remove")) {
					if (args.length == 2) {
						player.sendMessage(this.pName + Language.MSGS_WRONG_ARGS.sentence);
						return true;
					}

					String res = this.getFullPlayerName(args[2]);
					if (res.equalsIgnoreCase("-1")) {
						player.sendMessage(this.pName + Language.MSGS_WRONGE_PLAYER_NAME.sentence);
						return true;
					}
					if (res.equalsIgnoreCase("0")) {
						player.sendMessage(this.pName + Language.MSGS_BETTER_SPECIFY.sentence);
						return true;
					}

					PlayerInfo pi = Settings.players.get(player.getName());
					if (pi == null) {
						pi = this.readPlayerFile(player.getName());
						if (pi == null) {
							return true;
						}
					}

					pi.removeFriend(res);

					this.writePlayerFile(player.getName(), pi);

					player.sendMessage(this.pName + Language.MSGS_FRIEND_REMOVED.sentence);
					return true;
				}
			}

			player.sendMessage(this.pName + Language.MSGS_WRONG_ARGS.sentence);
			return true;
		}
		return false;
	}

	private boolean setOpened(CommandSender sender) {
		Settings.closed = false;
		sender.sendMessage(this.pName + Language.MSGS_IS_NOW_OPENED.sentence);
		return true;
	}

	private boolean setClosed(CommandSender sender) {
		Settings.closed = true;
		sender.sendMessage(this.pName + Language.MSGS_IS_NOW_CLOSED.sentence);
		return true;
	}

	private boolean setGameMode(CommandSender sender, String s) {
		if (s.equalsIgnoreCase("build")) {
			Settings.gameModeSelected = Settings.GAMEMODE.BUILD;
			this.setStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_GAMEMODE.path, "build");
			sender.sendMessage(this.pName + Language.MSGS_GAMEMODE_CHANGED.sentence);
			return true;
		}
		if (s.equalsIgnoreCase("pvp")) {
			Settings.gameModeSelected = Settings.GAMEMODE.PVP;
			this.setStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_GAMEMODE.path, "pvp");
			SkyBlockMultiplayer.createSpawnTower();
			sender.sendMessage(this.pName + Language.MSGS_GAMEMODE_CHANGED.sentence);
			return true;
		}
		sender.sendMessage(this.pName + Language.MSGS_WRONG_ARGS.sentence);
		return true;
	}

	/**
	 * Activate SkyBlock, permission needed.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	public boolean setSkyBlockOffline(CommandSender sender) {
		try {
			sender.sendMessage(this.pName + Language.MSGS_STOPPING.sentence);

			// Checking if there are no more players in SkyBlock
			Player[] playerList = this.getServer().getOnlinePlayers();
			for (Player p : playerList) {
				if (p.getWorld().getName().equals(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
					sender.sendMessage(this.pName + Language.MSGS_PLAYERS_IN_SB.sentence);
					return true;
				}
			}

			this.getServer().unloadWorld(Settings.worldName, true);
			SkyBlockMultiplayer.skyBlockWorld = null;
			Settings.skyBlockOnline = false;
			this.setStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_SKYBLOCKONLINE.path, false);
			sender.sendMessage(this.pName + Language.MSGS_IS_NOW_OFFLINE.sentence);
			return true;

		} catch (Exception ex) {
			this.log.warning(ex.getMessage());
			sender.sendMessage(this.pName + Language.MSGS_ERROR_OCCURED.sentence);
			return true;
		}
	}

	/**
	 * Deactivate SkyBlock, permission needed.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	public boolean setSkyBlockOnline(CommandSender sender) {
		sender.sendMessage(this.pName + Language.MSGS_STARTING.sentence);
		SkyBlockMultiplayer.skyBlockWorld = null;
		SkyBlockMultiplayer.getSkyBlockWorld();
		Settings.skyBlockOnline = true;
		this.setStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_SKYBLOCKONLINE.path, true);
		sender.sendMessage(this.pName + Language.MSGS_IS_NOW_ONLINE.sentence);
		return true;
	}

	/**
	 * Gives the sender or an other player a new island
	 * 
	 * @param player that types the command.
	 * @param target the player that is given.
	 * @return returns true.
	 */
	private boolean playerNewIsland(Player player, String target) {
		if (!Settings.skyBlockOnline) {
			player.sendMessage(this.pName + Language.MSGS_IS_OFFLINE.sentence);
			return true;
		}

		if (Settings.gameModeSelected == Settings.GAMEMODE.BUILD) {
			PlayerInfo pi = Settings.players.get(player.getName());
			if (pi == null) {
				pi = this.readPlayerFile(player.getName());
				if (pi == null) {
					return true;
				}
			}

			pi.setHasIsland(false);

			Location l = pi.getIslandLocation();
			this.removeIsland(l);

			this.writePlayerFile(player.getName(), pi);
			this.playerStart(player);
			// player.sendMessage(this.pName + Language.MSGS_NEWISLANDPLAYER1.sentence + pi.getPlayer().getName() + Language.MSGS_NEWISLANDPLAYER2.sentence);
			return true;
		}

		if (!Permissions.SKYBLOCK_NEWISLAND.has(player)) {
			return this.notAuthorized(player);
		}

		PlayerInfo pi = null;
		String res = "";
		if (target.trim().equalsIgnoreCase("")) {
			pi = Settings.players.get(player.getName());
		} else {
			res = this.getFullPlayerName(target);
			if (res.equalsIgnoreCase("-1")) {
				player.sendMessage(this.pName + Language.MSGS_WRONGE_PLAYER_NAME.sentence);
				return true;
			}
			if (res.equalsIgnoreCase("0")) {
				player.sendMessage(this.pName + Language.MSGS_BETTER_SPECIFY.sentence);
				return true;
			}
			pi = Settings.players.get(res);
		}

		if (pi == null) {
			player.sendMessage(this.pName + Language.MSGS_WRONGE_PLAYER_NAME.sentence);
			return true;
		}

		pi.setDead(true);
		pi.setHasIsland(true);
		pi.setIslandInventory(null);
		pi.setIslandArmor(null);
		pi.setIslandsLeft(pi.getIslandsLeft() + 1);

		if (Settings.numbersPlayers > 1) {
			Settings.numbersPlayers--;
		}

		if (target.equalsIgnoreCase("")) {
			this.writePlayerFile(player.getName(), pi);
		} else {
			this.writePlayerFile(res, pi);
		}

		player.sendMessage(this.pName + Language.MSGS_NEW_ISLAND_PLAYER1.sentence + pi.getPlayer().getName() + Language.MSGS_NEW_ISLAND_PLAYER2.sentence);
		pi.getPlayer().sendMessage(this.pName + Language.MSGS_GOT_NEW_ISLAND1.sentence + player.getName() + Language.MSGS_GOT_NEW_ISLAND2.sentence);
		return true;
	}

	/**
	 * Leave SkyBlock.
	 * 
	 * @param player that types the command.
	 * @return returns true.
	 */
	private boolean playerLeave(Player player) {
		if (!player.getWorld().getName().equals(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			player.sendMessage(this.pName + Language.MSGS_ONLY_OUTSIDE_OF_SB.sentence);
			return true;
		}

		PlayerInfo pi = Settings.players.get(player.getName());
		if (pi == null) {
			pi = this.readPlayerFile(player.getName());
			if (pi == null) {
				player.teleport(this.getServer().getWorlds().get(0).getSpawnLocation());
				player.sendMessage(this.pName + Language.MSGS_LEFT_SKYBLOCK.sentence);
				return true;
			}
			Settings.players.put(player.getName(), pi);
		}

		if (!this.playerIsOnTower(player)) {
			player.sendMessage(this.pName + Language.MSGS_ONLY_ON_TOWER.sentence);
			return true;
		}

		Location l = pi.getOldLocation();
		if (l == null) {
			player.teleport(this.getServer().getWorlds().get(0).getSpawnLocation());
		} else {
			player.teleport(l);
		}

		if (pi.getIslandLocation() == null) {
			Settings.players.remove(player.getName());
		}

		this.writePlayerFile(player.getName(), pi);
		return true;
	}

	public void clearArmorContents(Player player) {
		ItemStack[] items = new ItemStack[player.getInventory().getArmorContents().length];
		player.getInventory().setArmorContents(items);
	}

	/**
	 * Get an island in the world SkyBlock.
	 * 
	 * @param player that types the command.
	 * @return returns true.
	 */
	private boolean playerStart(Player player) {
		if (!Settings.skyBlockOnline) {
			player.sendMessage(this.pName + Language.MSGS_IS_OFFLINE.sentence);
			return true;
		}

		if (!player.getWorld().getName().equals(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			player.sendMessage(this.pName + Language.MSGS_ONLY_INSIDE_OF_SB.sentence);
			return true;
		}

		if (!this.playerIsOnTower(player)) {
			player.sendMessage(this.pName + Language.MSGS_ONLY_ON_TOWER.sentence);
			return true;
		}

		PlayerInfo pi = Settings.players.get(player.getName());
		if (pi == null) {
			pi = this.readPlayerFile(player.getName());
			if (pi == null) { // if player not exists, create and add him
				pi = new PlayerInfo(player.getName());
				pi.setOldLocation(player.getLocation());
			}
			Settings.players.put(player.getName(), pi);
		}

		if (Settings.gameModeSelected == Settings.GAMEMODE.BUILD) {
			if (!pi.getHasIsland() || pi.getIslandLocation() == null) {
				// new player
				CreateNewIsland isl = new CreateNewIsland(player);
				pi.setIslandLocation(isl.Islandlocation);
				pi.setHasIsland(true);
				pi.setDead(false);

				if (!Settings.allowContent) {
					pi.setOldInventory(player.getInventory().getContents());
					pi.setOldArmor(player.getInventory().getArmorContents());
					pi.setOldExp(player.getExp());
					pi.setOldLevel(player.getLevel());
					pi.setOldFood(player.getFoodLevel());
					pi.setOldHealth(player.getHealth());

					// clear inventory
					player.getInventory().clear();
					this.clearArmorContents(player);

					player.setExp(0);
					player.setLevel(0);
					player.setFoodLevel(20);
					player.setHealth(player.getMaxHealth());
				}

				// teleport player
				player.teleport(pi.getIslandLocation());
				Settings.numbersPlayers++;

				// send message to all
				for (PlayerInfo pInfo : Settings.players.values()) {
					if (pInfo.getPlayer() != null) {
						if (pInfo.getPlayer().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()) || (Permissions.SKYBLOCK_MESSAGES.has(pInfo.getPlayer()) && Settings.messagesOutside)) {
							pInfo.getPlayer().sendMessage(this.pName + Language.MSGS_WELCOME_BROADCAST1.sentence + player.getName() + Language.MSGS_WELCOME_BROADCAST2.sentence);
						}
					}
				}

				this.writePlayerFile(player.getName(), pi);
				player.sendMessage(this.pName + Language.MSGS_TO_NEW_PLAYER.sentence);
				return true;
			}

			// player has a island
			if (!Settings.allowContent) {
				// save before joining inventory, exp, level, food and health
				pi.setOldInventory(player.getInventory().getContents());
				pi.setOldArmor(player.getInventory().getArmorContents());
				pi.setOldExp(player.getExp());
				pi.setOldLevel(player.getLevel());
				pi.setOldFood(player.getFoodLevel());
				pi.setOldHealth(player.getHealth());

				// load from island inventory, exp, level, food and health
				player.getInventory().setContents(pi.getIslandInventory());
				player.getInventory().setArmorContents(pi.getIslandArmor());
				player.setExp(pi.getIslandExp());
				player.setLevel(pi.getIslandLevel());

				// check food od player
				if (pi.getIslandFood() <= 0) {
					player.setFoodLevel(20);
					pi.setIslandFood(20);
				} else {
					player.setFoodLevel(pi.getIslandFood());
				}

				// check hp of player
				if (pi.getIslandHealth() <= 0) {
					player.setHealth(player.getMaxHealth());
					pi.setIslandHealth(player.getMaxHealth());
				} else {
					player.setHealth(pi.getIslandHealth());
				}
			}

			// teleport player
			player.teleport(pi.getIslandLocation());

			this.writePlayerFile(player.getName(), pi);
			player.sendMessage(this.pName + Language.MSGS_WELCOME_BACK.sentence + player.getName());
			return true;
		}

		// Game mode is PVP
		if (pi.getHasIsland()) { // player have a island
			if (pi.isDead()) {
				if (pi.getLivesLeft() == 0) {
					if (pi.getIslandsLeft() == 0) {
						// no more lives and islands left
						player.sendMessage(this.pName + Language.MSGS_NO_MORELIVES_AND_ISLANDS.sentence);
						return true;
					}

					// no more lives left, decrement islandsLeft
					pi.setLivesLeft(Settings.pvp_livesPerIsland);
					pi.setIslandsLeft(pi.getIslandsLeft() - 1);

					if (!Settings.allowContent) {
						// save before joining inventory, exp, level, food and health
						pi.setOldInventory(player.getInventory().getContents());
						pi.setOldArmor(player.getInventory().getArmorContents());
						pi.setOldExp(player.getExp());
						pi.setOldLevel(player.getLevel());
						pi.setOldFood(player.getFoodLevel());
						pi.setOldHealth(player.getHealth());

						// clear Inventory
						player.getInventory().clear();
						this.clearArmorContents(player);

						// reset exp, level and food
						player.setExp(0);
						player.setLevel(0);
						player.setFoodLevel(20);
						player.setHealth(player.getMaxHealth());
					}

					// create new island and teleport player
					CreateNewIsland island = new CreateNewIsland(player);
					pi.setIslandLocation(island.Islandlocation);
					pi.setDead(false);
					pi.setHasIsland(true);

					player.teleport(pi.getIslandLocation());
					Settings.numbersPlayers++;
					player.sendMessage(this.pName + "You have " + pi.getLivesLeft() + " lives on this island and " + pi.getIslandsLeft() + " islands left.");

					this.writePlayerFile(player.getName(), pi);
					player.sendMessage(this.pName + Language.MSGS_TO_NEW_PLAYER.sentence);
					return true;
				}

				// lives on island left
				pi.setDead(false);
				if (!Settings.allowContent) {
					// save before joining inventory, exp, level, food and health
					pi.setOldInventory(player.getInventory().getContents());
					pi.setOldArmor(player.getInventory().getArmorContents());
					pi.setOldExp(player.getExp());
					pi.setOldLevel(player.getLevel());
					pi.setOldFood(player.getFoodLevel());
					pi.setOldHealth(player.getHealth());

					// clear Inventory
					player.getInventory().clear();
					this.clearArmorContents(player);

					// reset exp, level and food
					player.setExp(0);
					player.setLevel(0);
					player.setFoodLevel(20);
					player.setHealth(player.getMaxHealth());
				}

				// teleport player
				player.teleport(pi.getIslandLocation());
				Settings.numbersPlayers++;
				player.sendMessage(this.pName + "You have " + pi.getLivesLeft() + " lives on this island and " + pi.getIslandsLeft() + " islands left.");

				this.writePlayerFile(player.getName(), pi);
				player.sendMessage(this.pName + Language.MSGS_WELCOME_BACK.sentence + player.getName());
				return true;
			}

			// Player is not dead and has a island
			if (!Settings.allowContent) {
				// save before joining inventory, exp, level, food and health
				pi.setOldInventory(player.getInventory().getContents());
				pi.setOldArmor(player.getInventory().getArmorContents());
				pi.setOldExp(player.getExp());
				pi.setOldLevel(player.getLevel());
				pi.setOldFood(player.getFoodLevel());
				pi.setOldHealth(player.getHealth());

				player.getInventory().setContents(pi.getIslandInventory());
				player.getInventory().setArmorContents(pi.getIslandArmor());
				player.setExp(pi.getIslandExp());
				player.setLevel(pi.getIslandLevel());

				// check food of player
				if (pi.getIslandFood() <= 0) {
					player.setFoodLevel(20);
					pi.setIslandFood(20);
				} else {
					player.setFoodLevel(pi.getIslandFood());
				}

				// check hp of player
				if (pi.getIslandHealth() <= 0) {
					player.setHealth(player.getMaxHealth());
					pi.setIslandHealth(player.getMaxHealth());
				} else {
					player.setHealth(pi.getIslandHealth());
				}
			}

			// teleport player
			player.teleport(pi.getIslandLocation());
			Settings.numbersPlayers++;
			player.sendMessage(this.pName + "You have " + pi.getLivesLeft() + " lives on this island and " + pi.getIslandsLeft() + " islands left.");

			this.writePlayerFile(player.getName(), pi);
			player.sendMessage(this.pName + Language.MSGS_WELCOME_BACK.sentence + player.getName());
			return true;
		}

		// player is new
		CreateNewIsland island = new CreateNewIsland(player);
		pi.setIslandLocation(island.Islandlocation);

		if (!Settings.allowContent) {
			// save before joining inventory, exp, level, food and health
			pi.setOldInventory(player.getInventory().getContents());
			pi.setOldArmor(player.getInventory().getArmorContents());
			pi.setOldExp(player.getExp());
			pi.setOldLevel(player.getLevel());
			pi.setOldFood(player.getFoodLevel());
			pi.setOldHealth(player.getHealth());

			// clear Inventory
			player.getInventory().clear();
			this.clearArmorContents(player);

			// reset exp, level and food
			player.setExp(0);
			player.setLevel(0);
			player.setFoodLevel(20);
			player.setHealth(player.getMaxHealth());
		}

		pi.setIslandsLeft(Settings.pvp_islandsPerPlayer);
		pi.setLivesLeft(Settings.pvp_livesPerIsland);
		pi.setHasIsland(true);
		pi.setDead(false);
		pi.setIslandsLeft(pi.getIslandsLeft() - 1);

		// teleport player
		player.teleport(pi.getIslandLocation());
		Settings.numbersPlayers++;
		this.writePlayerFile(player.getName(), pi);

		player.sendMessage(this.pName + Language.MSGS_TO_NEW_PLAYER.sentence);
		player.sendMessage(this.pName + "You have " + pi.getLivesLeft() + " lives on this island and " + pi.getIslandsLeft() + " islands left.");

		// Message to all
		for (PlayerInfo pInfo : Settings.players.values()) {
			if (pInfo.getPlayer() != null) {
				if (pInfo.getPlayer().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()) || (Permissions.SKYBLOCK_MESSAGES.has(pInfo.getPlayer()) && Settings.messagesOutside)) {
					pInfo.getPlayer().sendMessage(this.pName + Language.MSGS_WELCOME_BROADCAST1.sentence + player.getName() + Language.MSGS_WELCOME_BROADCAST2.sentence);
				}
			}
		}
		return true;
	}

	/**
	 * Join the world SkyBlock.
	 * 
	 * @param player that types the command.
	 * @return returns true
	 */
	private boolean playerJoin(Player player) {
		if (!Settings.skyBlockOnline) {
			player.sendMessage(this.pName + Language.MSGS_IS_OFFLINE.sentence);
			return true;
		}

		if (player.getWorld().getName().equals(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			player.sendMessage(this.pName + Language.MSGS_ONLY_OUTSIDE_OF_SB.sentence);
			return true;
		}

		if (!Permissions.SKYBLOCK_JOIN.has(player)) {
			return this.notAuthorized(player);
		}

		if (Settings.closed) {
			player.sendMessage(this.pName + Language.MSGS_IS_CLOSED.sentence);
			return true;
		}

		PlayerInfo pi = Settings.players.get(player.getName());
		if (pi == null) {
			pi = this.readPlayerFile(player.getName());
			if (pi == null) { // if player not in list, add and create him
				pi = new PlayerInfo(player.getName());
				pi.setOldLocation(player.getLocation());
			} else {
				//Refreshe OldLocation of the player
				pi.setOldLocation(player.getLocation());
			}
			Settings.players.put(player.getName(), pi);
		}

		int islands = CreateNewIsland.getAmountOfIslands();

		this.writePlayerFile(player.getName(), pi);

		player.teleport(SkyBlockMultiplayer.getSkyBlockWorld().getSpawnLocation()); // teleport player to the spawn tower
		player.sendMessage(this.pName + Language.MSGS_WELCOME1.sentence + islands + Language.MSGS_WELCOME2.sentence + Settings.numbersPlayers + Language.MSGS_WELCOME3.sentence);
		return true;
	}

	/**
	 * Change the lanugage.
	 * 
	 * @param sender that types the command.
	 * @param The given language.
	 * @return returns true
	 */
	private boolean setLanguage(CommandSender sender, String s) {
		if (!Settings.language.equalsIgnoreCase(s)) {
			File f = new File(this.getDataFolder() + File.separator + "language", s + ".yml");
			File sf = this.fileLanguage;
			if (f.exists()) {
				try {
					this.fileLanguage = f;
					Scanner scanner = new Scanner(new FileInputStream(this.fileLanguage), "Cp1252");
					String contentToRead = "";
					while (scanner.hasNextLine()) {
						contentToRead += scanner.nextLine() + System.getProperty("line.separator");
					}
					scanner.close();
					this.configLanguage.loadFromString(contentToRead);
					this.loadLanguageConfig();
					Settings.language = s;
					this.setStringbyPath(this.configPlugin, this.filePlugin, ConfigPlugin.OPTIONS_LANGUAGE.path, s);
					sender.sendMessage(this.pName + Language.MSGS_LANGUAGE_CHANGED.sentence);
					return true;
				} catch (Exception e) {
					this.fileLanguage = sf;
					sender.sendMessage(this.pName + Language.MSGS_ERROR_OCCURED.sentence + ": " + e.getLocalizedMessage());
					sender.sendMessage(this.pName + Language.MSGS_LANGUAGE_NOT_CHANGED.sentence);
					return true;
				}
			} else {
				sender.sendMessage(this.pName + Language.MSGS_LANGUAGE_FILE_NOT_EXISTS.sentence);
				return true;
			}
		} else {
			sender.sendMessage(this.pName + Language.MSGS_LANGUAGE_NOT_CHANGED.sentence);
			return true;
		}
	}

	/**
	 * Reloads the language.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 * @throws  
	 */
	private boolean reloadLanguage(CommandSender sender) {
		if (!Permissions.SKYBLOCK_RELOAD.has(sender)) {
			return this.notAuthorized(sender);
		}

		try {
			this.loadLanguageConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
		sender.sendMessage(this.pName + Language.MSGS_LANGUAGE_RELOADED.sentence);
		return true;
	}

	/**
	 * Send message not authorized to the sender.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	private boolean notAuthorized(CommandSender s) {
		s.sendMessage(this.pName + Language.MSGS_NOT_AUTHORIZED.sentence);
		return true;
	}

	/**
	 * Reset the world SkyBlock, and the players.yml.
	 * 
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	private boolean resetSkyBlock(CommandSender sender) {
		if (!Permissions.SKYBLOCK_RESET.has(sender)) {
			return this.notAuthorized(sender);
		}

		if (Settings.skyBlockOnline) {
			sender.sendMessage(this.pName + ChatColor.RED + Language.MSGS_MUST_BEOFFLINE.sentence);
			return true;
		}

		sender.sendMessage(this.pName + Language.MSGS_RESETING.sentence);
		this.getServer().unloadWorld(Settings.worldName, true);

		for (PlayerInfo pi : Settings.players.values()) {
			pi.setHasIsland(false);
			pi.setIslandsLeft(Settings.pvp_islandsPerPlayer);
			pi.setLivesLeft(Settings.pvp_livesPerIsland);
			pi.setIslandLocation(null);
			pi.setHomeLocation(null);
			pi.setDead(false);
			pi.setIslandFood(20);
			pi.setIslandExp(0);
			pi.setIslandLevel(0);

			this.writePlayerFile(pi.getPlayerName(), pi);
		}

		this.sfiles = new ArrayList<File>();
		this.getAllFiles(Settings.worldName);

		for (File f : this.sfiles) {
			f.delete();
		}

		// Create Skyblock
		SkyBlockMultiplayer.skyBlockWorld = null;
		SkyBlockMultiplayer.getSkyBlockWorld();
		SkyBlockMultiplayer.createSpawnTower();

		// Reset informations
		Settings.players.clear();
		Settings.players = new HashMap<String, PlayerInfo>();
		Settings.numbersPlayers = 0;
		//Settings.numberIslands = 0;

		sender.sendMessage(this.pName + Language.MSGS_IS_NOW_RESETED.sentence);
		return true;
	}

	/**
	 * Reloads the config.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	public boolean reloadConfig(CommandSender sender) {
		if (!Permissions.SKYBLOCK_RELOAD.has(sender)) {
			return this.notAuthorized(sender);
		}

		this.loadPluginConfig();
		sender.sendMessage(this.pName + Language.MSGS_CONFIG_RELOADED.sentence);
		return true;
	}

	/**
	 * Get informations about SkyBlock
	 * 
	 * @param sender
	 * @return
	 */
	public boolean getStatus(CommandSender sender) {
		if (Settings.skyBlockOnline) {
			sender.sendMessage(Language.MSGS_STATUS_ONLINE.sentence);
		} else {
			sender.sendMessage(Language.MSGS_STATUS_ONLINE.sentence);
		}

		int islands = CreateNewIsland.getAmountOfIslands();

		sender.sendMessage(Language.MSGS_NUMBER_OF_ISLANDS.sentence + islands);
		sender.sendMessage(Language.MSGS_NUMBER_OF_PLAYERS.sentence + Settings.players.size());
		return true;
	}

	/**
	 * Send a list of all commands, filtered by permissions.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	public boolean getListCommands(CommandSender sender, String p) {
		String sb_join = Language.MSGS_COMMAND_JOIN.sentence + "\n";
		String sb_start = Language.MSGS_COMMAND_START.sentence + "\n";
		String sb_tower = Language.MSGS_COMMAND_TOWER.sentence + "\n";
		String sb_leave = Language.MSGS_COMMAND_LEAVE.sentence + "\n";
		String sb_status = Language.MSGS_COMMAND_STATUS.sentence + "\n";
		String sb_home = Language.MSGS_COMMAND_HOME.sentence + "\n";
		String sb_home_add = Language.MSGS_COMMAND_HOME_ADD.sentence + "\n";
		String sb_home_remove = Language.MSGS_COMMAND_HOME_REMOVE.sentence + "\n";
		String sb_home_join = Language.MSGS_COMMAND_HOME_JOIN.sentence + "\n";
		String sb_home_list = Language.MSGS_COMMAND_HOME_LIST.sentence + "\n";
		String sb_home_set = Language.MSGS_COMMAND_HOME_SET.sentence + "\n";

		String sb_newIsland = Language.MSGS_COMMAND_NEW_ISLAND.sentence + "\n";
		String sb_closed = Language.MSGS_COMMAND_SET_CLOSED.sentence + "\n";
		String sb_opened = Language.MSGS_COMMAND_SET_OPENED.sentence + "\n";
		String sb_setOffline = Language.MSGS_COMMAND_SET_OFFLINE.sentence + "\n";
		String sb_setOnline = Language.MSGS_COMMAND_SET_ONLINE.sentence + "\n";
		String sb_tower_recreate = Language.MSGS_COMMAND_TOWER_RECREATE.sentence + "\n";
		String sb_setLanguage = Language.MSGS_COMMAND_SET_LANGUAGE.sentence + "\n";
		String sb_setGameMode = Language.MSGS_COMMAND_SET_GAMEMODE.sentence + "\n";
		String sb_setOwner = Language.MSGS_COMMAND_SET_OWNER.sentence + "\n";
		String sb_reset = Language.MSGS_COMMAND_RESET.sentence + "\n";
		String sb_reload_config = Language.MSGS_COMMAND_RELOAD_CONFIG.sentence + "\n";
		String sb_reload_language = Language.MSGS_COMMAND_RELOAD_LANGUAGE.sentence + "\n";

		int page = 1;
		try {
			page = Integer.parseInt(p);
		} catch (Exception e) {
			page = 1;
		}

		String pluginName = this.pName.replace("[", "").replace("]", "");
		if (page <= 1) {
			String top = ChatColor.GOLD + "----- " + pluginName + " help index (1/2) " + ChatColor.GOLD + " -----\n" + ChatColor.WHITE;
			String msgs = top + sb_join + sb_start + sb_tower + sb_leave + sb_status + sb_home + sb_home_add + sb_home_remove + sb_home_join + sb_home_list + sb_home_set;

			for (String s : msgs.split("\n")) {
				if (!s.trim().equalsIgnoreCase("")) {
					sender.sendMessage(s);
				}
			}
			return true;
		} else if (page >= 2) {
			String top = ChatColor.GOLD + "----- " + pluginName + " help index (2/2) " + ChatColor.GOLD + " -----\n" + ChatColor.WHITE;
			String msgs = top + sb_newIsland + sb_closed + sb_opened + sb_setOffline + sb_setOnline + sb_tower_recreate + sb_setLanguage + sb_setGameMode + sb_setOwner + sb_reset + sb_reload_config + sb_reload_language;

			for (String s : msgs.split("\n")) {
				if (!s.trim().equalsIgnoreCase("")) {
					sender.sendMessage(s);
				}
			}
			return true;
		}
		return true;
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

	private void removeIsland(Location l) {
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

	private static void createSpawnTower() {
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
