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
		if (!p.getWorld().equals(SkyBlockMultiplayer.getSkyblockIslands())) { // Exit, if player not on SkyBlock
			return;
		}

		int playerNr = plugin.findPlayer(p.getName());

		if (playerNr == -1) { // Check, if player is in playerlist (should be covered by function above)
			return;
		}

		PlayerInfo pi = Data.PLAYERS.get(playerNr);

		if (!Data.PVP)
			return;

		if (!Data.PVP) {
			pi.setIsOnIsland(false);
			return;
		}

		if (!pi.getHasIsland() || pi.isDead()) {
			return;
		}
		/*iif (!pi.getIsOnIsland() || pi.isDead()){
			return;
		}*/

		pi.setDead(true);

		if (Data.PLAYERS_NUMBER < 1)
			return;

		Data.PLAYERS_NUMBER--;

		for (PlayerInfo pinfo : Data.PLAYERS) {
			pinfo.getPlayer().sendMessage(Language.MSGS_PLAYERDIED1.sentence + Data.PLAYERS_NUMBER + Language.MSGS_PLAYERDIED2.sentence);
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

	}
}
