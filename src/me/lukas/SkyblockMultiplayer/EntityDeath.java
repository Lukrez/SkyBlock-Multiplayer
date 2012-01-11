package me.lukas.skyblockmultiplayer;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class EntityDeath extends EntityListener {

	SkyblockMultiplayer plugin;

	public EntityDeath(SkyblockMultiplayer instance) {
		this.plugin = instance;
	}

	public void onEntityDeath(EntityDeathEvent event) {
		Entity ent = event.getEntity();
		if (ent instanceof Player) {
			Player p = ((Player) ent).getPlayer();
			int PlayerNr = this.findPlayer(p.getName());

			if (PlayerNr == -1) {
				return;
			}

			Data.PLAYERS.get(PlayerNr).setDead(true);

			Data.PLAYERS_NUMBER--;
			if (Data.PLAYERS_NUMBER == 1) {
				String winner = "";
				for (PlayerInfo pinfo : Data.PLAYERS) {
					if (pinfo.isDead() == false) {
						winner = pinfo.getPlayerName();
					}
				}

				for (PlayerInfo pinfo : Data.PLAYERS) {
					pinfo.getPlayer().sendMessage(ChatColor.GREEN + "Spieler " + winner + " hat das Spiel gewonnen.");
				}
				return;
			}

			for (PlayerInfo pinfo : Data.PLAYERS) {
				pinfo.getPlayer().sendMessage("Jetzt sind (nur) noch " + Data.PLAYERS_NUMBER + " Spieler übrig!");

			}
		}
	}

	public int findPlayer(String playername) {
		for (int i = 0; i < Data.PLAYERS.size(); i++) {
			if (Data.PLAYERS.get(i).getPlayerName().equalsIgnoreCase(playername)) {
				return i;
			}
		}
		return -1;
	}
}