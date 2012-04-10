package me.lukas.skyblockmultiplayer;

import org.bukkit.ChatColor;

public enum Language {

	MSGS_SKYBLOCK("msgs.skyblock", "Use \"/skyblock help\" for more information."),
	MSGS_STOPPING("msgs.stopping", "Stopping SkyBlock..."),
	MSGS_PLAYERS_IN_SB("msgs.playersInSb", "There are players in SkyBlock. SkyBlock can not be disabled!"),
	MSGS_IS_NOW_OFFLINE("msgs.isNowOffline", "SkyBlock is now offline."),
	MSGS_IS_OFFLINE("msgs.isOffline", "SkyBlock is offline."),
	MSGS_ERROR_OCCURED("msgs.errorOccured", "An error occurred."),
	MSGS_STARTING("msgs.starting", "Starting SkyBlock..."),
	MSGS_IS_NOW_ONLINE("msgs.isNowOnline", "SkyBlock is now online."),
	MSGS_MUST_BEOFFLINE("msgs.mustBeOffline", "SkyBlock must be offline before you can reset it."),
	MSGS_RESETING("msgs.reseting", "Reseting SkyBlock..."),
	MSGS_IS_NOW_RESETED("msgs.isNowReseted", "SkyBlock has been reseted."),
	MSGS_CONFIG_RELOADED("msgs.configReloaded", "Config has been reloaded."),
	MSGS_LANGUAGE_RELOADED("msgs.languageReloaded", "Language has been reloaded."),
	MSGS_LANGUAGE_CHANGED("msgs.languageChanged", "Language has been changed."),
	MSGS_LANGUAGE_NOT_CHANGED("msgs.languageNotChanged", "Language has not been changed."),
	MSGS_LANGUAGE_FILE_NOT_EXISTS("msgs.languageFileNotExists", "Language file does not exists."),
	MSGS_NOT_AUTHORIZED("msgs.notAuthorized", "You are not authorized!"),
	MSGS_STATUS_OFFLINE("msgs.statusOffline", "Is Offline"),
	MSGS_STATUS_ONLINE("msgs.statusOnline", "Is Online."),
	MSGS_NUMBER_OF_ISLANDS("msgs.numberOfIslands", "Number of islands: "),
	MSGS_NUMBER_OF_PLAYERS("msgs.numberOfPlayers", "Number of players: "),
	/* commands list */
	MSGS_COMMAND_JOIN("msgs.commands.join", "§6/skyblock join§7 - join SkyBlock"),
	MSGS_COMMAND_START("msgs.commands.start", "§6/skyblock start§7 - get an island"),
	MSGS_COMMAND_LEAVE("msgs.commands.leave", "§6/skyblock leave§7 - leave SkyBlock"),
	MSGS_COMMAND_TOWER("msgs.commands.tower", "§6/skyblock tower§7 - teleport back to spawn tower"),
	MSGS_COMMAND_NEW_ISLAND("msgs.commands.newIsland", "§6/skyblock newIsland §a[player]§7 - give yourself or an other player a new island"),
	MSGS_COMMAND_SET_OFFLINE("msgs.commands.setOffline", "§6/skyblock set offline§7 - deactivate SkyBlock"),
	MSGS_COMMAND_SET_ONLINE("msgs.commands.setOnline", "§6/skyblock set online§7 - activate SkyBlock"),
	MSGS_COMMAND_TOWER_RECREATE("msgs.commands.towerReCreate", "§6/skyblock tower recreate§7 - recreates the tower"),
	MSGS_COMMAND_SET_LANGUAGE("msgs.commands.setLanguage", "§6/skyblock set language §c<language>§7 - change language"),
	MSGS_COMMAND_SET_GAMEMODE("msgs.commands.setGameMode", "§6/skyblock set gamemode §c<option>§7 - <build> or <pvp>"),
	MSGS_COMMAND_SET_CLOSED("msgs.commands.setClosed", "§6/skyblock set closed§7 - close SkyBlock to stop players to join"),
	MSGS_COMMAND_SET_OPENED("msgs.commands.setOpened", "§6/skyblock set opened§7 - open SkyBlock to allow players to join"),
	MSGS_COMMAND_SET_OWNER("msgs.commands.setOwner", "§6/skyblock setOwner §c<island number> <player>§7 - change the owner of a island"),
	MSGS_COMMAND_RESET("msgs.commands.reset", "§6/skyblock reset§7 - reset SkyBlock"),
	MSGS_COMMAND_RELOAD_CONFIG("msgs.commands.reloadConfig", "§6/skyblock reload config§7 - reload config"),
	MSGS_COMMAND_RELOAD_LANGUAGE("msgs.commands.reloadLanguage", "§6/skyblock reload language§7 - reload language"),
	MSGS_COMMAND_STATUS("msgs.commands.status", "§6/skyblock status§7 - show status"),
	MSGS_COMMAND_HOME("msgs.commands.home", "§6/skyblock home§7 - teleport back to your island"),
	MSGS_COMMAND_HOME_ADD("msgs.commands.homeAdd", "§6/skyblock home add §c<player>§7 - add a player to your friendlist"),
	MSGS_COMMAND_HOME_REMOVE("msgs.commands.homeRemove", "§6/skyblock home remove §c<player>§7 - remove a player from your friendlist"),
	MSGS_COMMAND_HOME_JOIN("msgs.commands.homeJoin", "§6/skyblock home join §c<player>§7 - teleport to a friends island"),
	MSGS_COMMAND_HOME_LIST("msgs.commands.homeList", "§6/skyblock home list§7 - show all friends from your list"),
	MSGS_COMMAND_HOME_SET("msgs.commands.homeSet", "§6/skyblock home set§7 - change your spawn location"),
	/* end of command list */
	MSGS_WRONG_ARGS("msgs.wrongArgs", "Incorrect or missing arguments"),
	MSGS_WELCOME1("msgs.welcome1", "Welcome to the world SkyBlock for multiplayer! At the moment there are "),
	MSGS_WELCOME2("msgs.welcome2", " islands and  "),
	MSGS_WELCOME3("msgs.welcome3", " players. Use \"/skyblock start\" to get an own island."),
	MSGS_WELCOME_BACK("msgs.welcomeBack", "Welcome back "),
	MSGS_WELCOME_BROADCAST1("msgs.welcomeBroadcast1", "The player "),
	MSGS_WELCOME_BROADCAST2("msgs.welcomeBroadcast2", " has joined the SkyBlock game."),
	MSGS_TO_NEW_PLAYER("msgs.toNewPlayer", "Do not fall and make no obsidian :-)."),
	MSGS_SHOW_ISLAND_NUMBER("msgs.showIslandNumber", "You are on island number "),
	MSGS_LEFT_SKYBLOCK("msgs.leftSkyblock", "You left SkyBlock."),
	MSGS_WRONGE_PLAYER_NAME("msgs.wrongPlayerName", "There is no player with that name."),
	MSGS_NEW_ISLAND_PLAYER1("msgs.newIslandPlayer1", "The player "),
	MSGS_NEW_ISLAND_PLAYER2("msgs.newIslandPlayer2", " has get a new island."),
	MSGS_GOT_NEW_ISLAND1("msgs.gotNewIsland1", "The player "),
	MSGS_GOT_NEW_ISLAND2("msgs.gotNewIsland2", " has given you a new island."),
	MSGS_SIGN1LINE1("msgs.sign1Line1", "Welcome to"),
	MSGS_SIGN1LINE2("msgs.sign1Line2", "SkyBlock-"),
	MSGS_SIGN1LINE3("msgs.sign1Line3", "Multiplayer"),
	MSGS_SIGN2LINE1("msgs.sign2Line1", "For more"),
	MSGS_SIGN2LINE2("msgs.sign2Line2", "information use"),
	MSGS_SIGN2LINE3("msgs.sign2Line3", "/skyblock help"),
	MSGS_SIGN2LINE4("msgs.sign2Line4", "Good luck!"),
	MSGS_PLAYER_DIED1("msgs.playerDied1", "One player died. There are "),
	MSGS_PLAYER_DIED2("msgs.playerDied2", " players remaining."),
	MSGS_PLAYER_WIN_BROADCAST1("msgs.playerWinBroadcast1", "The player "),
	MSGS_PLAYER_WIN_BROADCAST2("msgs.playerWinBroadcast2", " won the game. Congratulations!"),
	MSGS_BETTER_SPECIFY("msgs.betterSpecify", "There are more players that begin with this name."),
	MSGS_GAMEMODE_CHANGED("msgs.gameModeChanged", "Game mode has been changed."),
	MSGS_FRIEND_REMOVED("msgs.friendRemoved", "Friend removed"),
	MSGS_FRIEND_ADDED("msgs.friendAdded", "Friend added"),
	MSGS_NOT_FRIEND_FROM_YOU("msgs.notFriendFromYou", "Not possible, you are not in the players home list!"),
	MSGS_SOMEONE_ADDED_YOU("msgs.playerAddedYou", " added you to his friend list."),
	MSGS_IS_NOW_CLOSED("msgs.isNowClosed", "SkyBlock is now closed."),
	MSGS_IS_NOW_OPENED("msgs.isNowOpened", "SkyBlock is now open."),
	MSGS_IS_CLOSED("msgs.isClosed", "SkyBlock is closed."),
	MSGS_AREA_BORDERS("msgs.areaBorders", "Protected area or borders."),
	MSGS_ONLY_INBUILD_MODE("msgs.onlyInBuildMode", "This works only in build mode!"),
	MSGS_BACK_ON_TOWER("msgs.backOnTower", "You are now back on the spawn tower."),
	MSGS_SPAWN_TOWER_RECREATED("msgs.spawnTowerReCreated", "The spawn tower has been recreated."),
	MSGS_NO_MORELIVES_AND_ISLANDS("msgs.noMoreLivesAndIslands", "GAME OVER!!!. No more lives and islands left!"),
	MSGS_INVALID_ISLAND_NUMBER("msgs.invalidIslandNumber", "Invalid island number!"),
	MSGS_CHANGED_OWNER_TO("msgs.changedOwnerTo", "Owner of the island is now "),
	MSGS_SPAWN_LOCATION_CHANGED("msgs.spawnLocationChanged", "Your spawn point is changed."),
	MSGS_HOME_CHANGE_ONYL_INOWN_AREA("msgs.changeOfHomeLocationOnlyInOwnArea", "You can change your spawn point only in your region."),
	MSGS_NO_ISLAND_TELEPORT_IMPOSSIBLE("msgs.noIslandTeleportImpossible", "You have no island, you can not teleport to your friend."),
	MSGS_AREA_OFSPAWN_TOWER("msgs.areaOfSpawnTower", "Area of spawn tower."),
	MSGS_ONLY_ON_TOWER("msgs.onlyOnTower", "This command works only on the tower."),
	MSGS_ONLY_OUTSIDE_OF_SB("msgs.onlyOutsideOfSb", "This command works only outside of SkyBlock."),
	MSGS_ONLY_INSIDE_OF_SB("msgs.onlyInsideOfSb", "This command works only inside of SkyBlock.");

	public String path;
	public String sentence;

	private Language(String path, String sentence) {
		this.path = path;
		this.sentence = this.replaceColor(sentence);
	}

	private String replaceColor(String s) {
		for (ChatColor c : ChatColor.values()) {
			s = s.replaceAll("§" + c.getChar(), "" + ChatColor.getByChar(c.getChar()));
		}
		return s;
	}
}
