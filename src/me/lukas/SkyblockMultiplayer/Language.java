package me.lukas.skyblockmultiplayer;

public enum Language {

	MSGS_SKYBLOCK("msgs.skyblock", "Use \"/skyblock help\" for more informations."),
	MSGS_STOPPING("msgs.stopping", "Stopping skyblock..."),
	MSGS_PLAYERSINSB("msgs.playersInSb", "There are players in Skyblock. Skyblock can not be disabled!"),
	MSGS_ISNOWOFFLINE("msgs.isNowOffline", "Skyblock is now offline."),
	MSGS_ISOFFLINE("msgs.isOffline", "Skyblock is offline."),
	MSGS_ERROROCCURED("msgs.errorOccured", "An error occurred."),
	MSGS_STARTING("msgs.starting", "Starting Skyblock..."),
	MSGS_ISNOWONLINE("msgs.isNowOnline", "Skyblock is now online."),
	MSGS_MUSTBEOFFLINE("msgs.mustBeOffline", "Skyblock must be offline before you can reset it."),
	MSGS_RESETING("msgs.reseting", "Reseting Skyblock..."),
	MSGS_ISNOWRESETED("msgs.isNowRested", "Skyblock has been reseted."),
	MSGS_CONFIGRELOADED("msgs.configReloaded", "Config has been reloaded."),
	MSGS_LANGUAGERELOADED("msgs.languageReloaded", "Language has been reloaded."),
	MSGS_LANGUAGECHANGED("msgs.languageChanged", "Language has been changed."),
	MSGS_LANGUAGENOTCHANGED("msgs.languageNotChanged", "Language has not been changed."),
	MSGS_LANGUAGEFILENOTEXISTS("msgs.languageFileNotExists", "Language file does not exists."),
	MSGS_notAuthorized("msgs.notAuthorized", "You are not authorized!"),
	MSGS_STATUSOFFLINE("msgs.statusOffline", "Is Offline"),
	MSGS_STATUSONLINE("msgs.statusOnline", "Is Online."),
	MSGS_NUMBEROFISLANDS("msgs.numberOfIslands", "Number of islands: "),
	MSGS_NUMBEROFPLAYERS("msgs.numberOfPlayers", "Number of players: "),
	/**commands list**/
	MSGS_CMDJOIN("msgs.commands.join", "/skyblock join - join Skyblock"),
	MSGS_CMDSTART("msgs.commands.start", "/skyblock start - get an island"),
	MSGS_CMDLEAVE("msgs.commands.leave", "/skyblock leave - leave Skyblock"),
	MSGS_CMDNEWISLAND("msgs.commands.newIsland", "/skyblock newIsland [player] - give yourself or an other player a new island"),
	MSGS_CMDSETOFFLINE("msgs.commands.setOffline", "/skyblock set offline - deactivate Skyblock"),
	MSGS_CMDSETONLINE("msgs.commands.setOnline", "/skyblock set online - activate Skyblock"),
	MSGS_CMDSETLANGUAGE("msgs.commands.setLanguage", "/skyblock set language <language> - change language"),
	MSGS_CMDRESET("msgs.commands.reset", "/skyblock reset - reset Skyblock"),
	MSGS_CMDRELOADCONFIG("msgs.commands.reloadConfig", "/skyblock reload config - reload config"),
	MSGS_CMDRELOADLANGUAGE("msgs.commands.reloadLanguage", "/skyblock reload language - reload language"),
	MSGS_CMDSTATUS("msgs.commands.status", "/skyblock status - show status"),
	/**end of commands**/
	MSGS_WRONGARGS("msgs.wrongArgs", "Incorrect or missing arguments"),
	MSGS_WELCOME1("msgs.welcome1", "Welcome to the world Skyblock for multiplayer! At the moment there are "),
	MSGS_WELCOME2("msgs.welcome2", " islands and  "),
	MSGS_WELCOME3("msgs.welcome3", " players. Use \"/skyblock start\" to get an own island."),
	MSGS_HADAISLAND("msgs.hadAIsland", "You already had an island."),
	MSGS_WELCOMEBACK("msgs.welcomeBack", "Welcome back "),
	MSGS_WELCOMEBROADCAST1("msgs.welcomeBroadcast1", "The player "),
	MSGS_WELCOMEBROADCAST2("msgs.welcomeBroadcast2", " plays with."),
	MSGS_TONEWPLAYER("msgs.toNewPlayer", "Do not fall and make no obsidian :-)."),
	MSGS_showIslandNumber("msgs.showIslandNumber", "You are on island number "),
	MSGS_LEFTSKYBLOCK("msgs.leftSkyblock", "You left Skyblock."),
	MSGS_NOEMPTYINVENTORYLEAVE("msgs.noEmptyInventoryLeave", "You cannot leave skyblock with content in inventory."),
	MSGS_NOEMPTYINVENTORYSTART("msgs.noEmptyInventoryStart", "You can not play with content in your inventory."),
	MSGS_WRONEPLAYERNAME("msgs.wrongPlayerName", "There is no player with that name. Or the player is not in Skyblock."),
	MSGS_NEWISLANDPLAYER1("msgs.newIslandPlayer1", "The player "),
	MSGS_NEWISLANDPLAYER2("msgs.newIslandPlayer2", " has a new island."),
	MSGS_GOTNEWISLAND1("msgs.gotNewIsland1", "The player "),
	MSGS_GOTNEWISLAND2("msgs.gotNewIsland2", " has given you a new island."),
	MSGS_SIGN1LINE1("msgs.Sign1Line1", "Welcome to"),
	MSGS_SIGN1LINE2("msgs.Sign1Line2", "Skyblock-"),
	MSGS_SIGN1LINE3("msgs.Sign1Line3", "Multiplayer"),
	MSGS_SIGN2LINE1("msgs.Sign2Line1", "Fore more"),
	MSGS_SIGN2LINE2("msgs.Sign2Line2", "informations"),
	MSGS_SIGN2LINE3("msgs.Sign2Line3", "/skyblock help"),
	MSGS_SIGN2LINE4("msgs.Sign2Line4", "Good luck!"),
	MSGS_PLAYERDIED1("msgs.playerDied1", "Now another "),
	MSGS_PLAYERDIED2("msgs.playerDied2", " player left."),
	MSGS_PLAYERWINBROADCAST1("msgs.playerWinBroadcast1", "The player "),
	MSGS_PLAYERWINBROADCAST2("msgs.playerWinBroadcast2", " has win the game. Congratulations");

	String path;
	String sentence;

	private Language(String path, String sentence) {
		this.path = path;
		this.sentence = sentence;
	}
}
