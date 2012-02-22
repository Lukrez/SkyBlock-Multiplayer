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
		if (!Data.PVP) {
			return;
		}

		Entity ent = event.getEntity();
		if (ent instanceof Player) {
			Player p = (Player) ent;
			int playerNr = plugin.findPlayer(p.getName());

			if (playerNr == -1) {
				return;
			}

			if (!Data.PLAYERS.get(playerNr).getHasIsland()) {
				return;
			}

			if (!Data.PLAYERS.get(playerNr).getPlayer().getWorld().equals(SkyblockMultiplayer.getSkyblockIslands())) {
				return;
			}

			Data.PLAYERS.get(playerNr).setDead(true);

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
				pinfo.getPlayer().sendMessage(Language.MSGS_PLAYERDIED1.sentence + Data.PLAYERS_NUMBER + Language.MSGS_PLAYERDIED2.sentence);
			}
		}
	}
}