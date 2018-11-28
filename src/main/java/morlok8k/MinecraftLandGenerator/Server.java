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
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Start and manipulate a Minecraft server.
 * 
 * @author morlok8k, piegames
 */
public class Server {

	private static Log log = LogFactory.getLog(Server.class);

	protected final ProcessBuilder builder;
	protected final Path workDir;
	protected BackupHandler serverProperties;

	/**
	 * @param serverFile
	 *            the path to the server.jar. The folder it is in will be used as server work directory.
	 * @param javaOpts
	 *            the command line to start the server. To actually launch it, the path to the server file and "nogui" will be appended.
	 *            The default value is ["java", "-jar"]. Use this to specify JVM options (like more RAM etc.) or to enforce the usage of a specific java version.
	 * @throws FileAlreadyExistsException
	 *             if there is an old backup of the server.properties
	 * @throws NoSuchFileException
	 *             if the server jar file does not exist
	 */
	public Server(Path serverFile, String[] javaOpts)
			throws FileAlreadyExistsException, NoSuchFileException {
		if (!Files.exists(serverFile)) throw new NoSuchFileException(serverFile.toString());
		workDir = serverFile.getParent();
		serverProperties = new BackupHandler(workDir.resolve("server.properties"));

		List<String> opts = new ArrayList<>(
				Arrays.asList(javaOpts != null ? javaOpts : new String[] { "java", "-jar" }));
		opts.add(serverFile.toString());
		opts.add("nogui");
		builder = new ProcessBuilder(opts);
		builder.redirectErrorStream(true);
		builder.directory(workDir.toFile());
	}

	/**
	 * Initialize the world. This will do the following:
	 * <ul>
	 * <li>If {@code worldPath} is not {@code null}, store it in the {@code server.properties}, creating it when needed</li>
	 * <li>If {@code worldPath} is {@code null}, read the {@code server.properties} for one</li>
	 * <li>If {@code worldPath} is {@code null} and no path could be retrieved from the {@code server.properties}, start the server to generate a new world and use its path</li>
	 * <li>If there is still no valid path pointing to an existing folder, throw an {@link NoSuchFileException}</li>
	 * </ul>
	 * 
	 * @param worldPath
	 *            the path to the world to generate. Should be relative to the server's folder (because the server can't handle absolute paths). May be {@code null}.
	 * @see #runMinecraft(boolean)
	 * @throws NoSuchFileException
	 *             if we fail to get a valid world folder using multiple methods
	 * @return A {@link World} object representing the world that will be loaded from the server by {@link #runMinecraft(boolean)}
	 * @author piegames
	 */
	public World initWorld(Path worldPath, boolean debugServer)
			throws IOException, InterruptedException {
		if (worldPath != null) {
			setWorld(worldPath);
			worldPath = workDir.resolve(worldPath);
		} else worldPath = getWorld();
		if (worldPath == null || !Files.exists(worldPath)) {
			log.warn(
					"No world was specified or the world at the given path does not exist. Starting the server once to create one...");
			log.debug("The path is " + worldPath + " | " + worldPath.toAbsolutePath());
			runMinecraft(debugServer);
			worldPath = getWorld();
			if (!worldPath.isAbsolute()) worldPath = workDir.resolve(worldPath);
		}
		if (worldPath == null || !Files.exists(worldPath))
			throw new NoSuchFileException(String.valueOf(worldPath));
		log.debug("Using world path " + worldPath);
		return new World(worldPath);
	}

	/**
	 * Set the world's path in the server.properties, creating it if it does not exist yet.
	 * 
	 * @author piegames
	 */
	public void setWorld(Path worldPath) throws IOException {
		Path propsFile = workDir.resolve("server.properties");
		if (!Files.exists(propsFile)) {
			Files.write(propsFile, "level-name=".concat(worldPath.toString()).getBytes());
		} else {
			serverProperties.backup();

			Properties props = new Properties();
			props.load(Files.newInputStream(propsFile));
			props.put("level-name", worldPath.toString());
			props.store(Files.newOutputStream(propsFile), null);
		}
	}

	/**
	 * Retrieve the world currently specified in the {@code server.properties} file. Returns {@code null} if the file does not exist or if the world is not specified in the file. The returned path
	 * will be made absolute by resolving it against the server folder.
	 * 
	 * @author piegames
	 */
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

	/** Reset all changes made to the server. Currently, this is only the {@code server.properties} */
	public void resetChanges() throws IOException {
		serverProperties.restore();
	}

	/**
	 * Start the Minecraft server using the current settings, wait until it finished loading the world and stop it. Communication with the server is done via standard IO streams and commands.
	 * 
	 * @param debugServer
	 *            if set to true, all output from the server will be redirected to {@link System#out} for debugging purposes.
	 * @throws IOException
	 * @throws InterruptedException
	 *             if the thread gets interrupted while waiting for the server process to finish. This should never happen.
	 * @author Corrodias, Morlok8k, piegames
	 */
	public void runMinecraft(boolean debugServer) throws IOException, InterruptedException {
		log.debug("Setting EULA");
		Files.write(workDir.resolve("eula.txt"), "eula=true".getBytes(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
		log.info("Starting server");
		final Process process = builder.start();

		final BufferedReader pOut =
				new BufferedReader(new InputStreamReader(process.getInputStream()));
		for (String line = pOut.readLine(); line != null; line = pOut.readLine()) {
			if (Thread.interrupted()) {
				log.warn("Got interrupted by other process, stopping");
				process.destroy();
				break;
			}
			line = line.trim();
			if (debugServer) System.out.println(line);

			if (line.contains("Done")) {
				PrintStream out = new PrintStream(process.getOutputStream());

				out.println("forceload query");
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
