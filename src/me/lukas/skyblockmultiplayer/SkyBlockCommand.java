package me.lukas.skyblockmultiplayer;

import java.io.File;
import java.io.FileInputStream;
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
					return SkyBlockMultiplayer.getInstance().sendHelpPage(sender, 1);
				} else {
					int page = 1;
					try {
						page = Integer.parseInt(args[1]);
						return SkyBlockMultiplayer.getInstance().sendHelpPage(sender, page);
					} catch (Exception e) {
						return SkyBlockMultiplayer.getInstance().sendHelpPage(sender, page);
					}
				}
			}

			/*// only for testing
			if (args[0].equalsIgnoreCase("create")) {
				new CreateNewIsland(Integer.parseInt(args[1]));
				return true;
			}

			if (args[0].equalsIgnoreCase("amount")) {
				sender.sendMessage("" + CreateNewIsland.getAmountOfIslands());
				return true;
			}*/

			if (args[0].equalsIgnoreCase("tower")) {
				if (args.length == 2) {
					if (args[1].equalsIgnoreCase("recreate")) {
						if (!Permissions.SKYBLOCK_BUILD.has(sender)) {
							return this.notAuthorized(sender);
						}

						Location locTower = new Location(SkyBlockMultiplayer.getSkyBlockWorld(), 0, Settings.towerYHeight, 0);

						File f = new File(Settings.towerFileName);
						if (f.exists() && f.isFile()) {
							try {
								int res = CreateNewIsland.createStructure(locTower, f);
								if (res != 1) {
									SkyBlockMultiplayer.createSpawnTower();
									if (res == 0) {
										SkyBlockMultiplayer.getInstance().log.warning("Tower contains no bedrock.");
									} else {
										SkyBlockMultiplayer.getInstance().log.warning("Tower contains too much bedrock.");
									}
								}
								SkyBlockMultiplayer.getSkyBlockWorld().setSpawnLocation(0, SkyBlockMultiplayer.getSkyBlockWorld().getHighestBlockYAt(0, 0), 0);
								sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_SPAWN_TOWER_RECREATED.sentence);
								return true;
							} catch (Exception e) {
								e.printStackTrace();
								SkyBlockMultiplayer.createSpawnTower();
								SkyBlockMultiplayer.getSkyBlockWorld().setSpawnLocation(0, SkyBlockMultiplayer.getSkyBlockWorld().getHighestBlockYAt(0, 0), 0);
								return true;
							}
						}

						f = new File(SkyBlockMultiplayer.getInstance().getDataFolder(), Settings.towerFileName);
						if (f.exists() && f.isFile()) {
							try {
								int res = CreateNewIsland.createStructure(locTower, f);
								if (res != 1) {
									SkyBlockMultiplayer.createSpawnTower();
									if (res == 0) {
										SkyBlockMultiplayer.getInstance().log.warning("Tower contains no bedrock.");
									} else {
										SkyBlockMultiplayer.getInstance().log.warning("Tower contains too much bedrock.");
									}
								}
								SkyBlockMultiplayer.getSkyBlockWorld().setSpawnLocation(0, SkyBlockMultiplayer.getSkyBlockWorld().getHighestBlockYAt(0, 0), 0);
								sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_SPAWN_TOWER_RECREATED.sentence);
								return true;
							} catch (Exception e) {
								e.printStackTrace();
								SkyBlockMultiplayer.createSpawnTower();
								SkyBlockMultiplayer.getSkyBlockWorld().setSpawnLocation(0, SkyBlockMultiplayer.getSkyBlockWorld().getHighestBlockYAt(0, 0), 0);
								return true;
							}
						}

						SkyBlockMultiplayer.createSpawnTower();
						SkyBlockMultiplayer.getSkyBlockWorld().setSpawnLocation(0, SkyBlockMultiplayer.getSkyBlockWorld().getHighestBlockYAt(0, 0), 0);
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

		for (PlayerData pdata : Settings.players.values()) {
			pdata.setHasIsland(false);
			pdata.setIslandsLeft(Settings.pvp_islandsPerPlayer);
			pdata.setLivesLeft(Settings.pvp_livesPerIsland);
			pdata.setIslandLocation(null);
			pdata.setHomeLocation(null);
			pdata.setDeathStatus(false);
			pdata.setIslandFood(20);
			pdata.setIslandExp(0);
			pdata.setIslandLevel(0);
			
			SQLInstructions.writeIslandData(pdata);
			SQLInstructions.writePartialPlayerData(pdata);

		}

		// Create Skyblock
		SkyBlockMultiplayer.skyBlockWorld = null;
		SkyBlockMultiplayer.getSkyBlockWorld();
		SkyBlockMultiplayer.createSpawnTower();

		// Reset informations
		Settings.players.clear();
		Settings.players = new HashMap<String, PlayerData>();
		Settings.numbersPlayers = 0;
		//Settings.numberIslands = 0;

		sender.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_IS_NOW_RESETED.sentence);
		return true;
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

		if (player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
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

		int islands = CreateNewIsland.getAmountOfIslands();

		//----------------  neuer Speicher -------------------//
		PlayerData pdata = SQLInstructions.loadOrCreatePlayer(player.getName());
		pdata.setOldWorldValues(player);
		// ---------------------------------------------------//

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

		// Write into SQL:
		PlayerData pdata = SQLInstructions.loadOrCreatePlayer(player.getName());


		if (Settings.gameModeSelected == Settings.GameMode.BUILD) {
			if (!pdata.getHasIsland() || pdata.getIslandLocation() == null) {
				// new player
				CreateNewIsland isl = new CreateNewIsland(player);
				Location islLocation = SkyBlockMultiplayer.getInstance().getYLocation(isl.Islandlocation);

				// SQL
				pdata.setIslandLocation(islLocation);
				pdata.setHomeLocation(null);
				pdata.setHasIsland(true);
				pdata.setDeathStatus(false);
				pdata.updateSQLPartialData();
				
				// put island into SQL
				SQLInstructions.writeNewIsland(pdata, isl);
				
				if (!Settings.allowContent) {
					// clear inventory
					player.getInventory().clear();
					SkyBlockMultiplayer.getInstance().clearArmorContents(player);
					player.setExp(0);
					player.setLevel(0);
					player.setFoodLevel(20);
					player.setHealth(player.getMaxHealth());
				}

				// teleport player
				SkyBlockMultiplayer.getInstance().removeCreatures(pdata.getIslandLocation());
				player.teleport(SkyBlockMultiplayer.getInstance().getSafeHomeLocation(pdata));
				
				Settings.numbersPlayers++;

				// send message to all
				for (PlayerData pInfo : Settings.players.values()) {
					if (pInfo.getPlayer() != null) {
						if (pInfo.getPlayer().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()) || (Permissions.SKYBLOCK_MESSAGES.has(pInfo.getPlayer()) && Settings.messagesOutside)) {
							pInfo.getPlayer().sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME_BROADCAST1.sentence + player.getName() + Language.MSGS_WELCOME_BROADCAST2.sentence);
						}
					}
				}

				Settings.islandsAndOwners.put(player.getName(), pdata.getIslandLocation());
				//SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_TO_NEW_PLAYER.sentence);
				return true;
			}

			// player has a island
			if (!Settings.allowContent) {
				// load from island inventory, exp, level, food and health
				
				player.getInventory().setContents(pdata.getIslandInventory());
				player.getInventory().setArmorContents(pdata.getIslandArmor());
				player.setExp(pdata.getIslandExp());
				player.setLevel(pdata.getIslandLevel());
				player.setFoodLevel(pdata.getIslandFood());
				player.setHealth(pdata.getIslandHealth());

			}

			// teleport player
			if (pdata.getHomeLocation() == null) {
				SkyBlockMultiplayer.getInstance().removeCreatures(pdata.getIslandLocation());
				player.teleport(pdata.getIslandLocation());
			} else {
				Location homeSweetHome = SkyBlockMultiplayer.getInstance().getSafeHomeLocation(pdata);
				if (homeSweetHome == null) { // if null, island is missing and home location returns no safe block
					player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "Cannot teleport to your home location, your island is probably missing.");
					return true;
				}
				SkyBlockMultiplayer.getInstance().removeCreatures(homeSweetHome);
				player.teleport(homeSweetHome);
			}

			//SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME_BACK.sentence + player.getName());
			return true;
		}

		// Game mode is PVP
		if (pdata.getHasIsland()) { // player have a island
			if (pdata.isDead()) {
				if (pdata.getLivesLeft() == 0) {
					if (pdata.getIslandsLeft() == 0) {
						// no more lives and islands left
						player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NO_MORELIVES_AND_ISLANDS.sentence);
						return true;
					}

					// no more lives left, decrement islandsLeft
					pdata.setLivesLeft(Settings.pvp_livesPerIsland);
					pdata.setIslandsLeft(pdata.getIslandsLeft() - 1);
					pdata.updateSQLPartialData();

					if (!Settings.allowContent) {
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
					pdata.setIslandLocation(island.Islandlocation);
					pdata.setDeathStatus(false);
					pdata.setHasIsland(true);

					player.teleport(pdata.getIslandLocation());
					Settings.numbersPlayers++;
					player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "You have " + pdata.getLivesLeft() + " lives on this island and " + pdata.getIslandsLeft() + " islands left.");

					//SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);
					pdata.updateSQLPartialData();
					player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_TO_NEW_PLAYER.sentence);
					return true;
				}

				// lives on island left
				pdata.setDeathStatus(false);
				if (!Settings.allowContent) {
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
				SkyBlockMultiplayer.getInstance().removeCreatures(pdata.getIslandLocation());
				player.teleport(pdata.getIslandLocation());
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "You have " + pdata.getLivesLeft() + " lives on this island and " + pdata.getIslandsLeft() + " islands left.");

				//SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME_BACK.sentence + player.getName());
				return true;
			}

			// Player is not dead and has a island
			if (!Settings.allowContent) {
				player.getInventory().setContents(pdata.getIslandInventory());
				player.getInventory().setArmorContents(pdata.getIslandArmor());
				player.setExp(pdata.getIslandExp());
				player.setLevel(pdata.getIslandLevel());

				// check food of player
				if (pdata.getIslandFood() <= 0) {
					player.setFoodLevel(20);
					pdata.setIslandFood(20);
				} else {
					player.setFoodLevel(pdata.getIslandFood());
				}

				// check hp of player
				if (pdata.getIslandHealth() <= 0) {
					player.setHealth(player.getMaxHealth());
					pdata.setIslandHealth(player.getMaxHealth());
				} else {
					player.setHealth(pdata.getIslandHealth());
				}
			}

			// teleport player
			SkyBlockMultiplayer.getInstance().removeCreatures(pdata.getIslandLocation());
			player.teleport(pdata.getIslandLocation());
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "You have " + pdata.getLivesLeft() + " lives on this island and " + pdata.getIslandsLeft() + " islands left.");

			//SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME_BACK.sentence + player.getName());
			return true;
		}

		// player is new
		CreateNewIsland isl = new CreateNewIsland(player);
		pdata.setIslandLocation(SkyBlockMultiplayer.getInstance().getYLocation(isl.Islandlocation));

		if (!Settings.allowContent) {
			// clear Inventory
			player.getInventory().clear();
			SkyBlockMultiplayer.getInstance().clearArmorContents(player);

			// reset exp, level and food
			player.setExp(0);
			player.setLevel(0);
			player.setFoodLevel(20);
			player.setHealth(player.getMaxHealth());
		}

		pdata.setIslandsLeft(Settings.pvp_islandsPerPlayer-1);
		pdata.setLivesLeft(Settings.pvp_livesPerIsland);
		pdata.setHasIsland(true);
		pdata.setDeathStatus(false);

		// teleport player
		player.teleport(pdata.getIslandLocation());
		Settings.numbersPlayers++;
		//SkyBlockMultiplayer.getInstance().writePlayerFile(player.getName(), pi);
		pdata.updateSQLPartialData();
		Settings.islandsAndOwners.put(player.getName(), pdata.getIslandLocation());

		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_TO_NEW_PLAYER.sentence);
		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "You have " + pdata.getLivesLeft() + " lives on this island and " + pdata.getIslandsLeft() + " islands left.");

		// Message to all
		for (PlayerData pInfo : Settings.players.values()) {
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

		PlayerData pdata = SQLInstructions.loadOrCreatePlayer(player.getName());

		if (pdata.getOldLocation() == null) {
			player.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_LEFT_SKYBLOCK.sentence);
			return true;
		}
		

		if (!SkyBlockMultiplayer.getInstance().playerIsOnTower(player)) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_ON_TOWER.sentence);
			return true;
		}

		Location l = pdata.getOldLocation();
		if (l == null) {
			player.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
		} else {
			player.teleport(l);
		}
				
		if (pdata.getIslandLocation() == null) {
			Settings.players.remove(player.getName());
		}

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
			PlayerData pdata = SQLInstructions.loadOrCreatePlayer(player.getName());
			
			pdata.setHasIsland(false);

			Location l = pdata.getIslandLocation();
			SkyBlockMultiplayer.getInstance().removeIsland(l);

			this.playerStart(player);
			// player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NEWISLANDPLAYER1.sentence + pi.getPlayer().getName() + Language.MSGS_NEWISLANDPLAYER2.sentence);
			return true;
		}

		if (!Permissions.SKYBLOCK_NEWISLAND.has(player)) {
			return this.notAuthorized(player);
		}

		PlayerData pdata = null;
		String res = "";
		if (target.trim().equalsIgnoreCase("")) {
			pdata = SQLInstructions.loadOrCreatePlayer(player.getName());
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
			pdata = Settings.players.get(res);
		}

		if (pdata == null) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WRONG_PLAYER_NAME.sentence);
			return true;
		}

		pdata.setDeathStatus(true);
		pdata.setHasIsland(true);
		pdata.setIslandInventory(null);
		pdata.setIslandArmor(null);
		pdata.setIslandsLeft(pdata.getIslandsLeft() + 1);

		if (Settings.numbersPlayers > 1) {
			Settings.numbersPlayers--;
		}


		pdata.updateSQLPartialData();
		SQLInstructions.writeIslandData(pdata);




		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NEW_ISLAND_PLAYER1.sentence + pdata.getPlayer().getName() + Language.MSGS_NEW_ISLAND_PLAYER2.sentence);
		pdata.getPlayer().sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_GOT_NEW_ISLAND1.sentence + player.getName() + Language.MSGS_GOT_NEW_ISLAND2.sentence);
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

		PlayerData pi = new PlayerData();
		pi.setPlayerName(res);
		if (Settings.players.containsKey(res)) {
			PlayerData oldPi = Settings.players.get(res);
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

		SQLInstructions.writePartialPlayerData(pi);
		SQLInstructions.writeIslandData(pi);
		SQLInstructions.writeOldWorldData(pi);

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

	
		PlayerData pdata = SQLInstructions.loadOrCreatePlayer(player.getName());
	
		if (!Settings.allowContent) {
			pdata.setIslandValues(player);
			
			player.getInventory().setContents(pdata.getOldInventory());
			player.getInventory().setArmorContents(pdata.getOldArmor());
			player.setExp(pdata.getOldExp());
			player.setLevel(pdata.getOldLevel());
			player.setFoodLevel(pdata.getOldFood());
			player.setHealth(pdata.getOldHealth());
		}

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

		PlayerData pdata = SQLInstructions.loadOrCreatePlayer(SkyBlockMultiplayer.getOwner(CreateNewIsland.getIslandPosition(islandNumber)));
		
		pdata.setDeathStatus(false);
		pdata.setHasIsland(false);
		pdata.setIslandArmor(null);
		pdata.setIslandInventory(null);
		pdata.setIslandExp(0);
		pdata.setIslandLevel(0);
		pdata.setIslandHealth(player.getMaxHealth());
		pdata.setIslandFood(20);
		pdata.setIslandLocation(null);
		pdata.setHomeLocation(null);
		SQLInstructions.writePartialPlayerData(pdata);
		SQLInstructions.writeIslandData(pdata);		

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
		if (!player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_INSIDE_OF_SB.sentence);
			return true;
		}

		PlayerData pdata = SQLInstructions.loadOrCreatePlayer(player.getName());
	
		if (SkyBlockMultiplayer.getInstance().playerIsOnTower(player)) {
			// player has a island
			if (!Settings.allowContent) {
				// save before joining inventory, exp, level, food and health
				//pdata.setOldWorldValues(player);
				
				
				// load from island inventory, exp, level, food and health
				player.getInventory().setContents(pdata.getIslandInventory());
				player.getInventory().setArmorContents(pdata.getIslandArmor());
				player.setExp(pdata.getIslandExp());
				player.setLevel(pdata.getIslandLevel());
				player.setFoodLevel(pdata.getIslandFood());
				player.setHealth(pdata.getIslandHealth());
			}
		}

		if (pdata.getHomeLocation() == null) {
			SkyBlockMultiplayer.getInstance().removeCreatures(pdata.getIslandLocation());
			player.teleport(pdata.getIslandLocation());
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME_BACK.sentence + player.getName());
		} else {
			Location homeSweetHome = SkyBlockMultiplayer.getInstance().getSafeHomeLocation(pdata);
			if (homeSweetHome == null) { // if null, island is missing and / or home location returns no safe block
				player.sendMessage(SkyBlockMultiplayer.getInstance().pName + "Cannot teleport to your home location, your island is probably missing.");
				return true;
			}

			SkyBlockMultiplayer.getInstance().removeCreatures(homeSweetHome);
			player.teleport(homeSweetHome);
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_WELCOME_BACK.sentence + player.getName());
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
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_INSIDE_OF_SB.sentence);
			return true;
		}

		PlayerData pdata = SQLInstructions.loadOrCreatePlayer(player.getName());
		if (pdata.checkBuildPermission(player.getLocation())){
			pdata.setHomeLocation(player.getLocation());
			pdata.updateSQLPartialData();
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
		
		PlayerData pdata = SQLInstructions.loadOrCreatePlayer(player.getName());

		String list = "";
		for (int i = 0; i < pdata.getFriends().size(); i++) {
			if (i != 0) {
				list += ", ";
			}
			list += pdata.getFriends().get(i);
		}
		player.sendMessage(list);
		return true;
	}

	private boolean homeJoin(Player player, String toPlayer) {
		if (!player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_INSIDE_OF_SB.sentence);
			return true;
		}

		PlayerData pdata = SQLInstructions.loadOrCreatePlayer(player.getName());
		if (pdata.getIslandLocation() == null || !pdata.getHasIsland()) {
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

		PlayerData pTarget = Settings.players.get(res);
		if (pTarget == null) {
			return true;
		}

		if (!pTarget.getFriends().containsKey(player.getName())) {
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_NOT_FRIEND_FROM_YOU.sentence);
		}

		if (SkyBlockMultiplayer.getInstance().playerIsOnTower(player)) {
			// player has a island
			if (!Settings.allowContent) {
				// save before joining inventory, exp, level, food and health
				//pdata.setOldWorldValues(player);

				// load from island inventory, exp, level, food and health
				player.getInventory().setContents(pdata.getIslandInventory());
				player.getInventory().setArmorContents(pdata.getIslandArmor());
				player.setExp(pdata.getIslandExp());
				player.setLevel(pdata.getIslandLevel());
				player.setFoodLevel(pdata.getIslandFood());
				player.setHealth(pdata.getIslandHealth());
			}
		}

		if (pTarget.getIslandLocation() == null) {
			SkyBlockMultiplayer.getInstance().removeCreatures(pTarget.getIslandLocation());
			player.teleport(pTarget.getIslandLocation());
		} else {
			Location homeSweetHome = SkyBlockMultiplayer.getInstance().getSafeHomeLocation(pTarget);
			if (homeSweetHome == null) { // if null, island is missing and home location returns no safe block
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

		PlayerData pdata = SQLInstructions.loadOrCreatePlayer(player.getName());
		if (!Settings.players.containsKey(res))
			return true; // res does not exist
		PlayerData friend = Settings.players.get(res);

		pdata.addFriendsToOwnIsland(friend);
		friend.addOwnBuildPermission(pdata);
		
		SQLInstructions.writePartialPlayerData(pdata);
		
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

		PlayerData pdata = SQLInstructions.loadOrCreatePlayer(player.getName());
		if (!Settings.players.containsKey(res))
			return true; // res does not exist
		PlayerData friend = Settings.players.get(res);

		pdata.removeFriendFromOwnIsland(friend);
		friend.removeBuildPermissionByFriends(pdata);

		SQLInstructions.writePartialPlayerData(pdata);
		
		player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_FRIEND_REMOVED.sentence);
		return true;
	}
}
