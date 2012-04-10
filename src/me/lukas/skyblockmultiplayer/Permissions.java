package me.lukas.skyblockmultiplayer;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public enum Permissions {
	SKYBLOCK_SET("skyblock.set"),
	SKYBLOCK_RESET("skyblock.reset"),
	SKYBLOCK_NEWISLAND("skyblock.newisland"),
	SKYBLOCK_RELOAD("skyblock.reload"),
	SKYBLOCK_JOIN("skyblock.join"),
	SKYBLOCK_BUILD("skyblock.build"),
	SKYBLOCK_OWNER_SET("skyblock.owner.set"),
	SKYBLOCK_MESSAGE("skyblock.message");

	private final String node;

	private Permissions(String node) {
		this.node = node;
	}

	public boolean has(CommandSender sender) {
		return sender.hasPermission(this.node) || sender.isOp();
	}

	public boolean has(Player player) {
		return player.hasPermission(this.node) || player.isOp();
	}
}
