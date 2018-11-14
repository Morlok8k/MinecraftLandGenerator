package morlok8k.MinecraftLandGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joml.Vector2i;
import org.joml.Vector3i;

import com.flowpowered.nbt.CompoundMap;
import com.flowpowered.nbt.CompoundTag;
import com.flowpowered.nbt.IntTag;
import com.flowpowered.nbt.Tag;
import com.flowpowered.nbt.stream.NBTInputStream;
import com.flowpowered.nbt.stream.NBTOutputStream;

public class World {
	private static Log log = LogFactory.getLog(World.class);

	public final Path world;

	public World(Path world) throws IOException {
		this.world = Objects.requireNonNull(world);
		Files.copy(world.resolve("level.dat"), world.resolve("level.dat.bak"));
	}

	public void resetSpawn() throws IOException {
		if (Files.exists(world.resolve("level.dat.bak"))) Files.move(world.resolve("level.dat.bak"),
				world.resolve("level.dat"), StandardCopyOption.REPLACE_EXISTING);
	}

	public void setSpawn(Vector2i chunkSpawn) throws IOException {
		setSpawn(new Vector3i(chunkSpawn.x << 4 | 7, 64, chunkSpawn.y << 4 | 8));
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
}
