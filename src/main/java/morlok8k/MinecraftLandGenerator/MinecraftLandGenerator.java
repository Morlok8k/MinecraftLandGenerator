package morlok8k.MinecraftLandGenerator;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.joml.Vector2i;

import morlok8k.MinecraftLandGenerator.MinecraftLandGenerator.AutoSpawnpoints;
import morlok8k.MinecraftLandGenerator.MinecraftLandGenerator.ForceloadChunks;
import morlok8k.MinecraftLandGenerator.MinecraftLandGenerator.ManualSpawnpoints;
import morlok8k.MinecraftLandGenerator.World.Dimension;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help.Visibility;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParentCommand;
import picocli.CommandLine.RunAll;

/** @author piegames, sommerlilie */
@Command(name = "MinecraftLandGenerator",
		subcommands = { HelpCommand.class, ManualSpawnpoints.class, AutoSpawnpoints.class,
				ForceloadChunks.class },
		description = "Generate Minecraft worlds by tricking the server to do it for you. You need to provide a server jar file for this to work.")
public class MinecraftLandGenerator implements Runnable {

	private static Log log = LogFactory.getLog(MinecraftLandGenerator.class);

	@Option(names = { "-v", "--verbose" }, description = "Be verbose.")
	private boolean verbose = false;

	@Option(names = { "--debug-server" },
			description = "Print the Minecraft server log to stdout for debugging")
	private boolean debugServer = false;

	@Option(names = { "-s", "--serverFile" }, description = "Path to the server's jar file.",
			required = true, defaultValue = "server.jar", showDefaultValue = Visibility.ALWAYS)
	private Path serverFile;

	@Option(names = { "-w", "--worldPath" },
			description = "Path to the world that should be generated. Defaults to the value in server.properties")
	private Path worldPath;

	@Option(names = { "--java-cmd" },
			description = "Java command to launch the server. Defaults to [java, -jar]. Use this to specify JVM options (like more RAM etc.) or to enforce the usage of a specific java version.")
	private String[] javaOpts;

	@Override
	public void run() {
		if (verbose) {
			Configurator.setRootLevel(Level.DEBUG);
		}
	}

	/**
	 * Mixin that provides positional arguments describing a rectangle.
	 * 
	 * @see Mixin
	 * @author piegames
	 */
	protected static class RectangleMixin {
		@Parameters(index = "0", paramLabel = "START_X",
				description = "X-coordinate of the first chunk to be generated (in chunk coordinates)")
		int x;
		@Parameters(index = "1", paramLabel = "START_Z",
				description = "Z-coordinate of the first chunk to be generated (in chunk coordinates)")
		int z;
		@Parameters(index = "2", paramLabel = "WIDTH",
				description = "Amount of chunks to generate from the starting chunk to the east")
		int w;
		@Parameters(index = "3", paramLabel = "HEIGHT",
				description = "Amount of chunks to generate from the starting chunk to the south")
		int h;
	}

	protected static abstract class CommandLineHelper implements Runnable {
		@ParentCommand
		protected MinecraftLandGenerator parent;

		protected Server server;
		protected World world;

		@Override
		public final void run() {
			try {
				server = new Server(parent.serverFile, parent.javaOpts);
			} catch (FileAlreadyExistsException e1) {
				log.fatal(
						"Server backup file already exists. Please delete or restore it and then start again",
						e1);
				return;
			} catch (NoSuchFileException e1) {
				log.fatal(
						"Server file does not exist. Please download the minecraft server and provide the path to it",
						e1);
				return;
			}
			try {
				world = server.initWorld(parent.worldPath, parent.debugServer);
			} catch (IOException | InterruptedException e) {
				log.fatal("Could not initialize world", e);
				return;
			}

			runGenerate();

			log.info("Cleaning up temporary files");
			try {
				world.resetChanges();
				server.resetChanges();
			} catch (IOException e) {
				log.warn(
						"Could not delete backup files (server.properties.bak and level.dat.bak). Please delete them manually",
						e);
			}
			log.info("Done.");
		}

		protected abstract void runGenerate();

	}

	@Command(name = "auto-spawnpoints",
			description = "Automatically generate spawnpoints that exactly fill up a rectangular area and generate the world around them.")
	public static class AutoSpawnpoints extends CommandLineHelper {

		@Mixin
		private RectangleMixin bounds;

