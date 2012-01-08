package me.lukas.SkyblockMultiplayer;

import java.io.IOException;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;

public class Data {

	public static ItemStack[] itemsChest;
	public static ArrayList<PlayerInfo> players = new ArrayList<PlayerInfo>();
	public static String[] mods = new String[0];
	public static int AnzahlPlayers;
	public static boolean skyblockonline;

	public static void addMod(String pname) {
		String mods = SkyblockMultiplayer.sconf.getString("mods");
		mods += " " + pname;
		mods = mods.trim();
		Data.mods = mods.split(" ");

		//Speichere in Datei		
		SkyblockMultiplayer.sconf.set("mods", mods);
		try {
			SkyblockMultiplayer.sconf.save(SkyblockMultiplayer.sconfFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void delMod(String pname) {
		String mods = SkyblockMultiplayer.sconf.getString("mods");

		String str = "";
		for (String m : mods.split(" ")) {
			if (!m.equalsIgnoreCase(pname)) {
				str += m + " ";
			}
		}

		mods = str.trim();
		Data.mods = mods.split(" ");

		//Speichere in Datei		
		SkyblockMultiplayer.sconf.set("mods", mods);
		try {
			SkyblockMultiplayer.sconf.save(SkyblockMultiplayer.sconfFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setStatus(boolean b) {
		SkyblockMultiplayer.sconf.set("skyblockonline", b);
		try {
			SkyblockMultiplayer.sconf.save(SkyblockMultiplayer.sconfFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
