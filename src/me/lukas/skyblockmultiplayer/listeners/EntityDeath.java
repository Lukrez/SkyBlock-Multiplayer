package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.Permissions;
import me.lukas.skyblockmultiplayer.Settings;
import me.lukas.skyblockmultiplayer.Language;
import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;
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
		if (!player.getWorld().equals(SkyBlockMultiplayer.getSkyBlockWorld())) { // Exit, if player not in SkyBlock
			return;
		}

		PlayerInfo pi = Settings.players.get(player.getName());
		if (pi == null) { // Check, if player is in playerlist
			pi = SkyBlockMultiplayer.getInstance().readPlayerFile(player.getName());
			if (pi == null) {
				return;
			}
			Settings.players.put(player.getName(), pi);
		}

		if (this.plugin.playerIsOnTower(player)) {
			pi.setOldInventory(player.getInventory().getContents());
			pi.setOldArmor(player.getInventory().getArmorContents());
			pi.setOldExp(player.getExp());
			pi.setOldLevel(player.getLevel());
			pi.setOldFood(player.getFoodLevel());
			pi.setOldHealth(player.getMaxHealth());

			event.getDrops().clear();
			event.setDroppedExp(0);

			this.plugin.writePlayerFile(player.getName(), pi);
			return;
		}

		if (Settings.gameModeSelected == Settings.GameMode.BUILD && Settings.build_respawnWithInventory) {
			pi.setIslandInventory(player.getInventory().getContents());
			pi.setIslandArmor(player.getInventory().getArmorContents());
			pi.setIslandExp(player.getExp());
			pi.setIslandLevel(player.getLevel());
			pi.setIslandFood(player.getFoodLevel());
			pi.setIslandHealth(player.getMaxHealth());

			event.getDrops().clear();
			event.setDroppedExp(0);

			this.plugin.writePlayerFile(player.getName(), pi);
			return;
		}

		if (Settings.gameModeSelected == Settings.GameMode.BUILD) {
			return;
		}

		pi.setDead(true);
		if (!pi.getHasIsland()) {
			return;
		}

		pi.setLivesLeft(pi.getLivesLeft() - 1);
		if (pi.getIslandsLeft() != 0 || pi.getLivesLeft() != 0) {
			return;
		}

		this.plugin.writePlayerFile(player.getName(), pi);

		if (Settings.numbersPlayers < 1) {
			return;
		}
		Settings.numbersPlayers--;

		for (PlayerInfo pInfo : Settings.players.values()) {
			if (pInfo.getPlayer() != null) {
				if (pInfo.getPlayer().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()) || (Permissions.SKYBLOCK_MESSAGES.has(pInfo.getPlayer()) && Settings.messagesOutside)) {
					pInfo.getPlayer().sendMessage(Language.MSGS_PLAYER_DIED1.sentence + Settings.numbersPlayers + Language.MSGS_PLAYER_DIED2.sentence);
				}
			}
		}

		if (Settings.numbersPlayers == 1) {
			String winner = "";
			for (PlayerInfo pinfo : Settings.players.values()) {
				if (pinfo.isDead() == false) {
					winner = pinfo.getPlayer().getName();
				}
			}

			for (PlayerInfo pInfo : Settings.players.values()) {
				if (pInfo.getPlayer() != null) {
					if (pInfo.getPlayer().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()) || (Permissions.SKYBLOCK_MESSAGES.has(pInfo.getPlayer()) && Settings.messagesOutside)) {
						pInfo.getPlayer().sendMessage(Language.MSGS_PLAYER_WIN_BROADCAST1.sentence + winner + Language.MSGS_PLAYER_WIN_BROADCAST2.sentence);
					}
				}
			}
			return;
		}
	}
}
