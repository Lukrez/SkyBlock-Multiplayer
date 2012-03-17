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

		if (!player.getWorld().equals(SkyBlockMultiplayer.getSkyBlockWorld())) {
			return;
		}

		if (item == null) {
			return;
		}

		if (item.getType().equals(Material.STICK)) {
			if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
				PlayerInfo owner = null;
				if (b == null) {
					owner = this.getOwner(player.getLocation());
				} else {
					owner = this.getOwner(b.getLocation());
				}

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
		}

		PlayerInfo pi = Data.PLAYERS.get(player.getName());
		PlayerInfo owner = Data.PLAYERS.get(player.getName());

		if (owner == null) {
			if (b == null) {
				if (this.canPlayerDoThat(pi, player.getLocation())) {
					return;
				}
				event.setCancelled(true);
				return;
			}
			if (this.canPlayerDoThat(pi, b.getLocation())) {
				return;
			}
			event.setCancelled(true);
			return;
		}
		if (owner.getFriends().contains(player.getName())) {
			return;
		}
		event.setCancelled(true);
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

	private boolean canPlayerDoThat(PlayerInfo pi, Location l) {
		int islandX = pi.getIslandLocation().getBlockX();
		int islandZ = pi.getIslandLocation().getBlockZ();

		int blockX = l.getBlockX();
		int blockZ = l.getBlockZ();

		int dist = (Data.ISLAND_DISTANCE / 2) - 3;

		if (islandX + dist >= blockX && islandX - dist <= blockX) {
			if (islandZ + dist >= blockZ && islandZ - dist <= blockZ) {
				return true;
			}
		}
		return false;
	}
}
