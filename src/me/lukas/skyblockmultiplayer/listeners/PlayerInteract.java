package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.Settings;
import me.lukas.skyblockmultiplayer.Language;
import me.lukas.skyblockmultiplayer.Permissions;
import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteract implements Listener {

	SkyBlockMultiplayer plugin;

	public PlayerInteract(SkyBlockMultiplayer instance) {
		this.plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		Block b = event.getClickedBlock();
		ItemStack item = event.getItem();

		player.sendMessage(this.plugin.getStringLocation(b.getLocation()));

		if (!Settings.skyBlockOnline) {
			return;
		}

		if (!player.getWorld().equals(SkyBlockMultiplayer.getSkyBlockWorld())) {
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

		PlayerInfo pi = Settings.PLAYERS.get(player.getName());
		PlayerInfo owner = null;

		if (b == null) {
			owner = this.getOwner(player.getLocation());
		} else {
			owner = this.getOwner(b.getLocation());
		}

		if (item != null) {
			if (Settings.gameModeSelected == Settings.GAMEMODE.BUILD) {
				if (item.getType().equals(Material.ENDER_PEARL)) {
					if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
						if (Settings.build_allowEnderpearl) {
							return;
						}
						event.setCancelled(true);
						return;
					}
					return;
				}
			}

			if (item.getType().equals(Material.STICK)) {
				if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
					if (owner == null) {
						player.sendMessage(Language.MSGS_AREABORDERS.sentence);
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

			if (Settings.gameModeSelected == Settings.GAMEMODE.PVP) {
				return;
			}
		}

		if (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK || action == Action.PHYSICAL) {
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

			if (owner.getPlayerName().equalsIgnoreCase(player.getName())) {
				return;
			}

			/*if (this.IsNeighborAFriend(player, b.getLocation())) {
				return;
			}*/
			if (owner.getFriends().contains(player.getName())) {
				return;
			}

			event.setCancelled(true);
			return;
		}
	}

	private PlayerInfo getOwner(Location l) {
		for (PlayerInfo pi : Settings.PLAYERS.values()) {
			if (pi != null) {
				if (pi.getIslandLocation() != null) {
					int islandX = pi.getIslandLocation().getBlockX();
					int islandZ = pi.getIslandLocation().getBlockZ();

					int blockX = l.getBlockX();
					int blockZ = l.getBlockZ();

					int dist = (Settings.distanceIslands / 2) - 3;

					if (islandX + dist >= blockX && islandX - dist <= blockX) {
						if (islandZ + dist >= blockZ && islandZ - dist <= blockZ) {
							return pi;
						}
					}
				}
			}
		}
		return null;
	}

	private boolean IsNeighborAFriend(Player player, Location l) {
		System.out.println("called...");
		for (PlayerInfo pi : Settings.PLAYERS.values()) {
			if (pi != null) {
				if (pi.getIslandLocation() != null) {
					int islandX = pi.getIslandLocation().getBlockX();
					int islandZ = pi.getIslandLocation().getBlockZ();

					int blockX = l.getBlockX();
					int blockZ = l.getBlockZ();

					int dist = (Settings.distanceIslands / 2) - 3;

					for (int x = -6; x <= 6; x++) {
						if (islandX + dist + x >= blockX && islandX - dist - x <= blockX) {
							for (int z = -6; z <= 6; z++) {
								if (islandZ + dist + z >= blockZ && islandZ - dist - z <= blockZ) {
									if (pi.getFriends().contains(player.getName())) {
										return true;
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	private boolean canPlayerDoThat(PlayerInfo pi, Location l) {
		if (pi == null) {
			return false;
		}
		int islandX = pi.getIslandLocation().getBlockX();
		int islandZ = pi.getIslandLocation().getBlockZ();

		int blockX = l.getBlockX();
		int blockZ = l.getBlockZ();

		int dist = (Settings.distanceIslands / 2) - 3;

		if (islandX + dist >= blockX && islandX - dist <= blockX) {
			if (islandZ + dist >= blockZ && islandZ - dist <= blockZ) {
				return true;
			}
		}
		return false;
	}
}
