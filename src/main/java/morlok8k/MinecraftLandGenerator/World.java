package morlok8k.MinecraftLandGenerator;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joml.Vector2i;
import org.joml.Vector3i;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.LongArrayTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;

public class World {

	public static enum Dimension {
		OVERWORLD("."), NETHER("DIM-1"), END("DIM1");
		public final Path path;

		Dimension(String path) {
			this.path = Paths.get(path);
		}
	}

	private static Log log = LogFactory.getLog(World.class);

	public final Path world;
	protected BackupHandler level, chunksOverworld, chunksNether, chunksEnd;
	protected BackupHandler[] chunks;

	public World(Path world) throws FileAlreadyExistsException {
		this.world = Objects.requireNonNull(world);
		level = new BackupHandler(world.resolve("level.dat"));
		chunksOverworld = new BackupHandler(
				world.resolve(Dimension.OVERWORLD.path).resolve("data/chunks.dat"));
		chunksNether =
				new BackupHandler(world.resolve(Dimension.NETHER.path).resolve("data/chunks.dat"));
		chunksEnd = new BackupHandler(world.resolve(Dimension.END.path).resolve("data/chunks.dat"));
		chunks = new BackupHandler[] { chunksOverworld, chunksNether, chunksEnd };
	}

	public void resetChanges() throws IOException {
		level.restore();
		chunksOverworld.restore();
		chunksNether.restore();
		chunksEnd.restore();
	}

	public void setSpawn(Vector2i chunkSpawn) throws IOException {
		setSpawn(new Vector3i(chunkSpawn.x << 4 | 7, 64, chunkSpawn.y << 4 | 8));
	}

	public void setLoadedChunks(List<Vector2i> chunks, Dimension dimension) throws IOException {
		BackupHandler handler = this.chunks[dimension.ordinal()];
		handler.backup();
		try (NBTOutputStream out = new NBTOutputStream(Files.newOutputStream(handler.file))) {
			CompoundMap dataMap = new CompoundMap();
			dataMap.put(new LongArrayTag("Forced",
					chunks.stream().mapToLong(v -> ((long) v.y << 32) | v.x).toArray()));
			CompoundMap rootMap = new CompoundMap();
			rootMap.put(new CompoundTag("data", dataMap));
			out.writeTag(new CompoundTag("", rootMap));
			out.flush();
		}
	}

	/**
	 * Changes the spawn point in the given Alpha/Beta level to the given coordinates.<br>
	 * Note that, in Minecraft levels, the Y coordinate is height.<br>
	 * (We picture maps from above, but the game was made from a different perspective)
	 * 
	 * @param level
	 *            the level file to change the spawn point in
	 * @param xyz
	 *            the Coordinates of the spawn point
	 * @throws IOException
	 *             if there are any problems reading/writing the file
	 * @author Corrodias
	 */
	public void setSpawn(final Vector3i xyz) throws IOException {
		level.backup();
		log.debug("Setting spawn to " + xyz);
		// TODO clean this up even more
		try (NBTInputStream input =
				new NBTInputStream(Files.newInputStream(world.resolve("level.dat")));) {
			final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
			input.close();

			final Map<String, Tag<?>> originalData =
					((CompoundTag) originalTopLevelTag.getValue().get("Data")).getValue();
			// This is our map of data. It is an unmodifiable map, for some reason, so we have to make a copy.
			final Map<String, Tag<?>> newData = new LinkedHashMap<>(originalData);

			newData.put("SpawnX", new IntTag("SpawnX", xyz.x));		// pulling the data out of the Coordinates,
			newData.put("SpawnY", new IntTag("SpawnY", xyz.y));		// and putting it into our IntTag's
			newData.put("SpawnZ", new IntTag("SpawnZ", xyz.z));

			// Again, we can't modify the data map in the old Tag, so we have to make a new one.
			final CompoundTag newDataTag = new CompoundTag("Data", new CompoundMap(newData));
			final Map<String, Tag<?>> newTopLevelMap = new HashMap<>(1);
			newTopLevelMap.put("Data", newDataTag);
			final CompoundTag newTopLevelTag = new CompoundTag("", new CompoundMap(newTopLevelMap));

			try (NBTOutputStream output =
					new NBTOutputStream(Files.newOutputStream(world.resolve("level.dat")));) {
				output.writeTag(newTopLevelTag);
				output.close();
			}
		} catch (final ClassCastException ex) {
			throw new IOException("Invalid level format.");
		} catch (final NullPointerException ex) {
			throw new IOException("Invalid level format.");
		}
	}

	/**
	 * @param increment
	 *            Maximum number of chunks between two spawn points, horizontally or vertically
	 * @param margin
	 *            The radius to each side that will be generated by the server (Not the diameter!)
	 */
	public static List<Vector2i> generateSpawnpoints(int startX, int startZ, int width, int height,
			int increment) {
		int margin = increment / 2;
		if (width < margin || height < margin)
			throw new IllegalArgumentException("Width and height must both be at least " + increment
					+ ", but are " + width + " and " + height);
		List<Integer> xPoints =
				generateLinearSpawnpoints(startX + margin, width - increment, increment);
		log.debug("X grid: " + xPoints);
		List<Integer> zPoints =
				generateLinearSpawnpoints(startZ + margin, height - increment, increment);
		log.debug("Z grid: " + zPoints);
		List<Vector2i> spawnPoints = new ArrayList<>(xPoints.size() * zPoints.size());
		for (int x : xPoints)
			for (int z : zPoints)
				spawnPoints.add(new Vector2i(x, z));
		return spawnPoints;
	}

	private static List<Integer> generateLinearSpawnpoints(int start, int length, int maxStep) {
		int stepCount = (int) Math.ceil((double) length / maxStep);
		double realStep = length / stepCount;
		return IntStream.rangeClosed(0, stepCount).mapToObj(i -> start + (int) (realStep * i))
				.collect(Collectors.toList());
	}
}
