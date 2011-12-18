package me.lukas.MultipleSurvivalIslands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

public class CreateNewIsland {
	public static int ISLANDNR;
	public static int IslandDistance;
	private static int posY = 64;
	
	public CreateNewIsland(Player player) {
		
		CreateNewIsland.ISLANDNR+=1;
		Location l = getIslandPosition(CreateNewIsland.ISLANDNR);
		
		while(this.checkIfOcupied(l)){
			CreateNewIsland.ISLANDNR+=1;
			l = getIslandPosition(CreateNewIsland.ISLANDNR);
		}
		
		player.sendMessage("Du bist auf Inselnr.: "+CreateNewIsland.ISLANDNR);
		this.createIsland(l);
		// transport player
		player.teleport(l);
	}
	
	public CreateNewIsland(){};

	
	/*public void CreateIt(int anzahl, Player player){
		for(int i = 0;i < anzahl;i++){		
			CreateNewIsland.ISLANDNR += 1; // Erhöhe die Nr der Insel, welche nun erstellt werden soll
			Location l = getIslandPosition(CreateNewIsland.ISLANDNR);
			this.createIsland(l);
			
			player.sendMessage(ChatColor.GREEN + "Insel-Nummer: " + CreateNewIsland.ISLANDNR + " PosX: " + l.getBlockX() + " PosZ: " + l.getBlockZ());
		}
	}*/
	
	private Location getIslandPosition(int N){
		//System.out.println("Erstelle Inselnr.: "+N);
		int posX,posZ;
		// Suche den momentanen Ring
		int R = (int)(0.5+Math.sqrt(N/2.0-0.25));
		//System.out.println("Insel befindet sich in Ringnr "+R);
		// Bestimme die Anzahl bereits vorhanderer Inseln auf dem Ring
		int NaufRing = N-2*R*(R-1);
		//System.out.println("Die Insel ist im Ring die "+NaufRing+" Insel.");
		// Bestimmen der Seite auf dem Ring
		int Seite = (int)(Math.ceil(NaufRing/(double)R));
		System.out.println("Die Insel befindet sich auf der "+Seite+" Seite.");
		// Bestimme die Position der Insel auf der Seite
		int PosSeite = NaufRing-(Seite-1)*R-1;
		//System.out.println("und ist auf der Seite die "+PosSeite+" Insel.");
		//System.out.println("Die Inseldistanz ist "+CreateNewIsland.IslandDistance);
		// Berechne die Positionen
		if (Seite == 1){
			posX = (PosSeite-R)*CreateNewIsland.IslandDistance;
	        posZ = -PosSeite*CreateNewIsland.IslandDistance;			
			
		} else if (Seite == 2){
			posX = PosSeite*CreateNewIsland.IslandDistance;
			posZ = (PosSeite-R)*CreateNewIsland.IslandDistance;
		} else if (Seite == 3){
			posX = (R-PosSeite)*CreateNewIsland.IslandDistance;
	        posZ = PosSeite*CreateNewIsland.IslandDistance;		
		} else {
			posX = -PosSeite*CreateNewIsland.IslandDistance;	
	        posZ = (R-PosSeite)*CreateNewIsland.IslandDistance;	
		}
		//System.out.println("Die Insel befindet sich auf "+posX+" in X-Richtung.");
		//System.out.println("Die Insel befindet sich auf "+posZ+" in Z-Richtung.");
		
		// Erstelle Location
		return new Location(MultipleSurvivalIslands.getWorldIslands(), posX, CreateNewIsland.posY, posZ);
	}
	
	private void createIsland(Location l){
		// Erstelle unterste Erdebene
		createLayer(l,61,Material.DIRT);
		//Erstelle mittlere Erdebene
		createLayer(l,62,Material.DIRT);
		// Ersetze Erde durch Sand
		for (int x = 2;x<=4;x++){
			for (int z = -1;z<=1;z++){
				MultipleSurvivalIslands.getWorldIslands().getBlockAt(x+l.getBlockX(), 62, z+l.getBlockZ()).setType(Material.SAND);
			}
		}
		//Erstelle oberste Grassebene
		createLayer(l,63,Material.GRASS);
		
		// Erstelle Chest		
		Block block = MultipleSurvivalIslands.getWorldIslands().getBlockAt(0+l.getBlockX(), 64, 4+l.getBlockZ());
		block.setType(Material.CHEST);
		Chest chest = (Chest)block.getState();
		chest.getBlock().setData((byte) 2);
		
		for(int i = 0;i < Data.itemsChest.length;i++){
			//System.out.println(Data.itemsChest[i].getTypeId() + " " + Data.itemsChest[i].getAmount());
			try{
				chest.getInventory().addItem(Data.itemsChest[i]);
			}catch(Exception ex){
				
			}
		}

		//Erstelle Baum
		MultipleSurvivalIslands.getWorldIslands().generateTree(new Location(MultipleSurvivalIslands.getWorldIslands(), 5+l.getBlockX(),64,l.getBlockZ()), TreeType.TREE);
		
		// Setze Bedrock
		MultipleSurvivalIslands.getWorldIslands().getBlockAt(l.getBlockX(), l.getBlockY()-3, l.getBlockZ()).setType(Material.BEDROCK);
		
	}
	
	private void createLayer(Location l, int y, Material m){
		for (int x = -1;x<=6;x++){
			for (int z = -1;z<=1;z++){
				MultipleSurvivalIslands.getWorldIslands().getBlockAt(x+l.getBlockX(), y, z+l.getBlockZ()).setType(m);
			}
		}
		for (int x = -1;x<=1;x++){
			for (int z = 2;z<=4;z++){
				MultipleSurvivalIslands.getWorldIslands().getBlockAt(x+l.getBlockX(), y, z+l.getBlockZ()).setType(m);
			}
		}
	}
	
	/*private boolean checkIfBedrock(Location l){
		if(MultipleSurvivalIslands.getWorldIslands().getBlockAt(l.getBlockX(), l.getBlockY()-3, l.getBlockZ()).getType().equals(Material.BEDROCK)){
			return true;
		}
		return false;
	}*/
	
	private boolean checkIfOcupied(Location l){
		for(int x = -5;x <=5;x++){
			for(int y = -5;y <=5;y++){
				for(int z = -5;z <=5;z++){
					if(!MultipleSurvivalIslands.getWorldIslands().getBlockAt(l.getBlockX()+x, l.getBlockY()+y, l.getBlockZ()+z).getType().equals(Material.AIR)){
						return true;
					}
				}	
			}	
		}
		return false;
	}
}
