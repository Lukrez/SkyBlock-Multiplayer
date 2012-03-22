package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.Settings;
import me.lukas.skyblockmultiplayer.Language;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerTeleport implements Listener {
	private SkyBlockMultiplayer plugin;

	public PlayerTeleport(SkyBlockMultiplayer instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();

		if (!Settings.skyBlockOnline) {
			return;
		}

		if (!player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			return;
		}

		if (player.isOp()) {
			return;
		}

		if (!this.plugin.checkIfEmpty(player.getInventory().getContents()) && !this.plugin.checkIfEmpty(player.getInventory().getArmorContents())) {
			if (!this.plugin.playerIsOnTower(player) && !Settings.allowContent) {
				event.setCancelled(true);
				player.sendMessage(this.plugin.pName + Language.MSGS_NOEMPTYINVENTORYLEAVE.sentence);
				return;
			}
		}
	}
}
