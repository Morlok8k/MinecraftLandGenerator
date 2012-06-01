package corrodias.minecraft.landgenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import morlok8k.minecraft.landgenerator.MLG_DownloadFile;
import morlok8k.minecraft.landgenerator.MLG_MD5;
import morlok8k.minecraft.landgenerator.MLG_Readme_and_HelpInfo;
import morlok8k.minecraft.landgenerator.MLG_StringArrayParse;
import morlok8k.minecraft.landgenerator.MLG_input_CLI;

import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.LongTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;

/**
 * 
 * @author Corrodias, Morlok8k, pr0f1x
 * 
 */
public class Main {

	// Version Number!
	public static final String PROG_NAME = "Minecraft Land Generator";
	public static final String VERSION = "1.6.3";
	public static final String AUTHORS = "Corrodias, Morlok8k, pr0f1x";

	public static final String fileSeparator = System.getProperty("file.separator");
	public static final String newLine = System.getProperty("line.separator");

	private int increment = 380;
	private static ProcessBuilder minecraft = null;
	private static String javaLine = null;
	private static final String defaultJavaLine =
			"java -Djava.awt.headless=true -Djline.terminal=jline.UnsupportedTerminal -Duser.language=en"
					+ " -Xms1024m -Xmx1024m -Xincgc -jar minecraft_server.jar nogui";

	private static String serverPath = null;
	private static String worldPath = null;
	private static String worldName = null;
	private static String doneText = null;
	private static String preparingText = null;
	private static String preparingLevel = null;

	private static String level_0 = null;			// the world
	private static String level_1 = null;			// the nether
	private static String level_2 = null;			// the end
	private static String level_3 = null;			// future worlds
	private static String level_4 = null;
	private static String level_5 = null;
	private static String level_6 = null;
	private static String level_7 = null;
	private static String level_8 = null;
	private static String level_9 = null;

	private int xRange = 0;
	private int zRange = 0;
	private Integer xOffset = null;
	private Integer zOffset = null;
	private static boolean verbose = false;
	private boolean alternate = false;
	private static boolean dontWait = false;
	private static boolean waitSave = false;
	private static boolean ignoreWarnings = false;
	private static LongTag randomSeed = null;

	public static String MLG = "[MLG] ";
	public static String MLGe = "[MLG-ERROR] ";

	private static DateFormat dateFormat = null;
	//private static DateFormat dateFormatBuildID = null;
	public static DateFormat dateFormat_MDY = null;
	private static Date date = null;
	public static Date MLG_Last_Modified_Date = null;
	private static Long MLG_Last_Modified_Long = 0L;

	private static final Class<?> cls = Main.class;
	private static String MLGFileName = null;
	public static String MLGFileNameShort = null;
	private static final String rsrcError = "rsrcERROR";
	private static String buildIDFile = "MLG-BuildID";
	private static boolean isCompiledAsJar = false;
	private static String MLG_Current_Hash = null;
	private static int inf_loop_protect_BuildID = 0;
	private static boolean flag_downloadedBuildID = false;
	public static Scanner sc = new Scanner(System.in);

	private static ArrayList<String> timeStamps = new ArrayList<String>();

	public static final String MinecraftLandGeneratorConf = "MinecraftLandGenerator.conf";
	public static final String defaultReadmeFile = "_MLG_Readme.txt";
	private static final String MLG_JarFile = "MinecraftLandGenerator.jar";

	private static final String github_URL =
			"https://raw.github.com/Morlok8k/MinecraftLandGenerator/master/bin/";			// just removing some redundancy

	private static final String github_MLG_Conf_URL = github_URL + MinecraftLandGeneratorConf;
	private static final String github_MLG_BuildID_URL = github_URL + buildIDFile;
	private static final String github_MLG_jar_URL = github_URL + MLG_JarFile;

	private static Boolean recheckFlag = false;
	private static long startTime = 0L;

	private static boolean useRCON = false;				//use RCON to communicate with server.  ***Experimental***
	@SuppressWarnings("unused")
	private static boolean rcon_Enabled = false;			//is server is set to use RCON?
	public static String rcon_IPaddress = "0.0.0.0";		//default is 0.0.0.0
	public static String rcon_Port = "25575";					//default is 25575, we are just initializing here.
	public static String rcon_Password = "test";			//default is "", but a password must be entered.

	//////

	public static final boolean testing = false;		// display more output when debugging

	//////

