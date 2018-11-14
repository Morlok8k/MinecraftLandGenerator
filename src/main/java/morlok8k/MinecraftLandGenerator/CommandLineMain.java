package morlok8k.MinecraftLandGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.joml.Vector2i;

import morlok8k.MinecraftLandGenerator.CommandLineMain.AutoSpawnpoints;
import morlok8k.MinecraftLandGenerator.CommandLineMain.ManualSpawnpoints;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.RunAll;

@Command(name = "MinecraftLandGenerator",
		subcommands = { HelpCommand.class, ManualSpawnpoints.class, AutoSpawnpoints.class })
public class CommandLineMain implements Runnable {

	private static Log log = LogFactory.getLog(CommandLineMain.class);

	@Option(names = { "-v", "--verbose" }, description = "Be verbose.")
	private boolean verbose = false;

	@Option(names = { "--debug-server" },
			description = "Print the Minecraft server log to stdout for debugging")
	private boolean debugServer = false;

	@Option(names = { "-s", "--serverFile" }, description = "Path to the server's jar file.",
			defaultValue = "server.jar", showDefaultValue = Visibility.ALWAYS)
	private Path serverFile;

	@Option(names = { "-w", "--worldPath" },
			description = "Path to the world that should be generated. Defaults to the value in server.properties")
	private Path worldPath;

	@Option(names = { "--java-cmd" },
			description = "Java command to launch the server. Defaults to `java -jar`.")
	private String[] javaOpts;

	@Command(name = "auto-spawnpoints")
	public static class AutoSpawnpoints implements Runnable {

		@ParentCommand
		private CommandLineMain parent;

		@Option(names = "-i", description = "override the iteration spawn offset increment",
				defaultValue = "25", showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
				hidden = true)
		private int increment = 25;

		@Parameters(index = "0", description = "X-coordinate")
		private int x;
		@Parameters(index = "1", description = "Z-coordinate")
		private int z;
		@Parameters(index = "2", description = "Width")
		private int w;
		@Parameters(index = "3", description = "Height")
		private int h;

		public AutoSpawnpoints() {
		}

		@Override
		public void run() {
			Server server = new Server(parent.debugServer, parent.javaOpts, parent.serverFile);
			World world;
			try {
				world = server.initWorld(parent.worldPath);
			} catch (IOException | InterruptedException e) {
				log.error("Could not initialize world", e);
				return;
			}

			log.info("Generating world");
			server.runMinecraft(world, generateSpawnpoints(x, z, w, h, increment));
			log.info("Cleaning up temporary files");
			try {
				world.resetSpawn();
				server.restoreWorld();
			} catch (IOException e) {
				log.warn(
						"Could not delete backup files (server.properties.bak and level.dat.bak). Please delete them manually",
						e);
			}
			log.info("Done.");
		}

	}

	@Command(name = "manual-spawnpoints")
	public static class ManualSpawnpoints implements Runnable {

		@ParentCommand
		private CommandLineMain parent;

		@Parameters(index = "0..*")
		private Vector2i[] spawnPoints;
//		@Option(names = { "-s", "--customspawn" }, description = "Customized SpawnPoints")
//		private String[] customSpawnPoints;

		public ManualSpawnpoints() {
		}

		@Override
		public void run() {
			Server server = new Server(parent.debugServer, parent.javaOpts, parent.serverFile);
			World world;
			try {
				world = server.initWorld(parent.worldPath);
			} catch (IOException | InterruptedException e) {
				log.error("Could not initialize world", e);
				return;
			}
			List<Vector2i> spawnpoints = new ArrayList<>();
			log.info("Generating world");
			server.runMinecraft(world, spawnpoints);
			log.info("Cleaning up temporary files");
			try {
				world.resetSpawn();
				server.restoreWorld();
			} catch (IOException e) {
				log.warn(
						"Could not delete backup files (server.properties.bak and level.dat.bak). Please delete them manually",
						e);
			}
			log.info("Done.");
		}
	}

	public CommandLineMain() {

	}

	@Override
	public void run() {
		if (verbose) {
			Configurator.setRootLevel(Level.DEBUG);
		}
	}

	public static void main(String[] args) {
		/* Without this, JOML will print vectors out in scientific notation which isn't the most human readable thing in the world */
		System.setProperty("joml.format", "false");

		CommandLine cli = new CommandLine(new CommandLineMain());
		cli.registerConverter(Vector2i.class, new ITypeConverter<Vector2i>() {

			@Override
			public Vector2i convert(String value) throws Exception {
				String[] dims = value.split(",");
				if (dims.length != 2) throw new IllegalArgumentException(
						"Input must have two values for the two dimensions");
				return new Vector2i(Integer.valueOf(dims[0]), Integer.valueOf(dims[1]));
			}
		});
		cli.parseWithHandler(new RunAll(), args);
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
