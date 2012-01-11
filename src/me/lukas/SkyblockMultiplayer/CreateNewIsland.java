package me.lukas.skyblockmultiplayer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

public class CreateNewIsland {
	private static int posY = 64;
	public Location Islandlocation;

	public CreateNewIsland(Player player) {

		Data.ISLAND_NUMBER += 1;
		Location l = getIslandPosition(Data.ISLAND_NUMBER);

		while (this.checkIfOcupied(l)) {
			Data.ISLAND_NUMBER += 1;
			l = getIslandPosition(Data.ISLAND_NUMBER);
		}

		player.sendMessage("Du bist auf Inselnr.: " + Data.ISLAND_NUMBER);
		this.createIsland(l);
		this.Islandlocation = l;
		// transport player
		player.teleport(l);
	}

	public CreateNewIsland() {
	};

	public Location getIslandPosition(int N) {
		//System.out.println("Erstelle Inselnr.: "+N);
		int posX, posZ;
		// Suche den momentanen Ring
		int R = (int) (0.5 + Math.sqrt(N / 2.0 - 0.25));
		//System.out.println("Insel befindet sich in Ringnr "+R);
		// Bestimme die Anzahl bereits vorhanderer Inseln auf dem Ring
		int NaufRing = N - 2 * R * (R - 1);
		//System.out.println("Die Insel ist im Ring die "+NaufRing+" Insel.");
		// Bestimmen der Seite auf dem Ring
		int Seite = (int) (Math.ceil(NaufRing / (double) R));
		//System.out.println("Die Insel befindet sich auf der "+Seite+" Seite.");
		// Bestimme die Position der Insel auf der Seite
		int PosSeite = NaufRing - (Seite - 1) * R - 1;
		//System.out.println("und ist auf der Seite die "+PosSeite+" Insel.");
		//System.out.println("Die Inseldistanz ist "+CreateNewIsland.IslandDistance);
		// Berechne die Positionen
		if (Seite == 1) {
			posX = (PosSeite - R) * Data.ISLAND_DISTANCE;
			posZ = -PosSeite * Data.ISLAND_DISTANCE;

		} else if (Seite == 2) {
			posX = PosSeite * Data.ISLAND_DISTANCE;
			posZ = (PosSeite - R) * Data.ISLAND_DISTANCE;
		} else if (Seite == 3) {
			posX = (R - PosSeite) * Data.ISLAND_DISTANCE;
			posZ = PosSeite * Data.ISLAND_DISTANCE;
		} else {
			posX = -PosSeite * Data.ISLAND_DISTANCE;
			posZ = (R - PosSeite) * Data.ISLAND_DISTANCE;
		}
		//System.out.println("Die Insel befindet sich auf "+posX+" in X-Richtung.");
		//System.out.println("Die Insel befindet sich auf "+posZ+" in Z-Richtung.");

		// Erstelle Location
		return new Location(SkyblockMultiplayer.getSkyblockIslands(), posX, posY, posZ);
	}

	private void createIsland(Location l) {
		// Erstelle unterste Erdebene
		createLayer(l, 61, Material.DIRT);
		//Erstelle mittlere Erdebene
		createLayer(l, 62, Material.DIRT);
		// Ersetze Erde durch Sand
		for (int x = 2; x <= 4; x++) {
			for (int z = -1; z <= 1; z++) {
				SkyblockMultiplayer.getSkyblockIslands().getBlockAt(x + l.getBlockX(), 62, z + l.getBlockZ()).setType(Material.SAND);
			}
		}
		//Erstelle oberste Grassebene
		createLayer(l, 63, Material.GRASS);

		// Erstelle Chest		
		Block block = SkyblockMultiplayer.getSkyblockIslands().getBlockAt(0 + l.getBlockX(), 64, 4 + l.getBlockZ());
		block.setType(Material.CHEST);
		Chest chest = (Chest) block.getState();
		chest.getBlock().setData((byte) 2);

		for (int i = 0; i < Data.ITEMSCHEST.length; i++) {
			try {
				chest.getInventory().addItem(Data.ITEMSCHEST[i]);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}

		//Erstelle Baum
		SkyblockMultiplayer.getSkyblockIslands().generateTree(new Location(SkyblockMultiplayer.getSkyblockIslands(), 5 + l.getBlockX(), 64, l.getBlockZ()), TreeType.TREE);

		// Setze Bedrock
		SkyblockMultiplayer.getSkyblockIslands().getBlockAt(l.getBlockX(), l.getBlockY() - 3, l.getBlockZ()).setType(Material.BEDROCK);

	}

	private void createLayer(Location l, int y, Material m) {
		for (int x = -1; x <= 6; x++) {
			for (int z = -1; z <= 1; z++) {
				SkyblockMultiplayer.getSkyblockIslands().getBlockAt(x + l.getBlockX(), y, z + l.getBlockZ()).setType(m);
			}
		}
		for (int x = -1; x <= 1; x++) {
			for (int z = 2; z <= 4; z++) {
				SkyblockMultiplayer.getSkyblockIslands().getBlockAt(x + l.getBlockX(), y, z + l.getBlockZ()).setType(m);
			}
		}
	}

	private boolean checkIfOcupied(Location l) {
		for (int x = -5; x <= 5; x++) {
			for (int y = -5; y <= 5; y++) {
				for (int z = -5; z <= 5; z++) {
					if (!SkyblockMultiplayer.getSkyblockIslands().getBlockAt(l.getBlockX() + x, l.getBlockY() + y, l.getBlockZ() + z).getType().equals(Material.AIR)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
