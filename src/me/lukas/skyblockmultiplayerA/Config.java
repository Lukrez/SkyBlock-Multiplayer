package me.lukas.skyblockmultiplayer;

public enum Config {

	OPTIONS_ISLANDDISTANCE("options.islandDistance"),
	OPTIONS_CHESTITEMS("options.chest.Items"),
	OPTIONS_SKYBLOCKONLINE("options.skyblockonline"),
	OPTIONS_PVP("options.pvp"),
	OPTIONS_LANGUAGE("options.language");
	
	;
	String path;

	private Config(String path) {
		this.path = path;
	}
}
