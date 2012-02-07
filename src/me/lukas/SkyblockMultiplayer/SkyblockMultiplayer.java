package me.lukas.skyblockmultiplayer;

import java.io.File;
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

public class SkyblockMultiplayer extends JavaPlugin {
	private PluginDescriptionFile pluginFile;
	private Logger log;

	private static World skyblockIslands = null;
	static String WORLD_NAME = "skyblockislands";

	private FileConfiguration configPlugin;
	private File fileConfig;

	FileConfiguration configPlayer;
	File filePlayer;

	FileConfiguration configLanguage;
	File fileLanguage;

	private String pName;
	private String pNameChat;

	@Override
	public void onDisable() {
		this.log.info(this.pName + "v" + pluginFile.getVersion() + " disabled.");
	}

	@Override
	public void onEnable() {
		this.pluginFile = this.getDescription();
		this.log = Logger.getLogger("Minecraft");

		this.pName = "[" + this.pluginFile.getName() + "] ";
		this.pNameChat = ChatColor.WHITE + "[" + ChatColor.GREEN + this.pluginFile.getName() + ChatColor.WHITE + "] ";

		//Register Events
		this.registerEvents();

		this.configPlugin = this.getConfig();
		this.fileConfig = new File(this.getDataFolder(), "config.yml");
		this.loadConfig();

		this.configPlayer = new YamlConfiguration();
		this.filePlayer = new File(this.getDataFolder(), "players.yml");
		this.loadPlayerConfig();

		this.configLanguage = new YamlConfiguration();
		this.fileLanguage = new File(this.getDataFolder(), "language" + File.separator + Data.LANGUAGE);
		this.loadLanguageConfig();

		this.log.info(this.pName + "v" + pluginFile.getVersion() + " enabled.");
	}

	public void registerEvents() {
		PlayerBreackBlockListener breakBlock = new PlayerBreackBlockListener(this);
		PlayerPlaceBlockListener placeBlock = new PlayerPlaceBlockListener(this);
		PlayerUseBucketListener useBucket = new PlayerUseBucketListener(this);
		EntityDeath deathListener = new EntityDeath(this);
		PlayerJoin lPlayerJoin = new PlayerJoin(this);

		PluginManager manager = this.getServer().getPluginManager();
		manager.registerEvents(breakBlock, this);
		manager.registerEvents(placeBlock, this);
		manager.registerEvents(useBucket, this);
		manager.registerEvents(deathListener, this);
		manager.registerEvents(lPlayerJoin, this);
	}

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

