package me.lukas.skyblockmultiplayer;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeath implements Listener {

	SkyblockMultiplayer plugin;

	public EntityDeath(SkyblockMultiplayer instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity ent = event.getEntity();
		if (ent instanceof Player) {
			Player p = (Player) ent;
			System.out.println(p.getName() + " died");
			int PlayerNr = plugin.findPlayer(p.getName());

			if (PlayerNr == -1) {
				return;
			}

			if (!Data.PVP) {
				return;
			}

			if (!Data.PLAYERS.get(PlayerNr).getHasIsland()) {
				return;
			}

			if (!Data.PLAYERS.get(PlayerNr).getPlayer().getWorld().equals(SkyblockMultiplayer.getSkyblockIslands())) {
				return;
			}

			Data.PLAYERS.get(PlayerNr).setDead(true);

			if (!(Data.PLAYERS_NUMBER - 1 < 0)) {
				Data.PLAYERS_NUMBER--;
			}
			if (Data.PLAYERS_NUMBER == 1) {
				String winner = "";
				for (PlayerInfo pinfo : Data.PLAYERS) {
					if (pinfo.isDead() == false) {
						winner = pinfo.getPlayerName();
					}
				}

				for (PlayerInfo pinfo : Data.PLAYERS) {
					pinfo.getPlayer().sendMessage(Language.MSGS_PLAYERWINBROADCAST1.sentence + winner + Language.MSGS_PLAYERWINBROADCAST2.sentence);
				}
				return;
			}

			for (PlayerInfo pinfo : Data.PLAYERS) {
				pinfo.getPlayer().sendMessage("Jetzt sind (nur) noch " + Data.PLAYERS_NUMBER + " Spieler übrig.");
			}
		}
	}
}