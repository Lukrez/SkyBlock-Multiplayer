package me.lukas.skyblockmultiplayer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
	private SkyBlockMultiplayer plugin;

	public PlayerJoin(SkyBlockMultiplayer instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!Data.SKYBLOCK_ONLINE) {
			return;
		}		
		
		PlayerInfo pi = this.plugin.getPlayer(event.getPlayer());
		if (pi == null) {
			return;
		}
		Data.PLAYERS.add(pi);
	}
}
