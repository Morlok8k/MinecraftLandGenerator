package morlok8k.MinecraftLandGenerator;

import java.nio.file.Path;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.RunLast;

import java.nio.file.Path;

@Command(name = "mlg", subcommands = {
		HelpCommand.class})

public class CommandLineMain implements Runnable {

	@Option(names = {"-v", "--verbose"}, description = "Be verbose.")
	private boolean verbose = false;
	@Option(names = {"-r", "--region"}, description = "Regionfiles instead of chunks")
	private boolean regionFile = false;

	@Option(names = {"-s", "--customspawn"}, description = "Customized SpawnPoints")
	private String[] customSpawnPoints;

	@Option(names = "-i", description = "override the iteration spawn offset increment", defaultValue = "380", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
	private int increment = 380;

	@Option(names = {"--x-offset", "-x"}, description = "set the X offset to generate land around")
	private int xOffset = 0;

	@Option(names = {"--y-offset", "-y"}, description = "set the Z offset to generate land around")
	private int zOffset = 0;

	@Parameters(index = "0", description = "X-coordinate")
	private int X;

	@Parameters(index = "1", description = "Z-coordinate")
	private int Z;

	@Option(names = {"-s","--serverFile"}, description = "path to the directory in which the server runs")
	private Path serverPath;

	@Option(names= {"-w","--worldPath"}, description = "path to the to be generated world")
	private Path worldPath;

	@Option(names= {"-d","--workDir"}, description = "workDirectory")
	private Path workDir;


	public CommandLineMain() {

	}

	@Override
	public void run() {
		CommandLine.usage(this, System.err);
	}

	public static void main(String[] args) {
		CommandLine cli = new CommandLine(new CommandLineMain());
		cli.parseWithHandler(new RunLast(), args);
	}

}
