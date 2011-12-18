package me.lukas.MultipleSurvivalIslands;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

public class WorldIslandsdChunkGenerator extends ChunkGenerator {

	@Override
	public byte[] generate(World world, Random random, int cx, int cz) {
		byte[] result = new byte[32768];

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int height = getHeight(world, cx + x * 0.0625, cz + z * 0.0625, 2) + 60;
                for (int y = 0; y < height; y++) {
                    result[(x * 16 + z) * 128 + y] = (byte)Material.AIR.getId();
                }
            }
        }

        return result;
	}

	private int getHeight(World world, double x, double y, int variance) {
        NoiseGenerator gen = getGenerator(world);

        double result = gen.noise(x, y);
        result *= variance;
        return NoiseGenerator.floor(result);
    }	
	
	private NoiseGenerator generator;

    private NoiseGenerator getGenerator(World world) {
        if (generator == null) {
            generator = new SimplexNoiseGenerator(world);
        }

        return generator;
    }
}
