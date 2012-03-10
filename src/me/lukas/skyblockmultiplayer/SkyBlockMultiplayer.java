package me.lukas.skyblockmultiplayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

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
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyBlockMultiplayer extends JavaPlugin {
	private PluginDescriptionFile pluginFile;
	private Logger log;

	private static String WORLD_NAME;
	private static World skyblockIslands = null;

	private FileConfiguration configPlugin;
	private File fileConfig;

	public FileConfiguration configPlayer;
	public File filePlayer;

	public FileConfiguration configLanguage;
	public File fileLanguage;

	private String pName;
	private String pNameChat;

	@Override
	public void onDisable() {
		this.log.info("v" + pluginFile.getVersion() + " disabled.");
	}

	@Override
	public void onEnable() {
		this.pluginFile = this.getDescription();
		this.log = this.getLogger();
		SkyBlockMultiplayer.WORLD_NAME = this.pluginFile.getName();

		this.pName = ChatColor.WHITE + "[" + ChatColor.GREEN + this.pluginFile.getName() + ChatColor.WHITE + "] ";
		this.pNameChat = ChatColor.WHITE + "[" + ChatColor.GREEN + this.pluginFile.getName() + ChatColor.WHITE + "] ";

		// register Events
		this.registerEvents();

		this.configPlugin = this.getConfig();
		this.fileConfig = new File(this.getDataFolder(), "config.yml");
		this.loadConfig();

		this.configPlayer = new YamlConfiguration();
		this.filePlayer = new File(this.getDataFolder(), "players.yml");
		this.loadPlayerConfig();

		this.configLanguage = new YamlConfiguration();
		this.fileLanguage = new File(this.getDataFolder() + File.separator + "language", Data.LANGUAGE + ".yml");
		this.loadLanguageConfig();

		this.log.info("v" + pluginFile.getVersion() + " enabled.");
	}

	/**
	 * Register the events
	 * 
	 */
	public void registerEvents() {
		PluginManager manager = this.getServer().getPluginManager();
		manager.registerEvents(new PlayerPlaceBlockListener(this), this);
		manager.registerEvents(new PlayerBreackBlockListener(this), this);
		manager.registerEvents(new PlayerUseBucketListener(this), this);
		manager.registerEvents(new EntityDeath(this), this);
		manager.registerEvents(new PlayerJoin(this), this);
	}

	/**
	 * Creates or loads the config file.
	 * 
	 */
	public void loadConfig() {
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
			items += i.getType().getId() + ":" + i.getAmount() + ";";
		}

		if (!this.fileConfig.exists()) {
			Data.ISLAND_DISTANCE = 50;
			Data.ITEMSCHEST = itemsChest;
			Data.SKYBLOCK_ONLINE = true;

			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_ISLANDDISTANCE.path, 50);
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_CHESTITEMS.path, items);
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_SKYBLOCKONLINE.path, true);
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_PVP.path, false);
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_ALLOWCONTENT.path, false);
		} else {
			try {
				this.configPlugin.load(this.fileConfig);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}

			try {
				Data.ISLAND_DISTANCE = Integer.parseInt(this.getStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_ISLANDDISTANCE.path, 50));
				if (Data.ISLAND_DISTANCE < 50) {
					Data.ISLAND_DISTANCE = 50;
				}
			} catch (NumberFormatException nfe) {
				Data.ISLAND_DISTANCE = 50;
			}

			String[] dataItems = this.getStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_CHESTITEMS.path, items).split(";");
			if (alitemsChest != null) {
				alitemsChest.clear();
			}
			alitemsChest = new ArrayList<ItemStack>();

			for (String s : dataItems) {
				if (s.trim() != "") {
					String[] dataValues = s.split(":");
					try {
						alitemsChest.add(new ItemStack(Integer.parseInt(dataValues[0]), Integer.parseInt(dataValues[1])));
					} catch (Exception ex) {
					}
				}
			}

			itemsChest = new ItemStack[alitemsChest.size()];
			for (int i = 0; i < itemsChest.length; i++) {
				itemsChest[i] = alitemsChest.get(i);
			}

			Data.ITEMSCHEST = itemsChest;
			Data.SKYBLOCK_ONLINE = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_SKYBLOCKONLINE.path, true));
			Data.PVP = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_PVP.path, false));
			Data.LANGUAGE = this.getStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_LANGUAGE.path, "english");
			Data.ALLOWCONTENT = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_ALLOWCONTENT.path, false));
		}
	}

	public void loadPlayerConfig() {
		if (!this.filePlayer.exists()) {
			return;
		}

		try {
			this.configPlayer.load(this.filePlayer);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}

		for (Player p : this.getServer().getOnlinePlayers()) {
			PlayerInfo pi = this.getPlayer(p);
			if (pi != null) {
				Data.PLAYERS.add(pi);
			}
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
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
		} else {
			try {
				this.configLanguage.load(this.fileLanguage);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
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

		SkyBlockMultiplayer.getSkyblockIslands();
	}

	/**
	 * This replace §0-§f by ChatColor.
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
	public String getStringbyPath(FileConfiguration fc, File file, String path, Object stdValue) {
		if (!fc.contains(path)) {
			this.setStringbyPath(fc, file, path, stdValue);
			return stdValue.toString();
		}
		return fc.getString(path);
	}

	/**
	 * Returns a Location of the given string, can be null.
	 * 
	 * @param s the string to get the location from it.
	 * @return Location or null
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
	 * Loads the informations from the file players.yml, if the player exists.
	 * 
	 * @param player
	 * @return PlayerInfo
	 */
	public PlayerInfo getPlayer(Player player) {
		PlayerInfo pi = new PlayerInfo(player, this);
		if (!this.configPlayer.contains("players." + player.getName())) {
			return null;
		}

		String path = "players." + player.getName() + ".";
		pi.setHasIsland(Boolean.parseBoolean(this.getStringbyPath(this.configPlayer, this.filePlayer, path + "hasIsland", false)));
		pi.setDead(Boolean.parseBoolean(this.getStringbyPath(this.configPlayer, this.filePlayer, path + "isDead", false)));
		pi.setIslandLocation(this.getLocationString(this.getStringbyPath(this.configPlayer, this.filePlayer, path + "islandLocation", "")));
		pi.setOldPlayerLocation(this.getLocationString(this.getStringbyPath(this.configPlayer, this.filePlayer, path + "oldLocation", this.getServer().getWorlds().get(0).getSpawnLocation())));
		return pi;
	}

	/**
	 * Creates the world and the tower of SkyBlock.
	 * 
	 * @return world instance of SkyBlock
	 */
	public static World getSkyblockIslands() {
		if (skyblockIslands == null) {
			skyblockIslands = WorldCreator.name(SkyBlockMultiplayer.WORLD_NAME).type(WorldType.NORMAL).environment(Environment.NORMAL).generator(new SkyBlockChunkGenerator()).createWorld();
			SkyBlockMultiplayer.CreateSpawnTower();
			skyblockIslands.setSpawnLocation(1, SkyBlockMultiplayer.getSkyblockIslands().getHighestBlockYAt(1, 1), 1);
		}
		return skyblockIslands;
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
	 * If a command is execute, this is called.
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
					return this.setSkyblockOffline(sender);
				}
				if (args[1].equalsIgnoreCase("online")) {
					return this.setSkyblockOnline(sender);
				}
				if (args.length < 3) {
					sender.sendMessage(this.pName + Language.MSGS_WRONGARGS.sentence);
					return true;
				}
				if (args[1].equalsIgnoreCase("language")) {
					return this.setLanguage(sender, args[2]);
				}

				if (args[1].equalsIgnoreCase("pvp")) {
					return this.setPVP(sender, args[2]);
				}
			}
			if (args[0].equalsIgnoreCase("reset")) {
				return this.resetSkyblock(sender);
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

			if (args[0].equalsIgnoreCase("help")) {
				return this.getListCommands(sender);
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

			player.sendMessage(this.pNameChat + Language.MSGS_WRONGARGS.sentence);
			return true;
		}
		return false;
	}

	/**
	 * Activate, or deactivate PVP mode.
	 * 	
	 * @param sender that types the command.
	 * @param option on or off.
	 * @return returns true.
	 */
	private boolean setPVP(CommandSender sender, String option) {
		if (option.equalsIgnoreCase("on")) {
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_PVP.path, true);
			sender.sendMessage(this.pNameChat + Language.MSGS_PVP_NOW_ON.sentence);
			return true;
		} else if (option.equalsIgnoreCase("off")) {
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_PVP.path, false);
			sender.sendMessage(this.pNameChat + Language.MSGS_PVP_NOW_OFF.sentence);
			return true;
		}
		sender.sendMessage(Language.MSGS_WRONGARGS.sentence);
		return true;
	}

	/**
	 * Activate SkyBlock, permission needed.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	public boolean setSkyblockOffline(CommandSender sender) {
		String msg = "";
		msg = this.pNameChat;

		try {
			sender.sendMessage(msg + Language.MSGS_STOPPING.sentence);

			//Checke Spieler ob keiner mehr in Welt Skyblock ist	
			Player[] playerList = this.getServer().getOnlinePlayers();
			for (Player p : playerList) {
				if (p.getWorld().equals(SkyBlockMultiplayer.getSkyblockIslands())) {
					sender.sendMessage(msg + Language.MSGS_PLAYERSINSB.sentence);
					return true;
				}
			}

			this.getServer().unloadWorld(SkyBlockMultiplayer.WORLD_NAME, true);
			SkyBlockMultiplayer.skyblockIslands = null;
			Data.SKYBLOCK_ONLINE = false;
			this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_SKYBLOCKONLINE.path, false);
			sender.sendMessage(msg + Language.MSGS_ISNOWOFFLINE.sentence);
			return true;

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			sender.sendMessage(msg + Language.MSGS_ERROROCCURED.sentence);
			return true;
		}
	}

	/**
	 * Deactivate SkyBlock, permission needed.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	public boolean setSkyblockOnline(CommandSender sender) {
		String msg = "";
		msg = this.pNameChat;

		sender.sendMessage(msg + Language.MSGS_STARTING.sentence);
		SkyBlockMultiplayer.skyblockIslands = null;
		SkyBlockMultiplayer.getSkyblockIslands();
		Data.SKYBLOCK_ONLINE = true;
		this.setStringbyPath(this.configPlugin, this.fileConfig, Config.OPTIONS_SKYBLOCKONLINE.path, true);
		sender.sendMessage(msg + Language.MSGS_ISNOWONLINE.sentence);
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
		if (!Data.PVP) {
			if (!Data.SKYBLOCK_ONLINE) {
				player.sendMessage(this.pNameChat + Language.MSGS_ISOFFLINE.sentence);
				return true;
			}

			int playerNr = this.findPlayer(player.getName());
			if (playerNr == -1) {
				player.sendMessage(this.pNameChat + Language.MSGS_WRONEPLAYERNAME.sentence);
				return true;
			}

			PlayerInfo pi = Data.PLAYERS.get(playerNr);
			pi.setHasIsland(false);
			player.sendMessage(this.pNameChat + Language.MSGS_NEWISLANDPLAYER1.sentence + pi.getPlayerName() + Language.MSGS_NEWISLANDPLAYER2.sentence);
			return true;
		}

		if (!Permissions.SKYBLOCK_NEWISLAND.has(player)) {
			return this.notAuthorized(player);
		}

		if (!Data.SKYBLOCK_ONLINE) {
			player.sendMessage(this.pNameChat + Language.MSGS_ISOFFLINE.sentence);
			return true;
		}

		int playerNr = -1;
		if (target.trim().equalsIgnoreCase("")) {
			playerNr = this.findPlayer(player.getName());
		} else {
			playerNr = this.findPlayer(target);
		}

		if (playerNr == -1) {
			player.sendMessage(this.pNameChat + Language.MSGS_WRONEPLAYERNAME.sentence);
			return true;
		}
		PlayerInfo pi = Data.PLAYERS.get(playerNr);
		pi.setDead(false);
		pi.setHasIsland(false);
		if (Data.PLAYERS_NUMBER > 1) {
			Data.PLAYERS_NUMBER--;
		}
		player.sendMessage(this.pNameChat + Language.MSGS_NEWISLANDPLAYER1.sentence + pi.getPlayerName() + Language.MSGS_NEWISLANDPLAYER2.sentence);
		Data.PLAYERS.get(playerNr).getPlayer().sendMessage(this.pNameChat + Language.MSGS_GOTNEWISLAND1.sentence + player.getName() + Language.MSGS_GOTNEWISLAND2.sentence);
		return true;
	}

	/**
	 * Leave SkyBlock.
	 * 
	 * @param player that types the command.
	 * @return returns true.
	 */
	private boolean playerLeave(Player player) {
		if (!player.getWorld().equals(SkyBlockMultiplayer.getSkyblockIslands())) {
			return true;
		}

		int playerNr = this.findPlayer(player.getName());
		if (playerNr == -1) {
			player.teleport(this.getServer().getWorlds().get(0).getSpawnLocation());
			player.sendMessage(this.pNameChat + Language.MSGS_LEFTSKYBLOCK.sentence);
			return true;
		}
		PlayerInfo pi = Data.PLAYERS.get(playerNr);

		boolean ismepty = true;
		if (!Data.ALLOWCONTENT) {
			ismepty = this.checkIfPlayerInventoryEmpty(player);
		}
		if (!ismepty && !this.isPlayerOnTower(player)) {
			if (pi.getHasIsland()) {
				player.sendMessage(this.pNameChat + Language.MSGS_NOEMPTYINVENTORYLEAVE.sentence);
				return true;
			}
		}

		Location l = Data.PLAYERS.get(playerNr).getOldPlayerLocation();
		if (l == null) {
			player.teleport(this.getServer().getWorlds().get(0).getSpawnLocation());
		} else {
			player.teleport(l);
		}

		player.sendMessage(this.pNameChat + Language.MSGS_LEFTSKYBLOCK.sentence);
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
			player.sendMessage(this.pNameChat + Language.MSGS_ISOFFLINE.sentence);
			return true;
		}

		if (!(player.getWorld().equals(SkyBlockMultiplayer.getSkyblockIslands()))) {
			return true;
		}

		boolean isempty = true;
		if (!Data.ALLOWCONTENT) {
			isempty = this.checkIfPlayerInventoryEmpty(player);
		}

		if (!isempty) {
			player.sendMessage(this.pNameChat + Language.MSGS_NOEMPTYINVENTORYSTART.sentence);
			return true;
		}

		int playerNr = this.findPlayer(player.getName()); // search player
		if (playerNr == -1) { // if player not in list, add him
			Data.PLAYERS.add(new PlayerInfo(player, this));
			playerNr = this.findPlayer(player.getName());
		}

		PlayerInfo pi = Data.PLAYERS.get(playerNr);
		if (!Data.PVP) {
			if (!pi.getHasIsland()) {
				CreateNewIsland isl = new CreateNewIsland(player);
				pi.setIslandLocation(isl.Islandlocation);
				pi.setHasIsland(true);
				Data.PLAYERS_NUMBER++;

				// send message to all
				for (PlayerInfo p : Data.PLAYERS) {
					p.getPlayer().sendMessage(this.pNameChat + Language.MSGS_WELCOMEBROADCAST1.sentence + player.getName() + Language.MSGS_WELCOMEBROADCAST2.sentence);
				}
				player.sendMessage(this.pNameChat + Language.MSGS_TONEWPLAYER.sentence);
				return true;
			} else {
				player.teleport(pi.getIslandLocation());
				player.sendMessage(this.pNameChat + Language.MSGS_WELCOMEBACK.sentence + player.getName());
				return true;
			}
		}

		if (Data.PLAYERS.get(playerNr).getHasIsland()) { // had already a island
			if (Data.PLAYERS.get(playerNr).isDead()) {
				player.sendMessage(this.pNameChat + Language.MSGS_HADAISLAND.sentence);
				return true;
			}
			// teleport player
			player.teleport(Data.PLAYERS.get(playerNr).getIslandLocation());
			player.sendMessage(this.pNameChat + Language.MSGS_WELCOMEBACK.sentence + player.getName() + ".");
			return true;
		} else {
			// create a new island for the player
			CreateNewIsland isl = new CreateNewIsland(player);
			pi.setIslandLocation(isl.Islandlocation);
			pi.setHasIsland(true);
			Data.PLAYERS_NUMBER++;

			// Nachricht an alle
			for (PlayerInfo p : Data.PLAYERS) {
				p.getPlayer().sendMessage(this.pNameChat + Language.MSGS_WELCOMEBROADCAST1.sentence + player.getName() + Language.MSGS_WELCOMEBROADCAST2.sentence);
			}
			player.sendMessage(this.pNameChat + Language.MSGS_TONEWPLAYER.sentence);
			return true;
		}
	}

	/**
	 * Join the world SkyBlock.
	 * 
	 * @param player that types the command.
	 * @return returns true
	 */
	private boolean playerJoin(Player player) {
		if (!Data.SKYBLOCK_ONLINE) {
			player.sendMessage(this.pNameChat + Language.MSGS_ISOFFLINE.sentence);
			return true;
		}

		if (player.getWorld().equals(SkyBlockMultiplayer.getSkyblockIslands())) {
			return true;
		}

		int playerNr = this.findPlayer(player.getName()); // search player
		if (playerNr == -1) { // if player not in list, add him
			Data.PLAYERS.add(new PlayerInfo(player, this));
		} else {
			//Refreshe OldLocation of the player
			Data.PLAYERS.get(playerNr).setOldPlayerLocation(player.getLocation());
		}

		int islands = 0;
		for (PlayerInfo p : Data.PLAYERS) {
			if (p.getHasIsland()) {
				islands++;
			}
		}

		player.teleport(SkyBlockMultiplayer.getSkyblockIslands().getSpawnLocation()); // Teleportiere Spieler zum Spawntower in der Mitte
		player.sendMessage(this.pNameChat + Language.MSGS_WELCOME1.sentence + islands + Language.MSGS_WELCOME2.sentence + Data.PLAYERS_NUMBER + Language.MSGS_WELCOME3.sentence);
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
					sender.sendMessage(this.pNameChat + Language.MSGS_LANGUAGECHANGED.sentence);
					return true;
				} catch (InvalidConfigurationException e) {
					this.fileLanguage = sf;
					sender.sendMessage(this.pNameChat + Language.MSGS_ERROROCCURED.sentence + ": " + e.getLocalizedMessage());
					sender.sendMessage(this.pNameChat + Language.MSGS_LANGUAGENOTCHANGED.sentence);
					return true;
				} catch (FileNotFoundException e) {
					this.fileLanguage = sf;
					sender.sendMessage(this.pNameChat + Language.MSGS_ERROROCCURED.sentence + ": " + e.getLocalizedMessage());
					sender.sendMessage(this.pNameChat + Language.MSGS_LANGUAGENOTCHANGED.sentence);
					return true;
				} catch (IOException e) {
					this.fileLanguage = sf;
					sender.sendMessage(this.pNameChat + Language.MSGS_ERROROCCURED.sentence + ": " + e.getLocalizedMessage());
					sender.sendMessage(this.pNameChat + Language.MSGS_LANGUAGENOTCHANGED.sentence);
					return true;
				}
			} else {
				sender.sendMessage(this.pNameChat + Language.MSGS_LANGUAGEFILENOTEXISTS.sentence);
				return true;
			}
		} else {
			sender.sendMessage(this.pNameChat + Language.MSGS_LANGUAGENOTCHANGED.sentence);
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
		sender.sendMessage(this.pNameChat + Language.MSGS_LANGUAGERELOADED.sentence);
		return true;
	}

	/**
	 * Send message not authorized to the sender.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	private boolean notAuthorized(CommandSender s) {
		s.sendMessage(this.pNameChat + Language.MSGS_NOTAUTHORIZED.sentence);
		return true;
	}

	/**
	 * Reset the world SkyBlock, and the players.yml.
	 * 
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	private boolean resetSkyblock(CommandSender sender) {
		String msg = "";
		msg = this.pNameChat;

		if (!Permissions.SKYBLOCK_RESET.has(sender)) {
			return this.notAuthorized((Player) sender);
		}

		if (Data.SKYBLOCK_ONLINE) {
			sender.sendMessage(msg + ChatColor.RED + Language.MSGS_MUSTBEOFFLINE.sentence);
			return true;
		}

		sender.sendMessage(msg + Language.MSGS_RESETING.sentence);
		this.getServer().unloadWorld(SkyBlockMultiplayer.WORLD_NAME, true);

		for (PlayerInfo pi : Data.PLAYERS) {
			pi.setHasIsland(false);
			pi.setDead(false);
		}

		this.sfiles = new ArrayList<File>();
		this.getAllFiles(SkyBlockMultiplayer.WORLD_NAME);

		for (File f : this.sfiles) {
			f.delete();
		}

		//Create Skyblock
		SkyBlockMultiplayer.skyblockIslands = null;
		SkyBlockMultiplayer.getSkyblockIslands();

		//Reset informations
		Data.ISLAND_DISTANCE = 50;
		Data.PLAYERS.clear();
		Data.PLAYERS = new ArrayList<PlayerInfo>();
		Data.PLAYERS_NUMBER = 0;
		Data.ISLAND_NUMBER = 0;

		sender.sendMessage(msg + Language.MSGS_ISNOWRESETED.sentence);
		return true;
	}

	/**
	 * Reloads the config.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	public boolean reloadConfig(CommandSender sender) {
		String msg = "";
		msg = this.pNameChat;

		if (!Permissions.SKYBLOCK_RELOAD.has(sender)) {
			return this.notAuthorized(sender);
		}

		this.loadConfig();
		sender.sendMessage(msg + Language.MSGS_CONFIGRELOADED.sentence);
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
		for (PlayerInfo p : Data.PLAYERS) {
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
		String sb_info = "";

		sb_info = "-----" + this.pNameChat + "v" + this.pluginFile.getVersion() + "-----\n";

		String sb = Language.MSGS_SKYBLOCK.sentence + "\n";
		String sb_start = Language.MSGS_CMDSTART.sentence + "\n";
		String sb_leave = Language.MSGS_CMDLEAVE.sentence + "\n";
		String sb_newisland = Language.MSGS_CMDNEWISLAND.sentence + "\n";
		String sb_setOffline = Language.MSGS_CMDSETOFFLINE.sentence + "\n";
		String sb_setOnline = Language.MSGS_CMDSETONLINE.sentence + "\n";
		String sb_setLanguage = Language.MSGS_CMDSETLANGUAGE.sentence + "\n";
		String sb_setPVP = Language.MSGS_CMDSETPVP.sentence + "\n";
		String sb_reset = Language.MSGS_CMDRESET.sentence + "\n";
		String sb_reload_config = Language.MSGS_CMDRELOADCONFIG.sentence + "\n";
		String sb_reload_language = Language.MSGS_CMDRELOADLANGUAGE.sentence + "\n";
		String sb_status = Language.MSGS_CMDSTATUS.sentence;

		String ret = "";
		ret += sb_info + sb + sb_start + sb_leave;
		if (Permissions.SKYBLOCK_NEWISLAND.has(sender)) {
			ret += sb_newisland;
		}
		if (Permissions.SKYBLOCK_SET.has(sender)) {
			ret += sb_setOnline + sb_setOffline + sb_setLanguage + sb_setPVP;
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

	ArrayList<File> sfiles;

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
	 * Check if the player name inside of a ArrayList.
	 * 
	 * @param name of the player
	 * @return a integer
	 */
	public int findPlayer(String playerName) {
		for (int i = 0; i < Data.PLAYERS.size(); i++) {
			if (Data.PLAYERS.get(i).getPlayerName().equalsIgnoreCase(playerName)) {
				return i;
			}
		}
		return -1;
	}

	public boolean isPlayerOnTower(Player player) {
		int px = player.getLocation().getBlockX();
		int pz = player.getLocation().getBlockZ();

		if (px >= -20 && px <= 20 && pz >= -20 && pz <= 20) {
			return true;
		}

		return false;
	}

	private static void makeBlock(int x, int y, int z, Material m) {
		SkyBlockMultiplayer.getSkyblockIslands().getBlockAt(x, y, z).setType(m);
	}

	private static void quader(int x, int y, int z, Material m) {
		if (y < 0)
			return;
		SkyBlockMultiplayer.makeBlock(x, y, z, m);
		SkyBlockMultiplayer.makeBlock(x, y, z + 1, m);
		SkyBlockMultiplayer.makeBlock(x + 1, y, z, m);
		SkyBlockMultiplayer.makeBlock(x + 1, y, z + 1, m);
	}

	private static void CreateSpawnTower() {
		int yStart = 2;
		int yEnde = 90;
		int[][] lavatreppe = { { 2, 0 }, { 2, 0 }, { 0, 2 }, { 0, 2 }, { -2, 0 }, { -2, 0 }, { 0, -2 }, { 0, -2 } };
		int i = 0;
		int x = -2;
		int z = -2;
		for (int y = yStart; y < yEnde - 2; y++) {
			// Mache Obsidian Turm
			SkyBlockMultiplayer.quader(0, y, 0, Material.OBSIDIAN);
			// Mache Innenwand
			for (int xw = -2; xw < 4; xw++) {
				SkyBlockMultiplayer.makeBlock(xw, y, -2, Material.GLASS);
				SkyBlockMultiplayer.makeBlock(xw, y, 3, Material.GLASS);
			}
			for (int zw = -2; zw < 4; zw++) {
				SkyBlockMultiplayer.makeBlock(-2, y, zw, Material.GLASS);
				SkyBlockMultiplayer.makeBlock(3, y, zw, Material.GLASS);
			}
			// Mache Lavatreppe
			SkyBlockMultiplayer.quader(x, y, z, Material.getMaterial(43));
			x += lavatreppe[i][0];
			z += lavatreppe[i][1];
			i++;
			if (i == lavatreppe.length)
				i = 0;

		}
		// Wassertreppe
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

		// Setze die Treppe, Ganzesteine
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
		// Setze die Treppe, Halbesteine
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

		// Setze Lava
		SkyBlockMultiplayer.makeBlock(2, yEnde - 3, 2, Material.LAVA);
		// Setze Wasser
		SkyBlockMultiplayer.makeBlock(-1, yEnde - 3, 0, Material.WATER);

		// Mache Dach
		for (x = -2; x < 4; x++) {
			for (z = -2; z < 4; z++) {
				SkyBlockMultiplayer.makeBlock(x, yEnde - 2, z, Material.getMaterial(43));
			}
		}

		// Zaun
		for (x = -2; x < 4; x++) {
			SkyBlockMultiplayer.makeBlock(x, yEnde - 1, -2, Material.FENCE);
		}
		for (z = -2; z < 4; z++) {
			SkyBlockMultiplayer.makeBlock(-2, yEnde - 1, z, Material.FENCE);
			SkyBlockMultiplayer.makeBlock(3, yEnde - 1, z, Material.FENCE);
		}

		// Fackeln
		SkyBlockMultiplayer.makeBlock(-2, yEnde, -2, Material.TORCH);
		SkyBlockMultiplayer.makeBlock(-2, yEnde, 3, Material.TORCH);
		SkyBlockMultiplayer.makeBlock(3, yEnde, -2, Material.TORCH);
		SkyBlockMultiplayer.makeBlock(3, yEnde, 3, Material.TORCH);

		// Mache den Towerboden
		for (x = -2; x < 4; x++) {
			for (z = -2; z < 4; z++) {
				for (int y = 0; y < yStart; y++) {
					SkyBlockMultiplayer.makeBlock(x, y, z, Material.AIR);
				}
				SkyBlockMultiplayer.makeBlock(x, yStart, z, Material.getMaterial(43));
			}
		}

		//Mache Schilder
		SkyBlockMultiplayer.getSkyblockIslands().getBlockAt(1, yEnde - 1, 2).setType(Material.SIGN_POST);
		Sign s1 = (Sign) SkyBlockMultiplayer.getSkyblockIslands().getBlockAt(1, yEnde - 1, 2).getState();
		s1.setLine(0, Language.MSGS_SIGN1LINE1.sentence);
		s1.setLine(1, Language.MSGS_SIGN1LINE2.sentence);
		s1.setLine(2, Language.MSGS_SIGN1LINE3.sentence);
		s1.update();
		s1.getBlock().setData((byte) 8);
		SkyBlockMultiplayer.getSkyblockIslands().getBlockAt(0, yEnde - 1, 2).setType(Material.SIGN_POST);
		Sign s2 = (Sign) SkyBlockMultiplayer.getSkyblockIslands().getBlockAt(0, yEnde - 1, 2).getState();
		s2.setLine(0, Language.MSGS_SIGN2LINE1.sentence);
		s2.setLine(1, Language.MSGS_SIGN2LINE2.sentence);
		s2.setLine(2, Language.MSGS_SIGN2LINE3.sentence);
		s2.setLine(3, Language.MSGS_SIGN2LINE4.sentence);
		s2.update();
		s2.getBlock().setData((byte) 8);
	}
}
