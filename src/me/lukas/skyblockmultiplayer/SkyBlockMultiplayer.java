package me.lukas.skyblockmultiplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import me.lukas.skyblockmultiplayer.listeners.EntityDeath;
import me.lukas.skyblockmultiplayer.listeners.PlayerInteract;
import me.lukas.skyblockmultiplayer.listeners.PlayerRespawn;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
	private File fileConfig;

	public FileConfiguration configLanguage;
	public File fileLanguage;

	public FileConfiguration configPlayer;
	public File filePlayer;

	public File directoryPlayers;

	private String pName;

	@Override
	public void onDisable() {
		this.log.info("v" + pluginFile.getVersion() + " disabled.");
	}

	@Override
	public void onEnable() {
		SkyBlockMultiplayer.instance = this;

		this.pluginFile = this.getDescription();
		this.log = this.getLogger();
		Data.WORLDNAME = this.pluginFile.getName();

		this.pName = ChatColor.WHITE + "[" + ChatColor.GREEN + this.pluginFile.getName() + ChatColor.WHITE + "] ";

		// register events
		this.registerEvents();

		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdir();
		}

		this.directoryPlayers = new File(this.getDataFolder() + File.separator + "players");
		if (!this.directoryPlayers.exists()) {
			this.directoryPlayers.mkdir();
		}

		this.configPlugin = this.getConfig();
		this.fileConfig = new File(this.getDataFolder(), "config.yml");
		this.loadPluginConfig();

		this.configLanguage = new YamlConfiguration();
		this.fileLanguage = new File(this.getDataFolder() + File.separator + "language", Data.LANGUAGE + ".yml");
		this.loadLanguageConfig();

		/** old code only for export players.yml to new saving **/
		this.configPlayer = new YamlConfiguration();
		this.filePlayer = new File(this.getDataFolder(), "players.yml");
		if (this.filePlayer.exists()) {
			this.log.info("Found players.yml exporting it...");
			this.exportOldPlayerFile();
			this.log.info("Exporting, successfully finished.");
		}

		this.loadPlayerFiles();

		this.log.info("v" + pluginFile.getVersion() + " enabled.");
	}

	/**
	 * method only for exporting players.yml, will be removed after a few versions
	 * 
	 */
	private void exportOldPlayerFile() {
		try {
			this.configPlayer.load(this.filePlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ConfigurationSection cgs = this.configPlayer.getConfigurationSection("players");
		if (cgs == null) {
			this.log.info("players.yml empty, exporting canceled...");
			return;
		}

		for (String s : this.configPlayer.getConfigurationSection("players").getKeys(false)) {
			Old_PlayerInfo oldPi = this.getPlayer(s);
			if (!Data.PLAYERS.containsKey(s) && oldPi != null && !new File(this.directoryPlayers, s).exists()) {
				PlayerInfo pi = new PlayerInfo(s);
				pi.setDead(oldPi.isDead());
				pi.setHasIsland(oldPi.getHasIsland());
				pi.setIslandLocation(oldPi.getIslandLocation());
				pi.setOldPlayerLocation(oldPi.getOldPlayerLocation());
				Data.PLAYERS.put(s, pi);
			}
		}
	}

	private Old_PlayerInfo getPlayer(String playerName) {
		String path = "players." + playerName + ".";
		if (!this.configPlayer.contains(path)) {
			return null;
		}

		Old_PlayerInfo pi = new Old_PlayerInfo(playerName);
		pi.setHasIsland(Boolean.parseBoolean(this.getStringbyPath(this.configPlayer, this.filePlayer, path + "hasIsland", false, false)));
		pi.setDead(Boolean.parseBoolean(this.getStringbyPath(this.configPlayer, this.filePlayer, path + "isDead", false, false)));
		pi.setIslandLocation(this.getLocationString(this.getStringbyPath(this.configPlayer, this.filePlayer, path + "islandLocation", "", false)));
		if (pi.getIslandLocation() == null) {
			return null;
		}
		pi.setOldPlayerLocation(this.getLocationString(this.getStringbyPath(this.configPlayer, this.filePlayer, path + "oldLocation", this.getServer().getWorlds().get(0).getSpawnLocation(), false)));
		if (pi.getOldPlayerLocation() == null) {
			pi.setOldPlayerLocation(this.getServer().getWorlds().get(0).getSpawnLocation());
		}
		return pi;
	}

	/**
	 * Register the events
	 * 
	 */
	public void registerEvents() {
		PluginManager manager = this.getServer().getPluginManager();
		/*manager.registerEvents(new PlayerPlaceBlockListener(this), this); // this 3 events 
		manager.registerEvents(new PlayerBreackBlockListener(this), this); // will not be removed
		manager.registerEvents(new PlayerUseBucketListener(this), this);*/// until I know that it works fine...
		manager.registerEvents(new EntityDeath(this), this);
		manager.registerEvents(new PlayerRespawn(this), this);
		manager.registerEvents(new PlayerInteract(this), this);
	}

	/**
	 * Creates or loads the config file.
	 * 
	 */
	public void loadPluginConfig() {
		ArrayList<ItemStack> alitemsChest = new ArrayList<ItemStack>();
		alitemsChest.add(new ItemStack(Material.ICE, 2));
		alitemsChest.add(new ItemStack(Material.SAPLING, 5));
		alitemsChest.add(new ItemStack(Material.MELON, 3));
		alitemsChest.add(new ItemStack(Material.CACTUS, 1));
		alitemsChest.add(new ItemStack(Material.LAVA_BUCKET, 1));
		alitemsChest.add(new ItemStack(Material.PUMPKIN, 1));

		ItemStack[] itemsChest = new ItemStack[alitemsChest.size()];

		for (int i = 0; i < itemsChest.length; i++) {
			itemsChest[i] = alitemsChest.get(i);
		}

		String items = "";
		for (ItemStack i : alitemsChest) {
			items += i.getType().getId() + ":" + i.getAmount() + " ";
		}

		if (!this.fileConfig.exists()) {
			Data.ISLAND_DISTANCE = 50;
			Data.ITEMSCHEST = itemsChest;
			Data.SKYBLOCK_ONLINE = true;
			Data.ALLOWCONTENT = false;
			Data.LANGUAGE = "english";
			Data.GAMEMODE_SELECTED = Data.GAMEMODE.BUILD;
			Data.BUILD_RESPAWNWITHINVENTORY = true;
			Data.BUILD_WITHPROTECTEDAREA = false;
			Data.BUILD_ALLOW_ENDERPEARL = false;

			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_ISLANDDISTANCE.path, 50);
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_CHESTITEMS.path, items);
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_SKYBLOCKONLINE.path, true);
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_ALLOWCONTENT.path, false);
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_LANGUAGE.path, "english");
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_GAMEMODE.path, "build");
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_WORLDNAME.path, this.pluginFile.getName());
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_CLOSED.path, false);
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_PVP.path, "");
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_BUILD_SPAWNTOWER.path, true);
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_BUILD_RESPAWNWITHINVENTORY.path, true);
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_BUILD_WITHPROTECTEDAREA.path, true);
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_BUILD_ALLOWENDERPEARL.path, false);
		} else {
			try {
				this.configPlugin.load(this.fileConfig);
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				Data.ISLAND_DISTANCE = Integer.parseInt(this.getStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_ISLANDDISTANCE.path, 50, true));
				if (Data.ISLAND_DISTANCE < 50) {
					Data.ISLAND_DISTANCE = 50;
				}
			} catch (NumberFormatException nfe) {
				Data.ISLAND_DISTANCE = 50;
			}

			String[] dataItems = this.getStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_CHESTITEMS.path, items, true).split(" ");
			if (alitemsChest != null) {
				alitemsChest.clear();
			}
			alitemsChest = new ArrayList<ItemStack>();

			for (String s : dataItems) {
				if (s.trim() != "") {
					String[] dataValues = s.split(":");
					try {
						if (dataValues.length == 2) {
							alitemsChest.add(new ItemStack(Integer.parseInt(dataValues[0]), Integer.parseInt(dataValues[1])));
						} else if (dataValues.length == 3) {
							alitemsChest.add(new ItemStack(Integer.parseInt(dataValues[0]), Integer.parseInt(dataValues[1]), (short) 0, Byte.parseByte(dataValues[2])));
						}
					} catch (Exception ex) {
					}
				}
			}

			itemsChest = new ItemStack[alitemsChest.size()];
			for (int i = 0; i < itemsChest.length; i++) {
				itemsChest[i] = alitemsChest.get(i);
			}

			Data.ITEMSCHEST = itemsChest;
			Data.SKYBLOCK_ONLINE = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_SKYBLOCKONLINE.path, true, true));
			Data.LANGUAGE = this.getStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_LANGUAGE.path, "english", true);
			Data.ALLOWCONTENT = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_ALLOWCONTENT.path, false, true));
			Data.GAMEMODE_SELECTED = Data.GAMEMODE.valueOf(this.getStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_GAMEMODE.path, "build", true).toUpperCase());
			Data.BUILD_RESPAWNWITHINVENTORY = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_BUILD_RESPAWNWITHINVENTORY.path, true, true));
			Data.BUILD_WITHPROTECTEDAREA = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_BUILD_WITHPROTECTEDAREA.path, true, true));
			Data.BUILD_ALLOW_ENDERPEARL = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_BUILD_ALLOWENDERPEARL.path, false, true));
			Data.WORLDNAME = this.getStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_WORLDNAME.path, this.pluginFile.getName(), true);
			Data.CLOSED = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_CLOSED.path, false, true));
		}
	}

	/**
	 * Load all informations from player who exists and are online.
	 * 
	 */
	public void loadPlayerFiles() {
		for (String f : new File(this.directoryPlayers.toString()).list()) {
			if (new File(this.directoryPlayers, f).isFile()) {
				PlayerInfo pi = this.readPlayerFile(f);
				if (pi != null) {
					if (pi.getIslandLocation() != null) {
						Data.PLAYERS.put(f, pi);
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

	public void writePlayerFile(String playerName) {
		File f = new File(this.directoryPlayers, playerName);

		try {
			FileOutputStream fileOut = new FileOutputStream(f);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(Data.PLAYERS.get(playerName));
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
	private void loadLanguageConfig() {
		if (!new File(this.getDataFolder() + File.separator + "language").exists()) {
			new File(this.getDataFolder() + File.separator + "language").mkdirs();
		}

		if (!this.fileLanguage.exists()) {
			try {
				this.fileLanguage.createNewFile(); //create file
				this.writeLanguageConfig(); //write standard language	
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				this.configLanguage.load(this.fileLanguage);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				this.configLanguage.load(this.fileLanguage);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}

			for (Language g : Language.values()) {
				String path = g.path;
				if (!this.configLanguage.contains(path)) {
					this.configLanguage.set(path, g.sentence);
					try {
						this.configLanguage.save(this.fileLanguage);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					g.sentence = this.replaceColor(this.configLanguage.getString(path));
				}
			}
		}

		SkyBlockMultiplayer.getSkyBlockWorld();
	}

	/**
	 * This replace §0-§f, $k with ChatColor.
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
	 * 
	 */
	private void writeLanguageConfig() {
		for (Language g : Language.values()) {
			String path = g.path;
			this.configLanguage.set(path, g.sentence);
		}
		try {
			this.configLanguage.save(this.fileLanguage);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	 * @return string.
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
			skyBlockWorld = WorldCreator.name(Data.WORLDNAME).type(WorldType.NORMAL).environment(Environment.NORMAL).generator(new SkyBlockChunkGenerator()).createWorld();
			SkyBlockMultiplayer.createSpawnTower();
			skyBlockWorld.setSpawnLocation(1, SkyBlockMultiplayer.getSkyBlockWorld().getHighestBlockYAt(1, 1), 1);
		}
		return skyBlockWorld;
	}

	/**
	 * Check if the player inventory and equipment is empty.
	 * 
	 * @param player to check.
	 * @return boolean true if empty, false if not empty
	 */
	private boolean checkIfPlayerInventoryEmpty(Player player) {
		for (ItemStack i : player.getInventory().getContents()) {
			if (i != null) {
				return false;
			}
		}

		if (player.getInventory().getHelmet() != null || player.getInventory().getChestplate() != null || player.getInventory().getLeggings() != null || player.getInventory().getBoots() != null) {
			return false;
		}

		return true;
	}

	/**
	 * If a command is called, this code will be running.
	 * 	
	 * @param sender that types the command.
	 * @param cmd the typed command.
	 * @param label includes the whole String, with command and arguments.
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

			if (args[0].equalsIgnoreCase("set")) {
				if (!Permissions.SKYBLOCK_SET.has(sender)) {
					return this.notAuthorized(sender);
				}

				if (args.length < 2) {
					sender.sendMessage(this.pName + Language.MSGS_WRONGARGS.sentence);
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
					sender.sendMessage(this.pName + Language.MSGS_WRONGARGS.sentence);
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
					sender.sendMessage(this.pName + Language.MSGS_WRONGARGS.sentence);
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
				return this.getListCommands(sender);
			}

			if (!(sender instanceof Player)) {
				return true;
			}

			Player player = (Player) sender;

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
				if (Data.GAMEMODE_SELECTED == Data.GAMEMODE.PVP) {
					player.sendMessage(this.pName + Language.MSGS_ONLYINBUILDMODE.sentence);
					return true;
				}

				if (args.length == 1) {
					PlayerInfo pi = Data.PLAYERS.get(player.getName());
					if (pi == null) {
						return true;
					}
					player.teleport(pi.getIslandLocation());
					return true;
				}

				if (args[1].equalsIgnoreCase("list")) {
					PlayerInfo pi = Data.PLAYERS.get(player.getName());
					if (pi == null) {
						return true;
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
						player.sendMessage(this.pName + Language.MSGS_WRONGARGS.sentence);
						return true;
					}

					String res = this.getFullPlayerName(args[2]);
					if (res.equalsIgnoreCase("-1")) {
						player.sendMessage(this.pName + Language.MSGS_NONAME.sentence);
						return true;
					}
					if (res.equalsIgnoreCase("0")) {
						player.sendMessage(this.pName + Language.MSGS_BETTERSPECIFY.sentence);
						return true;
					}

					PlayerInfo pTarget = Data.PLAYERS.get(res);
					if (pTarget == null) {
						return true;
					}

					if (!pTarget.getFriends().contains(player.getName())) {
						player.sendMessage(this.pName + Language.MSGS_NOTFRIENDFROMYOU.sentence);
						return true;
					}

					player.teleport(pTarget.getIslandLocation());
					return true;
				}

				if (args[1].equalsIgnoreCase("add")) {
					if (args.length == 2) {
						player.sendMessage(this.pName + Language.MSGS_WRONGARGS.sentence);
						return true;
					}

					String res = this.getFullPlayerName(args[2]);
					if (res.equalsIgnoreCase("-1")) {
						player.sendMessage(this.pName + Language.MSGS_NONAME.sentence);
						return true;
					}
					if (res.equalsIgnoreCase("0")) {
						player.sendMessage(this.pName + Language.MSGS_BETTERSPECIFY.sentence);
						return true;
					}

					if (res.equalsIgnoreCase(player.getName())) {
						return true;
					}

					Player toAdd = this.getServer().getPlayer(res);
					if (toAdd != null) {
						toAdd.sendMessage(player.getName() + " " + Language.MSGS_SOMEONEADDEDYOU.sentence);
					}

					PlayerInfo pi = Data.PLAYERS.get(player.getName());
					if (pi == null) {
						return true;
					}

					pi.addFriend(res);
					player.sendMessage(this.pName + Language.MSGS_FRIENDADDED.sentence);
					return true;
				}

				if (args[1].equalsIgnoreCase("remove")) {
					if (args.length == 2) {
						player.sendMessage(this.pName + Language.MSGS_WRONGARGS.sentence);
						return true;
					}

					String res = this.getFullPlayerName(args[2]);
					if (res.equalsIgnoreCase("-1")) {
						player.sendMessage(this.pName + Language.MSGS_NONAME.sentence);
						return true;
					}
					if (res.equalsIgnoreCase("0")) {
						player.sendMessage(this.pName + Language.MSGS_BETTERSPECIFY.sentence);
						return true;
					}

					PlayerInfo pi = Data.PLAYERS.get(player.getName());
					if (pi == null) {
						return true;
					}

					pi.removeFriend(res);
					player.sendMessage(this.pName + Language.MSGS_FRIENDREMOVED.sentence);
					return true;
				}
			}

			if (args[0].equalsIgnoreCase("check")) {

				return true;
			}

			player.sendMessage(this.pName + Language.MSGS_WRONGARGS.sentence);
			return true;
		}
		return false;
	}

	private boolean setOpened(CommandSender sender) {
		Data.CLOSED = false;
		sender.sendMessage(this.pName + Language.MSGS_ISNOWOPENED.sentence);
		return true;
	}

	private boolean setClosed(CommandSender sender) {
		Data.CLOSED = true;
		sender.sendMessage(this.pName + Language.MSGS_ISNOWCLOSED.sentence);
		return true;
	}

	private boolean setGameMode(CommandSender sender, String s) {
		if (s.equalsIgnoreCase("build")) {
			Data.GAMEMODE_SELECTED = Data.GAMEMODE.BUILD;
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_GAMEMODE.path, "build");
			sender.sendMessage(this.pName + Language.MSGS_GAMEMODECHANGED.sentence);
			return true;
		}
		if (s.equalsIgnoreCase("pvp")) {
			Data.GAMEMODE_SELECTED = Data.GAMEMODE.PVP;
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_GAMEMODE.path, "pvp");
			SkyBlockMultiplayer.createSpawnTower();
			sender.sendMessage(this.pName + Language.MSGS_GAMEMODECHANGED.sentence);
			return true;
		}
		sender.sendMessage(this.pName + Language.MSGS_WRONGARGS.sentence);
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
				if (p.getWorld().equals(SkyBlockMultiplayer.getSkyBlockWorld())) {
					sender.sendMessage(this.pName + Language.MSGS_PLAYERSINSB.sentence);
					return true;
				}
			}

			this.getServer().unloadWorld(Data.WORLDNAME, true);
			SkyBlockMultiplayer.skyBlockWorld = null;
			Data.SKYBLOCK_ONLINE = false;
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_SKYBLOCKONLINE.path, false);
			sender.sendMessage(this.pName + Language.MSGS_ISNOWOFFLINE.sentence);
			return true;

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			sender.sendMessage(this.pName + Language.MSGS_ERROROCCURED.sentence);
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
		Data.SKYBLOCK_ONLINE = true;
		this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_SKYBLOCKONLINE.path, true);
		sender.sendMessage(this.pName + Language.MSGS_ISNOWONLINE.sentence);
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
		if (Data.GAMEMODE_SELECTED == Data.GAMEMODE.BUILD) {
			if (!Data.SKYBLOCK_ONLINE) {
				player.sendMessage(this.pName + Language.MSGS_ISOFFLINE.sentence);
				return true;
			}

			PlayerInfo pi = Data.PLAYERS.get(player.getName());
			if (pi == null) {
				player.sendMessage(this.pName + Language.MSGS_WRONEPLAYERNAME.sentence);
				return true;
			}

			pi.setHasIsland(false);
			player.sendMessage(this.pName + Language.MSGS_NEWISLANDPLAYER1.sentence + pi.getPlayer().getName() + Language.MSGS_NEWISLANDPLAYER2.sentence);
			return true;
		}

		if (!Permissions.SKYBLOCK_NEWISLAND.has(player)) {
			return this.notAuthorized(player);
		}

		if (!Data.SKYBLOCK_ONLINE) {
			player.sendMessage(this.pName + Language.MSGS_ISOFFLINE.sentence);
			return true;
		}

		PlayerInfo pi = null;
		if (target.trim().equalsIgnoreCase("")) {
			pi = Data.PLAYERS.get(player.getName());
		} else {
			String res = this.getFullPlayerName(target);
			if (res.equalsIgnoreCase("-1")) {
				player.sendMessage(this.pName + Language.MSGS_NONAME.sentence);
				return true;
			}
			if (res.equalsIgnoreCase("0")) {
				player.sendMessage(this.pName + Language.MSGS_BETTERSPECIFY.sentence);
				return true;
			}

			pi = Data.PLAYERS.get(res);
		}

		if (pi == null) {
			player.sendMessage(this.pName + Language.MSGS_WRONEPLAYERNAME.sentence);
			return true;
		}

		pi.setDead(false);
		pi.setHasIsland(false);
		if (Data.PLAYERS_NUMBER > 1) {
			Data.PLAYERS_NUMBER--;
		}
		player.sendMessage(this.pName + Language.MSGS_NEWISLANDPLAYER1.sentence + pi.getPlayer() + Language.MSGS_NEWISLANDPLAYER2.sentence);
		pi.getPlayer().sendMessage(this.pName + Language.MSGS_GOTNEWISLAND1.sentence + player.getName() + Language.MSGS_GOTNEWISLAND2.sentence);
		return true;
	}

	/**
	 * Leave SkyBlock.
	 * 
	 * @param player that types the command.
	 * @return returns true.
	 */
	private boolean playerLeave(Player player) {
		if (!player.getWorld().equals(SkyBlockMultiplayer.getSkyBlockWorld())) {
			return true;
		}

		PlayerInfo pi = Data.PLAYERS.get(player.getName());
		if (pi == null) {
			player.teleport(this.getServer().getWorlds().get(0).getSpawnLocation());
			player.sendMessage(this.pName + Language.MSGS_LEFTSKYBLOCK.sentence);
			return true;
		}

		boolean ismepty = true;
		if (!Data.ALLOWCONTENT) {
			ismepty = this.checkIfPlayerInventoryEmpty(player);
		}
		if (!ismepty && !this.isPlayerOnTower(player)) {
			if (pi.getHasIsland()) {
				player.sendMessage(this.pName + Language.MSGS_NOEMPTYINVENTORYLEAVE.sentence);
				return true;
			}
		}

		Location l = pi.getOldPlayerLocation();
		if (l == null) {
			player.teleport(this.getServer().getWorlds().get(0).getSpawnLocation());
		} else {
			player.teleport(l);
		}

		if (pi.getIslandLocation() == null) {
			Data.PLAYERS.remove(player.getName());
		}

		player.sendMessage(this.pName + Language.MSGS_LEFTSKYBLOCK.sentence);
		return true;
	}

	/**
	 * Get an island in the world SkyBlock.
	 * 
	 * @param player that types the command.
	 * @return returns true.
	 */
	private boolean playerStart(Player player) {
		if (!Data.SKYBLOCK_ONLINE) {
			player.sendMessage(this.pName + Language.MSGS_ISOFFLINE.sentence);
			return true;
		}

		if (!player.getWorld().equals(SkyBlockMultiplayer.getSkyBlockWorld())) {
			return true;
		}

		PlayerInfo pi = Data.PLAYERS.get(player.getName());
		if (pi == null) { // if player not in list, add him
			pi = new PlayerInfo(player.getName());
			pi.setOldPlayerLocation(this.getServer().getWorlds().get(0).getSpawnLocation());
			Data.PLAYERS.put(player.getName(), pi);
			pi = Data.PLAYERS.get(player.getName());
		}

		boolean isempty = true;
		if (!Data.ALLOWCONTENT) {
			isempty = this.checkIfPlayerInventoryEmpty(player);
		}

		if (!isempty) {
			player.sendMessage(this.pName + Language.MSGS_NOEMPTYINVENTORYSTART.sentence);
			return true;
		}

		if (pi.getContentsInventory() != null) {
			player.getInventory().setContents(pi.getContentsInventory());
		}

		if (pi.getContentsArmor() != null) {
			player.getInventory().setArmorContents(pi.getContentsArmor());
		}

		if (Data.GAMEMODE_SELECTED == Data.GAMEMODE.BUILD) {
			if (!pi.getHasIsland() || pi.getIslandLocation() == null) {
				CreateNewIsland isl = new CreateNewIsland(player);
				pi.setIslandLocation(isl.Islandlocation);
				pi.setHasIsland(true);
				Data.PLAYERS_NUMBER++;

				// send message to all
				for (PlayerInfo pInfo : Data.PLAYERS.values()) {
					if (pInfo.getPlayer() != null) {
						pInfo.getPlayer().sendMessage(this.pName + Language.MSGS_WELCOMEBROADCAST1.sentence + player.getName() + Language.MSGS_WELCOMEBROADCAST2.sentence);
					}
				}

				player.sendMessage(this.pName + Language.MSGS_TONEWPLAYER.sentence);
				return true;
			}

			player.teleport(pi.getIslandLocation());
			player.sendMessage(this.pName + Language.MSGS_WELCOMEBACK.sentence + player.getName());
			return true;
		}

		if (pi.getHasIsland()) { // had already a island
			if (pi.isDead()) {
				player.sendMessage(this.pName + Language.MSGS_HADAISLAND.sentence);
				return true;
			}
			// teleport player
			player.teleport(pi.getIslandLocation());
			player.sendMessage(this.pName + Language.MSGS_WELCOMEBACK.sentence + player.getName() + ".");
			return true;
		}

		// create a new island for the player
		CreateNewIsland isl = new CreateNewIsland(player);
		pi.setIslandLocation(isl.Islandlocation);
		pi.setHasIsland(true);
		Data.PLAYERS_NUMBER++;

		// Nachricht an alle
		for (PlayerInfo pInfo : Data.PLAYERS.values()) {
			if (pInfo.getPlayer() != null) {
				pInfo.getPlayer().sendMessage(this.pName + Language.MSGS_WELCOMEBROADCAST1.sentence + player.getName() + Language.MSGS_WELCOMEBROADCAST2.sentence);
			}
		}
		player.sendMessage(this.pName + Language.MSGS_TONEWPLAYER.sentence);
		return true;
	}

	/**
	 * Join the world SkyBlock.
	 * 
	 * @param player that types the command.
	 * @return returns true
	 */
	private boolean playerJoin(Player player) {
		if (!Data.SKYBLOCK_ONLINE) {
			player.sendMessage(this.pName + Language.MSGS_ISOFFLINE.sentence);
			return true;
		}

		if (!Permissions.SKYBLOCK_JOIN.has(player)) {
			return this.notAuthorized(player);
		}

		if (Data.CLOSED) {
			player.sendMessage(this.pName + Language.MSGS_ISCLOSED.sentence);
			return true;
		}

		if (player.getWorld().equals(SkyBlockMultiplayer.getSkyBlockWorld())) {
			return true;
		}

		PlayerInfo pi = null;
		if (Data.PLAYERS.containsKey(player.getName())) {
			pi = Data.PLAYERS.get(player.getName());
		}

		if (pi == null) {
			pi = this.readPlayerFile(player.getName());
		}

		if (pi == null) { // if player not in list, add and create him
			pi = new PlayerInfo(player.getName());
			pi.setOldPlayerLocation(player.getLocation());
			Data.PLAYERS.put(player.getName(), pi);
		} else {
			//Refreshe OldLocation of the player
			pi.setOldPlayerLocation(player.getLocation());
		}

		int islands = 0;
		for (PlayerInfo p : Data.PLAYERS.values()) {
			if (p.getHasIsland()) {
				islands++;
			}
		}

		player.teleport(SkyBlockMultiplayer.getSkyBlockWorld().getSpawnLocation()); // teleport player to the spawn tower
		player.sendMessage(this.pName + Language.MSGS_WELCOME1.sentence + islands + Language.MSGS_WELCOME2.sentence + Data.PLAYERS_NUMBER + Language.MSGS_WELCOME3.sentence);
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
		if (!Data.LANGUAGE.equalsIgnoreCase(s)) {
			File f = new File(this.getDataFolder() + File.separator + "language", s + ".yml");
			File sf = this.fileLanguage;
			if (f.exists()) {
				try {
					this.fileLanguage = f;
					this.configLanguage.load(f);
					this.loadLanguageConfig();
					Data.LANGUAGE = s;
					this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_LANGUAGE.path, s);
					sender.sendMessage(this.pName + Language.MSGS_LANGUAGECHANGED.sentence);
					return true;
				} catch (Exception e) {
					this.fileLanguage = sf;
					sender.sendMessage(this.pName + Language.MSGS_ERROROCCURED.sentence + ": " + e.getLocalizedMessage());
					sender.sendMessage(this.pName + Language.MSGS_LANGUAGENOTCHANGED.sentence);
					return true;
				}
			} else {
				sender.sendMessage(this.pName + Language.MSGS_LANGUAGEFILENOTEXISTS.sentence);
				return true;
			}
		} else {
			sender.sendMessage(this.pName + Language.MSGS_LANGUAGENOTCHANGED.sentence);
			return true;
		}
	}

	/**
	 * Reloads the language.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	private boolean reloadLanguage(CommandSender sender) {
		if (!Permissions.SKYBLOCK_RELOAD.has(sender)) {
			return this.notAuthorized(sender);
		}

		this.loadLanguageConfig();
		sender.sendMessage(this.pName + Language.MSGS_LANGUAGERELOADED.sentence);
		return true;
	}

	/**
	 * Send message not authorized to the sender.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	private boolean notAuthorized(CommandSender s) {
		s.sendMessage(this.pName + Language.MSGS_NOTAUTHORIZED.sentence);
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
			return this.notAuthorized((Player) sender);
		}

		if (Data.SKYBLOCK_ONLINE) {
			sender.sendMessage(this.pName + ChatColor.RED + Language.MSGS_MUSTBEOFFLINE.sentence);
			return true;
		}

		sender.sendMessage(this.pName + Language.MSGS_RESETING.sentence);
		this.getServer().unloadWorld(Data.WORLDNAME, true);

		for (PlayerInfo pi : Data.PLAYERS.values()) {
			pi.setHasIsland(false);
			pi.setIslandLocation(null);
			pi.setDead(false);
		}

		this.sfiles = new ArrayList<File>();
		this.getAllFiles(Data.WORLDNAME);

		for (File f : this.sfiles) {
			f.delete();
		}

		//Create Skyblock
		SkyBlockMultiplayer.skyBlockWorld = null;
		SkyBlockMultiplayer.getSkyBlockWorld();

		//Reset informations
		Data.PLAYERS.clear();
		Data.PLAYERS = new HashMap<String, PlayerInfo>();
		Data.PLAYERS_NUMBER = 0;
		Data.ISLAND_NUMBER = 0;

		sender.sendMessage(this.pName + Language.MSGS_ISNOWRESETED.sentence);
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
		sender.sendMessage(this.pName + Language.MSGS_CONFIGRELOADED.sentence);
		return true;
	}

	/**
	 * Get informations about SkyBlock
	 * 
	 * @param sender
	 * @return
	 */
	public boolean getStatus(CommandSender sender) {
		if (Data.SKYBLOCK_ONLINE) {
			sender.sendMessage(Language.MSGS_STATUSONLINE.sentence);
		} else {
			sender.sendMessage(Language.MSGS_STATUSONLINE.sentence);
		}

		int islands = 0;
		for (PlayerInfo p : Data.PLAYERS.values()) {
			if (p.getHasIsland()) {
				islands++;
			}
		}

		sender.sendMessage(Language.MSGS_NUMBEROFISLANDS.sentence + islands);
		sender.sendMessage(Language.MSGS_NUMBEROFPLAYERS.sentence + Data.PLAYERS.size());
		return true;
	}

	/**
	 * Send a list of all commands, filtered by permissions.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	public boolean getListCommands(CommandSender sender) {
		String sb_info = "-----" + this.pName + "v" + this.pluginFile.getVersion() + "-----\n";

		String sb = Language.MSGS_SKYBLOCK.sentence + "\n";
		String sb_start = Language.MSGS_CMDSTART.sentence + "\n";
		String sb_leave = Language.MSGS_CMDLEAVE.sentence + "\n";
		String sb_newisland = Language.MSGS_CMDNEWISLAND.sentence + "\n";
		String sb_setOffline = Language.MSGS_CMDSETOFFLINE.sentence + "\n";
		String sb_setOnline = Language.MSGS_CMDSETONLINE.sentence + "\n";
		String sb_setLanguage = Language.MSGS_CMDSETLANGUAGE.sentence + "\n";
		String sb_setGameMode = Language.MSGS_CMDSETGAMEMODE.sentence + "\n";
		String sb_reset = Language.MSGS_CMDRESET.sentence + "\n";
		String sb_reload_config = Language.MSGS_CMDRELOADCONFIG.sentence + "\n";
		String sb_reload_language = Language.MSGS_CMDRELOADLANGUAGE.sentence + "\n";
		String sb_status = Language.MSGS_CMDSTATUS.sentence + "\n";
		String sb_home = Language.MSGS_CMDHOME.sentence + "\n";
		String sb_home_add = Language.MSGS_CMDHOMEADD.sentence + "\n";
		String sb_home_remove = Language.MSGS_CMDHOMEREMOVE.sentence + "\n";
		String sb_home_join = Language.MSGS_CMDHOMEJOIN.sentence + "\n";
		String sb_home_list = Language.MSGS_CMDHOMELIST.sentence + "\n";
		String sb_closed = Language.MSGS_CMDSETCLOSED.sentence + "\n";
		String sb_opened = Language.MSGS_CMDSETOPENED.sentence + "\n";

		String ret = sb_info + sb + sb_start + sb_leave + sb_home + sb_home_add + sb_home_remove + sb_home_join + sb_home_list;
		if (Permissions.SKYBLOCK_NEWISLAND.has(sender)) {
			ret += sb_newisland;
		}
		if (Permissions.SKYBLOCK_SET.has(sender)) {
			ret += sb_setOnline + sb_setOffline + sb_setLanguage + sb_setGameMode + sb_closed + sb_opened;
		}
		if (Permissions.SKYBLOCK_RESET.has(sender)) {
			ret += sb_reset;
		}
		if (Permissions.SKYBLOCK_RELOAD.has(sender)) {
			ret += sb_reload_config;
			ret += sb_reload_language;
		}
		ret += sb_status;

		for (String s : ret.split("\n")) {
			if (!s.trim().equals(""))
				sender.sendMessage(s);
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
				System.out.println(ex.getMessage());
			}
		}
	}

	/**
	 * Check if player is on tower.
	 * 
	 * @param player
	 * @return boolean true if player is on tower, false if not
	 */
	public boolean isPlayerOnTower(Player player) {
		int px = player.getLocation().getBlockX();
		int pz = player.getLocation().getBlockZ();

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
		String hName = "";
		for (PlayerInfo pi : Data.PLAYERS.values()) {
			if (pi.getPlayerName().toLowerCase().startsWith(partName.toLowerCase())) {
				amount++;
				hName = pi.getPlayerName();
			}
		}
		if (amount == 1)
			return hName;
		else if (amount > 1)
			return "0";
		return "-1";
	}

	public boolean checkIfEmpty(ItemStack[] items) {
		for (ItemStack i : items) {
			if (i != null) {
				return false;
			}
		}
		return true;
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
}
