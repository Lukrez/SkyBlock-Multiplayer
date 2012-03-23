package me.lukas.skyblockmultiplayer;

public enum Config {

	OPTIONS_ISLANDDISTANCE("options.islandDistance", 50),
	OPTIONS_CHESTITEMS("options.chestItems", "79:2 6:5 360:3 81:1 327:1 86:1"),
	OPTIONS_SKYBLOCKONLINE("options.skyblockOnline", true),
	OPTIONS_ALLOWCONTENT("options.allowContent", false),
	OPTIONS_LANGUAGE("options.language", "english"),
	OPTIONS_GAMEMODE("options.gameMode", "build"),
	OPTIONS_WORLDNAME("options.worldName", Settings.worldName),
	OPTIONS_SPAWNTOWERRECREATE("options.spawnTowerReCreate", false),
	OPTIONS_CLOSED("options.closed", false),
	OPTIONS_PVP("options.pvp", ""),
	OPTIONS_BUILD_RESPAWNWITHINVENTORY("options.build.respawnWithInventory", true),
	OPTIONS_BUILD_WITHPROTECTEDAREA("options.build.withProtectedArea", true),
	OPTIONS_BUILD_ALLOWENDERPEARL("options.build.allowEnderPearl", false),
	OPTIONS_BUILD_FRIENDSOVERBORDERS("options.build.friendsOverBorders", true);

	String path;
	Object value;

	private Config(String path, Object value) {
		this.path = path;
	}
}
