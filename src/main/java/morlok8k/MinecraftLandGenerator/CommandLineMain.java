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

import morlok8k.MinecraftLandGenerator.CommandLineMain.AutoSpawnpoints;
import morlok8k.MinecraftLandGenerator.CommandLineMain.ForceloadChunks;
import morlok8k.MinecraftLandGenerator.CommandLineMain.ManualSpawnpoints;
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

@Command(name = "MinecraftLandGenerator", subcommands = { HelpCommand.class,
		ManualSpawnpoints.class, AutoSpawnpoints.class, ForceloadChunks.class })
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

	@Override
	public void run() {
		if (verbose) {
			Configurator.setRootLevel(Level.DEBUG);
		}
	}

	protected static class RectangleMixin {
		@Parameters(index = "0", description = "X-coordinate")
		int x;
		@Parameters(index = "1", description = "Z-coordinate")
		int z;
		@Parameters(index = "2", description = "Width")
		int w;
		@Parameters(index = "3", description = "Height")
		int h;
	}

	protected static abstract class CommandLineHelper implements Runnable {
		@ParentCommand
		protected CommandLineMain parent;

		protected Server server;
		protected World world;

		@Override
		public final void run() {
			try {
				server = new Server(parent.serverFile, parent.debugServer, parent.javaOpts);
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
				world = server.initWorld(parent.worldPath);
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

	@Command(name = "auto-spawnpoints")
	public static class AutoSpawnpoints extends CommandLineHelper {

		@Mixin
		private RectangleMixin bounds;

		@Option(names = "-i", description = "override the iteration spawn offset increment",
				defaultValue = "25", showDefaultValue = CommandLine.Help.Visibility.ALWAYS,
				hidden = true)
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
					server.runMinecraft();
				} catch (IOException | InterruptedException e) {
					log.warn("Could not process spawn point " + spawn
							+ " this part of the world won't be generated", e);
				}
			}
		}

	}

	@Command(name = "manual-spawnpoints")
	public static class ManualSpawnpoints extends CommandLineHelper {

		@Parameters(index = "0..*")
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
					server.runMinecraft();
				} catch (IOException | InterruptedException e) {
					log.warn("Could not process spawn point " + spawn
							+ " this part of the world won't be generated", e);
				}
			}
		}
	}

	@Command(name = "forceload-chunks")
	public static class ForceloadChunks extends CommandLineHelper {
		@Mixin
		private RectangleMixin bounds;

		@Option(names = "--dimension")
		private Dimension dimension;

		@Option(names = "--max-loaded", defaultValue = "16384")
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
					server.runMinecraft();
				} catch (IOException | InterruptedException e) {
					log.error("Could not force-load chunks", e);
				}
			}
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
}
