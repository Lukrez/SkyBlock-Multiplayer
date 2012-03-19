package me.lukas.skyblockmultiplayer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class CreateNewIsland {
	private static int posY = 64;
	public Location Islandlocation;

	public CreateNewIsland(Player player) {

		Data.ISLAND_NUMBER += 1;
		Location l = getIslandPosition(Data.ISLAND_NUMBER);

		while (this.checkIfOccupied(l)) {
			Data.ISLAND_NUMBER += 1;
			l = getIslandPosition(Data.ISLAND_NUMBER);
		}

		player.sendMessage(Language.MSGS_showIslandNumber.sentence + Data.ISLAND_NUMBER);
		this.createIsland(l);
		this.Islandlocation = l;
		// transport player
		player.teleport(l);
	}

	public CreateNewIsland() {
	}

	public Location getIslandPosition(int n) {
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
			posX = (posSeite - r) * Data.ISLAND_DISTANCE;
			posZ = -posSeite * Data.ISLAND_DISTANCE;
		} else if (seite == 2) {
			posX = posSeite * Data.ISLAND_DISTANCE;
			posZ = (posSeite - r) * Data.ISLAND_DISTANCE;
		} else if (seite == 3) {
			posX = (r - posSeite) * Data.ISLAND_DISTANCE;
			posZ = posSeite * Data.ISLAND_DISTANCE;
		} else {
			posX = -posSeite * Data.ISLAND_DISTANCE;
			posZ = (r - posSeite) * Data.ISLAND_DISTANCE;
		}
		//System.out.println("Die Insel befindet sich auf "+posX+" in X-Richtung.");
		//System.out.println("Die Insel befindet sich auf "+posZ+" in Z-Richtung.");

		// Erstelle Location
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

		// Erstelle Chest		
		Block block = SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(0 + l.getBlockX(), 64, 4 + l.getBlockZ());
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

		// Erstelle Baum
		//SkyBlockMultiplayer.getSkyblockIslands().generateTree(new Location(SkyBlockMultiplayer.getSkyblockIslands(), 5 + l.getBlockX(), 64, l.getBlockZ()), TreeType.TREE);
		this.createTree(new Location(SkyBlockMultiplayer.getSkyBlockWorld(), 5 + l.getBlockX(), 64, l.getBlockZ()));

		// Setze Bedrock
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

	private void createTree(Location l) {
		int px = l.getBlockX();
		int py = l.getBlockY();
		int pz = l.getBlockZ();

		ItemStack itemWood = new ItemStack(17, 0);
		itemWood.setData(new MaterialData(1));

		ItemStack itemLeaves = new ItemStack(18, 0);
		itemLeaves.setData(new MaterialData(1));

		for (int y = 0; y <= 4; y++) {
			if (y != 4) {
				SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(px, py + y, pz).setType(itemWood.getType());
			}

			if (y != 0) {
				for (int x = -2; x <= 2; x++) {
					for (int z = -2; z <= 2; z++) {
						if (y != 4) {
							if (x != 0 || z != 0) {
								SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(px + x, py + y, pz + z).setType(itemLeaves.getType());
							}
						} else if (Math.abs(x) != 2 && Math.abs(z) != 2) {
							SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(px + x, py + y, pz + z).setType(itemLeaves.getType());
						}
					}
				}
			}
		}

		SkyBlockMultiplayer.getSkyBlockWorld().getBlockAt(px, py + 5, pz).setType(itemLeaves.getType());
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
