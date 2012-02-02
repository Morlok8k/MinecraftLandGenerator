package corrodias.minecraft.landgenerator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
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
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.security.MessageDigest;
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
	private static final String VERSION = "1.6.03";
	private static final String AUTHORS = "Corrodias, Morlok8k, pr0f1x";

	private static final String fileSeparator = System.getProperty("file.separator");
	private static final String newLine = System.getProperty("line.separator");

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
	private int yRange = 0;
	private Integer xOffset = null;
	private Integer yOffset = null;
	private boolean verbose = false;
	private boolean alternate = false;
	private static boolean waitSave = false;
	private static boolean ignoreWarnings = false;
	private static LongTag randomSeed = null;

	private static String MLG = "[MLG] ";
	private static String MLGe = "[MLG-ERROR] ";

	private static DateFormat dateFormat = null;
	//private static DateFormat dateFormatBuildID = null;
	private static DateFormat dateFormat_MDY = null;
	private static Date date = null;
	private static Date MLG_Last_Modified_Date = null;
	private static Long MLG_Last_Modified_Long = 0L;

	private static final Class<?> cls = Main.class;
	private static String MLGFileName = null;
	private static String MLGFileNameShort = null;
	private static final String rsrcError = "rsrcERROR";
	private static String buildIDFile = "MLG-BuildID";
	private static boolean isCompiledAsJar = false;
	private static String MLG_Current_Hash = null;
	private static int inf_loop_protect_BuildID = 0;
	private static boolean flag_downloadedBuildID = false;
	private static Scanner sc = new Scanner(System.in);

	private static ArrayList<String> timeStamps = new ArrayList<String>();

	private static final String MinecraftLandGeneratorConf = "MinecraftLandGenerator.conf";
	private static final String defaultReadmeFile = "MLG-Readme.txt";
	private static final String MLG_JarFile = "MinecraftLandGenerator.jar";

	private static final String github_URL =
			"https://raw.github.com/Morlok8k/MinecraftLandGenerator/master/bin/";			// just removing some redundancy

	private static final String github_MLG_Conf_URL = github_URL + MinecraftLandGeneratorConf;
	private static final String github_MLG_BuildID_URL = github_URL + buildIDFile;
	private static final String github_MLG_jar_URL = github_URL + MLG_JarFile;

	//////

	private static final boolean testing = false;	// display more output when debugging

	//////

	// REMINDER: because I always forget/mix up languages:
	// "static" in java means "global"
	// "final" means "constant"

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
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

		out("Minecraft Land Generator version " + VERSION);
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

		if (args.length == 0) {
			out("Please Enter the size of world you want.  Example: X:1000  Y:1000");
			outP(MLG + "X:");
			xRange = getInt("X:");
			outP(MLG + "Y:");
			yRange = getInt("Y:");
			args = new String[] { String.valueOf(xRange), String.valueOf(yRange) };

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
			return;
		} else if (args[0].equalsIgnoreCase("-build")) {
			buildID(false);
			return;
		} else if (args[0].equalsIgnoreCase("-update")) {
			updateMLG();
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
			}
			return;

		} else if (args[0].equalsIgnoreCase("-downloadlist")) {

			if (args.length == 2) {
				try {
					File config = new File(args[1]);
					BufferedReader in = new BufferedReader(new FileReader(config));
					String line;
					while ((line = in.readLine()) != null) {
						downloadFile(line, true);
					}
					in.close();

				} catch (FileNotFoundException ex) {
					System.err.println(args[1] + " - File not found");
					return;
				} catch (IOException ex) {
					System.err.println(args[1] + " - Could not read file.");
					return;
				}
			} else {
				out("No File with links!");
			}
			return;

		} else if (args.length == 1) {
			out("For help, use java -jar " + MLGFileNameShort + " -help");
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
		} else if (preparingText == null) {		// MLG 1.4.0
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

			outP(MLG);				//here we wait 10 sec before closing.
			int count = 0;
			while (count <= 100) {
				outP(count + "% ");

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				count += 10;
			}
			outP(newLine);
			return;

		}

		// ARGUMENTS
		try {
			xRange = Integer.parseInt(args[0]);
			yRange = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			err("Invalid X or Y argument.");
			err("Please Enter the size of world you want.  Example: X:1000  Y:1000");
			xRange = getInt("X:");
			yRange = getInt("Y:");

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
				} else if (nextSwitch.startsWith("-i")) {
					increment = Integer.parseInt(args[i + 2].substring(2));
				} else if (nextSwitch.startsWith("-w")) {
					ignoreWarnings = true;
				} else if (nextSwitch.equals("-alt") || nextSwitch.equals("-a")) {
					out("Using Alternate Launching...");
					alternate = true;
				} else if (nextSwitch.startsWith("-x")) {
					xOffset = Integer.valueOf(args[i + 2].substring(2));
				} else if (nextSwitch.startsWith("-y")) {
					yOffset = Integer.valueOf(args[i + 2].substring(2));
				} else {
					serverPath = args[i + 2];
				}
			}
		} catch (NumberFormatException ex) {
			err("Invalid -i switch value.");
			return;
		}

		verifyWorld();

		{
			File backupLevel = new File(worldPath + fileSeparator + "level_backup.dat");
			if (backupLevel.exists()) {
				err("There is a level_backup.dat file left over from a previous attempt that failed. You should go determine whether to keep the current level.dat"
						+ " or restore the backup.");
				err("You most likely will want to restore the backup!");
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

			runMinecraft(verbose, alternate);

			out("");

			File serverLevel = new File(worldPath + fileSeparator + "level.dat");
			File backupLevel = new File(worldPath + fileSeparator + "level_backup.dat");

			out("Backing up level.dat to level_backup.dat.");
			copyFile(serverLevel, backupLevel);
			out("");

			Integer[] spawn = getSpawn(serverLevel);
			out("Spawn point detected: [" + spawn[0] + ", " + spawn[2] + "]");
			{
				boolean overridden = false;
				if (xOffset == null) {
					xOffset = spawn[0];
				} else {
					overridden = true;
				}
				if (yOffset == null) {
					yOffset = spawn[2];
				} else {
					overridden = true;
				}
				if (overridden) {
					out("Centering land generation on [" + xOffset + ", " + yOffset
							+ "] due to switches.");
				}
			}
			out("");

			int totalIterations = (xRange / increment + 1) * (yRange / increment + 1);
			int currentIteration = 0;

			long differenceTime = System.currentTimeMillis();
			Long[] timeTracking =
					new Long[] { differenceTime, differenceTime, differenceTime, differenceTime };
			for (int currentX = 0 - xRange / 2; currentX <= xRange / 2; currentX += increment) {
				for (int currentY = 0 - yRange / 2; currentY <= yRange / 2; currentY += increment) {
					currentIteration++;

					out("Setting spawn to ["
							+ Integer.toString(currentX + xOffset)
							+ ", "
							+ Integer.toString(currentY + yOffset)
							+ "] ("
							+ currentIteration
							+ "/"
							+ totalIterations
							+ ") "
							+ Float.toString((Float.parseFloat(Integer.toString(currentIteration)) / Float
									.parseFloat(Integer.toString(totalIterations))) * 100)
							+ "% Done"); // Time Remaining estimate

					timeTracking[0] = timeTracking[1];
					timeTracking[1] = timeTracking[2];
					timeTracking[2] = timeTracking[3];
					timeTracking[3] = System.currentTimeMillis();

					//TODO: update this time remaining section, so it doesnt do last 4 runs, but all runs.

					if (currentIteration >= 4) {
						differenceTime = (timeTracking[3] - timeTracking[0]) / 3; // well, this is what it boils down to
						differenceTime *= 1 + (totalIterations - currentIteration);
						out(String.format("Estimated time remaining: %dh%dm%ds", differenceTime
								/ (1000 * 60 * 60), (differenceTime % (1000 * 60 * 60))
								/ (1000 * 60),
								((differenceTime % (1000 * 60 * 60)) % (1000 * 60)) / 1000));
					} else if (currentIteration == 3) {
						differenceTime = (timeTracking[3] - timeTracking[1]) / 2; // well, this is what it boils down to
						differenceTime *= 1 + (totalIterations - currentIteration);
						out(String.format("Estimated time remaining: %dh%dm%ds", differenceTime
								/ (1000 * 60 * 60), (differenceTime % (1000 * 60 * 60))
								/ (1000 * 60),
								((differenceTime % (1000 * 60 * 60)) % (1000 * 60)) / 1000));
					} else if (currentIteration == 2) {
						differenceTime = (timeTracking[3] - timeTracking[2]); // well, this is what it boils down to
						differenceTime *= 1 + (totalIterations - currentIteration);
						out(String.format("Estimated time remaining: %dh%dm%ds", differenceTime
								/ (1000 * 60 * 60), (differenceTime % (1000 * 60 * 60))
								/ (1000 * 60),
								((differenceTime % (1000 * 60 * 60)) % (1000 * 60)) / 1000));
					} else if (currentIteration <= 1) {
						out("Estimated time remaining: Calculating...");
					}

					// Set the spawn point
					setSpawn(serverLevel, currentX + xOffset, 128, currentY + yOffset);

					// Launch the server
					runMinecraft(verbose, alternate);
					out("");
				}
			}

			out("Finished generating chunks.");
			copyFile(backupLevel, serverLevel);
			backupLevel.delete();
			out("Restored original level.dat.");
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
	 * Note that, in Minecraft levels, the Y coordinate is height, while Z is what may normally be thought of as Y.<br>
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
		out("Starting server.");

		boolean warning = false;
		boolean cantKeepUp = false;
		final boolean ignoreWarningsOriginal = ignoreWarnings;

		// monitor output and print to console where required.
		// STOP the server when it's done.

		// Damn it Java! I hate you so much!
		// I can't reuse code the way I want to, like in other langauges.
		// So, here is a bunch of duplicate code...
		// Stupid compile errors...

		if (alternate) { // Alternate - a replication (slightly stripped down) of MLG 1.3.0's code. simplest code possible.
			out("Alternate Launch");
			Process process = minecraft.start();

			byte[] saveall = { 's', 'a', 'v', 'e', '-', 'a', 'l', 'l', '\r', '\n' };
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
					// removed waitSave code for alternate launch.  Not needed here.  Alt launch is basic, no frills, etc.

					out("Saving server data...");
					outputStream.write(saveall);
					outputStream.flush();

					out("Stopping server...");
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

			while ((line = pOut.readLine()) != null) {

				line = line.trim();

				if (verbose) {
					if (line.contains("[INFO]")) {
						out(line.substring(line.lastIndexOf("]") + 2));
					} else {
						out(line);
					}
				} else if (line.contains(preparingText)) {

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
				} else if (line.contains("server version")) {
					out(line.substring(line.lastIndexOf("]") + 2));
				}

				if (line.contains(doneText)) { // now this is configurable!
					outP(newLine);
					if (waitSave) {
						out("Waiting 30 seconds to save.");

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
					out("Saving server data.");
					outputStream.write(saveAll);
					outputStream.flush();

					out("Stopping server.");
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
				if (line.contains("Can't keep up!")) {
					cantKeepUp = true;			//[WARNING] Can't keep up! Did the system time change, or is the server overloaded?
					ignoreWarnings = true;
				}

				if (ignoreWarnings == false) {
					if (line.contains("[WARNING]")) { // If we have a warning, stop...
						out("");
						out("Warning found: Stopping Minecraft Land Generator");
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
			out("The current spawn point is: [" + spawn[0] + ", " + spawn[2] + "]");
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

		if (readmeFile == "" || readmeFile == null) {
			readmeFile = defaultReadmeFile;
		}

		String showHelpSTR = "";
		showHelpSTR = showHelp(false);		//stored as a string for easier manipulation in the future

		//@formatter:off
		String ReadMeText = "";
		ReadMeText = "Minecraft Land Generator version " + VERSION + newLine
				+ newLine
				+ "Updated " + dateFormat_MDY.format(MLG_Last_Modified_Date) + newLine
				+ newLine
				+ "Original Code by Corrodias		November 2010" + newLine
				+ "Enhanced Code by Morlok8k		Feb. 2011 to Now (or at least to the date listed above!)" + newLine
				+ "Additional Code by pr0f1x		October 2011" + newLine
				+ "Forum: http://www.minecraftforum.net/topic/187737-minecraft-land-generator/" + newLine
				+ "Source: https://github.com/Morlok8k/MinecraftLandGenerator" + newLine
				+ newLine
				+ "-----------------------------------------------" + newLine
				+ newLine
				+ "This program lets you generate an area of land with your Minecraft Beta SMP server (and is prossibly future-proof for newer versions). You set up your java command line and minecraft server paths in the MinecraftLandGenerator.conf file, set up the server's server.properties file with the name of the world you wish to use, and then run this program." + newLine
				+ "When a Minecraft server is launched, it automatically generates chunks within a square area of 25x25 chunks (400x400 blocks), centered on the current spawn point (formally 20x20 chunks, 320x320 blocks). When provided X and Y ranges as arguments, this program will launch the server repeatedly, editing the level.dat file between sessions, to generate large amounts of land without players having to explore them. The generated land will have about the X and Y ranges as requested by the arguments, though it will not be exact due to the spawn point typically not on the border of a chunk. (Because of this, MLG by default adds a slight overlap with each pass - 380x380 blocks) You can use the -x and -y switches to override the spawn offset and center the land generation on a different point." + newLine
				+ "The program makes a backup of level.dat as level_backup.dat before editing, and restores the backup at the end. In the event that a level_backup.dat file already exists, the program will refuse to proceed, leaving the user to determine why the level_backup.dat file exists and whether they would rather restore it or delete it, which must be done manually." + newLine
				+ newLine
				+ "This program is public domain, and the source code is included in the .jar file.  (If accidently missing, like in 1.3.0 and 1.4.0, it is always available at Github.)" + newLine
				+ "The JNLP library is included (inside the .jar). It is not public domain. Its license is included, as LICENSE.TXT." + newLine
				+ "It is also available at: http://jnbt.sourceforge.net/" + newLine
				+ newLine
				+ "The \"unescape\" method/function is also not Public Domain.  Its License is the W3C® Software License, and located here: http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231"
				+ newLine
				+ "Other Public Domain code has been used in this program, and references to sources are included in the comments of Minecraft Land Generator's source code."
				+ newLine
				+ "-----------------------------------------------" + newLine
				+ newLine
				+ "Version History:" + newLine
				+ "Morlok8k:" + newLine
				+ "1.6.03" + newLine
				+ "- added decoding of escape characters of URL's (so a space is a \" \" and not \"%20\")" + newLine
				+ "- added \"-downloadlist [list]\" where [list] is a text file with URL's on each line" + newLine
				+ newLine
				+ "1.6.02" + newLine
				+ "- small fix on caculating md5sum where old version didnt pad out to 32chars with zeros on the left side"
				+ "- quick Archive intergity fix after injecting source code into .jar after it compiled."
				+ "- no new functionality, md5 issue doesnt affect -update on old versions."
				+ newLine
				+ "1.6.0" + newLine
				+ "- NOW DOES NOT NEED ANY SCRIPT FILES!" + newLine
				+ "- Added the ability to download files from the internet" + newLine
				+ "- Added a switch to download any file off the internet, if needed (useless for most people, but included it in case I wanted it in the future.)" + newLine
				+ "- Added the ability to check what version the .jar is. (Using MD5 hashes, timestamps, and the BuildID file)" + newLine
				+ "- Added \"-update\" to download new versions of MLG directly from github." + newLine
				+ "- Updated estimated time.  Now shows up on loop 2+ instead of loop 4+." + newLine
				+ "- Standard % output of the Server should look nicer now." + newLine
				+ "- Code Refactoring" + newLine
				+ "- Code Formatting" + newLine
				+ "- Code Optimization" + newLine
				+ "- Duplicate sections of code have been turned into Methods/\"Functions\"" + newLine
				+ newLine
				+ "1.5.1" + newLine
				+ "- pr0f1x: Added the \"save-all\" command to be sent to the server before shutting it down." + newLine
				+ "- pr0f1x: Added a 40 second wait before shutting down." + newLine
				+ "- Morlok8k: Made 40 second wait optional." + newLine
				+ "- Morlok8k: Changed the Dimensions code.  (I had assumed it would be DIM-1, DIM-2, etc.  but it turned out to be DIM-1 and DIM1. Change reflects Server output of \"Level n\")" + newLine
				+ "- Morlok8k: Config file is automatically updated to reflect these changes." + newLine
				+ "- Morlok8k: Cleaned up code." + newLine
				+ newLine
				+ "1.5.0" + newLine
				+ "- Supports Server Beta 1.6.4 (& hopefully future versions as well, while remaining backward compatible.)" + newLine
				+ "- Added \"-a\",\"-alt\" to use alternate method (a slightly simplier version of 1.3.0's code - pure verbose only)" + newLine
				+ "- Added world specific output for 9 dimensions (DIM-1 is the Nether, DIM-2 through DIM-9 dont exist yet, but if and when they do, you can configure it's text).  (\"Level 0\", the default world, is displayed as the worlds name)" + newLine
				+ "- Updated Config File for these Dimensions." + newLine
				+ "- Reads and outputs the Seed to the output. (If you had used text for the Seed, Minecraft converts it into a number. This outputs the number.)" + newLine
				+ "- Changed the default 300 blocks to 380.  The server now makes a 400x400 square block terrain instead of 320x320.  Thus it is faster because there are less loops.  To use the old way, use \"-i300\"" + newLine
				+ "- Added total Percentage done (technically, it displays the % done once the server finishes...)" + newLine
				+ "- Added debugging output vars of conf file (disabled - need to re-compile source to activate)" + newLine
				+ newLine
				+ "\t\t+ (the goal is to have MLG be configureable, so it can work on any version of the server, past or present.)" + newLine
				+ newLine
				+ "*** 1.4.5 (pre 1.5.0) ***" + newLine
				+ "- sorry!  I shouldn't release untested code..." + newLine
				+ "*************************" + newLine
				+ newLine
				+ "1.4.4" + newLine
				+ "- Added ablilty to ignore [WARNING] and [SEVERE] errors with \"-w\"" + newLine
				+ newLine
				+ "1.4.3" + newLine
				+ "- Fixed \"-ps\",\"-printspawn\" as I had forgot I had broken it in 1.4.0 - due to config file change." + newLine
				+ newLine
				+ "1.4.2" + newLine
				+ "- No New Features" + newLine
				+ "- Changed non-verbose mode to display server progress on the same line, saving a lot of space." + newLine
				+ "	- This couldn't wait for 1.5.0 ...  I (Morlok8k) liked it too much." + newLine
				+ newLine
				+ "1.4.0" + newLine
				+ "- Future Proofing" + newLine
				+ "- Configurble Server Message reading. (If server updates and breaks MLG, you can add the new text!)" + newLine
				+ "- Updated config file, and auto updating from old format." + newLine
				+ "- Added % of spawn area to non-verbose output." + newLine
				+ "- Removed datetime stamps from server output in verbose mode" + newLine
				+ "- Other Misc fixes." + newLine
				+ newLine
				+ "1.3.0" + newLine
				+ "- Fixed Problems with Minecraft Beta 1.3 -- Morlok8k" + newLine
				+ newLine
				+ "-----------------------------------------------" + newLine
				+ newLine
				+ "Corrodias:" + newLine
				+ "1.2.0" + newLine
				+ "- land generation now centers on the spawn point instead of [0, 0]" + newLine
				+ "- the server is launched once before the spawn point is changed, to verify that it can run and to create a world if one doesn't exist" + newLine
				+ "- added -printspawn [-ps] switch to print the current spawn coordinates to the console" + newLine
				+ "- added -x and -y switches to override the X and Y offsets" + newLine
				+ "- added -v switch, does the same as -verbose" + newLine
				+ "- improved status message spacing to make things easier to read" + newLine
				+ "- improved time estimation algorithm: it now averages the last 3 launches" + newLine
				+ newLine
				+ "1.1.0" + newLine
				+ "- added MinecraftLandGenerator.conf file to hold the java command line and the server path" + newLine
				+ "- added -conf solo switch to generate a .conf file" + newLine
				+ "- added -verbose switch to output server output to the console (default is to ignore it)" + newLine
				+ "- added -i switch to allow customizing the block increment size (default is 300)" + newLine
				+ "- added instructions output in this version, i think" + newLine
				+ "- improved status message output to include current iteration and total iterations" + newLine
				+ newLine
				+ "1.0.0" + newLine
				+ "- initial release" + newLine
				+ newLine
				+ "-----------------------------------------------" + newLine
				+ newLine
				+ "Notes:" + newLine
				+ "Due to changes in server beta 1.6, it now generates the nether as well as the world at the same time." + newLine
				+ "However, Since beta 1.9 and Minecraft 1.0, the nether or the end is no longer generated."
				+ "I recommend using MCEDIT to relight the map after you generate it. This will take a long time, but should fix all those incorrectly dark spots in your level." + newLine
				+ newLine
				+ "-----------------------------------------------" + newLine
				+ newLine;
		//@formatter:on

		writeTxtFile(readmeFile, ReadMeText + showHelpSTR);

	}

	/**
	 * 
	 * Downloads a File using a URL in a String.<br>
	 * (If the file is a dynamic URL (Not like "http://example.com/file.txt") and it can't get the filename, it saves it as <i>"System.currentTimeMillis();"</i>) <br>
	 * <br>
	 * Thanks to bs123 at <br>
	 * <a href="http://www.daniweb.com/software-development/java/threads/84370"> http://www.daniweb.com/software-development/java/threads/84370</a>
	 * 
	 * @author Morlok8k
	 * @param URL
	 *            URL in a String
	 * @param Output
	 *            Displays output if true
	 */
	private static boolean downloadFile(String URL, boolean Output) {

		boolean success = true;

		String fileName = URL.substring(URL.lastIndexOf("/") + 1, URL.length());
		int size = 1024 * 4; // 1024 * n should be tested to get the optimum size (for download speed.)

		if (fileName.equals("")) {
			fileName = String.valueOf(System.currentTimeMillis());
		}

		fileName = unescape(fileName);

		if (Output) {
			out("Downloading: " + URL);
			out("Saving as: " + fileName);
		}

		long differenceTime = System.currentTimeMillis();
		Long[] timeTracking = new Long[] { differenceTime, differenceTime };
		timeTracking[0] = System.currentTimeMillis();

		outP(MLG);
		try {
			BufferedInputStream in;
			in = new BufferedInputStream(new URL(URL).openStream());
			FileOutputStream fos;
			fos = new FileOutputStream(fileName);
			BufferedOutputStream bout = new BufferedOutputStream(fos, size);
			byte[] data = new byte[size];
			int x = 0;
			int count = 0;
			while ((x = in.read(data, 0, size)) >= 0) {
				bout.write(data, 0, x);
				count = count + x;
				if (Output) {
					outP("*");
				}
			}
			bout.close();
			in.close();
			if (Output) {
				outP(newLine);
				out(count + " byte(s) copied");
			}
			timeTracking[1] = System.currentTimeMillis();
			differenceTime = (timeTracking[1] - timeTracking[0]) / 2;
			if (Output) {
				out(String.format(MLG + "Elapsed Time: %dm%ds", (differenceTime % (1000 * 60 * 60))
						/ (1000 * 60), ((differenceTime % (1000 * 60 * 60)) % (1000 * 60)) / 1000));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			success = false;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			success = false;
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
		}
		if (Output) {
			out("Done");
		}
		return success;
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
				// out(hash + "  " + MLGFileName);
			} catch (Exception e) {
				out("Error: MD5 from file failed");
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
				out("Error: MD5 from file failed");
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
											err("Archive Intergrity Check Passed. Offset: "
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
				System.err.println("");
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
				out("There is a NEW VERSION Of Minecraft Land Generator available online!");

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
	 * This gets the MD5 of a file <br>
	 * <br>
	 * Thanks to R.J. Lorimer at <br>
	 * <a href="http://www.javalobby.org/java/forums/t84420.html">http://www. javalobby.org/java/forums/t84420.html</a>
	 * 
	 * @author Morlok8k
	 */
	private static String fileMD5(String fileName) throws NoSuchAlgorithmException,
			FileNotFoundException {
		// out("");
		// out("");
		MessageDigest digest = MessageDigest.getInstance("MD5");
		InputStream is = new FileInputStream(fileName);
		byte[] buffer = new byte[8192];
		int read = 0;
		try {
			while ((read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			String output = String.format("%1$032X", bigInt);    //pad on left to 32 chars with 0's, also capitalize.
			// out("MD5: " + output);
			return output.toUpperCase(Locale.ENGLISH);
		} catch (IOException e) {
			throw new RuntimeException("Unable to process file for MD5", e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
			}
		}
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
		String Str = null;
		String NewLine = newLine;
		if (SysOut) {
			NewLine = newLine + MLG;
		}

		//@formatter:off
		Str =	"Usage: java -jar " + MLGFileNameShort + " x y [serverpath] [switches]" + NewLine
				+ NewLine
				+ "Arguments:" + NewLine
				+ "              x : X range to generate" + NewLine
				+ "              y : Y range to generate" + NewLine
				+ "     serverpath : the path to the directory in which the server runs (takes precedence over the config file setting)" + NewLine
				+ NewLine
				+ "Switches:" + NewLine
				+ "       -verbose : causes the application to output the server's messages to the console" + NewLine
				+ "             -v : same as -verbose" + NewLine
				+ "             -w : Ignore [WARNING] and [SEVERE] messages." + NewLine
				+ "           -alt : alternate server launch sequence" + NewLine
				+ "             -a : same as -alt" + NewLine
				+ "            -i# : override the iteration spawn offset increment (default 300) (example: -i100)" + NewLine
				+ "            -x# : set the X offset to generate land around (example: -x0)" + NewLine
				+ "            -y# : set the X offset to generate land around (example: -y0)" + NewLine
				+ NewLine
				+ "Other options:" + NewLine
				+ "  java -jar " + MLGFileNameShort + " -update" + NewLine
				+ "        Checks for and downloads new versions of MLG online." + NewLine
				+ NewLine
				+ "  java -jar " + MLGFileNameShort + " -printspawn" + NewLine
				+ "  java -jar " + MLGFileNameShort + " -ps" + NewLine
				+ "        Outputs the current world's spawn point coordinates." + NewLine 
				+ NewLine
				+ "  java -jar " + MLGFileNameShort + " -conf" + NewLine
				+ "  java -jar " + MLGFileNameShort + " -conf download" + NewLine
				+ "        Generates or downloads a "+ MinecraftLandGeneratorConf + " file." + NewLine
				+ NewLine
				+ "  java -jar " + MLGFileNameShort + " -readme readme.txt" + NewLine
				+ "  java -jar " + MLGFileNameShort + " -readme" + NewLine
				+ "        Generates a readme file using supplied name or the default " + defaultReadmeFile + NewLine
				+ NewLine
				+ "  java -jar " + MLGFileNameShort + " -downloadfile http://example.com/file.txt" + NewLine
				+ "        Downloads whatever file from the internet you give it." + NewLine
				+ "  java -jar " + MLGFileNameShort + " -downloadlist list.txt" + NewLine
				+ "        list.txt (or any other file) contains a URL on each line which will be downloaded." + NewLine
				+ NewLine
				+ "  java -jar " + MLGFileNameShort + " -version" + NewLine
				+ "  java -jar " + MLGFileNameShort + " -help" + NewLine
				+ "  java -jar " + MLGFileNameShort + " /?" + NewLine
				+ "        Prints this message." + NewLine 
				+ NewLine
				+ "When launched with the -conf switch, this application creates a " + MinecraftLandGeneratorConf + " file that contains configuration options." + NewLine
				+ "If this file does not exist or does not contain all required properties, the application will not run." + NewLine 
				+ NewLine
				+ MinecraftLandGeneratorConf + " properties:" + NewLine
				+ "           Java : The command line to use to launch the server" + NewLine
				+ "     ServerPath : The path to the directory in which the server runs (can be overridden by the serverpath argument)" + NewLine
				+ "      Done_Text : The output from the server that tells us that we are done" + NewLine
				+ " Preparing_Text : The output from the server that tells us the percentage" + NewLine
				+ "Preparing_Level : The output from the server that tells us the level it is working on" + NewLine 
				+ "        Level-0 : Name of Level 0: The Overworld" + NewLine
				+ "        Level-1 : Name of Level 1: The Nether" + NewLine
				+ "        Level-2 : Name of Level 2: The End" + NewLine
				+ "        Level-3 : Name of Level 3: (Future Level)" + NewLine
				+ "        Level-4 : Name of Level 4: (Future Level)" + NewLine
				+ "        Level-5 : Name of Level 5: (Future Level)" + NewLine
				+ "        Level-6 : Name of Level 6: (Future Level)" + NewLine
				+ "        Level-7 : Name of Level 7: (Future Level)" + NewLine
				+ "        Level-8 : Name of Level 8: (Future Level)" + NewLine
				+ "        Level-9 : Name of Level 9: (Future Level)" + NewLine
				+ "       WaitSave : Optional: Wait before saving." + NewLine;
		//@formatter:on

		if (SysOut) {
			out(Str);
			out("");
			return null;
		} else {
			return Str;
		}

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

		try {
			File config = new File(MinecraftLandGeneratorConf);
			BufferedReader in = new BufferedReader(new FileReader(config));
			String line;
			while ((line = in.readLine()) != null) {
				int pos = line.indexOf('=');
				int end = line.lastIndexOf('#'); // comments, ignored lines

				if (end == -1) { // If we have no hash sign, then we read till the end of the line
					end = line.length();
				}
				if (end <= pos) { // If hash is before the '=', we may have an issue... it should be fine, cause we check for issues next, but lets make sure.
					end = line.length();
				}

				if (pos != -1) {
					if (line.substring(0, pos).toLowerCase().equals("serverpath")) {
						serverPath = line.substring(pos + 1, end);
					} else if (line.substring(0, pos).toLowerCase().equals("java")) {
						javaLine = line.substring(pos + 1, end);
					} else if (line.substring(0, pos).toLowerCase().equals("done_text")) {
						doneText = line.substring(pos + 1, end);
					} else if (line.substring(0, pos).toLowerCase().equals("preparing_text")) {
						preparingText = line.substring(pos + 1, end);
					} else if (line.substring(0, pos).toLowerCase().equals("preparing_level")) {
						preparingLevel = line.substring(pos + 1, end);
					} else if (line.substring(0, pos).toLowerCase().equals("level-0")) {
						level_0 = line.substring(pos + 1, end);
					} else if (line.substring(0, pos).toLowerCase().equals("level-1")) {
						level_1 = line.substring(pos + 1, end);
					} else if (line.substring(0, pos).toLowerCase().equals("level-2")) {
						level_2 = line.substring(pos + 1, end);
					} else if (line.substring(0, pos).toLowerCase().equals("level-3")) {
						level_3 = line.substring(pos + 1, end);
					} else if (line.substring(0, pos).toLowerCase().equals("level-4")) {
						level_4 = line.substring(pos + 1, end);
					} else if (line.substring(0, pos).toLowerCase().equals("level-5")) {
						level_5 = line.substring(pos + 1, end);
					} else if (line.substring(0, pos).toLowerCase().equals("level-6")) {
						level_6 = line.substring(pos + 1, end);
					} else if (line.substring(0, pos).toLowerCase().equals("level-7")) {
						level_7 = line.substring(pos + 1, end);
					} else if (line.substring(0, pos).toLowerCase().equals("level-8")) {
						level_8 = line.substring(pos + 1, end);
					} else if (line.substring(0, pos).toLowerCase().equals("level-9")) {
						level_9 = line.substring(pos + 1, end);
					} else if (line.substring(0, pos).toLowerCase().equals("waitsave")) {
						String wstmp = line.toLowerCase().substring(pos + 1, end);
						if (wstmp.equals("true")) {
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
	 *            true: Uses Default values. false:
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
		txt = "#Minecraft Land Generator Configuration File:  Version: " + VERSION + newLine
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
				if (pos != -1) {
					if (line.substring(0, pos).toLowerCase().equals("level-name")) {
						worldPath = serverPath + fileSeparator + line.substring(pos + 1);
						worldName = line.substring(pos + 1);
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

	private static void writeTxtFile(String file, String txt) {

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

	private static void out(String str) {
		System.out.println(MLG + str);		// is there a better/easier way to do this?  I just wanted a lazier way to write "System.out.println(MLG + blah..."
	}

	private static void err(String str) {
		System.err.println(MLGe + str);
	}

	private static void outP(String str) {
		System.out.print(str);
	}

	/**
	 * getInt(String msg) - outputs a message, will only accept a valid integer from keyboard
	 * 
	 * @param msg
	 *            String
	 * @return int
	 */
	private static int getInt(String msg) {

		while (!sc.hasNextInt()) {
			sc.nextLine();
			outP(MLG + "Invalid Input. " + msg);
		}
		return sc.nextInt();

	}

	/**
	 * Created: 17 April 1997<br>
	 * Author: Bert Bos &lt;<a href="mailto:bert@w3.org">bert@w3.org</a>&gt;<br>
	 * <br>
	 * unescape: <a href="http://www.w3.org/International/unescape.java">http://www.w3.org/International/unescape.java</a><br>
	 * <br>
	 * Copyright © 1997 World Wide Web Consortium, (Massachusetts Institute of Technology, European Research Consortium for Informatics and Mathematics, Keio University). All Rights Reserved. This
	 * work is distributed under the W3C® Software License [1] in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
	 * PARTICULAR PURPOSE.<br>
	 * <br>
	 * [1] <a href="http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231">http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231</a>
	 * 
	 * @param s
	 *            string of URL
	 * @return decoded string of URL
	 */
	private static String unescape(String s) {
		StringBuffer sbuf = new StringBuffer();
		int l = s.length();
		int ch = -1;
		int b, sumb = 0;
		for (int i = 0, more = -1; i < l; i++) {
			/* Get next byte b from URL segment s */
			switch (ch = s.charAt(i)) {
				case '%':
					ch = s.charAt(++i);
					int hb =
							(Character.isDigit((char) ch) ? ch - '0' : 10 + Character
									.toLowerCase((char) ch) - 'a') & 0xF;
					ch = s.charAt(++i);
					int lb =
							(Character.isDigit((char) ch) ? ch - '0' : 10 + Character
									.toLowerCase((char) ch) - 'a') & 0xF;
					b = (hb << 4) | lb;
					break;
				case '+':
					b = ' ';
					break;
				default:
					b = ch;
			}
			/* Decode byte b as UTF-8, sumb collects incomplete chars */
			if ((b & 0xc0) == 0x80) {			// 10xxxxxx (continuation byte)
				sumb = (sumb << 6) | (b & 0x3f);	// Add 6 bits to sumb
				if (--more == 0) sbuf.append((char) sumb); // Add char to sbuf
			} else if ((b & 0x80) == 0x00) {		// 0xxxxxxx (yields 7 bits)
				sbuf.append((char) b);			// Store in sbuf
			} else if ((b & 0xe0) == 0xc0) {		// 110xxxxx (yields 5 bits)
				sumb = b & 0x1f;
				more = 1;				// Expect 1 more byte
			} else if ((b & 0xf0) == 0xe0) {		// 1110xxxx (yields 4 bits)
				sumb = b & 0x0f;
				more = 2;				// Expect 2 more bytes
			} else if ((b & 0xf8) == 0xf0) {		// 11110xxx (yields 3 bits)
				sumb = b & 0x07;
				more = 3;				// Expect 3 more bytes
			} else if ((b & 0xfc) == 0xf8) {		// 111110xx (yields 2 bits)
				sumb = b & 0x03;
				more = 4;				// Expect 4 more bytes
			} else /*if ((b & 0xfe) == 0xfc)*/{	// 1111110x (yields 1 bit)
				sumb = b & 0x01;
				more = 5;				// Expect 5 more bytes
			}
			/* We don't test if the UTF-8 encoding is well-formed */
		}
		return sbuf.toString();
	}

}
