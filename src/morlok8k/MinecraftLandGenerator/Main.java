package morlok8k.MinecraftLandGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Corrodias, Morlok8k, pr0f1x
 * 
 */
public class Main {

	//
	//
	// Public Vars:
	public static boolean testing = false;		// display more output when debugging

	public static final String PROG_NAME = "Minecraft Land Generator";		// Program Name
	public static final String VERSION = "1.7.0";								// Version Number!
	public static final String AUTHORS = "Corrodias, Morlok8k, pr0f1x";		// Authors

	public static final String fileSeparator = System.getProperty("file.separator");
	public static final String newLine = System.getProperty("line.separator");

	public static String[] originalArgs = {};

	public static String MLG = "[MLG] ";
	public static String MLGe = "[MLG-ERROR] ";

	public static DateFormat dateFormat_MDY = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
	public static Date MLG_Last_Modified_Date = null;

	public static String MLGFileNameShort = null;
	public static Scanner sc = new Scanner(System.in);
	public static final String MinecraftLandGeneratorConf = "MinecraftLandGenerator.conf";
	public static final String defaultReadmeFile = "_MLG_Readme.txt";

	public static String doneText = null;
	public static String preparingText = null;
	public static String preparingLevel = null;

	public static String level_0 = null;			// the world
	public static String level_1 = null;			// the nether
	public static String level_2 = null;			// the end
	public static String level_3 = null;			// future worlds
	public static String level_4 = null;
	public static String level_5 = null;
	public static String level_6 = null;
	public static String level_7 = null;
	public static String level_8 = null;
	public static String level_9 = null;

	public static ProcessBuilder minecraft = null;
	public static String javaLine = null;
	public static final String defaultJavaLine =
			"java -Djava.awt.headless=true -Djline.terminal=jline.UnsupportedTerminal -Duser.language=en"
					+ " -Xms1024m -Xmx1024m -Xincgc -jar minecraft_server.jar nogui";

	public static String serverPath = null;
	public static String worldPath = null;
	public static String worldName = null;

	public static boolean verbose = false;

	public static boolean dontWait = false;
	public static boolean waitSave = false;
	public static boolean ignoreWarnings = false;
	public static Long randomSeed = (long) 0;

	public static DateFormat dateFormat = new SimpleDateFormat(			// Lets get a nice Date format for display
			"EEEE, MMMM d, yyyy 'at' h:mm a zzzz", Locale.ENGLISH);
	public static Date date = null;										// date stuff
	public static Long MLG_Last_Modified_Long = 0L;

	public static final Class<?> cls = Main.class;
	public static String MLGFileName = null;

	public static final String rsrcError = "rsrcERROR";
	public static String buildIDFile = "MLG-BuildID";
	public static boolean isCompiledAsJar = false;
	public static String MLG_Current_Hash = null;
	public static int inf_loop_protect_BuildID = 0;
	public static boolean flag_downloadedBuildID = false;

	public static String MC_Server_Version = "";

	public static ArrayList<String> timeStamps = new ArrayList<String>();

	public static final String MLG_JarFile = "MinecraftLandGenerator.jar";

	public static final String github_URL =
			"https://raw.github.com/Morlok8k/MinecraftLandGenerator/master/bin/";			// just removing some redundancy
	public static final String github_MLG_Conf_URL = github_URL + MinecraftLandGeneratorConf;
	public static final String github_MLG_BuildID_URL = github_URL + buildIDFile;
	public static final String github_MLG_jar_URL = github_URL + MLG_JarFile;

	public static int resumeX = 0;				//resume data, if needed.
	public static int resumeZ = 0;

	//
	//
	//Private Vars:
	private static int MinecraftServerChunkPlayerCache = 625;	//You see this number when you first launch the server in GUI mode, after the world is loaded, but before anyone has connected.
	private static int increment = (int) (Math.sqrt(MinecraftServerChunkPlayerCache) * 16) - 20;			//private int increment = 380;

	private int xRange = 0;
	private int zRange = 0;
	private Integer xOffset = null;
	private Integer zOffset = null;

	private boolean alternate = false;

	private static Boolean recheckFlag = false;
	private static long startTime = 0L;
	public static boolean webLaunch = true;				// Launch website after generation.

	private static boolean assertsEnabled = false;				//debugging use...  use java -ea -jar MinecraftlandGenerator.jar...

