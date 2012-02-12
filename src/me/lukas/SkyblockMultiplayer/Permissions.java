package me.lukas.skyblockmultiplayer;

import org.bukkit.command.CommandSender;

public enum Permissions {
	SKYBLOCK_SET("skyblock.set"),
	SKYBLOCK_RESET("skyblock.reset"),
	SKYBLOCK_NEWISLAND("skyblock.newisland"),
	SKYBLOCK_RELOAD("skyblock.reload");

	private final String node;

	private Permissions(String node) {
		this.node = node;
	}

	public boolean has(CommandSender sender) {
		return sender.hasPermission(this.node) || sender.isOp();
	}
}
