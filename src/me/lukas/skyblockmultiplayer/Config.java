package me.lukas.skyblockmultiplayer;

public enum Config {

	OPTIONS_ISLANDDISTANCE("options.islandDistance"),
	OPTIONS_CHESTITEMS("options.chest.Items"),
	OPTIONS_SKYBLOCKONLINE("options.skyblockOnline"),
	OPTIONS_PVP("options.pvp"),
	OPTIONS_LANGUAGE("options.language"), 
	OPTIONS_ALLOWCONTENT("options.allowContent");

	String path;

	private Config(String path) {
		this.path = path;
	}
}
