package morlok8k.MinecraftLandGenerator;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import morlok8k.MinecraftLandGenerator.GUI.MLG_GUI;

/**
 * 
 * @author Corrodias, Morlok8k, pr0f1x
 * 
 */
public class Main {

	public static int xRange = 0;
	public static int zRange = 0;
	public static Integer zOffset = null;
	public static Integer xOffset = null;

	//////////////////////////////////////////////////////////
	// REMINDER: Because I always forget/mix up languages:	//
	// "static" in java means "global" to this class		//
	// "final" means "constant"								//
	// public/private shows/hides between classes			//
	//////////////////////////////////////////////////////////

	/**
	 * @param args
	 *            the command line arguments
	 */

	public static void main(String[] args) {
		var.startTime = System.currentTimeMillis();

		var.originalArgs = args;	// we may potentially remove some args later, but we keep a record of the original for the log file.

		// This is really just here for debugging...
		// I plan on adding more asserts later, but for now, this will do.
		// to enable this, run:
		// java -enableassertions -jar MinecraftLandGenerator.jar
		assert var.assertsEnabled = true;  // Intentional side-effect!!!  (This may cause a Warning, which is safe to ignore: "Possible accidental assignment in place of a comparison. A condition expression should not be reduced to an assignment")
		if (var.assertsEnabled) {
			Out.outD("assertsEnabled: " + var.assertsEnabled);
			var.verbose = true;
			Out.outD("Verbose mode forced!");
			var.testing = true;
			Out.outD("Debug mode forced!");
			var.dontWait = true;
			Out.outD("-nowait mode forced!");
			Out.outD("");
		}

		boolean GUI = false;
		boolean NOGUI = false;

		String[] argsNOGUI = new String[args.length];
		argsNOGUI = args;
		argsNOGUI = StringArrayParse.Parse(argsNOGUI, "nogui");		//parse out "nogui"
		if (!(args.equals(argsNOGUI))) {								//do the freshly parsed args match the original?
			args = argsNOGUI;											//use the freshly parsed args for everything else now...
			NOGUI = true;
		}

		//MLG_GUI Choosing code...
		if ((!NOGUI) && (!java.awt.GraphicsEnvironment.isHeadless())) {
			GUI = true;
			if (var.testing) {
				Out.outD("MLG_GUI: This is a graphical enviroment.");
			}

			//////
			GUI = false;				// forcing GUI to be false for now, because I don't have the MLG_GUI code ready yet!
			//////

		} else {
			GUI = false;				// No GUI for us today...
			if (var.testing) {
				Out.outD("MLG_GUI: Command Line Only!");
			}
		}

		if (GUI) {	//GUI
			// Launch MLG_GUI

			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						MLG_GUI frame = new MLG_GUI();
						frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

		} else {	//No GUI
			// Finally, Lets Start MLG!
			Main.runCLI(args);
		}

	}

