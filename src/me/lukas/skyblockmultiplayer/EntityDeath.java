package me.lukas.skyblockmultiplayer;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeath implements Listener {

	SkyBlockMultiplayer plugin;

	public EntityDeath(SkyBlockMultiplayer instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity ent = event.getEntity();
		if (!(ent instanceof Player)) {
			return;
		}

		Player p = (Player) ent;
		int playerNr = plugin.findPlayer(p.getName());

		if (playerNr == -1) {
			return;
		}

		PlayerInfo pi = Data.PLAYERS.get(playerNr);

		if (!Data.PVP) {
			pi.setIsOnIsland(false);
			return;
		}

		if (!pi.getHasIsland()) {
			return;
		}

		if (!pi.getPlayer().getWorld().equals(SkyBlockMultiplayer.getSkyblockIslands())) {
			return;
		}

		pi.setDead(true);

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
