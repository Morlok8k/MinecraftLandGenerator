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
	@Option(names = { "-v", "--verbose" }, description = "Be verbose.")
	private boolean verbose = false;

	@Parameters(index = "0", description = "X-coordinate")
	private int X;

	@Parameters(index = "1", description = "Z-coordinate")
	private int Z;

	@Parameters(index = "2", description = "path to the directory in which the server runs")
	private Path serverPath;

	@Option(names = "-i", description = "override the iteration spawn offset increment",  defaultValue = "380", showDefaultValue = CommandLine.Help.Visibility.ALWAYS)
	private int increment = 380;

	@Option(names = {"--x-offset", "-xoff"}, description = "set the X offset to generate land around")
	private int xOffset = 0;

	@Option(names = {"--y-offset", "-yoff"}, description = "set the Z offset to generate land around")
	private int zOffset = 0;

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
