package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.Settings;
import me.lukas.skyblockmultiplayer.Permissions;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerBreackBlockListener implements Listener {

	SkyBlockMultiplayer plugin;

	public PlayerBreackBlockListener(SkyBlockMultiplayer instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Block b = event.getBlock();

		if (!Settings.skyBlockOnline) {
			return;
		}

		if (!player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) { // Check if player is in world SkyBlockMultiplayer
			return;
		}

		if (Permissions.SKYBLOCK_BUILD.has(player)) {
			return;
		}

		if (b.getLocation().getBlockX() >= -20 && b.getLocation().getBlockX() <= 20) {
			if (b.getLocation().getBlockZ() >= -20 && b.getLocation().getBlockZ() <= 20) {
				event.setCancelled(true);
				return;
			}
		}

		if (Settings.gameModeSelected == Settings.GameMode.PVP || !Settings.build_withProtectedArea) {
			return;
		}

		if (Settings.gameModeSelected == Settings.GameMode.BUILD) {
			PlayerInfo pi = Settings.players.get(player.getName());
			if (pi == null) {
				pi = SkyBlockMultiplayer.getInstance().readPlayerFile(player.getName());
				if (pi == null) {
					return;
				}
			}

			if (SkyBlockMultiplayer.checkBuildPermission(pi, b.getLocation())) {
				return;
			}
			event.setCancelled(true);
			return;

			/*PlayerInfo owner = SkyBlockMultiplayer.getOwner(b.getLocation());
			if (owner == null) {
				if (SkyBlockMultiplayer.canPlayerDoThat(pi, b.getLocation())) {
					return;
				}
				event.setCancelled(true);
				return;
			}

			if (owner.getPlayerName().equalsIgnoreCase(player.getName())) {
				return;
			}

			if (owner.getFriends().contains(player.getName())) {
				return;
			}*/
		}
	}
}
