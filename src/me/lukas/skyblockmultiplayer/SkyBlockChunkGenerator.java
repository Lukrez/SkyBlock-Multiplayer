package me.lukas.skyblockmultiplayer;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

public class SkyBlockChunkGenerator extends ChunkGenerator {

	@Override
	public byte[] generate(World world, Random random, int cx, int cz) {
		byte[] result = new byte[32768];
		for (int i = 0; i < result.length; i++) {
			result[i] = (byte) Material.AIR.getId();
		}
		return result;
	}
}
