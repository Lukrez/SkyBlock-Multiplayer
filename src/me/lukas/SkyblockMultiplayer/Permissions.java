package me.lukas.skyblockmultiplayer;

import org.bukkit.command.CommandSender;

public enum Permissions {
	SKYBLOCK_ONLINE("skyblock.online"),
	SKYBLOCK_OFFLINE("skyblock.offline"),
	SKYBLOCK_RESET("skyblock.reset"),
	SKYBLOCK_NEWISLAND("skyblock.newisland"),
	SKYBLOCK_RELOAD_CONFIG("skyblock.reload.config");

	private final String node;

	private Permissions(String node) {
		this.node = node;
	}

	public boolean has(CommandSender sender) {
		return sender.hasPermission(this.node);
	}
	
	public String toString(){
		return this.node;
	}
}
