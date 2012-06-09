package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.Language;
import me.lukas.skyblockmultiplayer.PlayerData;
import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.Settings;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class PlayerTeleport implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();

		if (!Settings.skyBlockOnline) {
			return;
		}

		if (!event.getFrom().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			if (event.getCause() == TeleportCause.ENDER_PEARL) {
				return;
			}
		}

		if (!event.getFrom().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			if (event.getTo().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
				if (!SkyBlockMultiplayer.getInstance().locationIsOnTower(event.getTo())) {
					System.out.println("PlayerTeleport.onPlayerTeleport()");
					event.setCancelled(true);
					player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_INSIDE_OF_SB.sentence);
					return;
				}
			}
		}

		
		if (event.getCause().equals(TeleportCause.ENDER_PEARL)) {
			if (SkyBlockMultiplayer.getInstance().locationIsOnTower(event.getTo())) {
				event.setCancelled(true);
				return;
			}
			if (Settings.build_withProtectedArea) {
				PlayerData pdata = Settings.players.get(player.getName());
				if (pdata.checkBuildPermission(event.getTo())) {
					return;
				}
				event.setCancelled(true);
				return;
			}
		}

		if (event.getFrom().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()) && !event.getTo().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()) && !SkyBlockMultiplayer.getInstance().playerIsOnTower(player)) {
			event.setCancelled(true);
			player.sendMessage(SkyBlockMultiplayer.getInstance().pName + Language.MSGS_ONLY_ON_TOWER.sentence);
		}
	}
}
