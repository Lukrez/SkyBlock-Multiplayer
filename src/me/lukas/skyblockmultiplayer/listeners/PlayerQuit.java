package me.lukas.skyblockmultiplayer.listeners;

import me.lukas.skyblockmultiplayer.PlayerData;
import me.lukas.skyblockmultiplayer.PlayerInfo;
import me.lukas.skyblockmultiplayer.Settings;
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

		if (!player.getWorld().getName().equals(SkyBlockMultiplayer.getSkyBlockWorld().getName())) {
			return;
		}

		PlayerData pdata = Settings.players.get(player.getName());

		if (pdata.getIslandLocation() == null) {
			// check, if friends are online
			boolean foundOnlineFriend = false;
			for (Player online : SkyBlockMultiplayer.getInstance().getServer().getOnlinePlayers()){
				if (pdata.getFriends().containsKey(online.getName())){
					foundOnlineFriend = true;
					break;
				}
			}
			if (!foundOnlineFriend)
				Settings.players.remove(player.getName());
		}
	}
}
