/*
 * ####################################################################### # DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE # # Version 2, December 2004 # # # # Copyright (C) 2004 Sam Hocevar
 * <sam@hocevar.net> # # # # Everyone is permitted to copy and distribute verbatim or modified # # copies of this license document, and changing it is allowed as long # # as the name is changed. # # #
 * # DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE # # TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION # # # # 0. You just DO WHAT THE FUCK YOU WANT TO. # # #
 * #######################################################################
 */

package morlok8k.MinecraftLandGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

/**
 * 
 * @author morlok8k
 */
public class Server {

	private static Log log = LogFactory.getLog(Server.class);

	protected final ProcessBuilder builder;
	protected final Path workDir;

	public Server(String[] javaOpts, Path serverFile) {
		List<String> opts = new ArrayList<>(
				Arrays.asList(javaOpts != null ? javaOpts : new String[] { "java", "-jar" }));
		opts.add(serverFile.toString());
		opts.add("nogui");
		builder = new ProcessBuilder(opts);
		builder.redirectErrorStream(true);
		workDir = serverFile.getParent();
		builder.directory(workDir.toFile());
	}

	public void setWorld(Path worldPath) throws IOException {
		Path propsFile = workDir.resolve("server.properties");
		if (!Files.exists(propsFile)) {
			Files.write(propsFile, "level-name=".concat(propsFile.toString()).getBytes());
		} else {
			/* Make a backup first*/
			Files.copy(propsFile, propsFile.resolveSibling("server.properties.bak"));
			Properties props = new Properties();
			props.load(Files.newInputStream(propsFile));
			props.put("level-name", worldPath.toString());
			props.store(Files.newOutputStream(propsFile), null);
		}
	}

	public Path getWorld() throws IOException {
		Path propsFile = workDir.resolve("server.properties");
		Properties props = new Properties();
		if (!Files.exists(propsFile)) return null;
		props.load(Files.newInputStream(propsFile));
		return Paths.get(props.getProperty("level-name"));
	}

	public void restoreWorld() throws IOException {
		Path propsFile = workDir.resolve("server.properties");
		Files.move(propsFile.resolveSibling("server.properties.bak"), propsFile,
				StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * @param level
	 * @return
	 * @throws IOException
	 * @author Corrodias
	 */
	protected static Vector3i getSpawn(final File level) throws IOException {
		try (NBTInputStream input = new NBTInputStream(new FileInputStream(level));) {
			final CompoundTag levelTag = (CompoundTag) input.readTag();
			final Map<String, Tag<?>> levelData =
					((CompoundTag) levelTag.getValue().get("Data")).getValue();

			final IntTag spawnX = (IntTag) levelData.get("SpawnX");
			final IntTag spawnY = (IntTag) levelData.get("SpawnY");
			final IntTag spawnZ = (IntTag) levelData.get("SpawnZ");

			return new Vector3i(spawnX.getValue(), spawnY.getValue(), spawnZ.getValue());
		} catch (final ClassCastException | NullPointerException ex) {
			throw new IOException("Invalid level format.");
		}
	}

	protected static void setSpawn(Path world, Vector2i chunkSpawn) throws IOException {
		setSpawn(world.resolve("level.dat").toFile(),
				new Vector3i(chunkSpawn.x << 4 | 7, 64, chunkSpawn.y << 4 | 8));
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
	protected static void setSpawn(final File level, final Vector3i xyz) throws IOException {
		// TODO clean this up even more
		try (NBTInputStream input = new NBTInputStream(new FileInputStream(level));) {
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

			final NBTOutputStream output = new NBTOutputStream(new FileOutputStream(level));
			output.writeTag(newTopLevelTag);
			output.close();
		} catch (final ClassCastException ex) {
			throw new IOException("Invalid level format.");
		} catch (final NullPointerException ex) {
			throw new IOException("Invalid level format.");
		}
	}

	/**
	 * Starts the process in the given ProcessBuilder, monitors its output for a "Done" message, and sends it a "stop" message.
	 * If "verbose" is true, the process's output will also be printed to the console.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @author Corrodias, Morlok8k, piegames
	 */
	public void runMinecraft() throws IOException, InterruptedException {
		log.info("Starting server");
		final Process process = builder.start();

		final BufferedReader pOut =
				new BufferedReader(new InputStreamReader(process.getInputStream()));
		for (String line = pOut.readLine(); line != null; line = pOut.readLine()) {
			line = line.trim();
			if (log.isDebugEnabled()) log.debug(line);

			if (line.contains("Done")) {
				PrintStream out = new PrintStream(process.getOutputStream());

				log.info("Stopping server...");
				out.println("save-all");
				out.flush();
				out.println("stop");
				out.flush();
			}
		}

		/* The process should have stopped by now. */
		int exit = process.waitFor();
		if (exit != 0)
			log.warn("Process stopped with non-zero exit code (" + exit + ")");
		else log.info("Stopped server");
	}
}
