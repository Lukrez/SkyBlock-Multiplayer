package me.lukas.SkyblockMultiplayer;

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
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyblockMultiplayer extends JavaPlugin {
	PluginDescriptionFile pluginFile;
	Logger log;
	PluginManager manager;

	PlayerBreackBlockListener breakBlock;
	PlayerPlaceBlockListener placeBlock;
	PlayerUseBucketListener useBucket;
	EntityDeath deathListener;

	protected FileConfiguration config;
	private static World worldIslands = null;
	private static String WORLD_NAME = "skyblockislands";

	public static FileConfiguration sconf;
	public static File sconfFile;

	@Override
	public void onDisable() {
		log.info(pluginFile.getName() + " Version " + pluginFile.getVersion() + " disabled.");
	}

	@Override
	public void onEnable() {
		pluginFile = this.getDescription();
		log = this.getServer().getLogger();
		breakBlock = new PlayerBreackBlockListener();
		placeBlock = new PlayerPlaceBlockListener();
		useBucket = new PlayerUseBucketListener();
		deathListener = new EntityDeath();
		manager = this.getServer().getPluginManager();

		manager.registerEvent(Type.BLOCK_BREAK, breakBlock, Priority.Normal, this);
		manager.registerEvent(Type.BLOCK_PLACE, placeBlock, Priority.Normal, this);
		manager.registerEvent(Type.PLAYER_BUCKET_EMPTY, useBucket, Priority.Normal, this);
		manager.registerEvent(Type.ENTITY_DEATH, this.deathListener, Priority.Normal, this);

		log.info(pluginFile.getName() + " Version " + pluginFile.getVersion() + " enabled.");

		config = this.getConfig();
		File confFile = new File(this.getDataFolder(), "config.yml");
		SkyblockMultiplayer.sconf = config;
		SkyblockMultiplayer.sconfFile = confFile;

		if (!confFile.exists()) {

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

			CreateNewIsland.IslandDistance = 50;
			config.set("islandDistance", 50);
			config.set("chest.items", items);
			config.set("mods", "");
			config.set("skyblockonline", true);

			Data.itemsChest = itemsChest;

			try {
				config.save(confFile);
			} catch (IOException e) {
			}
		} else {
			try {
				config.load(confFile);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}

			try {
				CreateNewIsland.IslandDistance = config.getInt("islandDistance");
			} catch (Exception ex) {
				CreateNewIsland.IslandDistance = 50;
			}

			String[] dataItems = config.get("chest.items").toString().split(";");
			ArrayList<ItemStack> alitemsChest = new ArrayList<ItemStack>();

			for (String s : dataItems) {
				if (s.trim() != "") {
					String[] dataValues = s.split(":");
					try {
						alitemsChest.add(new ItemStack(Integer.parseInt(dataValues[0]), Integer.parseInt(dataValues[1])));
					} catch (Exception ex) {
					}
				}
			}

			ItemStack[] itemsChest = new ItemStack[alitemsChest.size()];

			for (int i = 0; i < itemsChest.length; i++) {
				itemsChest[i] = alitemsChest.get(i);
			}

			alitemsChest = new ArrayList<ItemStack>();
			alitemsChest.add(new ItemStack(Material.ICE, 2));
			alitemsChest.add(new ItemStack(Material.SAPLING, 5));
			alitemsChest.add(new ItemStack(Material.MELON, 3));
			alitemsChest.add(new ItemStack(Material.CACTUS, 1));
			alitemsChest.add(new ItemStack(Material.LAVA_BUCKET, 1));
			alitemsChest.add(new ItemStack(Material.PUMPKIN, 1));

			itemsChest = new ItemStack[alitemsChest.size()];

			for (int i = 0; i < itemsChest.length; i++) {
				itemsChest[i] = alitemsChest.get(i);
			}
			try {
				Data.itemsChest = itemsChest;
			} catch (Exception ex) {
				Data.itemsChest = itemsChest;
			}

			// Lade die Mods
			Data.mods = config.getString("mods").split(" ");

			//Lade Status Skyblock
			try {
				Data.skyblockonline = config.getBoolean("skyblockonline");
			} catch (Exception ex) {
				Data.skyblockonline = true;
			}

		}

		SkyblockMultiplayer.getWorldIslands();
	}

	public static World getWorldIslands() {
		if (worldIslands == null) {
			worldIslands = WorldCreator.name(SkyblockMultiplayer.WORLD_NAME).environment(Environment.NORMAL).generator(new SkyblockChunkGenerator()).createWorld();
			SkyblockMultiplayer.CreateSpawnTower();
			worldIslands.setSpawnLocation(1, SkyblockMultiplayer.getWorldIslands().getHighestBlockYAt(1, 1), 1);
		}

		return worldIslands;
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		return new SkyblockChunkGenerator();
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

	public static Chest chest;

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("This can only do a player!");
			return true;
		}

		Player player = (Player) sender;

		if (cmd.getName().equalsIgnoreCase("skyblock")) {
			if (args.length == 0) {
				if (!Data.skyblockonline) {
					player.sendMessage(ChatColor.GREEN + "Skyblock ist offline.");
					return true;
				}

				if (player.getWorld().equals(SkyblockMultiplayer.getWorldIslands())) {
					return true;
				}

				int playerNr = this.findPlayer(player.getName()); // Suche Spieler
				if (playerNr == -1) { // Falls der Spieler nicht in der Liste ist, füge ihn hinzu
					Data.players.add(new PlayerInfo(player));
				} else {
					//Refreshe OldLocation vom Spieler
					Data.players.get(playerNr).setOldPlayerLocation(player.getLocation());
				}
				player.teleport(SkyblockMultiplayer.getWorldIslands().getSpawnLocation()); // Teleportiere Spieler zum Spawntower in der Mitte
				player.sendMessage(ChatColor.GREEN + "Willkommen auf der Welt SkyBlock für Multiplayer! Es gibt momentan " + CreateNewIsland.ISLANDNR + " Inseln und " + Data.AnzahlPlayers + " Spieler. Gib '/skyblock start' ein um eine eigene Insel zu bekommen.");
				return true;
			}

			if (args[0].equalsIgnoreCase("start")) {
				if (!Data.skyblockonline) {
					player.sendMessage(ChatColor.GREEN + "Skyblock ist offline.");
					return true;
				}

				//Wenn Spieler nicht in Welt Skyblock: return true
				if (!(player.getWorld() == SkyblockMultiplayer.getWorldIslands()))
					return true;

				boolean ismepty = this.checkIfPlayerInventoryEmpty(player);
				if (!ismepty) {
					player.sendMessage(ChatColor.RED + "Es ist nicht erlaubt mit Inhalt im Inventar mitzuspielen!");
					return true;
				}

				int playerNr = this.findPlayer(player.getName()); // Suche Spieler
				if (playerNr == -1) { // Falls der Spieler nicht in der Liste ist, füge ihn hinzu
					Data.players.add(new PlayerInfo(player));

					playerNr = this.findPlayer(player.getName());
				}
				if (Data.players.get(playerNr).getHasIsland()) { // Hat bereits eine Insel
					if (Data.players.get(playerNr).getDead()) {
						player.sendMessage(ChatColor.RED + "Du hattest bereits eine Insel und hast Mist gebaut - heul den Eventmanager voll, vielleicht gibt er dir eine neue Insel.");
						return true;
					}
					// Spieler teleportieren
					player.teleport(Data.players.get(playerNr).getIslandLocation());
					player.sendMessage(ChatColor.GREEN + "Willkommen zurück " + player.getName() + ".");
					return true;
				} else {
					// Erstelle eine neue Insel für einen neuen Spieler
					CreateNewIsland isl = new CreateNewIsland(player);
					Data.players.get(playerNr).setIslandLocation(isl.Islandlocation);
					Data.players.get(playerNr).setHasIslandToTrue();
					Data.AnzahlPlayers++;
					// Nachricht an alle
					for (PlayerInfo pinfo : Data.players) {
						pinfo.getPlayer().sendMessage(ChatColor.GREEN + "Der Spieler " + player.getName() + " spielt mit. Damit sind es " + Data.AnzahlPlayers + " Spieler.");
					}
					player.sendMessage(ChatColor.GREEN + "Fall nicht runter, und mach kein Obsidian :-)");

					return true;
				}
			}

			if (args[0].equalsIgnoreCase("leave")) {
				if (!player.getWorld().equals(SkyblockMultiplayer.getWorldIslands())) {
					return true;
				}

				int playerNr = this.findPlayer(player.getName());
				if (playerNr == -1) {
					player.setHealth(0);
					return true;
				}

				boolean ismepty = this.checkIfPlayerInventoryEmpty(player);
				if (!ismepty) {
					if (Data.players.get(playerNr).getHasIsland()) {
						player.sendMessage(ChatColor.RED + "Es ist nicht erlaubt mit Inhalt im Inventar diese Welt zu verlassen. Packe dein Inhalt im Inventar in eine Kiste oder stirb.");
						return true;
					}
				}

				Location l = Data.players.get(playerNr).getOldPlayerLocation();
				player.teleport(l);
				player.sendMessage(ChatColor.GREEN + "Du hast die Welt Skyblock verlassen und bist (vielleicht) zurück auf sicherem Boden. Achte auf den Creeper hinter dir :-)");
				return true;
			}

			if (args[0].equalsIgnoreCase("newisland")) {
				if (!Data.skyblockonline) {
					player.sendMessage(ChatColor.GREEN + "Skyblock ist offline.");
					return true;
				}

				if (!player.isOp()) {
					if (!this.isMod(player)) {
						player.sendMessage(ChatColor.RED + "Du bist nicht autorisiert!");
						return true;
					}
				}

				if (args.length == 1) {
					player.sendMessage("Du musst einen Spielernamen angeben!");
					return true;
				}

				int playerNr = this.findPlayer(args[1]);
				if (playerNr == -1) {
					player.sendMessage("Es gibt keinen Spieler mit diesem Namen.");
					return true;
				}
				Player tp = Data.players.get(playerNr).getPlayer();
				Data.players.get(playerNr).setDeadToFalse();
				Data.players.get(playerNr).setHasIslandToFalse();
				Data.AnzahlPlayers--;
				player.sendMessage(ChatColor.GREEN + "Der Spieler " + tp.getName() + " hat eine neue Insel bekommen.");
				Data.players.get(playerNr).getPlayer().sendMessage(ChatColor.GREEN + "Du hast eine neue Insel von EventManger " + player.getName() + " bekommen. Benutze '/skyblock start' um dorthin zu kommen.");
				return true;
			}

			if (args[0].equalsIgnoreCase("addmod")) {
				if (!player.isOp()) {
					player.sendMessage(ChatColor.RED + "Du bist nicht autorisiert!");
					return true;
				}

				if (args.length == 1) {
					player.sendMessage("Du musst einen Spielernamen angeben!");
					return true;
				}

				for (String s : Data.mods) {
					if (s.equalsIgnoreCase(args[1])) {
						player.sendMessage(ChatColor.GREEN + "Der Spieler ist schon in der Liste der Mods");
						return true;
					}
				}

				Data.addMod(args[1]);
				player.sendMessage(ChatColor.GREEN + "Der Spieler " + args[1] + " wurde zu der Liste der Mods hinzugefügt.");
				return true;
			}

			if (args[0].equalsIgnoreCase("delmod")) {
				if (!player.isOp()) {
					player.sendMessage(ChatColor.RED + "Du bist nicht autorisiert!");
					return true;
				}

				if (args.length == 1) {
					player.sendMessage("Du musst einen Spielernamen angeben!");
					return true;
				}

				boolean exists = false;
				for (String s : Data.mods) {
					if (s.equalsIgnoreCase(args[1])) {
						exists = true;
						break;
					}
				}

				if (!exists) {
					player.sendMessage(ChatColor.GREEN + "Der Spieler " + args[1] + " ist nicht in der Liste der Mods.");
					return true;
				}

				Data.delMod(args[1]);
				player.sendMessage(ChatColor.GREEN + "Der Spieler " + args[1] + " wurde aus der Liste der Mods entfernt.");
				return true;
			}

			if (args[0].equalsIgnoreCase("mods")) {
				String mods = "";

				for (int x = 0; x < Data.mods.length; x++) {
					mods += Data.mods[x];
					if ((x + 1) != mods.length()) {
						mods += " ";
					}
				}

				player.sendMessage(ChatColor.GREEN + "Mods: " + mods.trim());
				return true;
			}

			/*if(args[0].equalsIgnoreCase("reset")){
				if(!player.isOp()){
					if (!this.isMod(player)){
						player.sendMessage(ChatColor.RED + "Du bist nicht autorisiert!");
						return true;
					}
				}
				
				// Suche entfernteste Insel
				// erstelle Insel
				CreateNewIsland isl = new CreateNewIsland();
				Location l = isl.getIslandPosition(CreateNewIsland.ISLANDNR);
				// Suche höchste Koordinate
				int delete = 0;
				try{
					delete = Integer.parseInt(args[1]);
				}catch(Exception ex){
					player.sendMessage(ChatColor.RED + "Du musst einen Wert angeben!");
				}
				
				if (Math.abs(l.getBlockX()) > Math.abs(l.getBlockZ())){
					delete += Math.abs(l.getBlockX());
				} else {
					delete += Math.abs(l.getBlockZ());
				}
				delete += 50;
				if (delete > 1000){
					player.sendMessage("Die Welt ist zu groß zum Reset, bitte lösche den Welt-Ordner manuell.");
					return true;
				} else {
					player.sendMessage("Die Welt wird in einem Radius von " + delete + " Blöcken gelöscht.");
				}
				
				for (int x=-delete;x<=delete;x++){
					for (int z=-delete;z<=delete;z++){
						for (int y=0;y<=128;y++){
							if(SkyblockMultiplayer.getWorldIslands().getBlockAt(x, y, z).getType() != Material.AIR){
								if (Math.abs(x) > 20 || Math.abs(z) > 20){
									SkyblockMultiplayer.getWorldIslands().getBlockAt(x, y, z).setType(Material.AIR);
								}
							}
						}
					}
				}
				// Erstelle den Spawntower
				//MultipleSurvivalIslands.CreateSpawnTower();
				
				// Resete die Spielerinfo
				CreateNewIsland.ISLANDNR = 0;
				Data.players = new ArrayList<PlayerInfo>();
				Data.AnzahlPlayers = 0;
				player.sendMessage("Die Welt wurde zurückgesetzt.");
				return true;
			}*/

			if (args[0].equalsIgnoreCase("offline")) {
				//Checke ob der Spieler OP ist
				if (!player.isOp()) {
					player.sendMessage(ChatColor.RED + "Du bist nicht autorisiert!");
					return true;
				}

				try {
					player.sendMessage("Stoppe Skyblock...");

					//Checke Spieler ob keiner mehr in Welt Skyblock ist					
					Player[] playerList = this.getServer().getOnlinePlayers();
					for (Player p : playerList) {
						if (p.getWorld().equals(SkyblockMultiplayer.getWorldIslands())) {
							player.sendMessage(ChatColor.RED + "Es sind Spieler in der Welt Skyblock. Skyblock kann nicht deaktviert werden!");
							return true;
						}
					}

					this.getServer().unloadWorld(SkyblockMultiplayer.WORLD_NAME, true);
					SkyblockMultiplayer.worldIslands = null;
					Data.skyblockonline = false;
					Data.setStatus(false);

					// Resete die Spielerinfo
					CreateNewIsland.ISLANDNR = 0;
					Data.players.clear();
					Data.AnzahlPlayers = 0;

					player.sendMessage(ChatColor.GREEN + "Skyblock für Multiplayer ist nun offline. Um die Welt zu resten lösche den Ordner " + SkyblockMultiplayer.WORLD_NAME);
					return true;

				} catch (Exception ex) {
					System.out.println(ex.getMessage());
					player.sendMessage(ChatColor.RED + "Error orccured! Error-Message posted in Server Konsole.");
					return true;
				}
			}

			if (args[0].equalsIgnoreCase("online")) {
				if (!player.isOp()) {
					player.sendMessage(ChatColor.RED + "Du bist nicht autorisiert!");
					return true;
				}

				player.sendMessage("Starte Skyblock...");
				SkyblockMultiplayer.worldIslands = null;
				SkyblockMultiplayer.getWorldIslands();
				Data.skyblockonline = true;
				Data.setStatus(true);

				// Resete die Spielerinfo
				CreateNewIsland.ISLANDNR = 0;
				Data.players.clear();
				Data.AnzahlPlayers = 0;
				player.sendMessage(ChatColor.GREEN + "Skyblock für Multiplayer ist nun online!");
				return true;
			}

			if (args[0].equalsIgnoreCase("help")) {
				for (String s : this.getListCommands(player).split("\n")) {
					player.sendMessage(ChatColor.GREEN + s);
				}
				return true;
			}

			//reset
			/*if(args[0].equalsIgnoreCase("reset")){
			   if(!player.isOp()){
					player.sendMessage(ChatColor.RED + "Du bist nicht autorisiert!");
					return true;
				}			  
			 
				if(player.getWorld().equals(SkyblockMultiplayer.getWorldIslands())){
					player.sendMessage(ChatColor.RED + "Du musst Skyblock verlassen, bevor du es reseten kannst!");
					return true;
				}
				
				player.sendMessage("Unloading Skyblock...");
				this.getServer().unloadWorld(SkyblockMultiplayer.WORLD_NAME, true);
				player.sendMessage("Unloading Skyblock - finished");
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				player.sendMessage("Deleting Skyblock...");
				//get Files
				this.sfiles = new ArrayList<File>();
				this.getAllFilesAndDirectories(SkyblockMultiplayer.WORLD_NAME);
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				for(File f:this.sfiles){
					f.delete();
				}
				
				for(File f:this.sfiles){
					f.delete();
				}
							
				player.sendMessage("Deleting Skyblock - finished");
				
				//Create Skyblock
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				player.sendMessage("Create Skyblock...");
				SkyblockMultiplayer.worldIslands = null;
				SkyblockMultiplayer.getWorldIslands();
				
				//Resete die Spielerinfo
				CreateNewIsland.ISLANDNR = 0;
				Data.players.clear();
				Data.AnzahlPlayers = 0;				
				player.sendMessage("Create Skyblock - finished.");
				player.sendMessage(ChatColor.GREEN + "World Skyblock wurde resetet.");
				
				return true;
			}*/
		}

		return true;
	}

	public String getListCommands(Player p) {
		String rightsPlayer = "Befehle für Spieler:\n" + "/skyblock help - Liste alle Kommandos nach Rechten gefiltert auf\n" + "/skyblock - Skyblock für Mutiplayer betreten\n" + "/skyblock start - Bei Skyblock mitspielen\n" + "/skyblock leave - Zur alten Position in der alten Welt zurückkehren\n" + "/skyblock mods - Listet alle Mods auf\n";

		String rightsMod = "Befehle für Mod:\n" + "/skyblock newisland [player] - Einem Spieler die Möglichkeit geben einer neuen Insel zu joinen\n";

		String rightsOp = "Befehle für Op:\n" + "/skyblock offline - Deaktiviert Skyblock, damit diese Option geht darf kein Spieler mehr in Skyblock sein und die Welt kann gelöscht werden\n" + "/skyblock online - Aktiviert Skyblock und erstellt die Welt neu, wenn nicht vorhanden" + "/skyblock addmod - Einen Spieler zur Liste der Mods hinzufügen\n" + "/skyblock delmod - Einen Spieler aus der Liste der Mods entfernen\n";

		if (p.isOp()) {
			return rightsPlayer + "\n" + rightsMod + "\n" + rightsOp;
		}

		if (this.isMod(p)) {
			return rightsPlayer + "\n" + rightsMod;
		}

		return rightsPlayer;
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
					this.sfiles.add(f);
					this.getAllFilesAndDirectories(f.getAbsolutePath());
				}
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}
	}

	public int findPlayer(String playername) {
		for (int i = 0; i < Data.players.size(); i++) {
			if (Data.players.get(i).getPlayerName().equalsIgnoreCase(playername)) {
				return i;
			}
		}
		return -1;
	}

	public boolean isMod(Player p) {
		for (String s : Data.mods) {
			if (s.equalsIgnoreCase(p.getName())) {
				return true;
			}
		}
		return false;
	}

	private static void makeBlock(int x, int y, int z, Material m) {
		SkyblockMultiplayer.getWorldIslands().getBlockAt(x, y, z).setType(m);
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
		SkyblockMultiplayer.getWorldIslands().getBlockAt(1, yEnde - 1, 2).setType(Material.SIGN_POST);
		Sign s1 = (Sign) SkyblockMultiplayer.getWorldIslands().getBlockAt(1, yEnde - 1, 2).getState();
		s1.getBlock().setData((byte) 8);
		s1.setLine(0, "Willkommen auf");
		s1.setLine(1, "SkyBlock-");
		s1.setLine(2, "Multiplayer!");
		SkyblockMultiplayer.getWorldIslands().getBlockAt(0, yEnde - 1, 2).setType(Material.SIGN_POST);
		Sign s2 = (Sign) SkyblockMultiplayer.getWorldIslands().getBlockAt(0, yEnde - 1, 2).getState();
		s2.getBlock().setData((byte) 8);
		s2.setLine(0, "Befehle");
		s2.setLine(1, "/SkyBlock start");
		s2.setLine(2, "/SkyBlock leave");
		s2.setLine(3, "Viel Erfolg!");
	}
}