package me.lukas.skyblockmultiplayer;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkyBlockCommand implements CommandExecutor {

	/**
	 * If a command is called, this code will be running.
	 * 	
	 * @param sender that types the com mand.
	 * @param cmd
	 * @param label 
	 * @param  args array that includes all given arguments.
	 * @return boolean command exists or not
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("skyblock")) {
			if (args.length == 0) {
				sender.sendMessage(SkyBlockMultiplayer.getInstance().pluginFile.getName() + " v" + SkyBlockMultiplayer.getInstance().pluginFile.getVersion());
				sender.sendMessage(Language.MSGS_SKYBLOCK.sentence);
				return true;
			}

			if (args[0].equalsIgnoreCase("help")) {
				if (args.length == 1) {
					return this.getListCommands(sender, "1");
				} else {
					return this.getListCommands(sender, args[1]);
				}
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
						File f = new File(SkyBlockMultiplayer.getInstance().getDataFolder(), Settings.towerFileName);
						if (Settings.islandFileName.equalsIgnoreCase("")) {
							SkyBlockMultiplayer.createSpawnTower();
						} else {
							if (f.exists() && f.isFile()) {
								try {
									CreateNewIsland.createStructure(new Location(SkyBlockMultiplayer.getSkyBlockWorld(), 0, Settings.towerYHeight, 0), new File(SkyBlockMultiplayer.getInstance().getDataFolder(), Settings.towerFileName));
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								SkyBlockMultiplayer.createSpawnTower();
							}
						}
						sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_SPAWN_TOWER_RECREATED.sentence);
						return true;
					}
				}
			}

			if (args[0].equalsIgnoreCase("set")) {
				if (!Permissions.SKYBLOCK_SET.has(sender)) {
					return this.notAuthorized(sender);
				}

				if (args.length < 2) {
					sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_ARGS.sentence);
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
					sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_ARGS.sentence);
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
					sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_ARGS.sentence);
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

			if (!(sender instanceof Player)) {
				return true;
			}

			Player player = (Player) sender;

			if (args[0].equalsIgnoreCase("setowner")) {
				if (args.length < 2) {
					player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_ARGS.sentence);
					return true;
				}
				return this.setOwner(player, args[1], args[2]);
			}

			if (args[0].equalsIgnoreCase("tower")) {
				return this.toTower(player);
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

			if (args[0].equalsIgnoreCase("remove")) {
				if (args.length == 0) {
					player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_ARGS.sentence);
					return true;
				}
				return this.removeIsland(player, args[1]);
			}

			if (args[0].equalsIgnoreCase("home")) {
				if (Settings.gameModeSelected == Settings.GameMode.PVP) {
					player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_INBUILD_MODE.sentence);
					return true;
				}

				if (args.length == 1) {
					return this.homeTeleport(player);
				}

				if (args[1].equalsIgnoreCase("set")) {
					return this.homeSet(player);
				}

				if (args[1].equalsIgnoreCase("list")) {
					return this.homeList(player);
				}

				if (args[1].equalsIgnoreCase("join")) {
					if (args.length == 2) {
						player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_ARGS.sentence);
						return true;
					}
					return this.homeJoin(player, args[2]);
				}

				if (args[1].equalsIgnoreCase("add")) {
					if (args.length == 2) {
						player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_ARGS.sentence);
						return true;
					}
					return this.homeAdd(player, args[2]);
				}

				if (args[1].equalsIgnoreCase("remove")) {
					if (args.length == 2) {
						player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_ARGS.sentence);
						return true;
					}
					return this.homeRemove(player, args[2]);
				}
			}

			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_ARGS.sentence);
			return true;
		}
		return false;
	}

	/**
	 * Send a list of all commands.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	private boolean getListCommands(CommandSender sender, String p) {
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
		String sb_removeIsland = "§6/skyblock remove §c<island number>§7 - remove island with given number\n";
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

		String pluginName = SkyBlockMultiplayer.getInstance().pName.replace("[", "").replace("]", "");
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
			String msgs = top + sb_newIsland + sb_closed + sb_opened + sb_setOffline + sb_setOnline + sb_tower_recreate + sb_setLanguage + sb_setGameMode + sb_removeIsland + sb_setOwner + sb_reset + sb_reload_config + sb_reload_language;

			for (String s : msgs.split("\n")) {
				if (!s.trim().equalsIgnoreCase("")) {
					sender.sendMessage(s);
				}
			}
			return true;
		}
		return true;
	}

	/**
	 * Send message not authorized to the sender.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	private boolean notAuthorized(CommandSender s) {
		s.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NOT_AUTHORIZED.sentence);
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
			sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_STOPPING.sentence);

			// Checking if there are no more players in SkyBlock
			Player[] playerList = Bukkit.getServer().getOnlinePlayers();
			for (Player p : playerList) {
				if (p.getWorld().getName().equals(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
					sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_PLAYERS_IN_SB.sentence);
					return true;
				}
			}

			Bukkit.getServer().unloadWorld(Settings.worldName, true);
			SkyBlockMultiplayer.skyBlockWorld = null;
			Settings.skyBlockOnline = false;
			SkyBlockMultiplayer.getInstance().setStringbyPath(SkyBlockMultiplayer.getInstance().configPlugin, SkyBlockMultiplayer.getInstance().filePlugin, ConfigPlugin.OPTIONS_SKYBLOCKONLINE.path, false);
			sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_NOW_OFFLINE.sentence);
			return true;

		} catch (Exception ex) {
			SkyBlockMultiplayer.getInstance().log.warning(ex.getMessage());
			sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ERROR_OCCURED.sentence);
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
		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_STARTING.sentence);
		SkyBlockMultiplayer.skyBlockWorld = null;
		SkyBlockMultiplayer.getSkyBlockWorld();
		Settings.skyBlockOnline = true;
		SkyBlockMultiplayer.getInstance().setStringbyPath(SkyBlockMultiplayer.getInstance().configPlugin, SkyBlockMultiplayer.getInstance().filePlugin, ConfigPlugin.OPTIONS_SKYBLOCKONLINE.path, true);
		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_NOW_ONLINE.sentence);
		return true;
	}

	/**
	 * Lock SkyBlock.
	 * 
	 * @param sender
	 * @return
	 */
	private boolean setOpened(CommandSender sender) {
		Settings.closed = false;
		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_NOW_OPENED.sentence);
		return true;
	}

	/**
	 * Unlock SkyBlock
	 * 
	 * @param sender
	 * @return
	 */
	private boolean setClosed(CommandSender sender) {
		Settings.closed = true;
		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_NOW_CLOSED.sentence);
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
			File f = new File(SkyBlockMultiplayer.getInstance().getDataFolder() + File.separator + "language", s + ".yml");
			File sf = SkyBlockMultiplayer.getInstance().fileLanguage;
			String encoding = "UTF-8";
			if (f.exists()) {
				try {
					SkyBlockMultiplayer.getInstance().fileLanguage = f;
					Scanner scanner = new Scanner(new FileInputStream(SkyBlockMultiplayer.getInstance().fileLanguage), encoding);
					String contentToRead = "";
					while (scanner.hasNextLine()) {
						contentToRead += scanner.nextLine() + System.getProperty("line.separator");
					}
					scanner.close();
					try {
						SkyBlockMultiplayer.getInstance().configLanguage.loadFromString(contentToRead);
					} catch (Exception e) {
						encoding = "Cp1252";
						scanner = new Scanner(new FileInputStream(SkyBlockMultiplayer.getInstance().fileLanguage), encoding);
						contentToRead = "";
						while (scanner.hasNextLine()) {
							contentToRead += scanner.nextLine() + System.getProperty("line.separator");
						}
						scanner.close();
						SkyBlockMultiplayer.getInstance().configLanguage.loadFromString(contentToRead);
					}
					SkyBlockMultiplayer.getInstance().loadLanguageConfig();
					Settings.language = s;
					SkyBlockMultiplayer.getInstance().setStringbyPath(SkyBlockMultiplayer.getInstance().configPlugin, SkyBlockMultiplayer.getInstance().filePlugin, ConfigPlugin.OPTIONS_LANGUAGE.path, s);
					sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_LANGUAGE_CHANGED.sentence);
					return true;
				} catch (Exception e) {
					SkyBlockMultiplayer.getInstance().fileLanguage = sf;
					sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ERROR_OCCURED.sentence + ": " + e.getLocalizedMessage());
					sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_LANGUAGE_NOT_CHANGED.sentence);
					return true;
				}
			} else {
				sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_LANGUAGE_FILE_NOT_EXISTS.sentence);
				return true;
			}
		} else {
			sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_LANGUAGE_NOT_CHANGED.sentence);
			return true;
		}
	}

	/**
	 * Change gamemode of SkyBlock.
	 * 
	 * @param sender
	 * @param s
	 * @return
	 */
	private boolean setGameMode(CommandSender sender, String s) {
		if (s.equalsIgnoreCase("build")) {
			Settings.gameModeSelected = Settings.GameMode.BUILD;
			SkyBlockMultiplayer.getInstance().setStringbyPath(SkyBlockMultiplayer.getInstance().configPlugin, SkyBlockMultiplayer.getInstance().filePlugin, ConfigPlugin.OPTIONS_GAMEMODE.path, "build");
			sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_GAMEMODE_CHANGED.sentence);
			return true;
		}
		if (s.equalsIgnoreCase("pvp")) {
			Settings.gameModeSelected = Settings.GameMode.PVP;
			SkyBlockMultiplayer.getInstance().setStringbyPath(SkyBlockMultiplayer.getInstance().configPlugin, SkyBlockMultiplayer.getInstance().filePlugin, ConfigPlugin.OPTIONS_GAMEMODE.path, "pvp");
			sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_GAMEMODE_CHANGED.sentence);
			return true;
		}
		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_ARGS.sentence);
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
			sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + ChatColor.RED + Language.MSGS_MUST_BEOFFLINE.sentence);
			return true;
		}

		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_RESETING.sentence);
		Bukkit.getServer().unloadWorld(Settings.worldName, true);

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

			SkyBlockMultiplayer.getInstance().writePlayerFile(pi.getPlayerName(), pi);
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

		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_NOW_RESETED.sentence);
		return true;
	}

	private ArrayList<File> sfiles;

	/**
	 * Get all files, directories inside of the given path.
	 * 
	 * @param path the directory. 
	 */
	private void getAllFiles(String path) {

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
				SkyBlockMultiplayer.getInstance().log.warning(ex.getMessage());
			}
		}
	}

	/**
	 * Reloads the config.
	 * 
	 * @param sender that types the command.
	 * @return returns true.
	 */
	private boolean reloadConfig(CommandSender sender) {
		if (!Permissions.SKYBLOCK_RELOAD.has(sender)) {
			return this.notAuthorized(sender);
		}

		SkyBlockMultiplayer.getInstance().loadPluginConfig();
		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_CONFIG_RELOADED.sentence);
		return true;
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
			SkyBlockMultiplayer.getInstance().loadLanguageConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_LANGUAGE_RELOADED.sentence);
		return true;
	}

	/**
	 * Get informations about SkyBlock
	 * 
	 * @param sender
	 * @return
	 */
	private boolean getStatus(CommandSender sender) {
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
	 * Join the world SkyBlock.
	 * 
	 * @param player that types the command.
	 * @return returns true
	 */
	private boolean playerJoin(Player player) {
		if (!Settings.skyBlockOnline) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_OFFLINE.sentence);
			return true;
		}

		if (player.getWorld().getName().equals(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_OUTSIDE_OF_SB.sentence);
			return true;
		}

		if (!Permissions.SKYBLOCK_JOIN.has(player)) {
			return this.notAuthorized(player);
		}

		if (Settings.closed) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_CLOSED.sentence);
			return true;
		}

		PlayerInfo pi = Settings.players.get(player.getName());
		if (pi == null) {
			pi = SkyBlockMultiplayer.getInstance().readPlayerFile(player.getName());
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

		SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);

		player.teleport(SkyBlockMultiplayer.getSkyBlockWorld().getSpawnLocation()); // teleport player to the spawn tower
		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME1.sentence + islands + Language.MSGS_WELCOME2.sentence + Settings.numbersPlayers + Language.MSGS_WELCOME3.sentence);
		return true;
	}

	/**
	 * Get an island in the world SkyBlock.
	 * 
	 * @param player that types the command.
	 * @return returns true.
	 */
	private boolean playerStart(Player player) {
		if (!Settings.skyBlockOnline) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_OFFLINE.sentence);
			return true;
		}

		if (!player.getWorld().getName().equals(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_INSIDE_OF_SB.sentence);
			return true;
		}

		if (!SkyBlockMultiplayer.getInstance().playerIsOnTower(player)) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_ON_TOWER.sentence);
			return true;
		}

		PlayerInfo pi = Settings.players.get(player.getName());
		if (pi == null) {
			pi = SkyBlockMultiplayer.getInstance().readPlayerFile(player.getName());
			if (pi == null) { // if player not exists, create and add him
				pi = new PlayerInfo(player.getName());
				pi.setOldLocation(player.getLocation());
			}
			Settings.players.put(player.getName(), pi);
		}

		if (Settings.gameModeSelected == Settings.GameMode.BUILD) {
			if (!pi.getHasIsland() || pi.getIslandLocation() == null) {
				// new player
				CreateNewIsland isl = new CreateNewIsland(player);
				pi.setIslandLocation(SkyBlockMultiplayer.getInstance().getYLocation(isl.Islandlocation));
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
					SkyBlockMultiplayer.getInstance().clearArmorContents(player);

					player.setExp(0);
					player.setLevel(0);
					player.setFoodLevel(20);
					player.setHealth(player.getMaxHealth());
				}

				// teleport player
				SkyBlockMultiplayer.getInstance().removeCreatures(pi.getIslandLocation());
				player.teleport(pi.getIslandLocation());
				Settings.numbersPlayers++;

				// send message to all
				for (PlayerInfo pInfo : Settings.players.values()) {
					if (pInfo.getPlayer() != null) {
						if (pInfo.getPlayer().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()) || (Permissions.SKYBLOCK_MESSAGES.has(pInfo.getPlayer()) && Settings.messagesOutside)) {
							pInfo.getPlayer().sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME_BROADCAST1.sentence + player.getName() + Language.MSGS_WELCOME_BROADCAST2.sentence);
						}
					}
				}

				SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_TO_NEW_PLAYER.sentence);
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
			if (pi.getHomeLocation() == null) {
				SkyBlockMultiplayer.getInstance().removeCreatures(pi.getIslandLocation());
				player.teleport(pi.getIslandLocation());
			} else {
				Location homeSweetHome = SkyBlockMultiplayer.getInstance().getSafeHomeLocation(pi);
				if (homeSweetHome == null){ // if null, island is missing and home location returns no safe block
					player.sendMessage("Cannot teleport to your home location, your island is probably missing.");
					return true;
				}
				SkyBlockMultiplayer.getInstance().removeCreatures(homeSweetHome);
				player.teleport(homeSweetHome);
			}

			SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME_BACK.sentence + player.getName());
			return true;
		}

		// Game mode is PVP
		if (pi.getHasIsland()) { // player have a island
			if (pi.isDead()) {
				if (pi.getLivesLeft() == 0) {
					if (pi.getIslandsLeft() == 0) {
						// no more lives and islands left
						player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NO_MORELIVES_AND_ISLANDS.sentence);
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
						SkyBlockMultiplayer.getInstance().clearArmorContents(player);

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
					player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "You have " + pi.getLivesLeft() + " lives on this island and " + pi.getIslandsLeft() + " islands left.");

					SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);
					player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_TO_NEW_PLAYER.sentence);
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
					SkyBlockMultiplayer.getInstance().clearArmorContents(player);

					// reset exp, level and food
					player.setExp(0);
					player.setLevel(0);
					player.setFoodLevel(20);
					player.setHealth(player.getMaxHealth());
				}

				// teleport player
				SkyBlockMultiplayer.getInstance().removeCreatures(pi.getIslandLocation());
				player.teleport(pi.getIslandLocation());
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "You have " + pi.getLivesLeft() + " lives on this island and " + pi.getIslandsLeft() + " islands left.");

				SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME_BACK.sentence + player.getName());
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
			SkyBlockMultiplayer.getInstance().removeCreatures(pi.getIslandLocation());
			player.teleport(pi.getIslandLocation());
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "You have " + pi.getLivesLeft() + " lives on this island and " + pi.getIslandsLeft() + " islands left.");

			SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME_BACK.sentence + player.getName());
			return true;
		}

		// player is new
		CreateNewIsland isl = new CreateNewIsland(player);
		pi.setIslandLocation(SkyBlockMultiplayer.getInstance().getYLocation(isl.Islandlocation));

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
			SkyBlockMultiplayer.getInstance().clearArmorContents(player);

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
		SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);

		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_TO_NEW_PLAYER.sentence);
		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "You have " + pi.getLivesLeft() + " lives on this island and " + pi.getIslandsLeft() + " islands left.");

		// Message to all
		for (PlayerInfo pInfo : Settings.players.values()) {
			if (pInfo.getPlayer() != null) {
				if (pInfo.getPlayer().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()) || (Permissions.SKYBLOCK_MESSAGES.has(pInfo.getPlayer()) && Settings.messagesOutside)) {
					pInfo.getPlayer().sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME_BROADCAST1.sentence + player.getName() + Language.MSGS_WELCOME_BROADCAST2.sentence);
				}
			}
		}
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
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_OUTSIDE_OF_SB.sentence);
			return true;
		}

		PlayerInfo pi = Settings.players.get(player.getName());
		if (pi == null) {
			pi = SkyBlockMultiplayer.getInstance().readPlayerFile(player.getName());
			if (pi == null) {
				player.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_LEFT_SKYBLOCK.sentence);
				return true;
			}
			Settings.players.put(player.getName(), pi);
		}

		if (!SkyBlockMultiplayer.getInstance().playerIsOnTower(player)) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_ON_TOWER.sentence);
			return true;
		}

		Location l = pi.getOldLocation();
		if (l == null) {
			player.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
		} else {
			player.teleport(l);
		}

		if (pi.getIslandLocation() == null) {
			Settings.players.remove(player.getName());
		}

		SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);
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
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_OFFLINE.sentence);
			return true;
		}

		if (!SkyBlockMultiplayer.getInstance().playerIsOnTower(player)) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_ON_TOWER.sentence);
			return true;
		}

		if (Settings.gameModeSelected == Settings.GameMode.BUILD) {
			PlayerInfo pi = Settings.players.get(player.getName());
			if (pi == null) {
				pi = SkyBlockMultiplayer.getInstance().readPlayerFile(player.getName());
				if (pi == null) {
					return true;
				}
			}

			pi.setHasIsland(false);

			Location l = pi.getIslandLocation();
			SkyBlockMultiplayer.getInstance().removeIsland(l);

			SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);
			this.playerStart(player);
			// player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NEWISLANDPLAYER1.sentence + pi.getPlayer().getName() + Language.MSGS_NEWISLANDPLAYER2.sentence);
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
			res = SkyBlockMultiplayer.getInstance().getFullPlayerName(target);
			if (res.equalsIgnoreCase("-1")) {
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_PLAYER_NAME.sentence);
				return true;
			}
			if (res.equalsIgnoreCase("0")) {
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_BETTER_SPECIFY.sentence);
				return true;
			}
			pi = Settings.players.get(res);
		}

		if (pi == null) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_PLAYER_NAME.sentence);
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
			SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);
		} else {
			SkyBlockMultiplayer.getInstance().writePlayerFile(res, pi);
		}

		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NEW_ISLAND_PLAYER1.sentence + pi.getPlayer().getName() + Language.MSGS_NEW_ISLAND_PLAYER2.sentence);
		pi.getPlayer().sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_GOT_NEW_ISLAND1.sentence + player.getName() + Language.MSGS_GOT_NEW_ISLAND2.sentence);
		return true;
	}

	/**
	 * Change the owner of an island.
	 * 
	 * @param player
	 * @param number of the island
	 * @param newOwner the new player who gets this island
	 * @return
	 */
	private boolean setOwner(Player player, String number, String newOwner) {
		if (!Permissions.SKYBLOCK_OWNER_SET.has(player)) {
			return this.notAuthorized(player);
		}

		int islandNumber = -1;
		try {
			islandNumber = Integer.parseInt(number);
			if (islandNumber <= 0 || islandNumber > CreateNewIsland.getAmountOfIslands()) {
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_INVALID_ISLAND_NUMBER.sentence);
				return true;
			}
		} catch (Exception e) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_INVALID_ISLAND_NUMBER.sentence);
			return true;
		}

		String res = SkyBlockMultiplayer.getInstance().getFullPlayerName(newOwner);
		if (res.equalsIgnoreCase("-1")) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_PLAYER_NAME.sentence);
			return true;
		}
		if (res.equalsIgnoreCase("0")) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_BETTER_SPECIFY.sentence);
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

		pi.setIslandLocation(CreateNewIsland.getIslandPosition(islandNumber));
		pi.setHasIsland(true);

		SkyBlockMultiplayer.getInstance().writePlayerFile(res, pi);

		Settings.players.put(res, pi);
		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_CHANGED_OWNER_TO.sentence + res);
		return true;
	}

	/**
	 * Teleport to tower.
	 * 
	 * @param player
	 * @return
	 */
	private boolean toTower(Player player) {
		if (!player.getWorld().getName().equals(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_INSIDE_OF_SB.sentence);
			return true;
		}

		if (SkyBlockMultiplayer.getInstance().playerIsOnTower(player)) {
			return true;
		}

		PlayerInfo pi = Settings.players.get(player.getName());
		if (pi == null) {
			pi = SkyBlockMultiplayer.getInstance().readPlayerFile(player.getName());
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
				SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);
				player.teleport(player.getWorld().getSpawnLocation());
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_BACK_ON_TOWER.sentence);
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

		SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);

		player.teleport(player.getWorld().getSpawnLocation());
		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_BACK_ON_TOWER.sentence);
		return true;
	}

	/**
	 * Remove a island.
	 * 
	 * @param player
	 * @param number
	 * @return
	 */
	private boolean removeIsland(Player player, String number) {
		if (!Settings.skyBlockOnline) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_OFFLINE.sentence);
			return true;
		}

		if (!Permissions.SKYBLOCK_REMOVE_ISLAND.has(player)) {
			return this.notAuthorized(player);
		}

		int islandNumber = 0;
		try {
			islandNumber = Integer.parseInt(number);
		} catch (Exception e) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_INVALID_ISLAND_NUMBER.sentence);
			return true;
		}

		if (islandNumber <= 0 || islandNumber > CreateNewIsland.getAmountOfIslands()) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_INVALID_ISLAND_NUMBER.sentence);
			return true;
		}

		PlayerInfo pi = SkyBlockMultiplayer.getOwner(CreateNewIsland.getIslandPosition(islandNumber));
		if (pi != null) {
			pi.setDead(false);
			pi.setHasIsland(false);
			pi.setIslandArmor(null);
			pi.setIslandInventory(null);
			pi.setIslandExp(0);
			pi.setIslandLevel(0);
			pi.setIslandHealth(player.getMaxHealth());
			pi.setIslandFood(20);
			pi.setIslandLocation(null);
			pi.setHomeLocation(null);
			SkyBlockMultiplayer.getInstance().writePlayerFile(pi.getPlayerName(), pi);
		}

		SkyBlockMultiplayer.getInstance().removeIsland(CreateNewIsland.getIslandPosition(islandNumber));
		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "Island removed!");
		return true;
	}

	/**
	 * Teleport player to home location.
	 * 
	 * @param player
	 * @return
	 */
	private boolean homeTeleport(Player player) {
		PlayerInfo pi = Settings.players.get(player.getName());
		if (pi == null) {
			pi = SkyBlockMultiplayer.getInstance().readPlayerFile(player.getName());
			if (pi == null) {
				return true;
			}
			Settings.players.put(player.getName(), pi);
		}

		if (SkyBlockMultiplayer.getInstance().playerIsOnTower(player)) {
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
		}

		if (pi.getHomeLocation() == null) {
			SkyBlockMultiplayer.getInstance().removeCreatures(pi.getIslandLocation());
			player.teleport(pi.getIslandLocation());
		} else {
			Location homeSweetHome = SkyBlockMultiplayer.getInstance().getSafeHomeLocation(pi);
			if (homeSweetHome == null){ // if null, island is missing and home location returns no safe block
				player.sendMessage("Cannot teleport to your home location, your island is probably missing.");
				return true;
			}
			
			SkyBlockMultiplayer.getInstance().removeCreatures(homeSweetHome);
			player.teleport(homeSweetHome);
		}
		return true;
	}

	/**
	 * Set a home location.
	 * 
	 * @param player
	 * @return
	 */
	private boolean homeSet(Player player) {
		if (!player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			return true;
		}

		PlayerInfo pi = Settings.players.get(player.getName());
		if (pi == null) {
			pi = SkyBlockMultiplayer.getInstance().readPlayerFile(player.getName());
			if (pi == null) {
				return true;
			}
			Settings.players.put(player.getName(), pi);
		}

		if (SkyBlockMultiplayer.canPlayerDoThat(pi, player.getLocation())) {
			pi.setHomeLocation(player.getLocation());
			SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_SPAWN_LOCATION_CHANGED.sentence);
			return true;
		} else {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_HOME_CHANGE_ONYL_INOWN_AREA.sentence);
			return true;
		}
	}

	/**
	 * Show a list of all friends.
	 * 
	 * @param player
	 * @return
	 */
	private boolean homeList(Player player) {
		PlayerInfo pi = Settings.players.get(player.getName());
		if (pi == null) {
			pi = SkyBlockMultiplayer.getInstance().readPlayerFile(player.getName());
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

	private boolean homeJoin(Player player, String toPlayer) {
		PlayerInfo pi = Settings.players.get(player.getName());
		if (pi == null) {
			pi = SkyBlockMultiplayer.getInstance().readPlayerFile(player.getName());
			if (pi == null) {
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NO_ISLAND_TELEPORT_IMPOSSIBLE.sentence);
				return true;
			}
			Settings.players.put(player.getName(), pi);
		}

		if (pi.getIslandLocation() == null) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NO_ISLAND_TELEPORT_IMPOSSIBLE.sentence);
			return true;
		}

		String res = SkyBlockMultiplayer.getInstance().getFullPlayerName(toPlayer);
		if (res.equalsIgnoreCase("-1")) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_PLAYER_NAME.sentence);
			return true;
		}
		if (res.equalsIgnoreCase("0")) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_BETTER_SPECIFY.sentence);
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
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NOT_FRIEND_FROM_YOU.sentence);
			return true;
		}

		if (SkyBlockMultiplayer.getInstance().playerIsOnTower(player)) {
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
		}

		if (pTarget.getHomeLocation() == null) {
			SkyBlockMultiplayer.getInstance().removeCreatures(pTarget.getIslandLocation());
			player.teleport(pTarget.getIslandLocation());
		} else {
			Location homeSweetHome = SkyBlockMultiplayer.getInstance().getSafeHomeLocation(pTarget);
			if (homeSweetHome == null){ // if null, island is missing and home location returns no safe block
				player.sendMessage("Cannot teleport to the friend home location, his island is probably missing.");
				return true;
			}

			SkyBlockMultiplayer.getInstance().removeCreatures(homeSweetHome);
			player.teleport(homeSweetHome);

		}
		return true;
	}

	/**
	 * Add a player to friend list.
	 * 
	 * @param player
	 * @return
	 */
	private boolean homeAdd(Player player, String playerToAdd) {
		String res = SkyBlockMultiplayer.getInstance().getFullPlayerName(playerToAdd);
		if (res.equalsIgnoreCase("-1")) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_PLAYER_NAME.sentence);
			return true;
		}
		if (res.equalsIgnoreCase("0")) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_BETTER_SPECIFY.sentence);
			return true;
		}

		if (res.equalsIgnoreCase(player.getName())) {
			return true;
		}

		Player toAdd = SkyBlockMultiplayer.getInstance().getServer().getPlayer(res);
		if (toAdd != null) {
			toAdd.sendMessage(player.getName() + Language.MSGS_SOMEONE_ADDED_YOU.sentence);
		}

		PlayerInfo pi = Settings.players.get(player.getName());
		if (pi == null) {
			pi = SkyBlockMultiplayer.getInstance().readPlayerFile(player.getName());
			if (pi == null) {
				return true;
			}
		}

		pi.addFriend(res);

		SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);

		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_FRIEND_ADDED.sentence);
		return true;
	}

	/**
	 * Remove a player from friend list.
	 * 
	 * @param player
	 * @param playerToRemove
	 * @return
	 */
	private boolean homeRemove(Player player, String playerToRemove) {
		String res = SkyBlockMultiplayer.getInstance().getFullPlayerName(playerToRemove);
		if (res.equalsIgnoreCase("-1")) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_PLAYER_NAME.sentence);
			return true;
		}
		if (res.equalsIgnoreCase("0")) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_BETTER_SPECIFY.sentence);
			return true;
		}

		PlayerInfo pi = Settings.players.get(player.getName());
		if (pi == null) {
			pi = SkyBlockMultiplayer.getInstance().readPlayerFile(player.getName());
			if (pi == null) {
				return true;
			}
		}

		pi.removeFriend(res);
		SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);

		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_FRIEND_REMOVED.sentence);
		return true;
	}
}
