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
		int numberIslands = 1;
		//Settings.numberIslands += 1;
		Location l = getIslandPosition(numberIslands);

		while (this.checkIfOccupied(l)) {
			//Settings.numberIslands += 1;
			numberIslands++;
			l = getIslandPosition(numberIslands);
		}

		player.sendMessage(Language.MSGS_SHOW_ISLAND_NUMBER.sentence + numberIslands);
		this.createIsland(l);
		this.Islandlocation = l;
		// transport player
		player.teleport(l);
	}

	public CreateNewIsland(int amount) {
		int numberIslands = 1;
		Location l = getIslandPosition(numberIslands);
		for (int i = 0; i < amount; i++) {
			while (this.checkIfOccupied(l)) {
				numberIslands++;
				l = getIslandPosition(numberIslands);
				//System.out.println(numberIslands + " : Location " + SkyBlockMultiplayer.instance.getStringLocation(l));

			}
			this.createIsland(l);
		}
	}

	public CreateNewIsland() {
	}

	public int getAmountOfIslands() {
		int amountIslands = 1;
		do {
			Location locIsland = this.getIslandPosition(amountIslands);
			int px = locIsland.getBlockX();
			int py = locIsland.getBlockY() - 3;
			int pz = locIsland.getBlockZ();
			if (!new Location(SkyBlockMultiplayer.getSkyBlockWorld(), px, py, pz).getBlock().getType().equals(Material.BEDROCK)) {
				break;
			}
			amountIslands++;
		} while (true);
		return amountIslands - 1;
	}

	public int getIslandNumber(Location l) {
		for (int i = 1; i <= this.getAmountOfIslands(); i++) {
			Location locIsland = this.getIslandPosition(i);
			int islandX = locIsland.getBlockX();
			int islandZ = locIsland.getBlockZ();

			int locationX = l.getBlockX();
			int locationZ = l.getBlockZ();

			int dist = (Settings.distanceIslands / 2) - 3;

			if (islandX + dist >= locationX && islandX - dist <= locationX) {
				if (islandZ + dist >= locationZ && islandZ - dist <= locationZ) {
					if (l.getWorld().getBlockAt(new Location(l.getWorld(), islandX, 61, islandZ)).getType().equals(Material.BEDROCK)) {
						return i;
					}
				}
			}
		}
		return 0;
	}

	public Location getIslandLocation(int number) {
		for (int i = 1; i <= this.getAmountOfIslands(); i++) {
			Location locIsland = this.getIslandPosition(i);
			if (i == number) {
				return locIsland;
			}
		}
		return null;
	}

	private Location getIslandPosition(int n) {
		//System.out.println("Erstelle Inselnr.: "+n);
		int posX, posZ;
		// Suche den momentanen Ring
		int r = (int) (0.5 + Math.sqrt(n / 2.0 - 0.25));
		//System.out.println("Insel befindet sich in Ringnr "+r);
		// Bestimme die Anzahl bereits vorhanderer Inseln auf dem Ring
		int naufRing = n - 2 * r * (r - 1);
		//System.out.println("Die Insel ist im Ring die "+naufRing+" Insel.");
		// Bestimmen der Seite auf dem Ring
		int seite = (int) (Math.ceil(naufRing / (double) r));
		//System.out.println("Die Insel befindet sich auf der "+seite+" Seite.");
		// Bestimme die Position der Insel auf der Seite
		int posSeite = naufRing - (seite - 1) * r - 1;
		//System.out.println("und ist auf der Seite die "+posSeite+" Insel.");
		//System.out.println("Die Inseldistanz ist "+CreateNewIsland.IslandDistance);
		// Berechne die Positionen
		if (seite == 1) {
			posX = (posSeite - r) * Settings.distanceIslands;
			posZ = -posSeite * Settings.distanceIslands;
		} else if (seite == 2) {
			posX = posSeite * Settings.distanceIslands;
			posZ = (posSeite - r) * Settings.distanceIslands;
		} else if (seite == 3) {
			posX = (r - posSeite) * Settings.distanceIslands;
			posZ = posSeite * Settings.distanceIslands;
		} else {
			posX = -posSeite * Settings.distanceIslands;
			posZ = (r - posSeite) * Settings.distanceIslands;
		}
		//System.out.println("Die Insel befindet sich auf "+posX+" in X-Richtung.");
		//System.out.println("Die Insel befindet sich auf "+posZ+" in Z-Richtung.");

		// create location for island
		return new Location(SkyBlockMultiplayer.getSkyBlockWorld(), posX, CreateNewIsland.posY, posZ);
	}

	private void createIsland(Location l) {
		// Erstelle unterste Erdebene
		createLayer(l, 61, Material.DIRT);
		//Erstelle mittlere Erdebene
		createLayer(l, 62, Material.DIRT);
		// Ersetze Erde durch Sand
		for (int x = 2; x <= 4; x++) {
			for (int z = -1; z <= 1; z++) {
				SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(x + l.getBlockX(), 62, z + l.getBlockZ()).setType(Material.SAND);
			}
		}
		//Erstelle oberste Grassebene
		createLayer(l, 63, Material.GRASS);

		// create Chest		
		Block block = SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(0 + l.getBlockX(), 64, 4 + l.getBlockZ());
		block.setType(Material.CHEST);
		Chest chest = (Chest) block.getState();
		chest.getBlock().setData((byte) 2);

		for (int i = 0; i < Settings.itemsChest.length; i++) {
			try {
				chest.getInventory().addItem(Settings.itemsChest[i]);
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
			}
		}

		// create tree
		SkyBlockMultiplayer.getSkyBlockWorld().generateTree(new Location(SkyBlockMultiplayer.getSkyBlockWorld(), 5 + l.getBlockX(), 64, l.getBlockZ()), TreeType.TREE);

		// place bedrock
		SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(l.getBlockX(), l.getBlockY() - 3, l.getBlockZ()).setType(Material.BEDROCK);

	}

	private void createLayer(Location l, int y, Material m) {
		for (int x = -1; x <= 6; x++) {
			for (int z = -1; z <= 1; z++) {
				SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(x + l.getBlockX(), y, z + l.getBlockZ()).setType(m);
			}
		}
		for (int x = -1; x <= 1; x++) {
			for (int z = 2; z <= 4; z++) {
				SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(x + l.getBlockX(), y, z + l.getBlockZ()).setType(m);
			}
		}
	}

	private boolean checkIfOccupied(Location l) {
		for (int x = -5; x <= 5; x++) {
			for (int y = -5; y <= 5; y++) {
				for (int z = -5; z <= 5; z++) {
					if (!SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(l.getBlockX() + x, l.getBlockY() + y, l.getBlockZ() + z).getType().equals(Material.AIR)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
