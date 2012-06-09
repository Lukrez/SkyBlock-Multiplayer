package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.Permissions;
import me.lukas.skyblockmultiplayer.PlayerData;
import me.lukas.skyblockmultiplayer.SQLInstructions;
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

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity ent = event.getEntity();
		if (!(ent instanceof Player)) {
			return;
		}

		Player player = (Player) ent;
		if (!player.getWorld().getName().equals(SkyBlockMultiplayer.getSkyBlockWorld().getName())) { // Exit, if player not in SkyBlock
			return;
		}


		PlayerData pdata = Settings.players.get(player.getName());
		if (pdata == null) { // Check, if player is in playerlist
			return;
		}

		if (SkyBlockMultiplayer.getInstance().playerIsOnTower(player)) {
			pdata.setOldInventory(player.getInventory().getContents());
			pdata.setOldArmor(player.getInventory().getArmorContents());
			pdata.setOldExp(player.getExp());
			pdata.setOldLevel(player.getLevel());
			pdata.setOldFood(player.getFoodLevel());
			pdata.setOldHealth(player.getMaxHealth());

			event.getDrops().clear();
			event.setDroppedExp(0);
			
			SQLInstructions.writeOldWorldData(pdata);

			return;
		}

		if (Settings.gameModeSelected == Settings.GameMode.BUILD && Settings.build_respawnWithInventory) {
			pdata.setIslandInventory(player.getInventory().getContents());
			pdata.setIslandArmor(player.getInventory().getArmorContents());
			pdata.setIslandExp(player.getExp());
			pdata.setIslandLevel(player.getLevel());
			pdata.setIslandFood(player.getFoodLevel());
			pdata.setIslandHealth(player.getMaxHealth());

			event.getDrops().clear();
			event.setDroppedExp(0);

			SQLInstructions.writeIslandData(pdata);

			return;
		}

		if (Settings.gameModeSelected == Settings.GameMode.BUILD) {
			return;
		}

		pdata.setDeathStatus(true);
		pdata.setLivesLeft(pdata.getLivesLeft() - 1);
		SQLInstructions.writePartialPlayerData(pdata);

		Settings.numbersPlayers--;

		for (PlayerData pInfo : Settings.players.values()) {
			if (pInfo.getPlayer() != null) {
				if (pInfo.getPlayer().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()) || (Permissions.SKYBLOCK_MESSAGES.has(pInfo.getPlayer()) && Settings.messagesOutside)) {
					pInfo.getPlayer().sendMessage(Language.MSGS_PLAYER_DIED1.sentence + Settings.numbersPlayers + Language.MSGS_PLAYER_DIED2.sentence);
				}
			}
		}

		if (Settings.numbersPlayers == 1) {
			String winner = "";
			for (PlayerData pinfo : Settings.players.values()) {
				if (pinfo.isDead() == false) {
					winner = pinfo.getPlayer().getName();
				}
			}

			for (PlayerData pInfo : Settings.players.values()) {
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