	// REMINDER: because I always forget/mix up languages:
	// "static" in java means "global"
	// "final" means "constant"

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		startTime = System.currentTimeMillis();
		(new Main()).run(args); // Why? this avoids "static" compiling issues.
	}

	/**
	 * Start MinecraftLandGenerator
	 * 
	 * @author Corrodias, Morlok8k
	 * @param args
	 * 
	 */
	private void run(String[] args) {

		// Lets get a nice Date format for display
		dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a zzzz", Locale.ENGLISH);
		dateFormat_MDY = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
		date = new Date();

		readBuildID();

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
		newArgs = MLG_StringArrayParse.Parse(newArgs, "-n");
		newArgs = MLG_StringArrayParse.Parse(newArgs, "-nowait");
		if (!(args.equals(newArgs))) {
			dontWait = true;
			args = newArgs;
		}

		if (args.length == 0) {
			out("Please Enter the size of world you want.  Example: X:1000  Z:1000");
			outP(MLG + "X:");
			xRange = MLG_input_CLI.getInt("X:");
			outP(MLG + "Z:");
			zRange = MLG_input_CLI.getInt("Z:");
			args = new String[] { String.valueOf(xRange), String.valueOf(zRange) };

		}

		if (args[0].equalsIgnoreCase("-version") || args[0].equalsIgnoreCase("-help")
				|| args[0].equals("/?")) {

			showHelp(true);

			return;
		}

		// =====================================================================
		//                         STARTUP AND CONFIG
		// =====================================================================

		// the arguments are apparently okay so far. parse the conf file.
		if (args[0].equalsIgnoreCase("-conf")) {

			if (args.length == 2) {
				if (args[1].equalsIgnoreCase("download")) {
					boolean fileSuccess = downloadFile(github_MLG_Conf_URL, testing);
					if (fileSuccess) {
						out(MinecraftLandGeneratorConf + " file downloaded.");
						return;
					}
				}
			}

			saveConf(true);  //new conf file
			return;

		} else if (args[0].equalsIgnoreCase("-ps") || args[0].equalsIgnoreCase("-printspawn")) {
			// okay, sorry, this is an ugly hack, but it's just a last-minute feature.
			printSpawn();
			waitTenSec(false);
			return;
		} else if (args[0].equalsIgnoreCase("-build")) {
			buildID(false);
			return;
		} else if (args[0].equalsIgnoreCase("-update")) {
			updateMLG();
			waitTenSec(false);
			return;
		} else if (args[0].equalsIgnoreCase("-readme")) {

			if (args.length == 2) {
				readMe(args[1]);
			} else {
				readMe(null);
			}
			return;
		} else if (args[0].equalsIgnoreCase("-downloadfile")) {
			if (args.length == 2) {
				downloadFile(args[1], true);
			} else {
				out("No File to Download!");
				waitTenSec(false);
			}
			return;

		} else if (args[0].equalsIgnoreCase("-downloadlist")) {

			if (args.length == 2) {
				String origMD5 = "";
				String recheckMD5 = "";

				try {
					File config = new File(args[1]);
					try {
						origMD5 = fileMD5(config.toString());
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					BufferedReader in = new BufferedReader(new FileReader(config));
					String line;
					while ((line = in.readLine()) != null) {
						if (line.contains("###RECHECK###")) {
							recheckFlag = !recheckFlag;
						} else {
							downloadFile(line, true);
						}
					}
					in.close();

					if (recheckFlag == true) {
						try {
							recheckMD5 = fileMD5(config.toString());
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (!origMD5.contentEquals(recheckMD5)) {
							BufferedReader in_recheck = new BufferedReader(new FileReader(config));
							String line_recheck;
							while ((line_recheck = in_recheck.readLine()) != null) {
								if (line_recheck.contains("###RECHECK###")) {
									recheckFlag = !recheckFlag;
								} else {
									downloadFile(line_recheck, true);
								}
							}
							in_recheck.close();
						}

					}

				} catch (FileNotFoundException ex) {
					System.err.println(args[1] + " - File not found");
					waitTenSec(false);
					return;
				} catch (IOException ex) {
					System.err.println(args[1] + " - Could not read file.");
					waitTenSec(false);
					return;
				}
			} else {
				out("No File with links!");
				waitTenSec(false);
			}
			return;

		} else if (args.length == 1) {
			out("For help, use java -jar " + MLGFileNameShort + " -help");
			waitTenSec(false);
			return;
		}

		readConf();

		boolean oldConf = false; // This next section checks to see if we have a old configuration file (or none!)

		if (serverPath == null || javaLine == null) { 			// MLG 1.2 Check for a valid .conf file.
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

			saveConf(false);		//old conf

			waitTenSec(false);
			return;

		}

		// ARGUMENTS
		try {
			xRange = Integer.parseInt(args[0]);
			zRange = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			err("Invalid X or Z argument.");
			err("Please Enter the size of world you want.  Example: X:1000  Z:1000");
			xRange = MLG_input_CLI.getInt("X:");
			zRange = MLG_input_CLI.getInt("Z:");

			//return;
		}

		verbose = false;			// Verifing that these vars are false
		alternate = false;			// before changing them...

		// This is embarrassing. Don't look.
		try {
			for (int i = 0; i < args.length - 2; i++) {
				String nextSwitch = args[i + 2].toLowerCase();
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
						waitTenSec(false);
					}

				} else {
					serverPath = args[i + 2];
					out("Notice: Attempting to use Alternate Server:" + serverPath);
				}
			}
		} catch (NumberFormatException ex) {
			err("Invalid switch value.");
			return;
		}

		verifyWorld();

		{
			File backupLevel = new File(worldPath + fileSeparator + "level_backup.dat");
			if (backupLevel.exists()) {
				err("There is a level_backup.dat file left over from a previous attempt that failed. You should go determine whether to keep the current level.dat"
						+ " or restore the backup.");
				err("You most likely will want to restore the backup!");
				waitTenSec(false);
				return;
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
		minecraft = new ProcessBuilder(javaLine.split("\\s")); // is this always going to work? i don't know.			
		minecraft.directory(new File(serverPath));
		minecraft.redirectErrorStream(true);

		try {
			out("Launching server once to make sure there is a world.");

			long generationStartTimeTracking = System.currentTimeMillis();		//Start of time remaining calculations.

			runMinecraft(verbose, alternate);

			if ((xRange == 0) & (zRange == 0)) {  //If the server is launched with an X and a Z of zero, then we just shutdown MLG after the initial launch.
				return;
			}

			xRange = (int) (Math.ceil(((double) xRange) / ((double) 16))) * 16;			//say xRange was entered as 1000.  this changes it to be 1008, a multiple of 16. (the size of a chunk)
			zRange = (int) (Math.ceil(((double) zRange) / ((double) 16))) * 16;			//say zRange was entered as 2000.  there is no change, as it already is a multiple of 16.

			out("");

			File serverLevel = new File(worldPath + fileSeparator + "level.dat");
			File backupLevel = new File(worldPath + fileSeparator + "level_backup.dat");

			out("Backing up level.dat to level_backup.dat.");
			copyFile(serverLevel, backupLevel);
			out("");

			Integer[] spawn = getSpawn(serverLevel);
			out("Spawn point detected: [X,Y,Z] [" + spawn[0] + ", " + spawn[1] + ", " + spawn[2]
					+ "]");
			{
				boolean overridden = false;
				if (xOffset == null) {
					xOffset = spawn[0];
				} else {
					overridden = true;
				}
				if (zOffset == null) {
					zOffset = spawn[2];
				} else {
					overridden = true;
				}
				if (overridden) {
					out("Centering land generation on [" + xOffset + ", " + zOffset
							+ "] due to switches.");
				}
			}
			out("");

			// new code to optimize the locations
			Integer incrementX, incrementZ;
			Double blah;
			double xLoops, zLoops;

			// X
			blah = ((double) xRange / (double) increment);		//How many loops do we need?
			xLoops = Math.ceil(blah);							//round up to find out!
			blah = Math.floor(xRange / xLoops);						//optimal distance calculations here
			incrementX = blah.intValue();							//save to an int
			blah =
					Math.floor((double) xRange
							/ Math.ceil(((double) xRange / ((double) increment + 20))));
			if (blah < increment) {							//should we use 380 or 400 as our original increment?  This decides it.
				incrementX = blah.intValue();
			}
			if (blah.isInfinite()) {
				incrementX = 0;			// An Infinity error.  this should never be less than (increment/2)!
			} else if (incrementX < (increment / 2)) {
				incrementX = 0;			// Should never happen except for the Infinity error
			} else if (incrementX > increment) {
				incrementX = increment;	// Should never happen. Just in case!
			}

			// Z
			blah = ((double) zRange / (double) increment);		//How many loops do we need?
			zLoops = Math.ceil(blah);							//round up to find out!
			blah = Math.floor(zRange / zLoops);						//optimal distance calculations here
			incrementZ = blah.intValue();							//save to an int
			blah =
					Math.floor((double) zRange
							/ Math.ceil(((double) zRange / ((double) increment + 20))));
			if (blah < increment) {							//should we use 380 or 400 as our original increment?  This decides it.
				incrementZ = blah.intValue();
			}
			if (blah.isInfinite()) {
				incrementZ = 0;			// An Infinity error.  this should never be less than (increment/2)!
			} else if (incrementZ < (increment / 2)) {
				incrementZ = 0;			// Should never happen except for the Infinity error
			} else if (incrementZ > increment) {
				incrementZ = increment;	// Should never happen. Just in case!
			}

			blah = null;	// I'm done with this temporary variable now.  I used it to make my code simplier,
			// and so I wouldn't need to constantly use casting
			// (as java will do it if one of the numbers is already a double)

			if (verbose) {
				if (incrementX != increment) {
					out("Optimized X increments from: " + increment + " to: " + incrementX);
				}
				if (incrementZ != increment) {
					out("Optimized Z increments from: " + increment + " to: " + incrementZ);
				}
			}
			// end new code for location optimizations

			int totalIterations = (int) (xLoops * zLoops);
			int currentIteration = 0;

			int curXloops = 0;
			int curZloops = 0;

			long differenceTime = System.currentTimeMillis();

			Long timeTracking = 0L;

			for (int currentX = (((0 - xRange) / 2) + (incrementX / 2)); currentX <= (xRange / 2); currentX +=
					incrementX) {
				curXloops++;
				if (curXloops == 1) {
					currentX = (((0 - xRange) / 2) + (increment / 2) + 16);
				} else if (curXloops == xLoops) {
					currentX = (xRange / 2) - (increment / 2);
				}

				for (int currentZ = (((0 - zRange) / 2) + (incrementZ / 2)); currentZ <= (zRange / 2); currentZ +=
						incrementZ) {
					currentIteration++;

					curZloops++;
					if (curZloops == 1) {
						currentZ = (((0 - zRange) / 2) + (increment / 2) + 16);
					} else if (curZloops == zLoops) {
						currentZ = (zRange / 2) - (increment / 2);
					}

					String curX = Integer.toString(currentX + xOffset);
					//String curY = "64";		//Y is always set to 64
					String curZ = Integer.toString(currentZ + zOffset);
					String percentDone =
							Double.toString((double) ((double) currentIteration / (double) totalIterations) * 100);
					int percentIndex =
							((percentDone.indexOf(".") + 3) > percentDone.length()) ? percentDone
									.length() : (percentDone.indexOf(".") + 3);		//fix index on numbers like 12.3
					percentDone =
							percentDone.substring(0,
									(percentDone.indexOf(".") == -1 ? percentDone.length()
											: percentIndex));			//Trim output, unless whole number

					out("Setting spawn to (X,Y,Z): [" + curX + ", 64, " + curZ + "] ("
							+ currentIteration + "/" + totalIterations + ") " + percentDone
							+ "% Done"); // Time Remaining estimate

					if (testing) {
						out("X:" + curXloops + ", Z:" + curZloops);
					}

					timeTracking = System.currentTimeMillis();

					//NEW CODE:
					differenceTime =
							(timeTracking - generationStartTimeTracking) / (currentIteration + 1);		// Updated.  we now count all runs, instead of the last 4.
					differenceTime *= 1 + (totalIterations - currentIteration);							// this should provide a more accurate result.
					out("Estimated time remaining: " + displayTime(differenceTime));					// I've noticed it gets pretty accurate after about 8 launches!

					// Set the spawn point
					setSpawn(serverLevel, currentX + xOffset, 64, currentZ + zOffset);

					// Launch the server
					runMinecraft(verbose, alternate);
					out("");

					if (curZloops == 1) {
						currentZ = (((0 - zRange) / 2) + (incrementZ / 2));
					}

				}
				curZloops = 0;
				if (curXloops == 1) {
					currentX = (((0 - xRange) / 2) + (incrementX / 2));
				}
			}

			out("Finished generating chunks.");
			copyFile(backupLevel, serverLevel);
			backupLevel.delete();
			out("Restored original level.dat.");
			//finishedImage();			//disabled, because I didn't care for it - it didn't flow well with MLG
			out("Generation complete in: " + displayTime(startTime, System.currentTimeMillis()));
			waitTenSec(false);
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	//TODO: update this
	/**
	 * @param level
	 * @return
	 * @throws IOException
	 * @author Corrodias
	 */
	protected static Integer[] getSpawn(File level) throws IOException {
		try {
			NBTInputStream input = new NBTInputStream(new FileInputStream(level));
			CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
			input.close();

			Map<String, Tag> originalData =
					((CompoundTag) originalTopLevelTag.getValue().get("Data")).getValue();
			// This is our map of data. It is an unmodifiable map, for some
			// reason, so we have to make a copy.
			Map<String, Tag> newData = new LinkedHashMap<String, Tag>(originalData);
			// .get() a couple of values, just to make sure we're dealing with a
			// valid level file, here. Good for debugging, too.
			IntTag spawnX = (IntTag) newData.get("SpawnX");
			IntTag spawnY = (IntTag) newData.get("SpawnY");
			IntTag spawnZ = (IntTag) newData.get("SpawnZ");

			randomSeed = (LongTag) newData.get("RandomSeed");
			out("Seed: " + randomSeed.getValue()); // lets output the seed, cause why not?

			Integer[] ret =
					new Integer[] { spawnX.getValue(), spawnY.getValue(), spawnZ.getValue() };
			return ret;
		} catch (ClassCastException ex) {
			throw new IOException("Invalid level format.");
		} catch (NullPointerException ex) {
			throw new IOException("Invalid level format.");
		}
	}

	/**
	 * Changes the spawn point in the given Alpha/Beta level to the given coordinates.<br>
	 * Note that, in Minecraft levels, the Y coordinate is height.<br>
	 * (We picture maps from above, but the game was made from a different perspective)
	 * 
	 * @param level
	 *            the level file to change the spawn point in
	 * @param x
	 *            the new X value
	 * @param y
	 *            the new Y value
	 * @param z
	 *            the new Z value
	 * @throws IOException
	 *             if there are any problems reading/writing the file
	 * @author Corrodias
	 */
	protected static void setSpawn(File level, Integer x, Integer y, Integer z) throws IOException {
		try {
			NBTInputStream input = new NBTInputStream(new FileInputStream(level));
			CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
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

			Map<String, Tag> originalData =
					((CompoundTag) originalTopLevelTag.getValue().get("Data")).getValue();
			// This is our map of data. It is an unmodifiable map, for some reason, so we have to make a copy.
			Map<String, Tag> newData = new LinkedHashMap<String, Tag>(originalData);
			// .get() a couple of values, just to make sure we're dealing with a valid level file, here. Good for debugging, too.

			@SuppressWarnings("unused")
			IntTag spawnX = (IntTag) newData.get("SpawnX"); // we never use these... Its only here for potential debugging.
			@SuppressWarnings("unused")
			IntTag spawnY = (IntTag) newData.get("SpawnY"); // but whatever... so I (Morlok8k) suppressed these warnings.
			@SuppressWarnings("unused")
			IntTag spawnZ = (IntTag) newData.get("SpawnZ"); // I don't want to remove existing code, either by myself (Morlok8k) or Corrodias

			newData.put("SpawnX", new IntTag("SpawnX", x));
			newData.put("SpawnY", new IntTag("SpawnY", y));
			newData.put("SpawnZ", new IntTag("SpawnZ", z));

			// Again, we can't modify the data map in the old Tag, so we have to make a new one.
			CompoundTag newDataTag = new CompoundTag("Data", newData);
			Map<String, Tag> newTopLevelMap = new HashMap<String, Tag>(1);
			newTopLevelMap.put("Data", newDataTag);
			CompoundTag newTopLevelTag = new CompoundTag("", newTopLevelMap);

			NBTOutputStream output = new NBTOutputStream(new FileOutputStream(level));
			output.writeTag(newTopLevelTag);
			output.close();
		} catch (ClassCastException ex) {
			throw new IOException("Invalid level format.");
		} catch (NullPointerException ex) {
			throw new IOException("Invalid level format.");
		}
	}

	/**
	 * Starts the process in the given ProcessBuilder, monitors its output for a "[INFO] Done!" message, and sends it a "stop\r\n" message. One message is printed to the console before launching and
	 * one is printed to the console when the Done! message is detected. If "verbose" is true, the process's output will also be printed to the console.
	 * 
	 * @param minecraft
	 * @param verbose
	 * @throws IOException
	 * @author Corrodias
	 */
	protected static void runMinecraft(boolean verbose, boolean alternate) throws IOException {
		if (verbose) {
			out("Starting server.");
		}

		boolean warning = false;
		boolean cantKeepUp = false;
		final boolean ignoreWarningsOriginal = ignoreWarnings;

		// monitor output and print to console where required.
		// STOP the server when it's done.

		if (alternate) { // Alternate - a replication (slightly stripped down) of MLG 1.3.0's code. simplest code possible.
			out("Alternate Launch");
			Process process = minecraft.start();

			byte[] stop = { 's', 't', 'o', 'p', '\r', '\n' };

			// monitor output and print to console where required.
			// STOP the server when it's done.
			BufferedReader pOut =
					new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = pOut.readLine()) != null) {

				line = line.trim(); //Trim spaces off the beginning and end, if any.

				out(line);
				if (line.contains(doneText)) { // EDITED By Morlok8k for Minecraft 1.3+ Beta
					OutputStream outputStream = process.getOutputStream();

					out("Stopping server...  (Please Wait...)");
					outputStream.write(stop);
					outputStream.flush();

				}
			}
			// readLine() returns null when the process exits

		} else { // start minecraft server normally!
			Process process = minecraft.start();
			if (verbose) {
				out("Started Server.");
			}
			BufferedReader pOut =
					new BufferedReader(new InputStreamReader(process.getInputStream()));
			if (verbose) {
				out("Accessing Server Output...");
			}

			String line = null;
			String outTmp = "";
			String outTmp2 = null;

			byte[] stop = { 's', 't', 'o', 'p', '\r', '\n' }; // Moved here, so this code wont run every loop, thus Faster!
			// and no, i can't use a string here!

			byte[] saveAll = { 's', 'a', 'v', 'e', '-', 'a', 'l', 'l', '\r', '\n' };

			boolean prepTextFirst = true;

			OutputStream outputStream = process.getOutputStream(); // moved here to remove some redundancy

			//TODO:  add converting section:
			/*
			2012-02-29 03:50:28 [INFO] Converting map!
			Scanning folders...
			Total conversion count is 9
			2012-02-29 03:50:29 [INFO] Converting... 8%
			2012-02-29 03:50:30 [INFO] Converting... 9%
			2012-02-29 03:50:31 [INFO] Converting... 10%
			2012-02-29 03:50:32 [INFO] Converting... 12%
			2012-02-29 03:50:33 [INFO] Converting... 13%
			*/

			boolean convertedMapFormattingFlag = false;		// This allows MLG to track if we converted a map to a new format (such as Chunk-file -> McRegion, or McRegion -> Anvil)
			// just so it gets a line ending after the % output finishes
			while ((line = pOut.readLine()) != null) {

				line = line.trim();

				if (verbose) {
					if (line.contains("[INFO]")) {		//TODO: add to .conf
						out(line.substring(line.lastIndexOf("]") + 2));
					} else {
						out(line);
					}
				} else if (line.contains(preparingText) || line.contains("Converting...")) {
					if (line.contains("Converting...")) {
						convertedMapFormattingFlag = true;
					}
					outTmp2 = line.substring(line.length() - 3, line.length());
					outTmp2 = outTmp2.trim();				//we are removing extra spaces here
					if (outTmp.equals(outTmp2)) {
						//instead of printing the same number, we add another dot
						outP(".");
					} else {
						outTmp = outTmp2;

						if (prepTextFirst) {
							outP(MLG + outTmp + "...");
							prepTextFirst = false;
						} else {
							outP(" " + outTmp + "...");
						}

					}

				} else if (line.contains(preparingLevel)) {
					prepTextFirst = true;

					if (convertedMapFormattingFlag == true) {
						outP(newLine);
						convertedMapFormattingFlag = false;
					}

					if (line.contains("level 0")) { // "Preparing start region for level 0"
						outP(MLG + worldName + ": " + level_0 + ":" + newLine);
					} else if (line.contains("level 1")) { // "Preparing start region for level 1"
						outP(newLine + MLG + worldName + ": " + level_1 + ":" + newLine);
					} else if (line.contains("level 2")) { // "Preparing start region for level 2"
						outP(newLine + MLG + worldName + ": " + level_2 + ":" + newLine);
					} else if (line.contains("level 3")) { // "Preparing start region for level 3"
						outP(newLine + MLG + worldName + ": " + level_3 + ":" + newLine);
					} else if (line.contains("level 4")) { // "Preparing start region for level 4"
						outP(newLine + MLG + worldName + ": " + level_4 + ":" + newLine);
					} else if (line.contains("level 5")) { // "Preparing start region for level 5"
						outP(newLine + MLG + worldName + ": " + level_5 + ":" + newLine);
					} else if (line.contains("level 6")) { // "Preparing start region for level 6"
						outP(newLine + MLG + worldName + ": " + level_6 + ":" + newLine);
					} else if (line.contains("level 7")) { // "Preparing start region for level 7"
						outP(newLine + MLG + worldName + ": " + level_7 + ":" + newLine);
					} else if (line.contains("level 8")) { // "Preparing start region for level 8"
						outP(newLine + MLG + worldName + ": " + level_8 + ":" + newLine);
					} else if (line.contains("level 9")) { // "Preparing start region for level 9"
						outP(newLine + MLG + worldName + ": " + level_9 + ":" + newLine);
					} else {
						outP(newLine + MLG + line.substring(line.lastIndexOf("]") + 2));
					}
				} else if (line.contains("server version") || line.contains("Converting map!")) {	//TODO: add to .conf
					out(line.substring(line.lastIndexOf("]") + 2));
				}

				if (line.contains(doneText)) { // now this is configurable!
					outP(newLine);
					if (waitSave) {
						out("Waiting 30 seconds to save...");

						int count = 1;
						while (count <= 30) {
							outP(".");

							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							count += 1;
						}
						out("");
					}
					out("Saving server data...");
					outputStream.write(saveAll);
					outputStream.flush();

					out("Stopping server...  (Please Wait...)");
					// OutputStream outputStream = process.getOutputStream();
					outputStream.write(stop);
					outputStream.flush();
					// outputStream.close();

					if (waitSave) {
						out("Waiting 10 seconds to save.");
						int count = 1;
						while (count <= 10) {
							outP(".");

							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							count += 1;
						}
						out("");
					}
				}

				//Here we want to ignore the most common warning: "Can't keep up!"
				if (line.contains("Can't keep up!")) {	//TODO: add to .conf
					cantKeepUp = true;			//[WARNING] Can't keep up! Did the system time change, or is the server overloaded?
					ignoreWarnings = true;
				}

				if (ignoreWarnings == false) {
					if (line.contains("[WARNING]")) { // If we have a warning, stop...
						out("");
						out("Warning found: Stopping " + PROG_NAME);
						if (verbose == false) { // If verbose is true, we already displayed it.
							out(line);
						}
						out("");
						out("Forcing Save...");
						outputStream.write(saveAll);
						outputStream.flush();
						// OutputStream outputStream = process.getOutputStream();
						outputStream.write(stop); // if the warning was a fail to bind to port, we may need to write stop twice!
						outputStream.flush();
						outputStream.write(stop);
						outputStream.flush();
						// outputStream.close();
						warning = true;
						// System.exit(1);
					}
					if (line.contains("[SEVERE]")) { // If we have a severe error, stop...
						out("");
						out("Severe error found: Stopping server.");
						if (verbose == false) { // If verbose is true, we already displayed it.
							out(line);
						}
						out("");
						out("Forcing Save...");
						outputStream.write(saveAll);
						outputStream.flush();
						// OutputStream outputStream = process.getOutputStream();
						outputStream.write(stop);
						outputStream.flush();
						outputStream.write(stop); // sometimes we need to do stop twice...
						outputStream.flush();
						// outputStream.close();
						warning = true;
						// System.exit(1);
						// Quit!
					}
				}

				if (cantKeepUp) {
					ignoreWarnings = ignoreWarningsOriginal;
				}
			}

			if (warning == true) { // in 1.4.4 we had a issue. tried to write stop twice, but we had closed the stream already. this, and other lines should fix this.
				outputStream.flush();
				outputStream.close();
				System.exit(1);
			}

			outputStream.close();
		}

		// readLine() returns null when the process exits
	}

	/**
	 * I'd love to use nio, but it requires Java 7.<br>
	 * I could use Apache Commons, but i don't want to include a library for one little thing.<br>
	 * Copies src file to dst file.<br>
	 * If the dst file does not exist, it is created<br>
	 * 
	 * @author Corrodias
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	private static void copyFile(File src, File dst) throws IOException {
		InputStream copyIn = new FileInputStream(src);
		OutputStream copyOut = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = copyIn.read(buf)) >= 0) {
			if (len > 0) {
				copyOut.write(buf, 0, len);
			}
		}
		copyIn.close();
		copyOut.flush();
		copyOut.close();
	}

	//TODO: add description
	/**
	 * @return
	 */
	private boolean printSpawn() {
		// ugh, sorry, this is an ugly hack, but it's a last-minute feature. this is a lot of duplicated code.
		// - Fixed by Morlok8k

		readConf();
		verifyWorld();

		File level = new File(worldPath + fileSeparator + "level.dat");
		try {
			Integer[] spawn = getSpawn(level);
			out("The current spawn point is: [X,Y,Z] [" + spawn[0] + ", " + spawn[1] + ", "
					+ spawn[2] + "]");
			return true;
		} catch (IOException ex) {
			err("Error while reading " + level.getPath());
			return false;
		}
	}

	/**
	 * Saves a Readme file.
	 * 
	 * @param readmeFile
	 * @author Morlok8k
	 * 
	 */
	private static void readMe(String readmeFile) {

		MLG_Readme_and_HelpInfo.readMe(readmeFile);

	}

	/**
	 * @author Morlok8k
	 * @param URL
	 *            URL in a String
	 * @param Output
	 *            Displays output if true
	 * @return Boolean: true if download was successful, false if download wasn't
	 */
	private static boolean downloadFile(String URL, boolean Output) {
		//This exists so I don't need to type "MLG_DownloadFile.downloadFile" every time.
		return MLG_DownloadFile.downloadFile(URL, Output);
	}

	/**
	 * This is an "undocumented" function to create a BuildID file. It should only be used right after compiling a .jar file<br>
	 * The resulting BuildID file is uploaded to github, and also distributed with the program.<br>
	 * <b>THE FILE SHOULD ONLY BE MADE FROM SCRATCH AT THE TIME OF PUBLISHING!</b><br>
	 * (Otherwise, the purpose is defeated!)<br>
	 * <br>
	 * The problem is that when a .jar file is downloaded from the internet, it gets a new date stamp - the time that it was downloaded. Only the original copy that was compiled on the original
	 * computer will have the correct time stamp. (or possibly a copy from the original computer)<br>
	 * <br>
	 * This saves the hash and the timestamp (now known as the BuildID)
	 * 
	 * @author Morlok8k
	 */
	private static void buildID(boolean downloadOnly) {

		// download BuildID from Github.
		boolean fileSuccess = downloadFile(github_MLG_BuildID_URL, testing);
		if (fileSuccess) {
			out(buildIDFile + " file downloaded.");
			flag_downloadedBuildID = true;

			if (downloadOnly) { return; }

		}

		if (downloadOnly) {
			err("Couldn't Download new " + buildIDFile);
			return;
		}

		// If not available, create.
		// After downloading, check to see if it matches hash.

		if (MLGFileName == null) {
			try {
				MLGFileName = getClassLoader(cls);
			} catch (Exception e) {
				out("Error: Finding file failed");
				e.printStackTrace();
			}
			if (MLGFileName.equals(rsrcError)) { return; }
		}

		if (MLG_Current_Hash == null) {

			try {
				MLG_Current_Hash = fileMD5(MLGFileName);
				// out(hash + "  " + MLGFileName);Â®
			} catch (Exception e) {
				out("Error: MLG_MD5 from file failed");
				e.printStackTrace();
			}
		}

		Date time = null;
		try {
			time = getCompileTimeStamp(cls);
		} catch (Exception e) {
			out("Error: TimeStamp from file failed");
			e.printStackTrace();
		}
		// out(d.toString());

		boolean notNew = false;
		String INFO = "";
		if (isCompiledAsJar == false) {
			INFO = " (Class File, Not .Jar)";
		}

		try {
			String line;

			BufferedReader inFile = new BufferedReader(new FileReader(buildIDFile));
			BufferedWriter outFile = new BufferedWriter(new FileWriter(buildIDFile + ".temp"));

			while ((line = inFile.readLine()) != null) {

				if (line.contains(MLG_Current_Hash)) {
					notNew = true;
					if (testing) {
						out("[DEBUG] NotNew");
					}
				}

				outFile.write(line);
				outFile.newLine();
			}

			if (notNew == false) {
				outFile.write(MLG_Current_Hash + "=" + String.valueOf(time.getTime()) + "# MLG v"
						+ VERSION + INFO);
				outFile.newLine();
			}
			outFile.close();
			inFile.close();

			File fileDelete = new File(buildIDFile);
			fileDelete.delete();
			File fileRename = new File(buildIDFile + ".temp");
			fileRename.renameTo(new File(buildIDFile));

		} catch (FileNotFoundException ex) {
			out("\"" + buildIDFile + "\" file not Found.  Generating New \"" + buildIDFile
					+ "\" File");

			writeTxtFile(buildIDFile, MLG_Current_Hash + "=" + String.valueOf(time.getTime())
					+ "#MLG v" + VERSION + INFO);

		} catch (IOException ex) {
			err("Could not create \"" + buildIDFile + "\".");
			return;
		}

	}

	/**
	 * Gets the BuildID for MLG
	 * 
	 * @author Morlok8k
	 * 
	 */
	private void readBuildID() {

		if (inf_loop_protect_BuildID > 10) {
			MLG_Last_Modified_Date = new Date(new Long(0));  //set the day to Jan 1, 1970 for failure
			return;
		}
		inf_loop_protect_BuildID++;		// this is to prevent an infinite loop (however unlikely)

		if (MLGFileName == null) {
			try {
				MLGFileName = getClassLoader(cls);
			} catch (Exception e) {
				out("Error: Finding file failed");
				e.printStackTrace();
			}
			if (MLGFileName.equals(rsrcError)) { return; }
		}

		MLGFileNameShort =
				MLGFileName.substring(MLGFileName.lastIndexOf(fileSeparator) + 1,
						MLGFileName.length());

		if (testing) {
			out("Currently Running as file:" + MLGFileNameShort);
		}

		if (MLG_Current_Hash == null) {

			try {
				MLG_Current_Hash = fileMD5(MLGFileName);
				// out(hash + "  " + MLGFileName);
			} catch (Exception e) {
				out("Error: MLG_MD5 from file failed");
				e.printStackTrace();
			}
		}

		int tsCount = 0;

		timeStamps.clear();

		if (MLG_Last_Modified_Date == null) {
			boolean foundLine = false;
			try {
				BufferedReader in = new BufferedReader(new FileReader(buildIDFile));
				String line;

				while ((line = in.readLine()) != null) {

					int pos = line.indexOf('=');
					if (pos == -1) { // If we have no = sign
						pos = 0;
					}

					int end = line.lastIndexOf('#'); // comments, ignored lines

					if (end == -1) { // If we have no hash sign, then we read till the end of the line
						end = line.length();
					}
					if (end <= pos) { // If hash is before the '=', we may have an issue... it should be fine, cause we check for issues next, but lets make sure.
						end = line.length();
					}

					timeStamps.add(line.substring(pos + 1, end));

					if (testing) {
						out(timeStamps.get(tsCount));
					}

					tsCount++;

					if (line.contains(MLG_Current_Hash)) {
						// out("[DEBUG] Found!");
						foundLine = true;

						if (pos != -1) {
							if (line.substring(0, pos).equals(MLG_Current_Hash)) {
								MLG_Last_Modified_Long = new Long(line.substring(pos + 1, end));
								MLG_Last_Modified_Date = new Date(MLG_Last_Modified_Long);

								Long highestModTime = ZipGetModificationTime(MLGFileName);
								long tCalc = MLG_Last_Modified_Long - highestModTime;

								if (testing) {
									err("tCalc\tMLG_Last_Modified_Long\thighestModTime" + newLine
											+ tCalc + "\t" + MLG_Last_Modified_Long + "\t"
											+ highestModTime);
								}

								if (highestModTime == 0L) {

									err("Archive Intergrity Check Failed: .zip/.jar file Issue.");
									err("Archive Intergrity Check Failed: (MLG will still run.  Just note that this may not be an official version.)");

								} else {
									if (tCalc < -15000L) {

										//time is newer?  (.zip file is newer than BuildID)
										err("Archive Intergrity Check Failed: .zip file is newer than BuildID. Offset: "
												+ (tCalc / 1000) + "sec.");
										err("Archive Intergrity Check Failed: (MLG will still run.  Just note that this may not be an official version.)");
									}

									if (tCalc < 15000L) {

										//times are within 30 seconds (+/- 15 seconds) of each other.  (typically 1-2 seconds, but left room for real-world error)
										if (testing | flag_downloadedBuildID) {
											out("Archive Intergrity Check Passed. Offset: "
													+ (tCalc / 1000) + "sec.");
										}

									} else {
										//times dont match.  (.zip file is older than specified BuildID)
										err("Archive Intergrity Check Failed: .zip file is older than BuildID. Offset: "
												+ (tCalc / 1000) + "sec.");
										err("Archive Intergrity Check Failed: (MLG will still run.  Just note that this may not be an official version.)");
									}
								}
								//return;
							}

						}
					}

				}
				in.close();

				if (foundLine == false) {
					// out("[DEBUG] FoundLine False");
					buildID(false);
					readBuildID();	// yes I'm calling the function from itself. potential infinite loop? possibly. I haven't encountered it yet!
					return;
				}
			} catch (Exception e) {
				err("Cant Read " + buildIDFile + "!");
				err(e.getLocalizedMessage());
				err("");
				// e.printStackTrace();
				buildID(false);
				readBuildID();
				return;

			}
		}

	}

	/**
	 * Updates MLG to the Latest Version
	 * 
	 * @author Morlok8k
	 * 
	 */
	private void updateMLG() {

		buildID(true);		//get latest BuildID file.  
		MLG_Last_Modified_Date = null;
		readBuildID();

		Iterator<String> e = timeStamps.iterator();
		String s;
		int diff;

		//boolean renameFailed = false;

		while (e.hasNext()) {
			s = e.next();
			diff = MLG_Last_Modified_Date.compareTo(new Date(new Long(s)));
			//out(diff);

			if (diff < 0) {	// if this is less than 0, there is a new version of MLG on the Internet!
				out("There is a NEW VERSION Of " + PROG_NAME + " available online!");

				try {
					File fileRename = new File(MLG_JarFile);
					fileRename.renameTo(new File(MLG_JarFile + ".old"));
				} catch (Exception e1) {
					out("Rename attempt #1 failed!");
					e1.printStackTrace();

					try {
						copyFile(new File(MLG_JarFile), new File(MLG_JarFile + ".old"));
						File fileDelete = new File(MLG_JarFile);
						fileDelete.delete();
					} catch (Exception e2) {
						out("Rename attempt #2 failed!");
						e2.printStackTrace();
						//renameFailed = true;
						return;
					}

				}

				boolean fileSuccess = downloadFile(github_MLG_jar_URL, true);
				if (fileSuccess) {
					out(MLG_JarFile + " downloaded.");
					return;
				}

			}
		}

	}

	/**
	 * This gets the filename of a .jar (typically this one!)
	 * 
	 * @author Morlok8k
	 */
	private static String getClassLoader(Class<?> classFile) throws IOException {
		ClassLoader loader = classFile.getClassLoader();
		String filename = classFile.getName().replace('.', '/') + ".class";
		URL resource =
				(loader != null) ? loader.getResource(filename) : ClassLoader
						.getSystemResource(filename);
		filename = URLDecoder.decode(resource.toString(), "UTF-8");
		// out(filename);

		// START Garbage removal:
		int bang = filename.indexOf("!");		// remove everything after xxxx.jar
		if (bang == -1) { 						// a real example:
			bang = filename.length();			// jar:file:/home/morlok8k/test.jar!/me/Morlok8k/test/Main.class
		}
		int file = filename.indexOf("file:");	// removes junk from the beginning of the path
		file = file + 5;
		if (file == -1) {
			file = 0;
		}
		if (filename.contains("rsrc:")) {
			err("THIS WAS COMPILED USING \"org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader\"! ");
			err("DO NOT PACKAGE YOUR .JAR'S WITH THIS CLASSLOADER CODE!");
			err("(Your Libraries need to be extracted.)");
			return rsrcError;
		}
		if (filename.contains(".jar")) {
			isCompiledAsJar = true;
		}
		filename = filename.replace('/', File.separatorChar);
		String returnString = filename.substring(file, bang);
		// END Garbage removal
		return returnString;
	}

	/**
	 * This gets the TimeStamp (last modified date) of a class file (typically this one!) <br>
	 * <br>
	 * Thanks to Roedy Green at <br>
	 * <a href="http://mindprod.com/jgloss/compiletimestamp.html">http://mindprod .com/jgloss/compiletimestamp.html</a>
	 * 
	 * @author Morlok8k
	 */
	private static Date getCompileTimeStamp(Class<?> classFile) throws IOException {
		ClassLoader loader = classFile.getClassLoader();
		String filename = classFile.getName().replace('.', '/') + ".class";
		// get the corresponding class file as a Resource.
		URL resource =
				(loader != null) ? loader.getResource(filename) : ClassLoader
						.getSystemResource(filename);
		URLConnection connection = resource.openConnection();
		// Note, we are using Connection.getLastModified not File.lastModifed.
		// This will then work both or members of jars or standalone class files.
		// NOTE: NOT TRUE! IT READS THE JAR, NOT THE FILES INSIDE!
		long time = connection.getLastModified();
		return (time != 0L) ? new Date(time) : null;
	}

	/**
	 * This gets the MLG_MD5 of a file <br>
	 * 
	 * @author Morlok8k
	 */
	private static String fileMD5(String fileName) throws NoSuchAlgorithmException,
			FileNotFoundException {
		return MLG_MD5.fileMD5(fileName);
	}

	/**
	 * Displays or returns Help information
	 * 
	 * @param SysOut
	 * <br>
	 *            Set TRUE to display info to System.out. (Returns null) <br>
	 *            Set FALSE to return info as String.
	 * @author Morlok8k
	 */
	private static String showHelp(boolean SysOut) {

		return MLG_Readme_and_HelpInfo.showHelp(SysOut);
	}

	/**
	 * <b>.zip file Get Modification Time</b><br>
	 * 
	 * Takes a string of a path to a .zip file (or .jar), and and returns a Long of the latest "Last Time Modified". <br>
	 * <br>
	 * 
	 * Thanks to the following:<br>
	 * <a href="http://www.java-examples.com/get-modification-time-zip-entry-example">http://www.java-examples.com/get-modification-time-zip-entry-example</a><br>
	 * <a href="http://www.java-examples.com/get-crc-32-checksum-zip-entry-example">http://www.java-examples.com/get-crc-32-checksum-zip-entry-example</a>
	 * 
	 * @param zipFile
	 * @param timeBuildID
	 * @author Morlok8k
	 */
	private static Long ZipGetModificationTime(String zipFile) {

		Long highestModTime = 0L;

		try {

			ZipFile zipF = new ZipFile(zipFile);

			/*
			 * Get list of zip entries using entries method of ZipFile class.
			 */

			Enumeration<? extends ZipEntry> e = zipF.entries();

			if (testing) {
				out("File Name\t\tCRC\t\tModification Time\n---------------------------------\n");
			}

			while (e.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) e.nextElement();

				Long modTime = entry.getTime();

				if (entry.getName().toUpperCase().contains("MAIN.JAVA")) {			//ignore highest timestamp for the source code, as that can be injected into the .jar file much later after compiling.
					modTime = 0L;
				}

				if (highestModTime < modTime) {
					highestModTime = modTime;
				}

				if (testing) {

					String entryName = entry.getName();
					Date modificationTime = new Date(modTime);
					String CRC = Long.toHexString(entry.getCrc());

					out(entryName + "\t" + CRC + "\t" + modificationTime + "\t"
							+ modTime.toString());
				}

			}

			zipF.close();

			return highestModTime;

		} catch (IOException ioe) {
			out("Error opening zip file" + ioe);
			return 0L;		//return Jan. 1, 1970 12:00 GMT for failures
		}
	}

	private void readConf() {
		//TODO: element comment
		//String errorMsg = "";

		try {
			File config = new File(MinecraftLandGeneratorConf);
			BufferedReader in = new BufferedReader(new FileReader(config));
			String line = "";
			String property = "";
			String value = "";

			while ((line = in.readLine()) != null) {
				int pos = line.indexOf('=');

				int end = line.lastIndexOf('#'); // comments, ignored lines

				if (end == -1) { // If we have no hash sign, then we read till the end of the line
					end = line.length();

				}
				if (end <= (pos + 1)) { // If hash is before the '=', we may have an issue... it should be fine, cause we check for issues next, but lets make sure.
					end = line.length();
					pos = -1;
				}

				if (end == 0) {	//hash is first char, meaning entire line is a comment
					end = line.length();
					pos = 0;
				}

				/*errorMsg =
						line + " pos: " + pos + " end: " + end + " line.length(): " + line.length();
				try {
					property = line.substring(0, pos).toLowerCase();
					value = line.substring(pos + 1, end);
				} catch (Exception e) {
					err(errorMsg);
				}
				*/
				if (pos != -1) {
					if (line.length() == 0) {
						property = "";
						value = "";
					} else {
						property = line.substring(0, pos).toLowerCase();
						value = line.substring(pos + 1, end);
					}

					if (property.equals("serverpath")) {
						serverPath = value;
					} else if (property.equals("java")) {
						javaLine = value;
					} else if (property.equals("done_text")) {
						doneText = value;
					} else if (property.equals("preparing_text")) {
						preparingText = value;
					} else if (property.equals("preparing_level")) {
						preparingLevel = value;
					} else if (property.equals("level-0")) {
						level_0 = value;
					} else if (property.equals("level-1")) {
						level_1 = value;
					} else if (property.equals("level-2")) {
						level_2 = value;
					} else if (property.equals("level-3")) {
						level_3 = value;
					} else if (property.equals("level-4")) {
						level_4 = value;
					} else if (property.equals("level-5")) {
						level_5 = value;
					} else if (property.equals("level-6")) {
						level_6 = value;
					} else if (property.equals("level-7")) {
						level_7 = value;
					} else if (property.equals("level-8")) {
						level_8 = value;
					} else if (property.equals("level-9")) {
						level_9 = value;
					} else if (property.equals("waitsave")) {
						if (value.toLowerCase().equals("true")) {
							waitSave = true;
						} else {
							waitSave = false;
						}
					}
				}
			}
			in.close();

			if (testing) {
				err("[TEST] Test Output: Reading of Config File ");
				err("[TEST]     serverPath: " + serverPath);
				err("[TEST]       javaLine: " + javaLine);
				err("[TEST]       doneText: " + doneText);
				err("[TEST]  preparingText: " + preparingText);
				err("[TEST] preparingLevel: " + preparingLevel);
				err("[TEST]        level_0: " + level_0);
				err("[TEST]        level_1: " + level_1);
				err("[TEST]        level_2: " + level_2);
				err("[TEST]        level_3: " + level_3);
				err("[TEST]        level_4: " + level_4);
				err("[TEST]        level_5: " + level_5);
				err("[TEST]        level_6: " + level_6);
				err("[TEST]        level_7: " + level_7);
				err("[TEST]        level_8: " + level_8);
				err("[TEST]        level_9: " + level_9);
				err("[TEST]       waitSave: " + waitSave);
			}
		} catch (FileNotFoundException ex) {
			out("Could not find "
					+ MinecraftLandGeneratorConf
					+ ". It is recommended that you run the application with the -conf option to create it.");
			return;
		} catch (IOException ex) {
			err("Could not read " + MinecraftLandGeneratorConf + ".");
			return;
		}
	}

	/**
	 * Generates a Config File.
	 * 
	 * @param newConf
	 *            true: Uses Default values. false: uses existing values
	 * @author Morlok8k
	 */
	private void saveConf(boolean newConf) {

		String jL = null;			//javaLine
		String sP = null;			//serverPath

		if (newConf) {
			jL = defaultJavaLine;	// reads the default from a constant, makes it easier!
			sP = ".";				// 
		} else {
			jL = javaLine;			// we read these values from an existing Conf File.
			sP = serverPath;		//
		}

		String txt = null;
		//@formatter:off
		txt = "#" + PROG_NAME + " Configuration File:  Version: " + VERSION + newLine
					+ "#Authors: " + AUTHORS + newLine
					+ "#Auto-Generated: " + dateFormat.format(date) + newLine
					+ newLine
					+ "#Line to run server:" + newLine
					+ "Java=" + jL // reads the default from a constant, makes it easier!
					+ newLine 
					+ newLine
					+ "#Location of server.  use \".\" for the same folder as MLG" + newLine
					+ "ServerPath=" + sP 
					+ newLine 
					+ newLine 
					+ "#Strings read from the server" + newLine 
					+ "Done_Text=[INFO] Done" + newLine
					+ "Preparing_Text=[INFO] Preparing spawn area:" + newLine
					+ "Preparing_Level=[INFO] Preparing start region for" + newLine
					+ "Level-0=The Overworld" + newLine
					+ "Level-1=The Nether" + newLine
					+ "Level-2=The End" + newLine 
					+ "Level-3=Level 3 (Future Level)" + newLine
					+ "Level-4=Level 4 (Future Level)" + newLine 
					+ "Level-5=Level 5 (Future Level)" + newLine 
					+ "Level-6=Level 6 (Future Level)" + newLine
					+ "Level-7=Level 7 (Future Level)" + newLine 
					+ "Level-8=Level 8 (Future Level)" + newLine 
					+ "Level-9=Level 9 (Future Level)" + newLine 
					+ newLine
					+ "#Optional: Wait a few seconds after saving." + newLine + "WaitSave=false";
			//@formatter:on

		writeTxtFile(MinecraftLandGeneratorConf, txt);

		return;

	}

	private void verifyWorld() {
		//TODO: element comment

		// verify that we ended up with a good server path, either from the file or from an argument.
		File file = new File(serverPath);
		if (!file.exists() || !file.isDirectory()) {
			err("The server directory is invalid: " + serverPath);
			return;
		}

		try {
			// read the name of the current world from the server.properties file
			BufferedReader props =
					new BufferedReader(new FileReader(new File(serverPath + fileSeparator
							+ "server.properties")));
			String line;
			while ((line = props.readLine()) != null) {
				int pos = line.indexOf('=');
				int end = line.lastIndexOf('#'); // comments, ignored lines

				if (end == -1) { // If we have no hash sign, then we read till the end of the line
					end = line.length();
				}
				if (end <= pos) { // If hash is before the '=', we may have an issue... it should be fine, cause we check for issues next, but lets make sure.
					end = line.length();
				}

				if (pos != -1) {

					String property = line.substring(0, pos).toLowerCase();
					String value = line.substring(pos + 1);

					if (property.equals("level-name")) {
						worldPath = serverPath + fileSeparator + value;
						worldName = value;
					}
					if (useRCON) {
						if (property.equals("enable-rcon")) {

							if (value.contains("true")) {
								rcon_Enabled = true;
								out("RCON is set to be Enabled on the server.");
							} else {
								rcon_Enabled = false;
								useRCON = false;
								err("RCON is not Enabled on the server.");
							}
						} else if (property.equals("rcon.password")) {
							rcon_Password = value;
							if (rcon_Password.isEmpty()) {
								useRCON = false;
								err("RCON Needs a password!.");
							}
							out("RCON Password:" + rcon_Password);
						} else if (property.equals("rcon.port")) {
							rcon_Port = value;
							out("RCON Port:" + rcon_Port);
						} else if (property.equals("server-ip")) {
							String IP = value;
							if (IP.isEmpty()) {
								IP = "0.0.0.0";
							}
							rcon_IPaddress = IP;

						}
					}

				}
			}

		} catch (FileNotFoundException ex) {
			err("Could not open " + serverPath + fileSeparator + "server.properties");
			return;
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			return;
		}

		File level = new File(worldPath + fileSeparator + "level.dat");
		if (!level.exists() || !level.isFile()) {
			err("The currently-configured world does not exist. Please launch the server once, first.");
			return;
		}

	}

	public static void writeTxtFile(String file, String txt) {
		//TODO: element comment

		/*
		 * NOTE: I don't include a generic readTxtFile method, as that code depends on what I'm reading.
		 * For things like that I make a special method for it, if its used in more than one place.
		 * Like reading the config file.
		 */

		try {
			File oFile = new File(file);
			BufferedWriter outFile = new BufferedWriter(new FileWriter(oFile));
			outFile.write(txt);
			outFile.newLine();
			outFile.close();
			out(file + " file created.");
			return;
		} catch (IOException ex) {
			err("Could not create " + MinecraftLandGeneratorConf + ".");
			ex.printStackTrace();
			return;
		}

	}

	/**
	 * Outputs a formatted string to System.out as a line.
	 * 
	 * @param str
	 *            String to display and format
	 * @author Morlok8k
	 */
	public static void out(String str) {
		System.out.println(MLG + str);		// is there a better/easier way to do this?  I just wanted a lazier way to write "System.out.println(MLG + blah..."
	}

	/**
	 * Outputs a formatted string to System.err as a line.
	 * 
	 * @param str
	 *            String to display and format
	 * @author Morlok8k
	 */
	public static void err(String str) {
		System.err.println(MLGe + str);
	}

	/**
	 * Outputs a string to System.out without a newline.
	 * 
	 * @param str
	 *            String to display and format
	 * @author Morlok8k
	 */
	public static void outP(String str) {
		System.out.print(str);
	}

	/**
	 * waits ten seconds. outputs 10%, 20%, etc after each second.
	 * 
	 * @author Morlok8k
	 */
	private static void waitTenSec(boolean output) {

		if (dontWait) { return; }			//Don't wait!

		if (output) {
			outP(MLG);						//here we wait 10 sec.
		}

		int count = 0;
		while (count <= 100) {
			if (output) {
				outP(count + "% ");
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			count += 10;
		}
		if (output) {
			outP(newLine);
		}
		return;

	}

	/*
	private static void finishedImage() {
		System.out.println(newLine + "  .l0kkKMl                     lMKkk0l.  " + newLine
				+ ".;kMc  ;KK;  .,,lkkkkkkkl,,.  ;KK,  cMk,." + newLine
				+ "KKxkc..lKK;.oKKxxl,;;;;lkkKKo.,KKl..ckkKK" + newLine
				+ "Mx. ...;x0OOl'.           .'lOOOx:... .xM" + newLine
				+ "lKKk0l. ,KK;                 ,KK, .l0xKKl" + newLine
				+ " .'',xOxOWl                   lMOxOx,''. " + newLine
				+ "      .,xWl                   lWx;.      " + newLine
				+ "        lMl ,.             ., lMl        " + newLine
				+ "        ;XO';Odl:,.. .',:ldO;'OX;        " + newLine
				+ "         ,KK,cxkkkc...ckxkx:,KK,         " + newLine
				+ "          lMc .,,. .o. .,,. cMl          " + newLine
				+ "          .lO;;'  .lkl.  ';;Ol.          " + newLine
				+ "         .;;kMk.         .kMk;;.         " + newLine
				+ "       .lKKK0kl;;.......;,lk0KKKl.       " + newLine
				+ "  .;llxOx;.'Ox;llxxldlxxll;xO'.;xOxll;.  " + newLine
				+ " ,KKcll.  .l0KKl...   ...lKK0l.  .llcKK, " + newLine
				+ " lMx'''..lXX;:X0:'.   .':OX;,XXl..'''xMl " + newLine
				+ " .lxXk.  ,KM; .lkKKxkkKKkl. ,MK,  .kXxl. " + newLine
				+ "    oKl;;kMk.    .,,,,;.    .kMk,,lKo    " + newLine
				+ "    .0MMMMO'                 .OMMMMO.    ");
	}
	 */

	/**
	 * Returns the time in a readable format between two points of time given in Millis.
	 * 
	 * @param startTimeMillis
	 * @param endTimeMillis
	 * @author Morlok8k
	 * @return String of Readable Time
	 */
	public static String displayTime(long startTimeMillis, long endTimeMillis) {

		long millis = (endTimeMillis - startTimeMillis);
		//I just duplicated displayTime to have a start & end times, because it just made things simpler to code.
		return (displayTime(millis));
	}

	/**
	 * Returns the time in a readable format given a time in Millis.
	 * 
	 * @param timeMillis
	 * @author Morlok8k
	 * @return String of Readable Time
	 */
	public static String displayTime(long timeMillis) {

		long seconds = timeMillis / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;
		long years = days / 365;

		String took =
				(years > 0 ? String.format("%d " + ((years) == 1 ? "Year, " : "Years, "), years)
						: "")
						+ (days > 0 ? String.format("%d "
								+ ((days % 365) == 1 ? "Day, " : "Days, "), days % 365) : "")
						+ (hours > 0 ? String.format("%d "
								+ ((hours % 24) == 1 ? "Hour, " : "Hours, "), hours % 24) : "")
						+ (minutes > 0 ? String.format("%d "
								+ ((minutes % 60) == 1 ? "Minute, " : "Minutes, "), minutes % 60)
								: "")
						+ String.format("%d " + ((seconds % 60) == 1 ? "Second" : "Seconds"),
								seconds % 60);

		if (!(verbose)) {
			int end = took.indexOf(",");
			if (end == -1) {
				end = took.length();
			} else {
				end = end - 1;
			}
			took = took.substring(0, end);
		}

		return (took);
	}

}
