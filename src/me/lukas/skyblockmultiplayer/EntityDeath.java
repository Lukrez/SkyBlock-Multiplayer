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

		Player player = (Player) ent;
		if (!player.getWorld().equals(SkyBlockMultiplayer.getSkyBlockWorld())) { // Exit, if player not on SkyBlock
			return;
		}

		PlayerInfo pi = Data.PLAYERS.get(player.getName());

		if (pi == null) { // Check, if player is in playerlist
			return;
		}

		if (Data.GAMEMODE_SELECTED == Data.GAMEMODE.BUILD && Data.BUILD_RESPAWNWITHINVENTORY) {
			Data.PLAYERINVENTORYS.put(player, player.getInventory().getContents());
			Data.PLAYEREQUIPMENTS.put(player, player.getInventory().getArmorContents());
			event.getDrops().clear();
			return;
		}

		if (!pi.getHasIsland() || pi.isDead()) {
			return;
		}

		pi.setDead(true);

		if (Data.PLAYERS_NUMBER < 1)
			return;

		Data.PLAYERS_NUMBER--;

		for (PlayerInfo pinfo : Data.PLAYERS.values()) {
			pinfo.getPlayer().sendMessage(Language.MSGS_PLAYERDIED1.sentence + Data.PLAYERS_NUMBER + Language.MSGS_PLAYERDIED2.sentence);
		}

		if (Data.PLAYERS_NUMBER == 1) {
			String winner = "";
			for (PlayerInfo pinfo : Data.PLAYERS.values()) {
				if (pinfo.isDead() == false) {
					winner = pinfo.getPlayer().getName();
				}
			}

			for (PlayerInfo pinfo : Data.PLAYERS.values()) {
				pinfo.getPlayer().sendMessage(Language.MSGS_PLAYERWINBROADCAST1.sentence + winner + Language.MSGS_PLAYERWINBROADCAST2.sentence);
			}
			return;
		}

	}
}