	// RCON Stuff (not currently used yet...)
	public static boolean useRCON = false;				//use RCON to communicate with server.  ***Experimental***
	public static boolean rcon_Enabled = false;			//is server is set to use RCON?
	public static String rcon_IPaddress = "0.0.0.0";		//default is 0.0.0.0
	public static String rcon_Port = "25575";				//default is 25575, we are just initializing here.
	public static String rcon_Password = "test";			//default is "", but a password must be entered.

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
		startTime = System.currentTimeMillis();

		originalArgs = args;

		// This is really just here for debugging...
		// I plan on adding more asserts later, but for now, this will do.
		// to enable this, run:
		// java -enableassertions -jar MinecraftLandGenerator.jar
		assert assertsEnabled = true;  // Intentional side-effect!!!  (This may cause a Warning, which is safe to ignore: "Possible accidental assignment in place of a comparison. A condition expression should not be reduced to an assignment")
		if (assertsEnabled) {
			outD("assertsEnabled: " + assertsEnabled);
			verbose = true;
			outD("Verbose mode forced!");
			testing = true;
			outD("Debug mode forced!");
			dontWait = true;
			outD("-nowait mode forced!");
			outD("");
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

		//GUI Choosing code...
		if (!java.awt.GraphicsEnvironment.isHeadless() || (!NOGUI)) {
			GUI = true;
			if (testing) {
				outD("GUI: This is a graphical enviroment.");
			}

			//////
			GUI = false;				// forcing GUI to be false for now, because I don't have the GUI code ready yet!
			//////

		} else {
			GUI = false;				// No GUI for us today...
			if (testing) {
				outD("GUI: Command Line Only!");
			}
		}

		if (GUI) {	//GUI
			// Launch GUI

			/*
			try {
				(new Main()).runGUI(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
			 */

		} else {	//No GUI
			// Finally, Lets Start MLG!
			(new Main()).runCLI(args);				// this avoids "static" compiling issues.
		}

	}

	/**
	 * Start MinecraftLandGenerator (Command Line Interface)
	 * 
	 * @author Corrodias, Morlok8k
	 * @param args
	 * 
	 */
	private void runCLI(String[] args) {

		// Lets get the date, and our BuildID
		date = new Date();
		Update.readBuildID();

		// The following displays no matter what happens, so we needed this date stuff to happen first.

		out(PROG_NAME + " version " + VERSION);
		out("BuildID: (" + MLG_Last_Modified_Date.getTime() + ")");		// instead of dateformatting the buildid, we return the raw Long number. 
		// thus different timezones wont display a different buildID
		out("This version was last modified on " + dateFormat.format(MLG_Last_Modified_Date));
		out("");
		out("Uses a Minecraft server to generate square land of a specified size.");
		out("");
		out("");

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
			dontWait = true;											//if not, we dont wait for anything!
			args = newArgs;												//use the freshly parsed args for everything else now...
			out("Notice: Not waiting for anything...");
		}

		if (args.length == 0) {																//we didnt find a an X and Z size, so lets ask for one.
			out("Please Enter the size of world you want.  Example: X:1000  Z:1000");
			outP(MLG + "X:");
			xRange = Input_CLI.getInt("X:");
			outP(MLG + "Z:");
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
							DownloadFile.downloadFile(github_MLG_Conf_URL, testing);
					if (fileSuccess) {
						out(MinecraftLandGeneratorConf + " file downloaded.");
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
				out("No File to Download!");
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
							recheckFlag = !recheckFlag;
						} else {
							DownloadFile.downloadFile(line, true);
						}
					}
					in.close();

