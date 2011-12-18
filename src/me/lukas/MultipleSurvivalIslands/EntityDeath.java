package me.lukas.MultipleSurvivalIslands;


import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class EntityDeath extends EntityListener {
	public void onEntityDeath(EntityDeathEvent event){
		 Entity ent = event.getEntity();
		   if (ent instanceof Player) {
			   Player p = ((Player) ent).getPlayer();
			   int PlayerNr = this.findPlayer(p.getName());
			   
			   if(PlayerNr == -1){
				   return;
			   }
			   
			   Data.players.get(PlayerNr).setDeadToTrue();
			   
			   Data.AnzahlPlayers--;
			   if(Data.AnzahlPlayers == 1){
				   String winner = "";
				   for(PlayerInfo pinfo: Data.players){
					   if(pinfo.getDead() == false){
						  winner = pinfo.getPlayerName();
					   }
				   }
				   
				   for(PlayerInfo pinfo: Data.players){
					   pinfo.getPlayer().sendMessage("Spieler " + winner + " hat des Spiel gewonnen.");
				   }
				   return;
			   }
			   
			  for(PlayerInfo pinfo: Data.players){
				  pinfo.getPlayer().sendMessage("Jetzt sind (nur) noch " + Data.AnzahlPlayers + " Spieler übrig!");
				  
			  }
		   }
	}

	public int findPlayer(String playername){
		for (int i=0;i<Data.players.size();i++){
			if(Data.players.get(i).getPlayerName().equalsIgnoreCase(playername)){
				return i;
			}	
		}
		return -1;
	}
}