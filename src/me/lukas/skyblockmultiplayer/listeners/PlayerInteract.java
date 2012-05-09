package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.CreateNewIsland;
import me.lukas.skyblockmultiplayer.Language;
import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.Settings;
import me.lukas.skyblockmultiplayer.Permissions;
import me.lukas.skyblockmultiplayer.SkyBlockMultiplayer;

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

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		Block b = event.getClickedBlock();
		ItemStack item = event.getItem();

		if (!Settings.skyBlockOnline) {
			return;
		}

		if (!player.getWorld().equals(SkyBlockMultiplayer.getSkyBlockWorld())) {
			return;
		}

		PlayerInfo pi = Settings.players.get(player.getName());
		if (pi == null) {
			pi = SkyBlockMultiplayer.getInstance().readPlayerFile(player.getName());
		}
		PlayerInfo owner = null;

		if (b == null) {
			owner = SkyBlockMultiplayer.getOwner(player.getLocation());
		} else {
			owner = SkyBlockMultiplayer.getOwner(b.getLocation());
		}

		if (item != null) {
			if (Settings.gameModeSelected == Settings.GameMode.BUILD) {
				if (item.getType().equals(Material.ENDER_PEARL)) {
					if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK || action == Action.LEFT_CLICK_BLOCK) {
						if (Settings.build_allowEnderpearl) {
							if (owner.getPlayerName().equalsIgnoreCase(player.getName())) {
								return;
							}
							if (owner.getFriends().contains(player.getName())) {
								return;
							}
						}
						if (Settings.build_withProtectedArea) {
							event.setCancelled(true);
							return;
						}
						return;
					}
				}
			}

			if (item.getType().equals(Material.STICK)) {
				if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
					if (owner == null) {
						int i = -1;
						if (b == null) {
							i = CreateNewIsland.getIslandNumber(player.getLocation());
						} else {
							i = CreateNewIsland.getIslandNumber(b.getLocation());
						}

						if (i == 0) {
							if (this.plugin.locationIsOnTower(player.getLocation())) {
								player.sendMessage(this.plugin.pName + Language.MSGS_AREA_OF_SPAWN_TOWER.sentence);
								event.setCancelled(true);
								return;
							}

							player.sendMessage(Language.MSGS_AREA_BORDERS.sentence);
							event.setCancelled(true);
							return;
						}
						player.sendMessage("Island number: " + i);
						event.setCancelled(true);
						return;
					} else {
						int i = -1;
						if (b == null) {
							i = CreateNewIsland.getIslandNumber(player.getLocation());
						} else {
							i = CreateNewIsland.getIslandNumber(b.getLocation());
						}

						if (i == -1) {
							player.sendMessage(Language.MSGS_AREA_BORDERS.sentence);
							event.setCancelled(true);
							return;
						}
						player.sendMessage("Island number: " + i);
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
					event.setCancelled(true);
					return;
				}
			}
		}

		if (event.getPlayer().getLocation().getBlockX() >= -20 && event.getPlayer().getLocation().getBlockX() <= 20) {
			if (event.getPlayer().getLocation().getBlockZ() >= -20 && event.getPlayer().getLocation().getBlockZ() <= 20) {
				if (Permissions.SKYBLOCK_BUILD.has(player)) {
					return;
				}
				event.setCancelled(true);
				return;
			}
		}

		if (Permissions.SKYBLOCK_BUILD.has(player)) {
			return;
		}

		if (Settings.gameModeSelected == Settings.GameMode.PVP || !Settings.build_withProtectedArea) {
			return;
		}

		if (action == Action.LEFT_CLICK_BLOCK || action == Action.RIGHT_CLICK_BLOCK || action == Action.PHYSICAL) {
			if (action == Action.RIGHT_CLICK_BLOCK && item != null && b == null) { // this let allow the event BlockPlace calling
				return;
			}

			if (owner == null) {
				if (b == null) {
					if (SkyBlockMultiplayer.canPlayerDoThat(pi, player.getLocation())) {
						return;
					}
					event.setCancelled(true);
					return;
				}
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
}
