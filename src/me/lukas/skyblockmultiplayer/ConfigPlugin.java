package me.lukas.skyblockmultiplayer;

public enum ConfigPlugin {

	OPTIONS_ISLANDDISTANCE("options.islandDistance", 50),
	OPTIONS_CHESTITEMS("options.chestItems", "79:2 6:5 360:3 81:1 327:1 86:1"),
	OPTIONS_SKYBLOCKONLINE("options.skyblockOnline", true),
	OPTIONS_ALLOWCONTENT("options.allowContent", false),
	OPTIONS_LANGUAGE("options.language", "english"),
	OPTIONS_GAMEMODE("options.gameMode", "build"),
	OPTIONS_WORLDNAME("options.worldName", Settings.worldName),
	OPTIONS_CLOSED("options.closed", false),
	OPTIONS_MESSAGES_OUTSIDE("options.messagesOutside", false),
	OPTIONS_PVP("options.pvp", ""),
	OPTIONS_PVP_LIVESPERISLAND("options.pvp.livesPerIsland", 1),
	OPTIONS_PVP_ISLANDSPERPLAYER("options.pvp.islandsPerPlayer", 1),
	OPTIONS_BUILD_RESPAWNWITHINVENTORY("options.build.respawnWithInventory", true),
	OPTIONS_BUILD_WITHPROTECTEDAREA("options.build.withProtectedArea", true),
	OPTIONS_BUILD_ALLOWENDERPEARL("options.build.allowEnderPearl", false),
	OPTIONS_BUILD_WITHPROTECTEDBORDER("options.build.withProtectedBorder", true),
	OPTIONS_SCHEMATIC_ISLAND_FILENAME("options.schematic.island.fileName", ""),
	OPTIONS_SCHEMATIC_TOWER_FILENAME("options.schematic.tower.fileName", ""),
	OPTIONS_SCHEMATIC_TOWER_HEIGHT("options.schematic.tower.height", 80);

	String path;
	Object value;

	private ConfigPlugin(String path, Object value) {
		this.path = path;
		this.value = value;
	}
}
