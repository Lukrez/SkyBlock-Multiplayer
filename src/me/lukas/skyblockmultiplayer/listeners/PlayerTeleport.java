package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.Language;
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
	private SkyBlockMultiplayer plugin;

	public PlayerTeleport(SkyBlockMultiplayer instance) {
		this.plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();

		if (!Settings.skyBlockOnline) {
			return;
		}

		if (!event.getFrom().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()) && event.getTo().getWorld().getName().equalsIgnoreCase((SkyBlockMultiplayer.getSkyBlockWorld().getName()))) {
			if (!SkyBlockMultiplayer.instance.locationIsOnTower(event.getTo())) {
				event.setCancelled(true);
				player.sendMessage(Language.MSGS_ONLY_INSIDE_OF_SB.sentence);
				return;
			}
		}

		PlayerInfo pi = Settings.players.get(player.getName());
		if (event.getCause().equals(TeleportCause.ENDER_PEARL)) {
			if (this.plugin.locationIsOnTower(event.getTo())) {
				event.setCancelled(true);
				return;
			}
			if (Settings.build_withProtectedArea) {
				if (SkyBlockMultiplayer.canPlayerDoThat(pi, event.getTo())) {
					return;
				}
				PlayerInfo owner = SkyBlockMultiplayer.getOwner(event.getTo());
				if (owner == null) {
					event.setCancelled(true);
					return;
				}
				if (owner.getFriends().contains(player.getName())) {
					return;
				}
				event.setCancelled(true);
				return;
			}
		}

		if (event.getFrom().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()) && !event.getTo().getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName()) && !this.plugin.playerIsOnTower(player)) {
			event.setCancelled(true);
			player.sendMessage(this.plugin.pName + Language.MSGS_ONLY_ON_TOWER.sentence);
		}
	}
}
