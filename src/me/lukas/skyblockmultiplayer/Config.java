package me.lukas.skyblockmultiplayer;

public enum Config {

	OPTIONS_ISLANDDISTANCE("options.islandDistance"),
	OPTIONS_CHESTITEMS("options.chestItems"),
	OPTIONS_SKYBLOCKONLINE("options.skyblockOnline"),
	OPTIONS_ALLOWCONTENT("options.allowContent"),
	OPTIONS_LANGUAGE("options.language"),
	OPTIONS_GAMEMODE("options.gameMode"),
	OPTIONS_WORLDNAME("options.worldName"),
	OPTIONS_CLOSED("options.closed"),
	OPTIONS_SPAWNTOWER_RECREATE("options.spawnTower.recreate"),
	OPTIONS_PVP("options.pvp"),
	OPTIONS_BUILD_RESPAWNWITHINVENTORY("options.build.respawnWithInventory"),
	OPTIONS_BUILD_WITHPROTECTEDAREA("options.build.withProtectedArea"),
	OPTIONS_BUILD_ALLOWENDERPEARL("options.build.allowEnderPearl");

	String path;

	private Config(String path) {
		this.path = path;
	}
}
