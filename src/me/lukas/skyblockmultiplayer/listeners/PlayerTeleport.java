package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.Data;
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

		if (!Data.SKYBLOCK_ONLINE) {
			return;
		}

		if (!player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			return;
		}

		if (player.isOp()) {
			return;
		}

		if (!this.plugin.checkIfEmpty(player.getInventory().getContents()) && !this.plugin.checkIfEmpty(player.getInventory().getArmorContents())) {
			if (!this.plugin.isPlayerOnTower(player) && !Data.ALLOWCONTENT) {
				event.setCancelled(true);
				player.sendMessage(this.plugin.pName + Language.MSGS_NOEMPTYINVENTORYLEAVE.sentence);
				return;
			}
		}
	}
}
