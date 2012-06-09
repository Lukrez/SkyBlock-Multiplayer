package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.PlayerData;
import me.lukas.skyblockmultiplayer.Settings;
import me.lukas.skyblockmultiplayer.Permissions;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;

public class PlayerUseBucketListener implements Listener {

	@EventHandler
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		Block b = event.getBlockClicked();

		if (!Settings.skyBlockOnline) {
			return;
		}

		if (!player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) { // Check if player is in world SkyBlockMultiplayer
			return;
		}

		if (Permissions.SKYBLOCK_BUILD.has(player)) {
			return;
		}

		if (event.getPlayer().getLocation().getBlockX() >= -20 && event.getPlayer().getLocation().getBlockX() <= 20) {
			if (event.getPlayer().getLocation().getBlockZ() >= -20 && event.getPlayer().getLocation().getBlockZ() <= 20) {
				event.setCancelled(true);
				return;
			}
		}

		if (Settings.gameModeSelected == Settings.GameMode.PVP || !Settings.build_withProtectedArea) {
			return;
		}

		if (Settings.gameModeSelected == Settings.GameMode.BUILD) {

			PlayerData pdata = Settings.players.get(player.getName());
			if (pdata == null) {
				return;
			}

			if (pdata.checkBuildPermission(b.getLocation())) {
				return;
			}
			event.setCancelled(true);
		}
	}
}
