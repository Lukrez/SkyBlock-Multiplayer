package me.lukas.skyblockmultiplayer;

import org.bukkit.entity.Player;
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
		Player player = event.getPlayer();

		if (!Data.SKYBLOCK_ONLINE) {
			return;
		}

		PlayerInfo pi = this.plugin.readPlayerFile(player.getName());
		if (pi == null) {
			Data.PLAYERS.put(player.getName(), new PlayerInfo(player));
			return;
		}
		Data.PLAYERS.put(player.getName(), pi);
	}
}