		@Option(names = "-i",
				description = "Maximum number of chunks between two spawn points, horizontally or vertically. Since Minecraft by default generates 25 chunks around each spawn point, this should be 25 for most servers. This value should be unpair.",
				defaultValue = "25", showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
				hidden = false)
		private int increment = 25;

		@Override
		public void runGenerate() {
			log.info("Generating world");
			List<Vector2i> spawnpoints =
					World.generateSpawnpoints(bounds.x, bounds.z, bounds.w, bounds.h, increment);
			for (int i = 0; i < spawnpoints.size(); i++) {
				Vector2i spawn = spawnpoints.get(i);
				try {
					log.info("Processing " + i + "/" + spawnpoints.size() + ", spawn point "
							+ spawn);
					world.setSpawn(spawn);
					server.runMinecraft(parent.debugServer);
				} catch (IOException | InterruptedException e) {
					log.warn("Could not process spawn point " + spawn
							+ " this part of the world won't be generated", e);
				}
			}
		}

	}

	@Command(name = "manual-spawnpoints",
			description = "Provide a list of spawnpoints and a Minecraft server will be started on each one to generate the chunks around it.")
	public static class ManualSpawnpoints extends CommandLineHelper {

		@Parameters(index = "0..*", paramLabel = "SPAWNPOINTS",
				description = "Provide a lsit of spawnpoints. All chunks in a 25x25 area aroud each spawn point will be generated. Usage: x1,y1 x2,y2 x3,y3 ...")
		private Vector2i[] spawnpoints;

		@Override
		public void runGenerate() {
			log.info("Generating world");
			log.debug("All spawn points: " + Arrays.toString(spawnpoints));
			for (int i = 0; i < spawnpoints.length; i++) {
				Vector2i spawn = spawnpoints[i];
				try {
					log.info("Processing " + i + "/" + spawnpoints.length + ", spawn point "
							+ spawn);
					world.setSpawn(spawn);
					server.runMinecraft(parent.debugServer);
				} catch (IOException | InterruptedException e) {
					log.warn("Could not process spawn point " + spawn
							+ " this part of the world won't be generated", e);
				}
			}
		}
	}

	@Command(name = "forceload-chunks",
			description = "Force the chunks in a given rectangular area to be permanently loaded. If the do not exist, the server will conveniently generate them for us.")
	public static class ForceloadChunks extends CommandLineHelper {
		@Mixin
		private RectangleMixin bounds;

		@Option(names = "--dimension", description = "The dimension to generate the chunks in.",
				defaultValue = "OVERWORLD", showDefaultValue = Visibility.ALWAYS)
		private Dimension dimension;

		@Option(names = "--max-loaded", defaultValue = "16384",
				showDefaultValue = Visibility.ALWAYS,
				description = "The maximum amount of chunks to force-load at once. Increasing this number will result in more RAM consumption and thus "
						+ "in slower generation. Smaller values will result in starting the server more often, which has some overhead and takes time. "
						+ "Set this as high as possible without totally filling up your RAM.")
		private int maxLoaded;

		@Override
		protected void runGenerate() {
			ArrayList<Vector2i> loadedChunks = new ArrayList<>();
			for (int x = bounds.x; x < bounds.x + bounds.w; x++)
				for (int z = bounds.z; z < bounds.z + bounds.h; z++)
					loadedChunks.add(new Vector2i(x, z));
			log.info("Generating world");
			if (loadedChunks.size() < 5000)
				log.debug("Chunks to generate: " + loadedChunks);
			else log.debug(loadedChunks.size() + " chunks to generate");
			int stepCount = (int) Math.ceil((double) loadedChunks.size() / maxLoaded);
			for (int i = 0; i < stepCount; i++) {
				List<Vector2i> batch = loadedChunks.subList(i * maxLoaded,
						Math.min((i + 1) * maxLoaded, loadedChunks.size() - 1));
				log.info("Generating batch " + i + " / " + stepCount + " with " + batch.size()
						+ " chunks");
				try {
					world.setLoadedChunks(batch, dimension);
					server.runMinecraft(parent.debugServer);
				} catch (IOException | InterruptedException e) {
					log.error("Could not force-load chunks", e);
				}
			}
		}
	}

	public static void main(String[] args) {
		/* Without this, JOML will print vectors out in scientific notation which isn't the most human readable thing in the world */
		System.setProperty("joml.format", "false");

		CommandLine cli = new CommandLine(new MinecraftLandGenerator());
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
}
