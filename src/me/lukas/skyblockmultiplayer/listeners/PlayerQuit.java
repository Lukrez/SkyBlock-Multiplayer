package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.Data;
import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

	private SkyBlockMultiplayer plugin;

	public PlayerQuit(SkyBlockMultiplayer instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (!player.getWorld().equals(SkyBlockMultiplayer.getSkyBlockWorld())) {
			return;
		}

		PlayerInfo pi = Data.PLAYERS.get(player.getName());

		if (!this.plugin.checkIfEmpty(player.getInventory().getContents())) {
			pi.setContentsInventory(player.getInventory().getContents());
			player.getInventory().clear();
		}

		if (!this.plugin.checkIfEmpty(player.getInventory().getArmorContents())) {
			pi.setContentsArmor(player.getInventory().getArmorContents());
		}

		if (pi.getIslandLocation() == null) {
			Data.PLAYERS.remove(player.getName());
		}
	}
}