			this.setStringbyPath(this.configPlugin, this.fileConfig, "options." + "islandDistance", 50);
			this.setStringbyPath(this.configPlugin, this.fileConfig, "options." + "chest.items", items);
			this.setStringbyPath(this.configPlugin, this.fileConfig, "options." + "skyblockonline", true);
			this.setStringbyPath(this.configPlugin, this.fileConfig, "options." + "hardcoremode", false);
		} else {
			try {
				this.configPlugin.load(this.fileConfig);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}

			try {
				Data.ISLAND_DISTANCE = Integer.parseInt(this.getStringbyPath(this.configPlugin, this.fileConfig, "islandDistance", 50));
				if (Data.ISLAND_DISTANCE < 50) {
					Data.ISLAND_DISTANCE = 50;
				}
			} catch (NumberFormatException nfe) {
				Data.ISLAND_DISTANCE = 50;
			}

			String[] dataItems = this.getStringbyPath(this.configPlugin, this.fileConfig, "chest.items", items).split(";");
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
			Data.SKYBLOCK_ONLINE = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.fileConfig, "options.skyblockonline", true));
			Data.HARDCOREMODE = Boolean.parseBoolean(this.getStringbyPath(this.configPlugin, this.fileConfig, "option.hardcoremode", false));
		}

		SkyblockMultiplayer.getSkyblockIslands();
	}

	public void loadPlayerConfig() {
		if (!this.filePlayer.exists()) {
			return;
		}

		try {
			this.configPlayer.load(this.filePlayer);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		for (Player p : this.getServer().getOnlinePlayers()) {
			PlayerInfo pi = this.getPlayer(p);
			if (pi != null) {
				Data.PLAYERS.add(pi);
			}
		}
	}

	private void loadLanguageConfig() {
		if (!this.fileLanguage.exists()) {
			try {
				this.fileLanguage.createNewFile(); //create file
				this.writeLanguageConfig(); //write standard language	
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				this.configLanguage.load(this.fileLanguage);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		} else {
			try {
				this.configLanguage.load(this.fileLanguage);
			} catch (IOException | InvalidConfigurationException e) {
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
	}

	private String replaceColor(String s) {
		for (ChatColor c : ChatColor.values()) {
			s = s.replaceAll("§" + c.getChar(), "" + ChatColor.getByChar(c.getChar()));
		}
		return s;
	}

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

	public void setStringbyPath(FileConfiguration fc, File f, String path, Object content) {
		fc.set(path, content.toString());
		try {
			fc.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getStringbyPath(FileConfiguration fc, File file, String path, Object stdContent) {
		if (!fc.contains(path)) {
			this.setStringbyPath(fc, file, path, stdContent);
			return stdContent.toString();
		}
		return fc.getString(path);
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

	public String getStringLocation(Location l) {
		if (l == null) {
			return "";
		}
		return l.getWorld().getName() + ":" + l.getBlockX() + ":" + l.getBlockY() + ":" + l.getBlockZ();
	}

	public PlayerInfo getPlayer(Player p) {
		PlayerInfo pi = new PlayerInfo(p, this);
		if (!this.configPlayer.contains("players." + p.getName())) {
			return null;
		}

		String path = "players." + p.getName() + ".";
		pi.setHasIsland(Boolean.parseBoolean(this.getStringbyPath(this.configPlayer, this.fileConfig, path + "hasIsland", false)));
		pi.setDead(Boolean.parseBoolean(this.getStringbyPath(this.configPlayer, this.fileConfig, path + "isDead", false)));
		pi.setIslandLocation(this.getLocationString(this.getStringbyPath(this.configPlayer, this.fileConfig, path + "islandLocation", "")));
		this.log.info(this.getStringbyPath(this.configPlayer, this.fileConfig, path + "oldLocation", null));
		pi.setOldPlayerLocation(this.getLocationString(this.getStringbyPath(this.configPlayer, this.fileConfig, path + "oldLocation", null)));
		return pi;
	}

	public static World getSkyblockIslands() {
		if (skyblockIslands == null) {
			long seed = 89125;
			skyblockIslands = WorldCreator.name(SkyblockMultiplayer.WORLD_NAME).type(WorldType.NORMAL).seed(seed).environment(Environment.NORMAL).generator(new SkyblockChunkGenerator()).createWorld();
			SkyblockMultiplayer.CreateSpawnTower();
			skyblockIslands.setSpawnLocation(1, SkyblockMultiplayer.getSkyblockIslands().getHighestBlockYAt(1, 1), 1);
		}
		return skyblockIslands;
	}

	private boolean checkIfPlayerInventoryEmpty(Player p) {
		for (ItemStack i : p.getInventory().getContents()) {
			if (i != null) {
				return false;
			}
		}

		if (!p.getInventory().getHelmet().getType().equals(Material.AIR) || !p.getInventory().getChestplate().getType().equals(Material.AIR) || !p.getInventory().getLeggings().getType().equals(Material.AIR) || !p.getInventory().getBoots().getType().equals(Material.AIR)) {
			return false;
		}

		return true;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			if (cmd.getName().equalsIgnoreCase("skyblock")) {
				if (args.length == 0) {
					sender.sendMessage("SkyblockMultiplayer v" + this.pluginFile.getVersion());
					sender.sendMessage(Language.MSGS_SKYBLOCK.sentence);
					return true;
				}

				if (args[0].equalsIgnoreCase("set")) {

					if (args[1].equalsIgnoreCase("offline")) {
						return this.setSkyblockOffline(sender);
					}
					if (args[1].equalsIgnoreCase("online")) {
						return this.setSkyblockOnline(sender);
					}

					if (args.length <= 3) {
						sender.sendMessage(this.pName + Language.MSGS_WRONGARGS.sentence);
						return true;
					}

					if (args[1].equalsIgnoreCase("language")) {
						
					}
				}
				if (args[0].equalsIgnoreCase("reset")) {
					return this.resetSkyblock(sender);
				}
				if (args[0].equalsIgnoreCase("reload")) {
					if (args.length < 2) {
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

				sender.sendMessage(this.pNameChat + Language.MSGS_WRONGARGS.sentence);
				return true;
			}
			return false;
		}

		Player player = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("skyblock")) {
			if (args.length == 0) {
				sender.sendMessage(this.pNameChat + "SkyblockMultiplayer v" + this.pluginFile.getVersion());
				sender.sendMessage(this.pNameChat + Language.MSGS_SKYBLOCK.sentence);
				return true;
			}

			if (args[0].equalsIgnoreCase("join")) {
				if (!Data.SKYBLOCK_ONLINE) {
					player.sendMessage(this.pNameChat + Language.MSGS_ISOFFLINE.sentence);
					return true;
				}

				if (player.getWorld().equals(SkyblockMultiplayer.getSkyblockIslands())) {
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

				player.teleport(SkyblockMultiplayer.getSkyblockIslands().getSpawnLocation()); // Teleportiere Spieler zum Spawntower in der Mitte
				player.sendMessage(this.pNameChat + Language.MSGS_WELCOME1.sentence + islands + Language.MSGS_WELCOME2.sentence + Data.PLAYERS_NUMBER + Language.MSGS_WELCOME3.sentence);
				return true;
			}

			if (args[0].equalsIgnoreCase("start")) {
				if (!Data.SKYBLOCK_ONLINE) {
					player.sendMessage(this.pNameChat + Language.MSGS_ISOFFLINE.sentence);
					return true;
				}

				if (!(player.getWorld().equals(SkyblockMultiplayer.getSkyblockIslands()))) {
					return true;
				}

				boolean ismepty = this.checkIfPlayerInventoryEmpty(player);
				if (!ismepty) {
					player.sendMessage(this.pNameChat + ChatColor.RED + "Es ist nicht erlaubt mit Inhalt im Inventar mitzuspielen!");
					return true;
				}

				int playerNr = this.findPlayer(player.getName()); // search player
				if (playerNr == -1) { // if player not in list, add him
					Data.PLAYERS.add(new PlayerInfo(player, this));
					playerNr = this.findPlayer(player.getName());
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
					Data.PLAYERS.get(playerNr).setIslandLocation(isl.Islandlocation);
					Data.PLAYERS.get(playerNr).setHasIsland(true);
					Data.PLAYERS_NUMBER++;

					// Nachricht an alle
					for (PlayerInfo pi : Data.PLAYERS) {
						pi.getPlayer().sendMessage(this.pNameChat + Language.MSGS_WELCOMEBROADCAST1.sentence + player.getName() + Language.MSGS_WELCOMEBROADCAST2.sentence);
					}
					player.sendMessage(this.pNameChat + Language.MSGS_TONEWPLAYER.sentence);
					return true;
				}
			}

			if (args[0].equalsIgnoreCase("leave")) {
				if (!player.getWorld().equals(SkyblockMultiplayer.getSkyblockIslands())) {
					return true;
				}

				int playerNr = this.findPlayer(player.getName());
				if (playerNr == -1) {
					player.teleport(this.getServer().getWorlds().get(0).getSpawnLocation());
					player.sendMessage(this.pNameChat + Language.MSGS_LEFTSKYBLOCK.sentence);
					return true;
				}

				boolean ismepty = this.checkIfPlayerInventoryEmpty(player);
				if (!ismepty) {
					if (Data.PLAYERS.get(playerNr).getHasIsland()) {
						player.sendMessage(this.pNameChat + Language.MSGS_NOEMPTYINVENTORY.sentence);
						return true;
					}
				}

				Location l = Data.PLAYERS.get(playerNr).getOldPlayerLocation();
				player.teleport(l);
				player.sendMessage(this.pNameChat + Language.MSGS_LEFTSKYBLOCK.sentence);
				return true;
			}

			if (args[0].equalsIgnoreCase("newisland")) {
				if (!Permissions.SKYBLOCK_NEWISLAND.has(player)) {
					this.notAuthorized((Player) sender);
				}
				if (!Data.SKYBLOCK_ONLINE) {
					player.sendMessage(this.pNameChat + Language.MSGS_ISOFFLINE.sentence);
					return true;
				}

				if (args.length == 1) {
					player.sendMessage(this.pNameChat + Language.MSGS_WRONGARGS.sentence);
					return true;
				}

				int playerNr = this.findPlayer(args[1]);
				if (playerNr == -1) {
					player.sendMessage(this.pNameChat + Language.MSGS_WRONEPLAYERNAME.sentence);
					return true;
				}
				Player tp = Data.PLAYERS.get(playerNr).getPlayer();
				Data.PLAYERS.get(playerNr).setDead(false);
				Data.PLAYERS.get(playerNr).setHasIsland(false);
				if (!(Data.PLAYERS_NUMBER - 1 < 0)) {
					Data.PLAYERS_NUMBER--;
				}
				player.sendMessage(this.pNameChat + Language.MSGS_NEWISLANDPLAYER1.sentence + tp.getName() + Language.MSGS_NEWISLANDPLAYER2.sentence);
				Data.PLAYERS.get(playerNr).getPlayer().sendMessage(this.pNameChat + Language.MSGS_GOTNEWISLAND1.sentence + player.getName() + Language.MSGS_GOTNEWISLAND2.sentence);
				return true;
			}

			if (args[0].equalsIgnoreCase("offline")) {
				return this.setSkyblockOffline(sender);
			}
			if (args[0].equalsIgnoreCase("online")) {
				return this.setSkyblockOnline(sender);
			}
			if (args[0].equalsIgnoreCase("help")) {
				return this.getListCommands(sender);
			}
			if (args[0].equalsIgnoreCase("reset")) {
				return this.resetSkyblock(sender);
			}
			if (args[0].equalsIgnoreCase("reload")) {
				if (args.length < 2) {
					return true;
				}
				if (args[1].equalsIgnoreCase("config")) {
					return this.reloadConfig(sender);
				}
			}

			if (args[0].equalsIgnoreCase("status")) {
				return this.getStatus(sender);
			}

			player.sendMessage(this.pNameChat + Language.MSGS_WRONGARGS.sentence);
			return true;
		}

		return false;
	}

	private boolean reloadLanguage(CommandSender sender) {
		this.loadLanguageConfig();
		sender.sendMessage(this.pNameChat + Language.MSGS_LANGUAGERELOADED.sentence);
		return true;
	}

	public boolean notAuthorized(Player p) {
		p.sendMessage(this.pNameChat + Language.MSGS_notAuthorized.sentence);
		return true;
	}

	public boolean setSkyblockOffline(CommandSender sender) {
		String msg = "";
		if (sender instanceof Player) {
			msg = this.pNameChat + ChatColor.WHITE;
		} else {
			msg = this.pName;
		}

		if (!Permissions.SKYBLOCK_SET.has(sender)) {
			this.notAuthorized((Player) sender);
		}

		try {
			sender.sendMessage(msg + Language.MSGS_STOPPING.sentence);

			//Checke Spieler ob keiner mehr in Welt Skyblock ist	
			Player[] playerList = this.getServer().getOnlinePlayers();
			for (Player p : playerList) {
				if (p.getWorld().equals(SkyblockMultiplayer.getSkyblockIslands())) {
					sender.sendMessage(msg + Language.MSGS_PLAYERSINSB.sentence);
					return true;
				}
			}

			this.getServer().unloadWorld(SkyblockMultiplayer.WORLD_NAME, true);
			SkyblockMultiplayer.skyblockIslands = null;
			Data.SKYBLOCK_ONLINE = false;
			this.setStringbyPath(this.configPlugin, this.fileConfig, "options.skyblockonline", false);
			sender.sendMessage(msg + Language.MSGS_ISNOWOFFLINE.sentence);
			return true;

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			sender.sendMessage(msg + Language.MSGS_ERROROCCURED.sentence);
			return true;
		}
	}

	public boolean setSkyblockOnline(CommandSender sender) {
		String msg = "";
		if (sender instanceof Player) {
			msg = this.pNameChat;
		} else {
			msg = this.pName;
		}
		if (!Permissions.SKYBLOCK_SET.has(sender)) {
			this.notAuthorized((Player) sender);
		}

		sender.sendMessage(msg + Language.MSGS_STARTING.sentence);
		SkyblockMultiplayer.skyblockIslands = null;
		SkyblockMultiplayer.getSkyblockIslands();
		Data.SKYBLOCK_ONLINE = true;
		this.setStringbyPath(this.configPlugin, this.fileConfig, "options.skyblockonline", true);
		sender.sendMessage(msg + Language.MSGS_ISNOWONLINE.sentence);
		return true;
	}

	public boolean resetSkyblock(CommandSender sender) {
		String msg = "";
		if (sender instanceof Player) {
			msg = this.pNameChat;
		} else {
			msg = this.pName;
		}

		if (!Permissions.SKYBLOCK_RESET.has(sender)) {
			this.notAuthorized((Player) sender);
		}

		if (Data.SKYBLOCK_ONLINE) {
			sender.sendMessage(msg + ChatColor.RED + Language.MSGS_MUSTBEOFFLINE.sentence);
			return true;
		}

		sender.sendMessage(msg + Language.MSGS_RESETING.sentence);
		this.getServer().unloadWorld(SkyblockMultiplayer.WORLD_NAME, true);

		for (PlayerInfo pi : Data.PLAYERS) {
			pi.setHasIsland(false);
			pi.setDead(false);
		}

		this.sfiles = new ArrayList<File>();
		this.getAllFiles(SkyblockMultiplayer.WORLD_NAME);

		for (File f : this.sfiles) {
			f.delete();
		}

		//Create Skyblock
		SkyblockMultiplayer.skyblockIslands = null;
		SkyblockMultiplayer.getSkyblockIslands();

		//Reset informations
		Data.ISLAND_DISTANCE = 50;
		Data.PLAYERS.clear();
		Data.PLAYERS = new ArrayList<PlayerInfo>();
		Data.PLAYERS_NUMBER = 0;
		Data.ISLAND_NUMBER = 0;

		sender.sendMessage(msg + Language.MSGS_ISNOWRESETED.sentence);
		return true;
	}

	public boolean reloadConfig(CommandSender sender) {
		String msg = "";
		if (sender instanceof Player) {
			msg = this.pNameChat + ChatColor.WHITE;
		} else {
			msg = this.pName;
		}
		if (Permissions.SKYBLOCK_RELOAD.has(sender)) {
			this.notAuthorized((Player) sender);
		}

		this.loadConfig();
		sender.sendMessage(msg + Language.MSGS_CONFIGRELOADED.sentence);
		return true;
	}

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

	public boolean getListCommands(CommandSender sender) {
		String sb_info = "";
		if (sender instanceof Player) {
			sb_info = "-----" + this.pNameChat + "v" + this.pluginFile.getVersion() + "-----\n";
		} else {
			sb_info = "-----" + this.pName + "v" + this.pluginFile.getVersion() + "-----\n";
		}
		String sb = Language.MSGS_SKYBLOCK.sentence + "\n";
		String sb_start = Language.MSGS_CMDSTART.sentence + "\n";
		String sb_leave = Language.MSGS_CMDLEAVE.sentence + "\n";
		String sb_newisland = Language.MSGS_CMDNEWISLAND.sentence + "\n";
		String sb_setOffline = Language.MSGS_CMDSETOFFLINE + "\n";
		String sb_setOnline = Language.MSGS_CMDSETONLINE.sentence + "\n";
		String sb_setLanguage = Language.MSGS_CMDSETLANGUAGE.sentence + "\n";
		String sb_reset = Language.MSGS_CMDRESET.sentence + "\n";
		String sb_reload_config = Language.MSGS_CMDRELOADCONFIG.sentence + "\n";
		String sb_reload_language = Language.MSGS_CMDRELOADLANGUAGE.sentence + "\n";
		String sb_status = Language.MSGS_CMDSTATUS.sentence;

		String ret = "";
		ret += sb_info;
		ret += sb + sb_start + sb_leave + sb_status;
		if (Permissions.SKYBLOCK_NEWISLAND.has(sender)) {
			ret += sb_newisland;
		}
		if (Permissions.SKYBLOCK_SET.has(sender)) {
			ret += sb_setOnline + sb_setOffline + sb_setLanguage;
		}
		if (Permissions.SKYBLOCK_RESET.has(sender)) {
			ret += sb_reset;
		}
		if (Permissions.SKYBLOCK_RELOAD.has(sender)) {
			ret += sb_reload_config;
			ret += sb_reload_language;
		}

		for (String s : ret.split("\n")) {
			if (!s.trim().equals(""))
				sender.sendMessage(s);
		}
		return true;
	}

	ArrayList<File> sfiles;

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

	public int findPlayer(String playername) {
		for (int i = 0; i < Data.PLAYERS.size(); i++) {
			if (Data.PLAYERS.get(i).getPlayerName().equalsIgnoreCase(playername)) {
				return i;
			}
		}
		return -1;
	}

	private static void makeBlock(int x, int y, int z, Material m) {
		SkyblockMultiplayer.getSkyblockIslands().getBlockAt(x, y, z).setType(m);
	}

	private static void quader(int x, int y, int z, Material m) {
		if (y < 0)
			return;
		SkyblockMultiplayer.makeBlock(x, y, z, m);
		SkyblockMultiplayer.makeBlock(x, y, z + 1, m);
		SkyblockMultiplayer.makeBlock(x + 1, y, z, m);
		SkyblockMultiplayer.makeBlock(x + 1, y, z + 1, m);
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
			SkyblockMultiplayer.quader(0, y, 0, Material.OBSIDIAN);
			// Mache Innenwand
			for (int xw = -2; xw < 4; xw++) {
				SkyblockMultiplayer.makeBlock(xw, y, -2, Material.GLASS);
				SkyblockMultiplayer.makeBlock(xw, y, 3, Material.GLASS);
			}
			for (int zw = -2; zw < 4; zw++) {
				SkyblockMultiplayer.makeBlock(-2, y, zw, Material.GLASS);
				SkyblockMultiplayer.makeBlock(3, y, zw, Material.GLASS);
			}
			// Mache Lavatreppe
			SkyblockMultiplayer.quader(x, y, z, Material.getMaterial(43));
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
			SkyblockMultiplayer.quader(x, y - 3, z, Material.GLASS);
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
			SkyblockMultiplayer.quader(x, y, z, Material.getMaterial(43));
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
			SkyblockMultiplayer.quader(x, y, z, Material.getMaterial(44));
			x += stairsHalf[i][0];
			z += stairsHalf[i][1];
			i++;
			if (i == stairsHalf.length)
				i = 0;
		}

		// Setze Lava
		SkyblockMultiplayer.makeBlock(2, yEnde - 3, 2, Material.LAVA);
		// Setze Wasser
		SkyblockMultiplayer.makeBlock(-1, yEnde - 3, 0, Material.WATER);

		// Mache Dach
		for (x = -2; x < 4; x++) {
			for (z = -2; z < 4; z++) {
				SkyblockMultiplayer.makeBlock(x, yEnde - 2, z, Material.getMaterial(43));
			}
		}

		// Zaun
		for (x = -2; x < 4; x++) {
			SkyblockMultiplayer.makeBlock(x, yEnde - 1, -2, Material.FENCE);
		}
		for (z = -2; z < 4; z++) {
			SkyblockMultiplayer.makeBlock(-2, yEnde - 1, z, Material.FENCE);
			SkyblockMultiplayer.makeBlock(3, yEnde - 1, z, Material.FENCE);
		}

		// Fackeln
		SkyblockMultiplayer.makeBlock(-2, yEnde, -2, Material.TORCH);
		SkyblockMultiplayer.makeBlock(-2, yEnde, 3, Material.TORCH);
		SkyblockMultiplayer.makeBlock(3, yEnde, -2, Material.TORCH);
		SkyblockMultiplayer.makeBlock(3, yEnde, 3, Material.TORCH);

		// Mache den Towerboden
		for (x = -2; x < 4; x++) {
			for (z = -2; z < 4; z++) {
				for (int y = 0; y < yStart; y++) {
					SkyblockMultiplayer.makeBlock(x, y, z, Material.AIR);
				}
				SkyblockMultiplayer.makeBlock(x, yStart, z, Material.getMaterial(43));
			}
		}

		//Mache Schilder
		SkyblockMultiplayer.getSkyblockIslands().getBlockAt(1, yEnde - 1, 2).setType(Material.SIGN_POST);
		Sign s1 = (Sign) SkyblockMultiplayer.getSkyblockIslands().getBlockAt(1, yEnde - 1, 2).getState();
		s1.getBlock().setData((byte) 8);
		s1.setLine(0, "Willkommen auf");
		s1.setLine(1, "SkyBlock-");
		s1.setLine(2, "Multiplayer!");
		SkyblockMultiplayer.getSkyblockIslands().getBlockAt(0, yEnde - 1, 2).setType(Material.SIGN_POST);
		Sign s2 = (Sign) SkyblockMultiplayer.getSkyblockIslands().getBlockAt(0, yEnde - 1, 2).getState();
		s2.getBlock().setData((byte) 8);
		s2.setLine(0, "Für weitere");
		s2.setLine(1, "Informationen");
		s2.setLine(2, "</skyblock help>");
		s2.setLine(3, "Viel Erfolg!");
	}
}