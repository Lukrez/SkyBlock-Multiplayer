package me.lukas.skyblockmultiplayer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteract implements Listener {

	private SkyBlockMultiplayer plugin;

	public PlayerInteract(SkyBlockMultiplayer instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		Block b = event.getClickedBlock();
		ItemStack item = event.getItem();

		if (item == null || action != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		if (!item.getType().equals(Material.STICK)) {
			return;
		}
		
		

		PlayerInfo owner = this.getOwner(b.getLocation());
		if (owner == null) {
			player.sendMessage("Free area or borders.");
			return;
		}

		player.sendMessage("Owner: " + owner.getPlayerName());
		String list = "";
		for (int i = 0; i < owner.getFriends().size(); i++) {
			if (i != 0) {
				list += ", ";
			}
			list += owner.getFriends().get(i);
		}
		player.sendMessage("Friends: " + list);
		return;
	}

	private PlayerInfo getOwner(Location l) {
		for (PlayerInfo pi : Data.PLAYERS.values()) {

			int islandX = pi.getIslandLocation().getBlockX();
			int islandZ = pi.getIslandLocation().getBlockZ();

			int blockX = l.getBlockX();
			int blockZ = l.getBlockZ();

			int dist = (Data.ISLAND_DISTANCE / 2) - 3;

			if (islandX + dist >= blockX && islandX - dist <= blockX) {
				if (islandZ + dist >= blockZ && islandZ - dist <= blockZ) {
					return pi;
				}
			}
		}
		return null;
	}
}