					if (recheckFlag == true) {
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
									recheckFlag = !recheckFlag;
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
				out("No File with links!");
				Time.waitTenSec(false);
			}
			return;

		} else if (args.length == 1) {
			out("For help, use java -jar " + MLGFileNameShort + " -help");
			Time.waitTenSec(false);
			return;
		}

		FileRead.readConf();

		boolean oldConf = false; // This next section checks to see if we have a old configuration file (or none!)

		if ((serverPath == null) || (javaLine == null)) { 			// MLG 1.2 Check for a valid .conf file.
			err(MinecraftLandGeneratorConf
					+ " does not contain all required properties.  Making New File!");	// Please recreate it by running this application with -conf.

			// return;

			// We no longer quit. We generate a new one with defaults.

			javaLine = defaultJavaLine;
			serverPath = ".";
			oldConf = true;
		}

		if (doneText == null) {					// MLG 1.4.0
			oldConf = true;
		} else if (preparingText == null) {	// MLG 1.4.0
			oldConf = true;
		} else if (preparingLevel == null) {	// MLG 1.4.5 / 1.5.0
			oldConf = true;
		} else if (level_1 == null) {			// MLG 1.4.5 / 1.5.0
			oldConf = true;
		} else if (level_0 == null) {			// MLG 1.5.1 / 1.6.0
			oldConf = true;
		}

		if (oldConf) {
			err("Old Version of " + MinecraftLandGeneratorConf + " found.  Updating...");

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
				err("X size too small - Changing X to 1000");
			}
			if ((zRange < 1000) && (zRange != 0)) {
				zRange = 1000;
				err("Z size too small - Changing Z to 1000");
			}

		} catch (final NumberFormatException ex) {
			err("Invalid X or Z argument.");
			err("Please Enter the size of world you want.  Example: X:1000  Z:1000");
			xRange = Input_CLI.getInt("X:");
			zRange = Input_CLI.getInt("Z:");

			//return;
		}

		verbose = false;			// Verifing that these vars are false
		alternate = false;			// before changing them...

		// This is embarrassing. Don't look.
		try {
			for (int i = 0; i < (args.length - 2); i++) {
				final String nextSwitch = args[i + 2].toLowerCase();
				if (nextSwitch.equals("-verbose") || nextSwitch.equals("-v")) {
					verbose = true;
					out("Notice: Verbose Mode");

				} else if (nextSwitch.startsWith("-i")) {
					increment = Integer.parseInt(args[i + 2].substring(2));
					out("Notice: Non-Default Increment: " + increment);

				} else if (nextSwitch.startsWith("-w")) {
					ignoreWarnings = true;
					out("Notice: Warnings from Server are Ignored");

				} else if (nextSwitch.equals("-alt") || nextSwitch.equals("-a")) {
					alternate = true;
					out("Notice: Using Alternate Launching");

				} else if (nextSwitch.startsWith("-x")) {
					xOffset = Integer.valueOf(args[i + 2].substring(2));
					out("Notice: X Offset: " + xOffset);

				} else if (nextSwitch.startsWith("-rcon")) {
					if (testing) {
						useRCON = true;
						out("Notice: Attempting to use RCON to communicate with server...");
					} else {
						useRCON = false;
						err("MLG Using RCON is not enabled yet.");
					}

				} else if (nextSwitch.startsWith("-y") || nextSwitch.startsWith("-z")) {		//NOTE: "-y" is just here for backwards compatibility
					zOffset = Integer.valueOf(args[i + 2].substring(2));
					out("Notice: Z Offset: " + zOffset);
					if (nextSwitch.startsWith("-y")) {
						out("Notice: MLG now uses Z instead of Y.  Please use the -z switch instead");
						Time.waitTenSec(false);
					}

				} else {
					serverPath = args[i + 2];
					out("Notice: Attempting to use Alternate Server:" + serverPath);
				}
			}
		} catch (final NumberFormatException ex) {
			err("Invalid switch value.");
			return;
		}

		WorldVerify.verifyWorld();

		{
			final File backupLevel = new File(worldPath + fileSeparator + "level_backup.dat");
			if (backupLevel.exists()) {
				//err("There is a level_backup.dat file left over from a previous attempt that failed. You should go determine whether to keep the current level.dat"
				//		+ " or restore the backup.");
				//err("You most likely will want to restore the backup!");
				//Time.waitTenSec(false);

				err("There is a level_backup.dat file left over from a previous attempt that failed.");
				out("Resuming...");

				//use resume data
				final File serverLevel = new File(worldPath + fileSeparator + "level.dat");
				try {
					Misc.copyFile(backupLevel, serverLevel);
				} catch (final IOException e) {
					e.printStackTrace();
				}
				backupLevel.delete();

				//return;

				FileRead.readArrayListCoordLog(worldPath + fileSeparator
						+ "MinecraftLandGenerator.log");		// we read the .log just for any resume data, if any.

				System.gc();		//run the garbage collector - hopefully free up some memory!

				xRange = resumeX;
				zRange = resumeZ;

			}
		}

		// =====================================================================
		//                              PROCESSING
		// =====================================================================

		out("Processing world \"" + worldPath + "\", in " + increment + " block increments, with: "
				+ javaLine);
		// out( MLG + "Processing \"" + worldName + "\"...");

		out("");

		// prepare our two ProcessBuilders
		// minecraft = new ProcessBuilder(javaLine, "-Xms1024m", "-Xmx1024m", "-jar", jarFile, "nogui");
		minecraft = new ProcessBuilder(javaLine.split("\\s")); // is this always going to work? i don't know.	(most likely yes)		
		minecraft.directory(new File(serverPath));
		minecraft.redirectErrorStream(true);

		try {
			out("Launching server once to make sure there is a world.");

			final long generationStartTimeTracking = System.currentTimeMillis();		//Start of time remaining calculations.

			final boolean serverLaunch = Server.runMinecraft(alternate);

			if (!(serverLaunch)) {
				System.exit(1);				// we got a warning or severe error
			}

			if ((xRange == 0) & (zRange == 0)) {  //If the server is launched with an X and a Z of zero, then we just shutdown MLG after the initial launch.
				return;
			}

			xRange = (int) (Math.ceil(((double) xRange) / ((double) 16))) * 16;			//say xRange was entered as 1000.  this changes it to be 1008, a multiple of 16. (the size of a chunk)
			zRange = (int) (Math.ceil(((double) zRange) / ((double) 16))) * 16;			//say zRange was entered as 2000.  there is no change, as it already is a multiple of 16.

			FileWrite.AppendTxtFile(
					worldPath + fileSeparator + "MinecraftLandGenerator.log",
					"# " + PROG_NAME + " " + VERSION + " - " + SelfAware.JVMinfo() + newLine + "# "
							+ MC_Server_Version + newLine + "# Started: "
							+ dateFormat.format(generationStartTimeTracking) + newLine
							+ "##Size: X" + xRange + "Z" + zRange + newLine);

			out("");

			final File serverLevel = new File(worldPath + fileSeparator + "level.dat");
			final File backupLevel = new File(worldPath + fileSeparator + "level_backup.dat");

			out("Backing up level.dat to level_backup.dat.");
			Misc.copyFile(serverLevel, backupLevel);
			out("");

			final Coordinates spawn = SpawnPoint.getSpawn(serverLevel);
			out("Spawn point detected: [X,Y,Z] " + spawn);

			FileWrite.AppendTxtFile(worldPath + fileSeparator + "MinecraftLandGenerator.log",
					"# Seed: " + randomSeed + newLine + "# Spawn: " + spawn.toString() + newLine);

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
					out("Centering land generation on [" + xOffset + ", " + zOffset
							+ "] due to switches.");
				}
			}
			out("");

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
			xLoops = ((double) xRange / (double) increment);		//How many loops do we need?
			xLoops = Math.ceil(xLoops);								//round up to find out!
			xRangeAdj = (int) (xLoops * increment);
			xLoops = xLoops + 1;

			// Z
			zLoops = ((double) zRange / (double) increment);		//How many loops do we need?
			zLoops = Math.ceil(zLoops);								//round up to find out!
			zRangeAdj = (int) (zLoops * increment);
			zLoops = zLoops + 1;

			out("Calculating Spawn Points...");

			int totalIterations = (int) (xLoops * zLoops);
			int currentIteration = 0;

			long differenceTime = System.currentTimeMillis();

			Long timeTracking = 0L;

			final ArrayList<Coordinates> launchList = new ArrayList<Coordinates>(totalIterations);

			for (int currentX = 0; currentX <= (xRangeAdj / 2); currentX += increment) {
				curXloops++;
				if (curXloops == 1) {
					currentX = (((0 - xRange) / 2) + (increment / 2) + 16);
				} else if (curXloops == xLoops) {
					currentX = (xRange / 2) - (increment / 2);
				}

				for (int currentZ = 0; currentZ <= (zRangeAdj / 2); currentZ += increment) {
					currentIteration++;

					curZloops++;
					if (curZloops == 1) {
						currentZ = (((0 - zRange) / 2) + (increment / 2) + 16);
					} else if (curZloops == zLoops) {
						currentZ = (zRange / 2) - (increment / 2);
					}

					{
						// add Coordinates to arraylist here
						final Coordinates tempCoords =
								new Coordinates(currentX + xOffset, 64, currentZ + zOffset);
						launchList.add(tempCoords);

						//TODO: remove this before release:
						System.out.println(tempCoords);
					}

					if (curZloops == 1) {
						currentZ =
								(int) ((Math.ceil((((0 - zRangeAdj) / 2) / increment))) * increment);
					}

				}
				curZloops = 0;
				if (curXloops == 1) {
					currentX = (int) ((Math.ceil((((0 - xRangeAdj) / 2) / increment))) * increment);
				}
			}

			//get existing list, and remove this list from launchList
			final ArrayList<Coordinates> removeList =
					FileRead.readArrayListCoordLog(worldPath + fileSeparator
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
						Double.toString(((double) currentIteration / (double) totalIterations) * 100);
				final int percentIndex =
						((percentDone.indexOf(".") + 3) > percentDone.length()) ? percentDone
								.length() : (percentDone.indexOf(".") + 3);		//fix index on numbers like 12.3
				percentDone =
						percentDone.substring(0,
								(percentDone.indexOf(".") == -1 ? percentDone.length()
										: percentIndex));			//Trim output, unless whole number

				out("Setting spawn to [X,Y,Z]: " + xyz + " (" + currentIteration + " of "
						+ totalIterations + ") " + percentDone + "% Done"); // Time Remaining estimate

				timeTracking = System.currentTimeMillis();

				//NEW CODE:
				differenceTime =
						(timeTracking - generationStartTimeTracking) / (currentIteration + 1);		// Updated.  we now count all runs, instead of the last 4.
				differenceTime *= 1 + (totalIterations - currentIteration);									// this should provide a more accurate result.
				out("Estimated time remaining: " + Time.displayTime(differenceTime));						// I've noticed it gets pretty accurate after about 8 launches!

				// Set the spawn point
				SpawnPoint.setSpawn(serverLevel, xyz);

				// Launch the server
				boolean serverSuccess = false;

				serverSuccess = Server.runMinecraft(alternate);
				out("");

				//////// End server launch code

				if (serverSuccess) {
					// Write the current Coordinates to log file!
					FileWrite.AppendTxtFile(worldPath + fileSeparator
							+ "MinecraftLandGenerator.log", xyz.toString() + newLine);
				} else {
					System.exit(1);				// we got a warning or severe error
				}

			}

			if (currentIteration == 0) {
				out("Nothing to generate!");
			} else {
				out("Finished generating chunks.");
			}

			Misc.copyFile(backupLevel, serverLevel);
			backupLevel.delete();
			out("Restored original level.dat.");

			out("Generation complete in: "
					+ Time.displayTime(startTime, System.currentTimeMillis()));
			Time.waitTenSec(false);

			//TODO: add if's

			if (webLaunch) {		//if webLaunch is already false, don't check for these things
				if (java.awt.GraphicsEnvironment.isHeadless()) {
					webLaunch = false;					//headless enviroment - cant bring up webpage!
				}
				final File web1 = new File("web");
				final File web2 = new File("web.txt");		//user has put in the magical file to not launch the webpage	
				final File web3 = new File("web.txt.txt");
				if (web2.exists() || (web1.exists() || web3.exists())) {  //check for "web.txt", if not found, check for "web", and if still not found, check for "web.txt.txt"
					webLaunch = false;
				}
			}

			if (webLaunch && java.awt.Desktop.isDesktopSupported()) {
				final URI splashPage =
				//URI.create("https://sites.google.com/site/minecraftlandgenerator/home/mlg_splash");
						URI.create("http://adf.ly/520855/splashbanner");
				try {
					java.awt.Desktop.getDesktop().browse(splashPage);
				} catch (final IOException e) {
					err("Error displaying webpage... " + e.getLocalizedMessage());
				}
			} else {
				out("Please Visit: http://adf.ly/520855/mlg");
				out("Or: https://sites.google.com/site/minecraftlandgenerator/");
				out("Thanks!");
			}

		} catch (final IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	/**
	 * Outputs a formatted string to System.out as a line.
	 * 
	 * @param str
	 *            String to display and format
	 * @author Morlok8k
	 */
	public static void out(final String str) {
		System.out.println(MLG + str);		// is there a better/easier way to do this?  I just wanted a lazier way to write "System.out.println(MLG + blah..."
	}

	/**
	 * Outputs a formatted string to System.err as a line.
	 * 
	 * @param str
	 *            String to display and format
	 * @author Morlok8k
	 */
	public static void err(final String str) {
		System.err.println(MLGe + str);
	}

	/**
	 * Outputs a string to System.out without a newline.
	 * 
	 * @param str
	 *            String to display and format
	 * @author Morlok8k
	 */
	public static void outP(final String str) {
		System.out.print(str);
	}

	/**
	 * Outputs a formatted string to System.out as a line.
	 * 
	 * @param str
	 *            String to display and format
	 * @author Morlok8k
	 */
	static void outS(final String str) {
		System.out.println("[Server] " + str);
	}

	/**
	 * Outputs a formatted string to System.out as a line.
	 * 
	 * @param str
	 *            String to display and format
	 * @author Morlok8k
	 */
	public static void outD(final String str) {
		System.out.println(MLG + "[DEBUG] " + str);
	}

}
