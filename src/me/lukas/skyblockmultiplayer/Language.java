package me.lukas.skyblockmultiplayer;

import org.bukkit.ChatColor;

public enum Language {

	MSGS_SKYBLOCK("msgs.skyblock", "Use \"/skyblock help\" for more information."),
	MSGS_STOPPING("msgs.stopping", "Stopping SkyBlock..."),
	MSGS_PLAYERSINSB("msgs.playersInSb", "There are players in SkyBlock. SkyBlock can not be disabled!"),
	MSGS_ISNOWOFFLINE("msgs.isNowOffline", "SkyBlock is now offline."),
	MSGS_ISOFFLINE("msgs.isOffline", "SkyBlock is offline."),
	MSGS_ERROROCCURED("msgs.errorOccured", "An error occurred."),
	MSGS_STARTING("msgs.starting", "Starting SkyBlock..."),
	MSGS_ISNOWONLINE("msgs.isNowOnline", "SkyBlock is now online."),
	MSGS_MUSTBEOFFLINE("msgs.mustBeOffline", "SkyBlock must be offline before you can reset it."),
	MSGS_RESETING("msgs.reseting", "Reseting SkyBlock..."),
	MSGS_ISNOWRESETED("msgs.isNowReseted", "SkyBlock has been reseted."),
	MSGS_CONFIGRELOADED("msgs.configReloaded", "Config has been reloaded."),
	MSGS_LANGUAGERELOADED("msgs.languageReloaded", "Language has been reloaded."),
	MSGS_LANGUAGECHANGED("msgs.languageChanged", "Language has been changed."),
	MSGS_LANGUAGENOTCHANGED("msgs.languageNotChanged", "Language has not been changed."),
	MSGS_LANGUAGEFILENOTEXISTS("msgs.languageFileNotExists", "Language file does not exists."),
	MSGS_NOTAUTHORIZED("msgs.notAuthorized", "You are not authorized!"),
	MSGS_STATUSOFFLINE("msgs.statusOffline", "Is Offline"),
	MSGS_STATUSONLINE("msgs.statusOnline", "Is Online."),
	MSGS_NUMBEROFISLANDS("msgs.numberOfIslands", "Number of islands: "),
	MSGS_NUMBEROFPLAYERS("msgs.numberOfPlayers", "Number of players: "),
	/* commands list */
	MSGS_CMDJOIN("msgs.commands.join", "§6/skyblock join§7 - join SkyBlock"),
	MSGS_CMDSTART("msgs.commands.start", "§6/skyblock start§7 - get an island"),
	MSGS_CMDLEAVE("msgs.commands.leave", "§6/skyblock leave§7 - leave SkyBlock"),
	MSGS_CMDTOWER("msgs.commands.tower", "§6/skyblock tower§7 - teleport back to spawn tower"),
	MSGS_CMDNEWISLAND("msgs.commands.newIsland", "§6/skyblock newIsland [player]§7 - give yourself or an other player a new island"),
	MSGS_CMDSETOFFLINE("msgs.commands.setOffline", "§6/skyblock set offline§7 - deactivate SkyBlock"),
	MSGS_CMDSETONLINE("msgs.commands.setOnline", "§6/skyblock set online§7 - activate SkyBlock"),
	MSGS_CMDTOWERRECREATE("msgs.commands.towerReCreate", "§6/skyblock tower recreate§7 - recreates the tower"),
	MSGS_CMDSETLANGUAGE("msgs.commands.setLanguage", "§6/skyblock set language <language>§7 - change language"),
	MSGS_CMDSETGAMEMODE("msgs.commands.setGameMode", "§6/skyblock set gamemode <option>§7 - <build> or <pvp>"),
	MSGS_CMDSETCLOSED("msgs.commands.setClosed", "§6/skyblock set closed§7 - close SkyBlock to stop players to join"),
	MSGS_CMDSETOPENED("msgs.commands.setOpened", "§6/skyblock set opened§7 - open SkyBlock to allow players to join"),
	MSGS_CMDRESET("msgs.commands.reset", "§6/skyblock reset§7 - reset SkyBlock"),
	MSGS_CMDRELOADCONFIG("msgs.commands.reloadConfig", "§6/skyblock reload config§7 - reload config"),
	MSGS_CMDRELOADLANGUAGE("msgs.commands.reloadLanguage", "§6/skyblock reload language§7 - reload language"),
	MSGS_CMDSTATUS("msgs.commands.status", "§6/skyblock status§7 - show status"),
	MSGS_CMDHOME("msgs.commands.home", "§6/skyblock home§7 - teleport back to your island"),
	MSGS_CMDHOMEADD("msgs.commands.homeAdd", "§6/skyblock home add <player>§7 - add a player to your friendlist"),
	MSGS_CMDHOMEREMOVE("msgs.commands.homeRemove", "§6/skyblock home remove <player>§7 - remove a player from your friendlist"),
	MSGS_CMDHOMEJOIN("msgs.commands.homeJoin", "§6/skyblock home join <player>§7 - teleport to a friends island"),
	MSGS_CMDHOMELIST("msgs.commands.homeList", "§6/skyblock home list§7 - show all friends from your list"),
	MSGS_CMDHOMESET("msgs.commands.homeSet", "§6/skyblock home set§7 - change your spawn location"),
	/* end of command list */
	MSGS_WRONGARGS("msgs.wrongArgs", "Incorrect or missing arguments"),
	MSGS_WELCOME1("msgs.welcome1", "Welcome to the world SkyBlock for multiplayer! At the moment there are "),
	MSGS_WELCOME2("msgs.welcome2", " islands and  "),
	MSGS_WELCOME3("msgs.welcome3", " players. Use \"/skyblock start\" to get an own island."),
	MSGS_WELCOMEBACK("msgs.welcomeBack", "Welcome back "),
	MSGS_WELCOMEBROADCAST1("msgs.welcomeBroadcast1", "The player "),
	MSGS_WELCOMEBROADCAST2("msgs.welcomeBroadcast2", " has joined the SkyBlock game."),
	MSGS_TONEWPLAYER("msgs.toNewPlayer", "Do not fall and make no obsidian :-)."),
	MSGS_showIslandNumber("msgs.showIslandNumber", "You are on island number "),
	MSGS_LEFTSKYBLOCK("msgs.leftSkyblock", "You left SkyBlock."),
	MSGS_WRONGEPLAYERNAME("msgs.wrongPlayerName", "There is no player with that name."),
	MSGS_NEWISLANDPLAYER1("msgs.newIslandPlayer1", "The player "),
	MSGS_NEWISLANDPLAYER2("msgs.newIslandPlayer2", " has get a new island."),
	MSGS_GOTNEWISLAND1("msgs.gotNewIsland1", "The player "),
	MSGS_GOTNEWISLAND2("msgs.gotNewIsland2", " has given you a new island."),
	MSGS_SIGN1LINE1("msgs.sign1Line1", "Welcome to"),
	MSGS_SIGN1LINE2("msgs.sign1Line2", "SkyBlock-"),
	MSGS_SIGN1LINE3("msgs.sign1Line3", "Multiplayer"),
	MSGS_SIGN2LINE1("msgs.sign2Line1", "For more"),
	MSGS_SIGN2LINE2("msgs.sign2Line2", "informations"),
	MSGS_SIGN2LINE3("msgs.sign2Line3", "/skyblock help"),
	MSGS_SIGN2LINE4("msgs.sign2Line4", "Good luck!"),
	MSGS_PLAYERDIED1("msgs.playerDied1", "One player died. There are "),
	MSGS_PLAYERDIED2("msgs.playerDied2", " players remaining."),
	MSGS_PLAYERWINBROADCAST1("msgs.playerWinBroadcast1", "The player "),
	MSGS_PLAYERWINBROADCAST2("msgs.playerWinBroadcast2", " won the game. Congratulations!"),
	MSGS_BETTERSPECIFY("msgs.betterSpecify", "There are more players that begin with this name."),
	MSGS_GAMEMODECHANGED("msgs.gameModeChanged", "Game mode has been changed."),
	MSGS_FRIENDREMOVED("msgs.friendRemoved", "Friend removed"),
	MSGS_FRIENDADDED("msgs.friendAdded", "Friend added"),
	MSGS_NOTFRIENDFROMYOU("msgs.notFriendFromYou", "Not possible, you are not in the players home list!"),
	MSGS_SOMEONEADDEDYOU("msgs.playerAddedYou", " added you to his friend list."),
	MSGS_ISNOWCLOSED("msgs.isNowClosed", "SkyBlock is now closed."),
	MSGS_ISNOWOPENED("msgs.isNowOpened", "SkyBlock is now open."),
	MSGS_ISCLOSED("msgs.isClosed", "SkyBlock is closed."),
	MSGS_AREABORDERS("msgs.areaBorders", "Protected area or borders."),
	MSGS_ONLYINBUILDMODE("msgs.onlyInBuildMode", "This works only in build mode!"),
	MSGS_BACKONTOWER("msgs.backOnTower", "You are now back on the spawn tower."),
	MSGS_SPAWNTOWERRECREATED("msgs.spawnTowerReCreated", "The spawn tower has been recreated."),
	MSGS_NOMORELIVESANDISLANDS("msgs.noMoreLivesAndIslands", "GAME OVER!!!. No more lives and islands left!"),
	MSGS_INVALIDISLANDNUMBER("msgs.invalidIslandNumber", "Invalid island number!"),
	MSGS_CHANGEDOWNERTO("msgs.changedOwnerTo", "Owner of the island is now "),
	MSGS_SPAWNLOCATIONCHANGED("msgs.spawnLocationChanged", "Your spawn point is changed."),
	MSGS_HOMECHANGEONYLINOWNAREA("msgs.changeOfHomeLocationOnlyInOwnArea", "You can change your spawn point only in your region."),
	MSGS_NOISLANDTELEPORIMPOSSIBLE("msgs.noIslandTeleportImpossible", "You have no island, you can not teleport to your friend."),
	MSGS_AREAOFSPAWNTOWER("msgs.areaOfSpawnTower", "Area of spawn tower.");

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
