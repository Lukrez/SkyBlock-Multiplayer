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

	private FileConfiguration config;
	private File file;

	FileConfiguration playerConfig;
	File playerFile;

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

		this.config = this.getConfig();
		this.file = new File(this.getDataFolder(), "config.yml");
		this.loadConfig();

		this.playerConfig = new YamlConfiguration();
		this.playerFile = new File(this.getDataFolder(), "players.yml");
		this.loadPlayerConfig();

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

		if (!this.file.exists()) {

			Data.ISLAND_DISTANCE = 50;
			Data.ITEMSCHEST = itemsChest;
			Data.SKYBLOCK_ONLINE = true;

			this.setStringbyPath(this.config, this.file, "options." + "islandDistance", 50);
			this.setStringbyPath(this.config, this.file, "options." + "chest.items", items);
			this.setStringbyPath(this.config, this.file, "options." + "skyblockonline", true);
		} else {
			try {
				this.config.load(this.file);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}

			try {
				Data.ISLAND_DISTANCE = Integer.parseInt(this.getStringbyPath(this.config, "islandDistance", 50));
			} catch (NumberFormatException nfe) {
				Data.ISLAND_DISTANCE = 50;
			}

			if (Data.ISLAND_DISTANCE < 50) {
				Data.ISLAND_DISTANCE = 50;
			}

			String[] dataItems = this.getStringbyPath(this.config, "chest.items", items).split(";");
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
			Data.SKYBLOCK_ONLINE = Boolean.parseBoolean(this.getStringbyPath(this.config, "options.skyblockonline", true));
		}

		SkyblockMultiplayer.getSkyblockIslands();
	}

	public void loadPlayerConfig() {
		if (!this.playerFile.exists()) {
			return;
		}

		try {
			this.playerConfig.load(this.playerFile);
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

	public void setStringbyPath(FileConfiguration fc, File f, String path, Object content) {
		fc.set(path, content.toString());
		try {
			fc.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getStringbyPath(FileConfiguration fc, String path, Object stdContent) {
		if (!fc.contains(path)) {
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

	/*public void addPlayer(PlayerInfo pi) {
		String path = "players." + pi.getPlayerName() + ".";
		this.setStringbyPath(this.playerconfig, this.playerfile, path + "hasIsland", pi.getHasIsland());
		this.setStringbyPath(this.playerconfig, this.playerfile, path + "isDead", pi.isDead());
		this.setStringbyPath(this.playerconfig, this.playerfile, path + "islandLocation", this.getStringLocation(pi.getIslandLocation()));
		this.setStringbyPath(this.playerconfig, this.playerfile, path + "oldLocation", this.getStringLocation(pi.getOldPlayerLocation()));
	}*/

	public PlayerInfo getPlayer(Player p) {
		PlayerInfo pi = new PlayerInfo(p, this);
		if (!this.playerConfig.contains("players." + p.getName())) {
			return null;
		}

		String path = "players." + p.getName() + ".";
		pi.setHasIsland(Boolean.parseBoolean(this.getStringbyPath(this.playerConfig, path + "hasIsland", false)));
		pi.setDead(Boolean.parseBoolean(this.getStringbyPath(this.playerConfig, path + "isDead", false)));
		pi.setIslandLocation(this.getLocationString(this.getStringbyPath(this.playerConfig, path + "islandLocation", "")));
		this.log.info(this.getStringbyPath(this.playerConfig, path + "oldLocation", null));
		pi.setOldPlayerLocation(this.getLocationString(this.getStringbyPath(this.playerConfig, path + "oldLocation", null)));
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
					sender.sendMessage("Gib /skyblock help ein für weitere Informationen");
					return true;
				}
				if (args[0].equalsIgnoreCase("offline")) {
					return this.setSkyblockOffline(sender);
				}
				if (args[0].equalsIgnoreCase("online")) {
					return this.setSkyblockOnline(sender);
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
				if (args[0].equalsIgnoreCase("help")) {
					return this.getListCommands(sender);
				}

				sender.sendMessage(this.pName + "Da ist kein Argument mit diesen Namen.");
				return true;
			}
			return false;
		}

		Player player = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("skyblock")) {
			if (args.length == 0) {
				sender.sendMessage(this.pNameChat + "SkyblockMultiplayer v" + this.pluginFile.getVersion());
				sender.sendMessage(this.pNameChat + "Gib '/skyblock help' für weitere Informationen ein.");
				return true;
			}

			if (args[0].equalsIgnoreCase("join")) {
				if (!Data.SKYBLOCK_ONLINE) {
					player.sendMessage(this.pNameChat + ChatColor.WHITE + "Skyblock ist offline.");
					return true;
				}

				if (player.getWorld().equals(SkyblockMultiplayer.getSkyblockIslands())) {
					return true;
				}

				int playerNr = this.findPlayer(player.getName()); // Suche Spieler
				if (playerNr == -1) { // Falls der Spieler nicht in der Liste ist, füge ihn hinzu
					Data.PLAYERS.add(new PlayerInfo(player, this));
				} else {
					//Refreshe OldLocation vom Spieler
					Data.PLAYERS.get(playerNr).setOldPlayerLocation(player.getLocation());
				}

				int islands = 0;
				for (PlayerInfo p : Data.PLAYERS) {
					if (p.getHasIsland()) {
						islands++;
					}
				}

				player.teleport(SkyblockMultiplayer.getSkyblockIslands().getSpawnLocation()); // Teleportiere Spieler zum Spawntower in der Mitte
				player.sendMessage(this.pNameChat + ChatColor.WHITE + "Willkommen auf der Welt SkyBlock für Multiplayer! Es gibt momentan " + islands + " Inseln und " + Data.PLAYERS_NUMBER + " Spieler. Gib '/skyblock start' ein um eine eigene Insel zu bekommen.");
				return true;
			}

			if (args[0].equalsIgnoreCase("start")) {
				if (!Data.SKYBLOCK_ONLINE) {
					player.sendMessage(this.pNameChat + ChatColor.WHITE + "Skyblock ist offline.");
					return true;
				}

				//Wenn Spieler nicht in Welt Skyblock: return true
				if (!(player.getWorld().equals(SkyblockMultiplayer.getSkyblockIslands()))) {
					return true;
				}

				boolean ismepty = this.checkIfPlayerInventoryEmpty(player);
				if (!ismepty) {
					player.sendMessage(this.pNameChat + ChatColor.RED + "Es ist nicht erlaubt mit Inhalt im Inventar mitzuspielen!");
					return true;
				}

				int playerNr = this.findPlayer(player.getName()); // Suche Spieler
				if (playerNr == -1) { // Falls der Spieler nicht in der Liste ist, füge ihn hinzu
					Data.PLAYERS.add(new PlayerInfo(player, this));
					playerNr = this.findPlayer(player.getName());
				}
				if (Data.PLAYERS.get(playerNr).getHasIsland()) { // Hat bereits eine Insel
					if (Data.PLAYERS.get(playerNr).isDead()) {
						player.sendMessage(this.pNameChat + ChatColor.RED + "Du hattest bereits eine Insel und hast Mist gebaut - heul den Eventmanager voll, vielleicht gibt er dir eine neue Insel.");
						return true;
					}
					// Spieler teleportieren
					player.teleport(Data.PLAYERS.get(playerNr).getIslandLocation());
					player.sendMessage(this.pNameChat + ChatColor.WHITE + "Willkommen zurück " + player.getName() + ".");
					return true;
				} else {
					// Erstelle eine neue Insel für einen neuen Spieler
					CreateNewIsland isl = new CreateNewIsland(player);
					Data.PLAYERS.get(playerNr).setIslandLocation(isl.Islandlocation);
					Data.PLAYERS.get(playerNr).setHasIsland(true);
					Data.PLAYERS_NUMBER++;

					// Nachricht an alle
					for (PlayerInfo pi : Data.PLAYERS) {
						pi.getPlayer().sendMessage(this.pNameChat + ChatColor.WHITE + "Der Spieler " + player.getName() + " spielt mit. Damit sind es " + Data.PLAYERS_NUMBER + " Spieler.");
					}
					player.sendMessage(this.pNameChat + ChatColor.WHITE + "Fall nicht runter, und mach kein Obsidian :-)");
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
					player.sendMessage(this.pNameChat + ChatColor.WHITE + "Du hast die Welt Skyblock verlassen und bist (vielleicht) zurück auf sicherem Boden. Achte auf den Creeper hinter dir :-)");
					return true;
				}

				boolean ismepty = this.checkIfPlayerInventoryEmpty(player);
				if (!ismepty) {
					if (Data.PLAYERS.get(playerNr).getHasIsland()) {
						player.sendMessage(this.pNameChat + ChatColor.RED + "Es ist nicht erlaubt mit Inhalt im Inventar diese Welt zu verlassen. Packe dein Inhalt im Inventar in eine Kiste oder stirb.");
						return true;
					}
				}

				Location l = Data.PLAYERS.get(playerNr).getOldPlayerLocation();
				player.teleport(l);
				player.sendMessage(this.pNameChat + ChatColor.WHITE + "Du hast die Welt Skyblock verlassen und bist (vielleicht) zurück auf sicherem Boden. Achte auf den Creeper hinter dir :-)");
				return true;
			}

			if (args[0].equalsIgnoreCase("newisland")) {
				if (!Permissions.SKYBLOCK_NEWISLAND.has(player)) {
					player.sendMessage(this.pNameChat + ChatColor.RED + "Du bist nicht autorisiert!");
					return true;
				}
				if (!Data.SKYBLOCK_ONLINE) {
					player.sendMessage(this.pNameChat + ChatColor.WHITE + "Skyblock ist offline.");
					return true;
				}

				if (args.length == 1) {
					player.sendMessage(this.pNameChat + ChatColor.WHITE + "Du musst einen Spielernamen angeben!");
					return true;
				}

				int playerNr = this.findPlayer(args[1]);
				if (playerNr == -1) {
					player.sendMessage(this.pNameChat + ChatColor.WHITE + "Es gibt keinen Spieler mit diesem Namen.");
					return true;
				}
				Player tp = Data.PLAYERS.get(playerNr).getPlayer();
				Data.PLAYERS.get(playerNr).setDead(false);
				Data.PLAYERS.get(playerNr).setHasIsland(false);
				if (!(Data.PLAYERS_NUMBER - 1 < 0)) {
					Data.PLAYERS_NUMBER--;
				}
				player.sendMessage(this.pNameChat + ChatColor.WHITE + "Der Spieler " + tp.getName() + " hat eine neue Insel bekommen.");
				Data.PLAYERS.get(playerNr).getPlayer().sendMessage(ChatColor.GREEN + "Du hast eine neue Insel von EventManger " + player.getName() + " bekommen. Benutze '/skyblock start' um dorthin zu kommen.");
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

			player.sendMessage(this.pNameChat + "Da ist kein Argument mit diesen Namen.");
			return true;
		}

		return false;
	}

	public boolean setSkyblockOffline(CommandSender sender) {
		String msg = "";
		if (sender instanceof Player) {
			msg = this.pNameChat + ChatColor.WHITE;
		} else {
			msg = this.pName;
		}

		if (!Permissions.SKYBLOCK_OFFLINE.has(sender)) {
			sender.sendMessage(msg + "Du bist nicht autorisiert!");
			return true;
		}

		try {
			sender.sendMessage(msg + "Stoppe Skyblock...");

			//Checke Spieler ob keiner mehr in Welt Skyblock ist	
			Player[] playerList = this.getServer().getOnlinePlayers();
			for (Player p : playerList) {
				if (p.getWorld().equals(SkyblockMultiplayer.getSkyblockIslands())) {
					sender.sendMessage(msg + "Es sind Spieler in der Welt Skyblock. Skyblock kann nicht deaktviert werden!");
					return true;
				}
			}

			this.getServer().unloadWorld(SkyblockMultiplayer.WORLD_NAME, true);
			SkyblockMultiplayer.skyblockIslands = null;
			Data.SKYBLOCK_ONLINE = false;
			this.setStringbyPath(this.config, this.file, "options.skyblockonline", false);
			sender.sendMessage(msg + "Skyblock für Multiplayer ist nun offline. Um die Welt zu resten, lösche den Ordner " + SkyblockMultiplayer.WORLD_NAME);
			return true;

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			sender.sendMessage(msg + "Error orccured! Error-Message posted in Server Konsole.");
			return true;
		}
	}

	public boolean setSkyblockOnline(CommandSender sender) {
		String msg = "";
		if (sender instanceof Player) {
			msg = this.pNameChat + ChatColor.WHITE;
		} else {
			msg = this.pName;
		}
		if (!Permissions.SKYBLOCK_ONLINE.has(sender)) {
			sender.sendMessage(msg + ChatColor.RED + "Du bist nicht autorisiert!");
			return true;
		}

		sender.sendMessage(msg + "Starte Skyblock...");
		SkyblockMultiplayer.skyblockIslands = null;
		SkyblockMultiplayer.getSkyblockIslands();
		Data.SKYBLOCK_ONLINE = true;
		this.setStringbyPath(this.config, this.file, "options.skyblockonline", true);
		sender.sendMessage(msg + "Skyblock für Multiplayer ist nun online!");
		return true;
	}

	public boolean resetSkyblock(CommandSender sender) {
		String msg = "";
		if (sender instanceof Player) {
			msg = this.pNameChat + ChatColor.WHITE;
		} else {
			msg = this.pName;
		}

		if (!Permissions.SKYBLOCK_RESET.has(sender)) {
			sender.sendMessage(msg + "Du bist nicht autorisiert!");
			return true;
		}

		if (Data.SKYBLOCK_ONLINE) {
			sender.sendMessage(msg + ChatColor.RED + "Skyblock muss offline sein bevor, du es reseten kannst.");
			return true;
		}

		sender.sendMessage(msg + "Reseting Skyblock...");
		this.getServer().unloadWorld(SkyblockMultiplayer.WORLD_NAME, true);

		//Get Files and delete them
		this.playerFile.delete();

		this.sfiles = new ArrayList<File>();
		this.getAllFilesAndDirectories(SkyblockMultiplayer.WORLD_NAME);

		for (File f : this.sfiles) {
			f.delete();
		}

		//Create Skyblock
		SkyblockMultiplayer.skyblockIslands = null;
		SkyblockMultiplayer.getSkyblockIslands();

		//Resete die Spielerinfo
		Data.ISLAND_DISTANCE = 50;
		Data.PLAYERS.clear();
		Data.PLAYERS = new ArrayList<PlayerInfo>();
		Data.PLAYERS_NUMBER = 0;
		Data.ISLAND_NUMBER = 0;

		sender.sendMessage(msg + "Skyblock wurde resetet.");
		return true;
	}

	public boolean reloadConfig(CommandSender sender) {
		String msg = "";
		if (sender instanceof Player) {
			msg = this.pNameChat + ChatColor.WHITE;
		} else {
			msg = this.pName;
		}
		if (Permissions.SKYBLOCK_RELOAD_CONFIG.has(sender)) {
			sender.sendMessage(msg + "Du bist nicht autorisiert!");
			return true;
		}

		this.loadConfig();
		sender.sendMessage(msg + "Config-Datei wurde neu geladen.");
		return true;
	}

	public boolean getStatus(CommandSender sender) {
		if (Data.SKYBLOCK_ONLINE) {
			sender.sendMessage("Status: Online");
		} else {
			sender.sendMessage("Status: Offline");
		}

		int islands = 0;
		for (PlayerInfo p : Data.PLAYERS) {
			if (p.getHasIsland()) {
				islands++;
			}
		}

		sender.sendMessage("Anzahl der Inseln: " + islands);
		sender.sendMessage("Anzahl der Spieler: " + Data.PLAYERS.size());
		return true;
	}

	public boolean getListCommands(CommandSender sender) {
		String sb_info = "";
		if (sender instanceof Player) {
			sb_info = "-----" + this.pNameChat + "v" + this.pluginFile.getVersion() + "-----\n";
		} else {
			sb_info = "-----" + this.pName + "v" + this.pluginFile.getVersion() + "-----\n";
		}
		String sb_help = "/skyblock help - Listet alle Kommandos nach Rechten gefiltert auf\n";
		String sb = "/skyblock join - Die Welt Skyblock betreten\n";
		String sb_start = "/skyblock start - Bei Skyblock mitspielen\n";
		String sb_leave = "/skyblock leave - Zur alten Position in der alten Welt zurückkehren\n";
		String sb_newisland = "/skyblock newisland [player] - Einem Spieler die Möglichkeit geben einer neuen Insel zu joinen\n";
		String sb_offline = "/skyblock offline - Deaktiviert Skyblock, damit diese Option geht darf kein Spieler mehr in Skyblock sein\n";
		String sb_online = "/skyblock online - Aktiviert Skyblock und erstellt die Welt neu, wenn nicht vorhanden\n";
		String sb_reset = "/skyblock reset - Resetet die Welt Skyblock und die Spielerinfos\n";
		String sb_reload_config = "/skyblock reload config - Lädt die Konfigdatei neu\n";
		String sb_status = "/skyblock status - Zeigt Informationen zu Skyblock an\n";

		String ret = "";
		ret += sb_info;
		ret += sb_help + sb + sb_start + sb_leave + sb_status;
		if (Permissions.SKYBLOCK_NEWISLAND.has(sender)) {
			ret += sb_newisland;
		}
		if (Permissions.SKYBLOCK_ONLINE.has(sender)) {
			ret += sb_online;
		}
		if (Permissions.SKYBLOCK_OFFLINE.has(sender)) {
			ret += sb_offline;
		}
		if (Permissions.SKYBLOCK_RESET.has(sender)) {
			ret += sb_reset;
		}
		if (Permissions.SKYBLOCK_RELOAD_CONFIG.has(sender)) {
			ret += sb_reload_config;
		}

		for (String s : ret.split("\n")) {
			if (!s.trim().equals(""))
				sender.sendMessage(s);
		}
		return true;
	}

	ArrayList<File> sfiles;

	public void getAllFilesAndDirectories(String path) {

		File dirpath = new File(path);
		if (!dirpath.exists()) {
			return;
		}

		for (File f : dirpath.listFiles()) {
			try {
				if (!f.isDirectory()) {
					this.sfiles.add(f);
				} else {
					this.getAllFilesAndDirectories(f.getAbsolutePath());
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
		s2.setLine(2, "/skyblock help ein");
		s2.setLine(3, "Viel Erfolg!");
	}
}