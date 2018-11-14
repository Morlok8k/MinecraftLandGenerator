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
import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;
import org.joml.Vector2i;
import org.joml.Vector3i;

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
		try {
			final NBTInputStream input = new NBTInputStream(new FileInputStream(level));
			final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
			input.close();

			final Map<String, Tag> originalData =
					((CompoundTag) originalTopLevelTag.getValue().get("Data")).getValue();
			// This is our map of data. It is an unmodifiable map, for some
			// reason, so we have to make a copy.
			final Map<String, Tag> newData = new LinkedHashMap<>(originalData);
			// .get() a couple of values, just to make sure we're dealing with a
			// valid level file, here. Good for debugging, too.
			final IntTag spawnX = (IntTag) newData.get("SpawnX");
			final IntTag spawnY = (IntTag) newData.get("SpawnY");
			final IntTag spawnZ = (IntTag) newData.get("SpawnZ");

			final Vector3i ret =
					new Vector3i(spawnX.getValue(), spawnY.getValue(), spawnZ.getValue());
			return ret;
		} catch (final ClassCastException ex) {
			throw new IOException("Invalid level format.");
		} catch (final NullPointerException ex) {
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

		try {
			final NBTInputStream input = new NBTInputStream(new FileInputStream(level));
			final CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
			input.close();

			//@formatter:off

            //Note: The Following Information is Old (from 2010), compared to the Data inside a current "level.dat".
            //However, What we look at (SpawnX,Y,Z and RandomSeed) have not changed.

            /* <editor-fold defaultstate="collapsed" desc="structure">
             * Structure:
             *
             *TAG_Compound("Data"): World data.
             *	* TAG_Long("Time"): Stores the current "time of day" in ticks. There are 20 ticks per real-life second, and 24000 ticks per Minecraft day, making the day length 20 minutes. 0 appears to be sunrise, 12000 sunset and 24000 sunrise again.
             *	* TAG_Long("LastPlayed"): Stores the Unix time stamp (in milliseconds) when the player saved the game.
             *	* TAG_Compound("Player"): Player entity information. See Entity Format and Mob Entity Format for details. Has additional elements:
             *		o TAG_List("Inventory"): Each TAG_Compound in this list defines an item the player is carrying, holding, or wearing as armor.
             *			+ TAG_Compound: Inventory item data
             *				# TAG_Short("id"): Item or Block ID.
             * 				# TAG_Short("Damage"): The amount of wear each item has suffered. 0 means undamaged. When the Damage exceeds the item's durability, it breaks and disappears. Only tools and armor accumulate damage normally.
             *				# TAG_Byte("Count"): Number of items stacked in this inventory slot. Any item can be stacked, including tools, armor, and vehicles. Range is 1-255. Values above 127 are not displayed in-game.
             *				# TAG_Byte("Slot"): Indicates which inventory slot this item is in.
             *		o TAG_Int("Score"): Current score, doesn't appear to be implemented yet. Always 0.
             *	* TAG_Int("SpawnX"): X coordinate of the player's spawn position. Default is 0.
             *	* TAG_Int("SpawnY"): Y coordinate of the player's spawn position. Default is 64.
             *	* TAG_Int("SpawnZ"): Z coordinate of the player's spawn position. Default is 0.
             *	* TAG_Byte("SnowCovered"): 1 enables, 0 disables, see Winter Mode
             *	* TAG_Long("SizeOnDisk"): Estimated size of the entire world in bytes.
             *	* TAG_Long("RandomSeed"): Random number providing the Random Seed for the terrain.
             * </editor-fold>
             */

            //@formatter:on

			final Map<String, Tag> originalData =
					((CompoundTag) originalTopLevelTag.getValue().get("Data")).getValue();
			// This is our map of data. It is an unmodifiable map, for some reason, so we have to make a copy.
			final Map<String, Tag> newData = new LinkedHashMap<>(originalData);

			// .get() a couple of values, just to make sure we're dealing with a valid level file, here. Good for debugging, too.
			@SuppressWarnings("unused")
			final IntTag spawnX = (IntTag) newData.get("SpawnX"); // we never use these... Its only here for potential debugging.
			@SuppressWarnings("unused")
			final IntTag spawnY = (IntTag) newData.get("SpawnY"); // but whatever... so I (Morlok8k) suppressed these warnings.
			@SuppressWarnings("unused")
			final IntTag spawnZ = (IntTag) newData.get("SpawnZ"); // I don't want to remove existing code, either by myself (Morlok8k) or Corrodias

			newData.put("SpawnX", new IntTag("SpawnX", xyz.x));		// pulling the data out of the Coordinates,
			newData.put("SpawnY", new IntTag("SpawnY", xyz.y));		// and putting it into our IntTag's
			newData.put("SpawnZ", new IntTag("SpawnZ", xyz.z));

			// Again, we can't modify the data map in the old Tag, so we have to make a new one.
			final CompoundTag newDataTag = new CompoundTag("Data", newData);
			final Map<String, Tag> newTopLevelMap = new HashMap<>(1);
			newTopLevelMap.put("Data", newDataTag);
			final CompoundTag newTopLevelTag = new CompoundTag("", newTopLevelMap);

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
