package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.Settings;
import me.lukas.skyblockmultiplayer.Language;
import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
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
		if (!player.getWorld().equals(SkyBlockMultiplayer.getSkyBlockWorld())) { // Exit, if player not in SkyBlock
			return;
		}

		PlayerInfo pi = Settings.PLAYERS.get(player.getName());
		if (pi == null) { // Check, if player is in playerlist
			return;
		}

		if (this.plugin.playerIsOnTower(player)) {
			if(pi.getIslandLocation() == null) {
				pi.setContentsInventory(player.getInventory().getContents());
				pi.setContentsArmor(player.getInventory().getArmorContents());
				event.getDrops().clear();
			}
			return;
		}

		if (Settings.gameModeSelected == Settings.GAMEMODE.BUILD && Settings.build_respawnWithInventory) {
			pi.setContentsInventory(player.getInventory().getContents());
			pi.setContentsArmor(player.getInventory().getArmorContents());
			event.getDrops().clear();
			return;
		}

		if (Settings.gameModeSelected == Settings.GAMEMODE.BUILD) {
			if (event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.VOID) {
				player.teleport(player.getWorld().getSpawnLocation());
				player.setHealth(player.getMaxHealth());
				return;
			}
			return;
		}

		if (!pi.getHasIsland() || pi.isDead()) {
			return;
		}

		pi.setDead(true);

		if (Settings.numbersPlayers < 1) {
			return;
		}

		Settings.numbersPlayers--;

		for (PlayerInfo pInfo : Settings.PLAYERS.values()) {
			if (pInfo.getPlayer() != null) {
				pInfo.getPlayer().sendMessage(Language.MSGS_PLAYERDIED1.sentence + Settings.numbersPlayers + Language.MSGS_PLAYERDIED2.sentence);
			}
		}

		if (Settings.numbersPlayers == 1) {
			String winner = "";
			for (PlayerInfo pinfo : Settings.PLAYERS.values()) {
				if (pinfo.isDead() == false) {
					winner = pinfo.getPlayer().getName();
				}
			}

			for (PlayerInfo pInfo : Settings.PLAYERS.values()) {
				if (pInfo.getPlayer() != null) {
					pInfo.getPlayer().sendMessage(Language.MSGS_PLAYERWINBROADCAST1.sentence + winner + Language.MSGS_PLAYERWINBROADCAST2.sentence);
				}
			}
			return;
		}
	}
}
