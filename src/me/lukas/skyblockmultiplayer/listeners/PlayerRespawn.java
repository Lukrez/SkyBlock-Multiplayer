package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.PlayerData;
import me.lukas.skyblockmultiplayer.Settings;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawn implements Listener {

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();

		if (!player.getWorld().getName().equalsIgnoreCase(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			return;
		}

		PlayerData pdata = Settings.players.get(player.getName());
		if (pdata == null || pdata.getIslandLocation() == null) {
			event.setRespawnLocation(player.getWorld().getSpawnLocation());
			return;
		}

		if (SkyBlockMultiplayer.getInstance().playerIsOnTower(player) || Settings.gameModeSelected == Settings.GameMode.PVP || pdata.getIslandLocation() == null) {
			player.getInventory().setContents(pdata.getOldInventory());
			player.getInventory().setArmorContents(pdata.getOldArmor());
			player.setExp(pdata.getOldExp());
			player.setLevel(pdata.getOldLevel());
			player.setFoodLevel(pdata.getOldFood());
			player.setHealth(player.getMaxHealth());

			event.setRespawnLocation(player.getWorld().getSpawnLocation());
			return;
		}

		if (Settings.gameModeSelected == Settings.GameMode.BUILD && Settings.build_respawnWithInventory) {
			if (!SkyBlockMultiplayer.getInstance().playerIsOnTower(player) && pdata.getIslandLocation() != null) {
				player.getInventory().setContents(pdata.getIslandInventory());
				player.getInventory().setArmorContents(pdata.getIslandArmor());
				player.setExp(pdata.getIslandExp());
				player.setLevel(pdata.getIslandLevel());
				player.setFoodLevel(20);
				player.setHealth(player.getMaxHealth());

				// check if bedrock is still there
				int px = pdata.getIslandLocation().getBlockX();
				int py = pdata.getIslandLocation().getBlockY() - 3;
				int pz = pdata.getIslandLocation().getBlockZ();

				if (!new Location(player.getWorld(), px, py, pz).getBlock().getType().equals(Material.BEDROCK)) {
					event.setRespawnLocation(player.getWorld().getSpawnLocation());

					player.getInventory().setContents(pdata.getOldInventory());
					player.getInventory().setArmorContents(pdata.getOldArmor());
					player.setExp(pdata.getOldExp());
					player.setLevel(pdata.getOldLevel());
					player.setFoodLevel(pdata.getOldFood());
					player.setHealth(player.getMaxHealth());
					return;
				}

				if (pdata.getHomeLocation() == null) {
					SkyBlockMultiplayer.getInstance().removeCreatures(pdata.getIslandLocation());
					event.setRespawnLocation(pdata.getIslandLocation());
				} else {
					Location homeSweetHome = SkyBlockMultiplayer.getInstance().getSafeHomeLocation(pdata);
					if (homeSweetHome == null) { // if null, island is missing and home location returns no safe block
						player.sendMessage("Cannot teleport to your home location, your island is probably missing.");
						return;
					}

					SkyBlockMultiplayer.getInstance().removeCreatures(homeSweetHome);
					event.setRespawnLocation(homeSweetHome);
				}
				return;
			}
		}

		if (Settings.gameModeSelected == Settings.GameMode.BUILD) {
			if (!SkyBlockMultiplayer.getInstance().playerIsOnTower(player)) {
				if (pdata.getHomeLocation() == null) {
					event.setRespawnLocation(pdata.getIslandLocation());
					Location homeSweetHome = SkyBlockMultiplayer.getInstance().getSafeHomeLocation(pdata);
					if (homeSweetHome == null) { // if null, island is missing and home location returns no safe block
						player.sendMessage("Cannot teleport to your home location, your island is probably missing.");
						return;
					}

					SkyBlockMultiplayer.getInstance().removeCreatures(homeSweetHome);
					event.setRespawnLocation(homeSweetHome);
				}
			}
		}
	}
}
