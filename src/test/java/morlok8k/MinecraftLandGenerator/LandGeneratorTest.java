package morlok8k.MinecraftLandGenerator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.joml.Vector2i;
import org.junit.Test;

import morlok8k.MinecraftLandGenerator.World;

public class LandGeneratorTest {

	static {
		System.setProperty("joml.format", "false");
	}

	@Test
	public void testForceload() {
		int SIZE = 256;
		List<Vector2i> chunks = new ArrayList<>(SIZE * SIZE * 4);
		for (int z = -SIZE; z < SIZE; z++)
			for (int x = -SIZE; x < SIZE; x++)
				chunks.add(new Vector2i(x, z));
		long[] data = World.mapLoadedChunks(chunks);
		List<Vector2i> chunks2 = Arrays.stream(data)
				.mapToObj(l -> new Vector2i((int) (l & 0xFFFFFFFF), (int) (l >>> 32)))
				.collect(Collectors.toList());
		assertEquals(chunks, chunks2);
	}

	@Test
	public void testSpawnpoints() {
		testSpawnpoint(0, 0, 25, 25, 25);
		testSpawnpoint(-100, 10, 500, 400, 25);
		testSpawnpoint(-256, 16, 512, 256, 25);
		testSpawnpoint(-255, 15, 512, 256, 25);
		testSpawnpoint(-256, 16, 511, 255, 25);
		testSpawnpoint(-255, 15, 511, 255, 25);
		testSpawnpoint(25, 24, 50, 49, 25);
		testSpawnpoint(25, 24, 49, 50, 25);
	}

	private static void testSpawnpoint(int startX, int startZ, int width, int height,
			int increment) {
		List<Vector2i> spawn = World.generateSpawnpoints(startX, startZ, width, height, increment);
		int margin = increment / 2;
		Set<Vector2i> coverage = new HashSet<>();
		for (Vector2i v : spawn) {
			for (int z = v.y - margin; z <= v.y + margin; z++)
				for (int x = v.x - margin; x <= v.x + margin; x++)
					coverage.add(new Vector2i(x, z));
		}
		for (int z = startZ; z < startZ + height; z++) {
			for (int x = startX; x < startX + width; x++) {
				assertTrue(
						"Chunk (" + x + ", " + z + ") in (" + startX + ", " + startZ + ", " + width
								+ ", " + height
								+ ") should be covered the spawn chunks, but they were " + spawn,
						coverage.remove(new Vector2i(x, z)));
			}
		}
		assertTrue(coverage.isEmpty());
	}
}
