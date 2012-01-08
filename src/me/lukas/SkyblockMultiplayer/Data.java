package me.lukas.skyblockmultiplayer;

import java.io.IOException;
import java.util.ArrayList;
import org.bukkit.inventory.ItemStack;

public class Data {

	public static ItemStack[] ITEMSCHEST;
	public static ArrayList<PlayerInfo> PLAYERS = new ArrayList<PlayerInfo>();
	public static int PLAYERS_NUMBER;
	public static boolean SKYBLOCK_ONLINE;
	public static int ISLAND_DISTANCE;
	public static int ISLAND_NUMBER;

	public static void setStatus(boolean b) {
		SkyblockMultiplayer.sconfig.set("skyblockonline", b);
		try {
			SkyblockMultiplayer.sconfig.save(SkyblockMultiplayer.sfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
