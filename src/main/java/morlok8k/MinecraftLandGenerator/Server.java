/*
 * ####################################################################### # DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE # # Version 2, December 2004 # # # # Copyright (C) 2004 Sam Hocevar
 * <sam@hocevar.net> # # # # Everyone is permitted to copy and distribute verbatim or modified # # copies of this license document, and changing it is allowed as long # # as the name is changed. # # #
 * # DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE # # TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION # # # # 0. You just DO WHAT THE FUCK YOU WANT TO. # # #
 * #######################################################################
 */

package morlok8k.MinecraftLandGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joml.Vector2i;

/**
 * 
 * @author morlok8k
 */
public class Server {

	private static Log log = LogFactory.getLog(Server.class);

	protected final ProcessBuilder builder;
	protected final Path workDir;
	protected final boolean debugServer;

	public Server(boolean debugServer, String[] javaOpts, Path serverFile) {
		this.debugServer = debugServer;
		if (!Files.exists(serverFile)) throw new IllegalArgumentException(serverFile.toString()
				+ " must be an existing file pointing to the minecraft server");
		List<String> opts = new ArrayList<>(
				Arrays.asList(javaOpts != null ? javaOpts : new String[] { "java", "-jar" }));
		opts.add(serverFile.toString());
		opts.add("nogui");
		builder = new ProcessBuilder(opts);
		builder.redirectErrorStream(true);
		workDir = serverFile.getParent();
		builder.directory(workDir.toFile());
	}

	public World initWorld(Path worldPath) throws IOException, InterruptedException {
		if (worldPath != null)
			setWorld(worldPath);
		else worldPath = getWorld();
		if (worldPath == null || !Files.exists(worldPath)) {
			log.warn(
					"No world was specified or the world at the given path does not exist. Starting the server once to create one...");
			runMinecraft();
			worldPath = getWorld();
			if (!worldPath.isAbsolute()) worldPath = workDir.resolve(worldPath);
		}
		if (worldPath == null || !Files.exists(worldPath))
			throw new NoSuchFileException(String.valueOf(worldPath));
		log.debug("Using world path " + worldPath);
		return new World(worldPath);
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
		if (!props.containsKey("level-name")) return null;
		log.info("Found level in server.properties: " + props.getProperty("level-name"));
		Path p = Paths.get(props.getProperty("level-name"));
		if (!p.isAbsolute()) p = workDir.resolve(p);
		return p;
	}

	public void restoreWorld() throws IOException {
		Path propsFile = workDir.resolve("server.properties");
		if (Files.exists(propsFile.resolveSibling("server.properties.bak")))
			Files.move(propsFile.resolveSibling("server.properties.bak"), propsFile,
					StandardCopyOption.REPLACE_EXISTING);
	}

	public void runMinecraft(World world, List<Vector2i> spawnpoints) {
		log.debug("All spawn points: " + spawnpoints);
		for (int i = 0; i < spawnpoints.size(); i++) {
			Vector2i spawn = spawnpoints.get(i);
			try {
				log.info("Processing " + i + "/" + spawnpoints.size() + ", spawn point " + spawn);
				world.setSpawn(spawn);
				runMinecraft();
			} catch (IOException | InterruptedException e) {
				log.warn("Could not process spawn point " + spawn
						+ " this part of the world don't be generated", e);
			}
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
		log.debug("Setting EULA");
		Files.write(workDir.resolve("eula.txt"), "eula=true".getBytes(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
		log.info("Starting server");
		final Process process = builder.start();

		final BufferedReader pOut =
				new BufferedReader(new InputStreamReader(process.getInputStream()));
		for (String line = pOut.readLine(); line != null; line = pOut.readLine()) {
			line = line.trim();
			if (debugServer) System.out.println(line);

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