	/**
	 * Start MinecraftLandGenerator (Command Line Interface)
	 * 
	 * @author Corrodias, Morlok8k
	 * @param args
	 * 
	 */
	private static void runCLI(String[] args) {

		// Lets get the date, and our BuildID
		var.date = new Date();
		Update.readBuildID();

		// The following displays no matter what happens, so we needed this date stuff to happen first.

		Out.out(var.PROG_NAME + " version " + var.VERSION);
		Out.out("BuildID: (" + var.MLG_Last_Modified_Date.getTime() + ")");		// instead of dateformatting the buildid, we return the raw Long number. 
		// thus different timezones wont display a different buildID
		Out.out("This version was last modified on "
				+ var.dateFormat.format(var.MLG_Last_Modified_Date));
		Out.out("");
		Out.out("Uses a Minecraft server to generate square land of a specified size.");
		Out.out("");
		Out.out("");

		// =====================================================================
		//                           INSTRUCTIONS
		// =====================================================================

		// check for -nowait, and remove from arguments if it exists.  (we remove it for compatibility reasons with the rest of the existing code.)
		// (-nowait is the only universal switch - it can be used with anything.  its basically for scripting, as it turns off the 10sec wait for human readability)
		String[] newArgs = new String[args.length];
		newArgs = args;
		newArgs = StringArrayParse.Parse(newArgs, "-n");		//parse out -n
		newArgs = StringArrayParse.Parse(newArgs, "-nowait");	//parse out -nowait
		if (!(args.equals(newArgs))) {								//do the freshly parsed args match the original?
			var.dontWait = true;											//if not, we dont wait for anything!
			args = newArgs;												//use the freshly parsed args for everything else now...
			Out.out("Notice: Not waiting for anything...");
		}

		if (args.length == 0) {																//we didn't find a an X and Z size, so lets ask for one.
			Out.out("Please Enter the size of world you want.  Example: X:1000  Z:1000");
			Out.outP(var.MLG + "X:");
			xRange = Input_CLI.getInt("X:");
			Out.outP(var.MLG + "Z:");
			zRange = Input_CLI.getInt("Z:");
			args = new String[] { String.valueOf(xRange), String.valueOf(zRange) };

		}

		if (args[0].equalsIgnoreCase("-version") || args[0].equalsIgnoreCase("-help")
				|| args[0].equals("/?")) {

			Readme_and_HelpInfo.showHelp(true);

			return;
		}

		// =====================================================================
		//                         STARTUP AND CONFIG
		// =====================================================================

		// the arguments are apparently okay so far. parse the conf file.
		if (args[0].equalsIgnoreCase("-conf")) {

			if (args.length == 2) {
				if (args[1].equalsIgnoreCase("download")) {
					final boolean fileSuccess =
							DownloadFile.downloadFile(var.github_MLG_Conf_URL, var.testing);
					if (fileSuccess) {
						Out.out(var.MinecraftLandGeneratorConf + " file downloaded.");
						return;
					}
				}
			}

			FileWrite.saveConf(true);  //new conf file
			return;

		} else if (args[0].equalsIgnoreCase("-ps") || args[0].equalsIgnoreCase("-printspawn")) {
			// okay, sorry, this is an ugly hack, but it's just a last-minute feature.
			Misc.printSpawn();
			Time.waitTenSec(false);
			return;
		} else if (args[0].equalsIgnoreCase("-build")) {
			Update.buildID(false);
			return;
		} else if (args[0].equalsIgnoreCase("-update")) {
			Update.updateMLG();
			Time.waitTenSec(false);
			return;
		} else if (args[0].equalsIgnoreCase("-readme")) {

			if (args.length == 2) {
				Readme_and_HelpInfo.readMe(args[1]);
			} else {
				Readme_and_HelpInfo.readMe(null);
			}
			return;
		} else if (args[0].equalsIgnoreCase("-downloadfile")) {
			if (args.length == 2) {
				DownloadFile.downloadFile(args[1], true);
			} else {
				Out.out("No File to Download!");
				Time.waitTenSec(false);
			}
			return;

		} else if (args[0].equalsIgnoreCase("-downloadlist")) {

			if (args.length == 2) {
				String origMD5 = "";
				String recheckMD5 = "";

				try {
					final File config = new File(args[1]);
					try {
						origMD5 = MD5.fileMD5(config.toString());
					} catch (final NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
					final BufferedReader in = new BufferedReader(new FileReader(config));
					String line;
					while ((line = in.readLine()) != null) {
						if (line.contains("###RECHECK###")) {
							var.recheckFlag = !var.recheckFlag;
						} else {
							DownloadFile.downloadFile(line, true);
						}
					}
					in.close();

					if (var.recheckFlag == true) {				// the first line is always the location of this file.  the second is the recheck flag, if we want to. 
						try {
							recheckMD5 = MD5.fileMD5(config.toString());
						} catch (final NoSuchAlgorithmException e) {
							e.printStackTrace();
						}

						if (!origMD5.contentEquals(recheckMD5)) {
							final BufferedReader in_recheck =
									new BufferedReader(new FileReader(config));
							String line_recheck;
							while ((line_recheck = in_recheck.readLine()) != null) {
								if (line_recheck.contains("###RECHECK###")) {
									var.recheckFlag = !var.recheckFlag;
								} else {
									DownloadFile.downloadFile(line_recheck, true);
								}
							}
							in_recheck.close();
						}

					}

				} catch (final FileNotFoundException ex) {
					System.err.println(args[1] + " - File not found");
					Time.waitTenSec(false);
					return;
				} catch (final IOException ex) {
					System.err.println(args[1] + " - Could not read file.");
					Time.waitTenSec(false);
					return;
				}
			} else {
				Out.out("No File with links!");
				Time.waitTenSec(false);
			}
			return;

		} else if (args.length == 1) {
			Out.out("For help, use java -jar " + var.MLGFileNameShort + " -help");
			Time.waitTenSec(false);
			return;
		}

		FileRead.readConf();

		boolean oldConf = false; // This next section checks to see if we have a old configuration file (or none!)

		if ((var.serverPath == null) || (var.javaLine == null)) { 			// MLG 1.2 Check for a valid .conf file.
			Out.err(var.MinecraftLandGeneratorConf
					+ " does not contain all required properties.  Making New File!");	// Please recreate it by running this application with -conf.

			// return;

			// We no longer quit. We generate a new one with defaults.

			var.javaLine = var.defaultJavaLine;
			var.serverPath = ".";
			oldConf = true;
		}

		if (var.doneText == null) {					// MLG 1.4.0
			oldConf = true;
		} else if (var.preparingText == null) {	// MLG 1.4.0
			oldConf = true;
		} else if (var.preparingLevel == null) {	// MLG 1.4.5 / 1.5.0
			oldConf = true;
		} else if (var.level_1 == null) {			// MLG 1.4.5 / 1.5.0
			oldConf = true;
		} else if (var.level_0 == null) {			// MLG 1.5.1 / 1.6.0
			oldConf = true;
		}

		if (oldConf) {
			Out.err("Old Version of " + var.MinecraftLandGeneratorConf + " found.  Updating...");

			FileWrite.saveConf(false);		//old conf

			Time.waitTenSec(false);
			return;

		}

		// ARGUMENTS
		try {
			xRange = Integer.parseInt(args[0]);
			zRange = Integer.parseInt(args[1]);

			if ((xRange < 1000) && (xRange != 0)) {
				xRange = 1000;							//if less than 1000, (and not 0) set to 1000 (Calculations don't work well on very small maps)
				Out.err("X size too small - Changing X to 1000");
			}
			if ((zRange < 1000) && (zRange != 0)) {
				zRange = 1000;
				Out.err("Z size too small - Changing Z to 1000");
			}

		} catch (final NumberFormatException ex) {
			Out.err("Invalid X or Z argument.");
			Out.err("Please Enter the size of world you want.  Example: X:1000  Z:1000");
			xRange = Input_CLI.getInt("X:");
			zRange = Input_CLI.getInt("Z:");

			//return;
		}

		var.verbose = false;			// Verifing that these vars are false
		var.alternate = false;			// before changing them...

		// This is embarrassing. Don't look.
		try {
			for (int i = 0; i < (args.length - 2); i++) {
				final String nextSwitch = args[i + 2].toLowerCase();
				if (nextSwitch.equals("-verbose") || nextSwitch.equals("-v")) {
					var.verbose = true;
					Out.out("Notice: Verbose Mode");

				} else if (nextSwitch.startsWith("-i")) {
					var.increment = Integer.parseInt(args[i + 2].substring(2));
					Out.out("Notice: Non-Default Increment: " + var.increment);

				} else if (nextSwitch.startsWith("-w")) {
					var.ignoreWarnings = true;
					Out.out("Notice: Warnings from Server are Ignored");

				} else if (nextSwitch.equals("-alt") || nextSwitch.equals("-a")) {
					var.alternate = true;
					Out.out("Notice: Using Alternate Launching");

				} else if (nextSwitch.startsWith("-x")) {
					xOffset = Integer.valueOf(args[i + 2].substring(2));
					Out.out("Notice: X Offset: " + xOffset);

				} else if (nextSwitch.startsWith("-y") || nextSwitch.startsWith("-z")) {		//NOTE: "-y" is just here for backwards compatibility
					zOffset = Integer.valueOf(args[i + 2].substring(2));
					Out.out("Notice: Z Offset: " + zOffset);
					if (nextSwitch.startsWith("-y")) {
						Out.out("Notice: MLG now uses Z instead of Y.  Please use the -z switch instead");
						Time.waitTenSec(false);
					}

				} else {
					var.serverPath = args[i + 2];
					Out.out("Notice: Attempting to use Alternate Server:" + var.serverPath);
				}
			}
		} catch (final NumberFormatException ex) {
			Out.err("Invalid switch value.");
			return;
		}

		WorldVerify.verifyWorld();

		{
			final File backupLevel =
					new File(var.worldPath + var.fileSeparator + "level_backup.dat");
			if (backupLevel.exists()) {
				//err("There is a level_backup.dat file left over from a previous attempt that failed. You should go determine whether to keep the current level.dat"
				//		+ " or restore the backup.");
				//err("You most likely will want to restore the backup!");
				//Time.waitTenSec(false);

				Out.err("There is a level_backup.dat file left over from a previous attempt that failed.");
				Out.out("Resuming...");

				//use resume data
				final File serverLevel = new File(var.worldPath + var.fileSeparator + "level.dat");
				try {
					Misc.copyFile(backupLevel, serverLevel);
				} catch (final IOException e) {
					e.printStackTrace();
				}
				backupLevel.delete();

				//return;

				FileRead.readArrayListCoordLog(var.worldPath + var.fileSeparator
						+ "MinecraftLandGenerator.log");		// we read the .log just for any resume data, if any.

				System.gc();		//run the garbage collector - hopefully free up some memory!

				xRange = var.resumeX;
				zRange = var.resumeZ;

			}
		}

		// =====================================================================
		//                              PROCESSING
		// =====================================================================

		Out.out("Processing world \"" + var.worldPath + "\", in " + var.increment
				+ " block increments, with: " + var.javaLine);
		// out( MLG + "Processing \"" + worldName + "\"...");

		Out.out("");

		// prepare our two ProcessBuilders
		// minecraft = new ProcessBuilder(javaLine, "-Xms1024m", "-Xmx1024m", "-jar", jarFile, "nogui");
		var.minecraft = new ProcessBuilder(var.javaLine.split("\\s")); // is this always going to work? i don't know.	(most likely yes)		
		var.minecraft.directory(new File(var.serverPath));
		var.minecraft.redirectErrorStream(true);

		try {
			Out.out("Launching server once to make sure there is a world.");

			final long generationStartTimeTracking = System.currentTimeMillis();		//Start of time remaining calculations.

			final boolean serverLaunch = Server.runMinecraft();

			if (!(serverLaunch)) {
				System.exit(1);				// we got a warning or severe error
			}

			if ((xRange == 0) & (zRange == 0)) {  //If the server is launched with an X and a Z of zero, then we just shutdown MLG after the initial launch.
				return;
			}

			//TODO: make this optional
			final boolean useChunks = false;

			if (useChunks) {		// use Chunks or Regions
				xRange = (int) (Math.ceil(((double) xRange) / ((double) 16))) * 16;			//say xRange was entered as 1000.  this changes it to be 1008, a multiple of 16. (the size of a chunk)
				zRange = (int) (Math.ceil(((double) zRange) / ((double) 16))) * 16;			//say zRange was entered as 2000.  there is no change, as it already is a multiple of 16.
				xOffset = (int) (Math.ceil(((double) xOffset) / ((double) 16))) * 16;
				zOffset = (int) (Math.ceil(((double) zOffset) / ((double) 16))) * 16;
			} else {
				xRange = (int) (Math.ceil(((double) xRange) / ((double) 512))) * 512;			//say xRange was entered as 1000.  this changes it to be 1024, a multiple of 512. (the size of a region)
				zRange = (int) (Math.ceil(((double) zRange) / ((double) 512))) * 512;			//say zRange was entered as 2048.  there is no change, as it already is a multiple of 512.
				xOffset = (int) (Math.ceil(((double) xOffset) / ((double) 512))) * 512;
				zOffset = (int) (Math.ceil(((double) zOffset) / ((double) 512))) * 512;
			}

			FileWrite.AppendTxtFile(var.worldPath + var.fileSeparator
					+ "MinecraftLandGenerator.log",
					"# " + var.PROG_NAME + " " + var.VERSION + " - " + SelfAware.JVMinfo()
							+ var.newLine + "# " + var.MC_Server_Version + var.newLine
							+ "# Started: " + var.dateFormat.format(generationStartTimeTracking)
							+ var.newLine + "##Size: X" + xRange + "Z" + zRange + var.newLine);

			Out.out("");

			final File serverLevel = new File(var.worldPath + var.fileSeparator + "level.dat");
			final File backupLevel =
					new File(var.worldPath + var.fileSeparator + "level_backup.dat");

			Out.out("Backing up level.dat to level_backup.dat.");
			Misc.copyFile(serverLevel, backupLevel);
			Out.out("");

			final Coordinates spawn = SpawnPoint.getSpawn(serverLevel);
			Out.out("Spawn point detected: [X,Y,Z] " + spawn);

			FileWrite.AppendTxtFile(var.worldPath + var.fileSeparator
					+ "MinecraftLandGenerator.log", "# Seed: " + var.randomSeed + var.newLine
					+ "# Spawn: " + spawn.toString() + var.newLine);

			{
				boolean overridden = false;
				if (xOffset == null) {
					xOffset = spawn.getX();
				} else {
					overridden = true;
				}
				if (zOffset == null) {
					zOffset = spawn.getZ();
				} else {
					overridden = true;
				}
				if (overridden) {
					Out.out("Centering land generation on [" + xOffset + ", " + zOffset
							+ "] due to switches.");
				}
			}
			Out.out("");

			double xLoops, zLoops;
			int curXloops = 0;
			int curZloops = 0;
			int xRangeAdj = 0;
			int zRangeAdj = 0;

			// have main loop make an arraylist of spawnpoints
			// read from a file if MLG has run before on this world.  save to arraylist
			// remove existing points from new list.
			// run mlg on remaining list of spawn points.

			// X
			xLoops = ((double) xRange / (double) var.increment);		//How many loops do we need?
			xLoops = Math.ceil(xLoops);								//round up to find out!
			xRangeAdj = (int) (xLoops * var.increment);
			xLoops = xLoops + 1;

			// Z
			zLoops = ((double) zRange / (double) var.increment);		//How many loops do we need?
			zLoops = Math.ceil(zLoops);								//round up to find out!
			zRangeAdj = (int) (zLoops * var.increment);
			zLoops = zLoops + 1;

			Out.out("Calculating Spawn Points...");

			int totalIterations = (int) (xLoops * zLoops);
			int currentIteration = 0;

			long differenceTime = System.currentTimeMillis();

			Long timeTracking = 0L;

			final ArrayList<Coordinates> launchList = new ArrayList<Coordinates>(totalIterations);

			for (int currentX = 0; currentX <= (xRangeAdj / 2); currentX += var.increment) {
				curXloops++;
				if (curXloops == 1) {
					currentX = (((0 - xRange) / 2) + (var.incrementFull / 2));  	// West Edge of map
				} else if (currentX >= ((xRangeAdj / 2) - (var.increment / 2))) {
					currentX = ((xRange / 2) - (var.incrementFull / 2));			// East Edge of map
				}

				for (int currentZ = 0; currentZ <= (zRangeAdj / 2); currentZ += var.increment) {
					currentIteration++;

					curZloops++;
					if (curZloops == 1) {
						currentZ = (((0 - zRange) / 2) + (var.incrementFull / 2));	// North Edge of map
					} else if (currentZ >= ((zRangeAdj / 2) - (var.increment / 2))) {
						currentZ = ((zRange / 2) - (var.incrementFull / 2));		// South Edge of map
					}

					{
						// add Coordinates to arraylist here
						final Coordinates tempCoords =
								new Coordinates(currentX + xOffset, 64, currentZ + zOffset);
						launchList.add(tempCoords);

						if (var.testing) {
							System.out.println(tempCoords);
						}
					}

					if (curZloops == 1) {
						currentZ =
								(int) ((Math.ceil((((0 - zRangeAdj) / 2) / var.increment))) * var.increment);
					}

				}
				curZloops = 0;
				if (curXloops == 1) {
					currentX =
							(int) ((Math.ceil((((0 - xRangeAdj) / 2) / var.increment))) * var.increment);
				}
			}

			//get existing list, and remove this list from launchList
			final ArrayList<Coordinates> removeList =
					FileRead.readArrayListCoordLog(var.worldPath + var.fileSeparator
							+ "MinecraftLandGenerator.log");

			if (!(removeList.isEmpty())) {
				Arraylist.arrayListRemove(launchList, removeList);
			}

			removeList.clear();		// we are done with this now.

			System.gc();		//run the garbage collector - hopefully free up some memory!

			currentIteration = 0;
			totalIterations = launchList.size();
			Coordinates xyz = null;
			final Iterator<Coordinates> coordArrayIterator = launchList.iterator();
			while (coordArrayIterator.hasNext()) {
				currentIteration++;
				xyz = coordArrayIterator.next();

				//////// Start server launch code

				String percentDone =
						Double.toString((((double) currentIteration - 1) / totalIterations) * 100);
				final int percentIndex =
						((percentDone.indexOf(".") + 3) > percentDone.length()) ? percentDone
								.length() : (percentDone.indexOf(".") + 3);		//fix index on numbers like 12.3
				percentDone =
						percentDone.substring(0,
								(percentDone.indexOf(".") == -1 ? percentDone.length()
										: percentIndex));			//Trim output, unless whole number

				Out.out("Setting spawn to [X,Y,Z]: " + xyz + " (" + currentIteration + " of "
						+ totalIterations + ") " + percentDone + "% Done"); // Time Remaining estimate

				timeTracking = System.currentTimeMillis();

				//NEW CODE:
				differenceTime =
						(timeTracking - generationStartTimeTracking) / (currentIteration + 1);		// Updated.  we now count all runs, instead of the last 4.
				differenceTime *= 1 + (totalIterations - currentIteration);									// this should provide a more accurate result.
				Out.out("Estimated time remaining: " + Time.displayTime(differenceTime));						// I've noticed it gets pretty accurate after about 8 launches!

				// Set the spawn point
				SpawnPoint.setSpawn(serverLevel, xyz);

				// Launch the server
				boolean serverSuccess = false;

				serverSuccess = Server.runMinecraft();
				Out.out("");

				//////// End server launch code

				if (serverSuccess) {
					// Write the current Coordinates to log file!
					FileWrite.AppendTxtFile(var.worldPath + var.fileSeparator
							+ "MinecraftLandGenerator.log", xyz.toString() + var.newLine);
				} else {
					System.exit(1);				// we got a warning or severe error
				}

			}

			if (currentIteration == 0) {
				Out.out("Nothing to generate!");
			} else {
				Out.out("Finished generating chunks.");
			}

			Misc.copyFile(backupLevel, serverLevel);
			backupLevel.delete();
			Out.out("Restored original level.dat.");

			Out.out("Generation complete in: "
					+ Time.displayTime(var.startTime, System.currentTimeMillis()));
			Time.waitTenSec(false);

			//TODO: add if's

			if (var.webLaunch) {		//if webLaunch is already false, don't check for these things
				if (java.awt.GraphicsEnvironment.isHeadless()) {
					var.webLaunch = false;					//headless enviroment - cant bring up webpage!
				}
				final File web1 = new File("web");
				final File web2 = new File("web.txt");		//user has put in the magical file to not launch the webpage	
				final File web3 = new File("web.txt.txt");
				if (web2.exists() || (web1.exists() || web3.exists())) {  //check for "web.txt", if not found, check for "web", and if still not found, check for "web.txt.txt"
					var.webLaunch = false;
				}
			}

			if (var.webLaunch && java.awt.Desktop.isDesktopSupported()) {
				final URI splashPage =
				//URI.create("https://sites.google.com/site/minecraftlandgenerator/home/mlg_splash");
						URI.create("http://adf.ly/520855/splashbanner");
				try {
					java.awt.Desktop.getDesktop().browse(splashPage);
				} catch (final IOException e) {
					Out.err("Error displaying webpage... " + e.getLocalizedMessage());
				}
			} else {
				Out.out("Please Visit: http://adf.ly/520855/mlg");
				Out.out("Or: https://sites.google.com/site/minecraftlandgenerator/");
				Out.out("Thanks!");
			}

		} catch (final IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
}
