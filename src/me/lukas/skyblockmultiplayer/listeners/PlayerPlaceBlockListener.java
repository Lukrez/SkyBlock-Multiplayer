package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.Settings;
import me.lukas.skyblockmultiplayer.Permissions;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlayerPlaceBlockListener implements Listener {

	SkyBlockMultiplayer plugin;

	public PlayerPlaceBlockListener(SkyBlockMultiplayer instance) {
		this.plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block b = event.getBlockPlaced();

		if (!Settings.skyBlockOnline) {
			return;
		}

		if (!player.getWorld().equals(SkyBlockMultiplayer.getSkyBlockWorld())) { // Check if player is in world SkyBlockMultiplayer
			return;
		}

		if (Permissions.SKYBLOCK_BUILD.has(player)) {
			event.setBuild(true);
			return;
		}

		if (b.getLocation().getBlockX() >= -20 && b.getLocation().getBlockX() <= 20) {
			if (b.getLocation().getBlockZ() >= -20 && b.getLocation().getBlockZ() <= 20) {
				event.setCancelled(true);
				return;
			}
		}

		if (Settings.gameModeSelected == Settings.GAMEMODE.PVP || !Settings.build_withProtectedArea) {
			return;
		}

		PlayerInfo pi = Settings.players.get(player.getName());

		PlayerInfo owner = SkyBlockMultiplayer.getOwner(b.getLocation());
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
		}

		event.setCancelled(true);
		return;
	}
}
