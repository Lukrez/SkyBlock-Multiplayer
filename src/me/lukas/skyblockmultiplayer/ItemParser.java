package me.lukas.skyblockmultiplayer;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ItemParser {

	public static String parseItemStackToString(ItemStack item) {
		if (item == null) {
			return "";

		}

		String s = "";
		s += "id:" + item.getTypeId() + ";";
		s += "amount:" + item.getAmount() + ";";
		s += "durab:" + item.getDurability() + ";";
		s += "data:" + item.getData().getData() + ";";
		// Add enchantments
		if (item.getEnchantments().size() > 0) {
			s += "ench:";
			for (Enchantment e : item.getEnchantments().keySet()) {
				s += "eid#" + e.getId() + " ";
				s += "elevel#" + item.getEnchantments().get(e) + " ";
			}
		}
		return s.trim();
	}

	public static ItemStack getItemStackfromString(String s) {
		if (s.equalsIgnoreCase("")) {
			return null;
		}
		ItemStack x = new ItemStack(1);

		for (String thing : s.split(";")) {
			String[] sp = thing.split(":");
			if (sp.length != 2)
				SkyBlockMultiplayer.getInstance().log.warning("error, wrong type size");
			String name = sp[0];

			if (name.equals("id")) {
				x.setTypeId(Integer.parseInt(sp[1]));
			} else if (name.equals("amount")) {
				x.setAmount(Integer.parseInt(sp[1]));
			} else if (name.equals("durab")) {
				x.setDurability((short) Integer.parseInt(sp[1]));
			} else if (name.equals("data")) {
				x.getData().setData((byte) Integer.parseInt(sp[1]));
			} else if (name.equals("ench")) {
				int enchId = 0;
				int level = 0;
				for (String enchantment : sp[1].split(" ")) {
					String[] prop = enchantment.split("#");
					if (prop.length != 2)
						SkyBlockMultiplayer.getInstance().log.warning("error, wrong enchantmenttype length");
					if (prop[0].equals("eid")) {
						enchId = Integer.parseInt(prop[1]);
					} else if (prop[0].equals("elevel")) {
						level = Integer.parseInt(prop[1]);
						x.addUnsafeEnchantment(Enchantment.getById(enchId), level);
					}
				}

			} else {
				SkyBlockMultiplayer.getInstance().log.warning("error, unknown itemvalue");
			}
		}
		return x;
	}
}
