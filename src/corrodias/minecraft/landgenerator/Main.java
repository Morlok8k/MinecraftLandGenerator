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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.LongTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;

/**
 * 
 * @author Corrodias, Morlok8k, pr0f1x
 */
public class Main {

	// Version Number!
	private static final String VERSION = "1.6.0 Testing 8";
	private static final String AUTHORS = "Corrodias, Morlok8k, pr0f1x";

	private static final String fileSeparator = System.getProperty("file.separator");
	private static final String newLine = System.getProperty("line.separator");

	private int increment = 380;
	private ProcessBuilder minecraft = null;
	private String javaLine = null;
	private static final String defaultJavaLine =
			"java -Djava.awt.headless=true -Djline.terminal=jline.UnsupportedTerminal -Duser.language=en"
					+ " -Xms1024m -Xmx1024m -Xincgc -jar minecraft_server.jar nogui";
	private String serverPath = null;
	private String worldPath = null;
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

	private static DateFormat dateFormat = null;
	private static DateFormat dateFormatBuildID = null;
	private static DateFormat dateFormat_MDY = null;
	private static Date date = null;
	private static Date MLG_Last_Modified_Date = null;

	private static final Class<?> cls = Main.class;
	private static String MLGFileName = null;
	private static final String rsrcError = "rsrcERROR";
	private static String buildIDFile = "MLG-BuildID";
	private static boolean isCompiledAsJar = false;
	private static String MLG_Current_Hash = null;

	@SuppressWarnings("unused")
	private static final String MinecraftLandGeneratorConfURL =
			"https://raw.github.com/Morlok8k/MinecraftLandGenerator/master/bin/MinecraftLandGenerator.conf";
	private static final String MinecraftLandGeneratorConf = "MinecraftLandGenerator.conf";

	private static final boolean testing = false;	// a constant to display more output when debugging

	// REMINDER: because I always forget/mix up languages:
	// "static" in java means "global"
	// "final" means "constant"

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		(new Main()).run(args); // Why? idk, but merging this with run() creates
								// errors, and i'm lazy!
	}

	/**
	 * Start MinecraftLandGenerator
	 * 
	 * @author Corrodias, Morlok8k
	 * @param args
	 */
	private void run(String[] args) {

		// Lets get a nice Date format for display, and a compact one for
		// telling apart builds.
		dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a zzzz", Locale.ENGLISH);
		dateFormatBuildID = new SimpleDateFormat("'BuildID:' (yyMMdd.HHmmss)", Locale.ENGLISH);
		dateFormat_MDY = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
		date = new Date();
		// dateFormat.format(date);

		readBuildID();

		//readMe("test.txt");

		// The following displays no matter what happens, so we needed this date
		// stuff to happen first.

		// MLG_Last_Modified_Date = date;

		System.out.println("Minecraft Land Generator version " + VERSION);
		System.out.println(dateFormatBuildID.format(MLG_Last_Modified_Date));
		System.out.println("This version was last modified on "
				+ dateFormat.format(MLG_Last_Modified_Date));
		System.out.println("");
		System.out.println("Uses a Minecraft server to generate square land of a specified size.");
		System.out.println("");
		System.out.println("");

		// =====================================================================
		//                           INSTRUCTIONS
		// =====================================================================

		if (args.length == 0 || args[0].equals("-version") || args[0].equals("-help")
				|| args[0].equals("/?")) {

			showHelp(true);

			return;
		}

		// =====================================================================
		//                         STARTUP AND CONFIG
		// =====================================================================

		// the arguments are apparently okay so far. parse the conf file.
		if (args[0].equalsIgnoreCase("-conf")) {
			try {
				File config = new File(MinecraftLandGeneratorConf);
				BufferedWriter out = new BufferedWriter(new FileWriter(config));
				out.write("#Minecraft Land Generator Configuration File:  Version: " + VERSION);
				out.newLine();
				out.write("#Authors: " + AUTHORS);
				out.newLine();
				out.write("#Auto-Generated: " + dateFormat.format(date));
				out.newLine();
				out.newLine();
				out.write("#Line to run server:");
				out.newLine();
				out.write("Java=" + defaultJavaLine); // reads the default from a constant, makes it easier!
				out.newLine();
				out.newLine();
				out.write("#Location of server.  use \".\" for the same folder as MLG");
				out.newLine();
				out.write("ServerPath=.");
				out.newLine();
				out.newLine();
				out.write("#Strings read from the server");
				out.newLine();
				out.write("Done_Text=[INFO] Done");
				out.newLine();
				out.write("Preparing_Text=[INFO] Preparing spawn area:");
				out.newLine();
				out.write("Preparing_Level=[INFO] Preparing start region for");
				out.newLine();
				out.write("Level-0=The Overworld");
				out.newLine();
				out.write("Level-1=The Nether");
				out.newLine();
				out.write("Level-2=The End");
				out.newLine();
				out.write("Level-3=Level 3 (Future Level)");
				out.newLine();
				out.write("Level-4=Level 4 (Future Level)");
				out.newLine();
				out.write("Level-5=Level 5 (Future Level)");
				out.newLine();
				out.write("Level-6=Level 6 (Future Level)");
				out.newLine();
				out.write("Level-7=Level 7 (Future Level)");
				out.newLine();
				out.write("Level-8=Level 8 (Future Level)");
				out.newLine();
				out.write("Level-9=Level 9 (Future Level)");
				out.newLine();
				out.newLine();
				out.write("#Optional: Wait a few seconds after saving.");
				out.newLine();
				out.write("WaitSave=false");
				out.newLine();
				out.close();
				System.out.println(MLG + MinecraftLandGeneratorConf + " file created.");
				return;
			} catch (IOException ex) {
				System.err.println(MLG + "Could not create " + MinecraftLandGeneratorConf + ".");
				return;
			}
		} else if (args[0].equalsIgnoreCase("-ps") || args[0].equalsIgnoreCase("-printspawn")) {
			// okay, sorry, this is an ugly hack, but it's just a last-minute feature.
			printSpawn();
			return;
		} else if (args[0].equalsIgnoreCase("-build")) {
			buildID();
			return;
		} else if (args[0].equalsIgnoreCase("-readme")) {
			readMe(args[1]);
			return;
		} else if (args.length == 1) {
			System.out.println(MLG + "For help, use java -jar MinecraftLandGenerator.jar -help");
			return;
		}

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
				System.out.println(MLG + "[TEST] Test Output: Reading of Config File ");
				System.out.println(MLG + "[TEST]     serverPath: " + serverPath);
				System.out.println(MLG + "[TEST]       javaLine: " + javaLine);
				System.out.println(MLG + "[TEST]       doneText: " + doneText);
				System.out.println(MLG + "[TEST]  preparingText: " + preparingText);
				System.out.println(MLG + "[TEST] preparingLevel: " + preparingLevel);
				System.out.println(MLG + "[TEST]        level_0: " + level_0);
				System.out.println(MLG + "[TEST]        level_1: " + level_1);
				System.out.println(MLG + "[TEST]        level_2: " + level_2);
				System.out.println(MLG + "[TEST]        level_3: " + level_3);
				System.out.println(MLG + "[TEST]        level_4: " + level_4);
				System.out.println(MLG + "[TEST]        level_5: " + level_5);
				System.out.println(MLG + "[TEST]        level_6: " + level_6);
				System.out.println(MLG + "[TEST]        level_7: " + level_7);
				System.out.println(MLG + "[TEST]        level_8: " + level_8);
				System.out.println(MLG + "[TEST]        level_9: " + level_9);
				System.out.println(MLG + "[TEST]       waitSave: " + waitSave);
			}

			boolean oldConf = false; // This next section checks to see if we have a old configuration file (or none!)

			if (serverPath == null || javaLine == null) { 			// MLG 1.2 Check for a valid .conf file.
				System.err.println(MLG + MinecraftLandGeneratorConf
						+ " does not contain all required properties.  Making New File!");
				// Please recreate it by running this application with -conf.
				// return;
				// We no longer quit. We generate a new one with defaults.

				javaLine = defaultJavaLine;
				serverPath = ".";
				oldConf = true;
			}

			if (doneText == null) {					// MLG 1.3
				oldConf = true;
			} else if (preparingText == null) {		// MLG 1.4?
				oldConf = true;
			} else if (preparingLevel == null) {	// MLG 1.4?
				oldConf = true;
			} else if (level_1 == null) {			// MLG 1.5.0?
				oldConf = true;
			} else if (level_0 == null) {			// MLG 1.5.1
				oldConf = true;
			}

			if (oldConf) {
				System.err.println(MLG + "Old Version of " + MinecraftLandGeneratorConf
						+ " found.  Updating...");
				try {
					File configUpdate = new File(MinecraftLandGeneratorConf);
					BufferedWriter out = new BufferedWriter(new FileWriter(configUpdate));
					out.write("#Minecraft Land Generator Configuration File:  Version: " + VERSION);
					out.newLine();
					out.write("#Authors: " + AUTHORS);
					out.newLine();
					out.write("#Auto-Updated: " + dateFormat.format(date));
					out.newLine();
					out.newLine();
					out.write("#Line to run server:");
					out.newLine();
					out.write("Java=" + javaLine);
					out.newLine();
					out.newLine();
					out.write("#Location of server.  use \".\" for the same folder as MLG");
					out.newLine();
					out.write("ServerPath=" + serverPath);
					out.newLine();
					out.newLine();
					out.write("#Strings read from the server");
					out.newLine();
					out.write("Done_Text=[INFO] Done");
					out.newLine();
					out.write("Preparing_Text=[INFO] Preparing spawn area:");
					out.newLine();
					out.write("Preparing_Level=[INFO] Preparing start region for");
					out.newLine();
					out.write("Level-0=The Overworld");
					out.newLine();
					out.write("Level-1=The Nether");
					out.newLine();
					out.write("Level-2=The End");
					out.newLine();
					out.write("Level-3=Level 3 (Future Level)");
					out.newLine();
					out.write("Level-4=Level 4 (Future Level)");
					out.newLine();
					out.write("Level-5=Level 5 (Future Level)");
					out.newLine();
					out.write("Level-6=Level 6 (Future Level)");
					out.newLine();
					out.write("Level-7=Level 7 (Future Level)");
					out.newLine();
					out.write("Level-8=Level 8 (Future Level)");
					out.newLine();
					out.write("Level-9=Level 9 (Future Level)");
					out.newLine();
					out.newLine();
					out.write("#Optional: Wait a few seconds after saving.");
					out.newLine();
					out.write("WaitSave=false");
					out.newLine();
					out.close();
					System.out.println(MLG + MinecraftLandGeneratorConf + " file created.");

					System.out.println("");
					int count = 0;
					while (count <= 100) {
						System.out.print(count + "% ");

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						count += 10;
					}
					System.out.println("");
					return;

				} catch (IOException ex) {
					System.err
							.println(MLG + "Could not create " + MinecraftLandGeneratorConf + ".");
					return;
				}
			}

		} catch (FileNotFoundException ex) {
			System.out
					.println(MLG
							+ "Could not find "
							+ MinecraftLandGeneratorConf
							+ ". It is recommended that you run the application with the -conf option to create it.");
			return;
		} catch (IOException ex) {
			System.err.println(MLG + "Could not read " + MinecraftLandGeneratorConf + ".");
			return;
		}

		// ARGUMENTS
		try {
			xRange = Integer.parseInt(args[0]);
			yRange = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			System.err.println(MLG + "Invalid X or Y argument.");
			return;
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
					System.out.println(MLG + "Using Alternate Launching...");
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
			System.err.println(MLG + "Invalid -i switch value.");
			return;
		}

		{
			// verify that we ended up with a good server path, either from the file or from an argument.
			File file = new File(serverPath);
			if (!file.exists() || !file.isDirectory()) {
				System.err.println(MLG + "The server directory is invalid: " + serverPath);
				return;
			}
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
			System.err.println(MLG + "Could not open " + serverPath + fileSeparator
					+ "server.properties");
			return;
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			return;
		}

		{
			File backupLevel = new File(worldPath + fileSeparator + "level_backup.dat");
			if (backupLevel.exists()) {
				System.err
						.println(MLG
								+ "There is a level_backup.dat file left over from a previous attempt that failed. You should go determine whether to keep the current level.dat"
								+ " or restore the backup.");
				return;
			}
		}

		// =====================================================================
		//                              PROCESSING
		// =====================================================================

		System.out.println(MLG + "Processing world \"" + worldPath + "\", in " + increment
				+ " block increments, with: " + javaLine);
		// System.out.println( MLG + "Processing \"" + worldName + "\"...");

		System.out.println("");

		// prepare our two ProcessBuilders
		// minecraft = new ProcessBuilder(javaLine, "-Xms1024m", "-Xmx1024m",
		// "-jar", jarFile, "nogui");
		minecraft = new ProcessBuilder(javaLine.split("\\s")); // is this always going to work? i don't know.
		minecraft.directory(new File(serverPath));
		minecraft.redirectErrorStream(true);

		try {
			System.out.println(MLG + "Launching server once to make sure there is a world.");
			runMinecraft(minecraft, verbose, alternate, javaLine);
			System.out.println("");

			File serverLevel = new File(worldPath + fileSeparator + "level.dat");
			File backupLevel = new File(worldPath + fileSeparator + "level_backup.dat");

			System.out.println(MLG + "Backing up level.dat to level_backup.dat.");
			copyFile(serverLevel, backupLevel);
			System.out.println("");

			Integer[] spawn = getSpawn(serverLevel);
			System.out.println(MLG + "Spawn point detected: [" + spawn[0] + ", " + spawn[2] + "]");
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
					System.out.println(MLG + "Centering land generation on [" + xOffset + ", "
							+ yOffset + "] due to switches.");
				}
			}
			System.out.println("");

			int totalIterations = (xRange / increment + 1) * (yRange / increment + 1);
			int currentIteration = 0;

			long differenceTime = System.currentTimeMillis();
			Long[] timeTracking =
					new Long[] { differenceTime, differenceTime, differenceTime, differenceTime };
			for (int currentX = 0 - xRange / 2; currentX <= xRange / 2; currentX += increment) {
				for (int currentY = 0 - yRange / 2; currentY <= yRange / 2; currentY += increment) {
					currentIteration++;

					System.out
							.println(MLG
									+ "Setting spawn to ["
									+ Integer.toString(currentX + xOffset)
									+ ", "
									+ Integer.toString(currentY + yOffset)
									+ "] ("
									+ currentIteration
									+ "/"
									+ totalIterations
									+ ") "
									+ Float.toString((Float.parseFloat(Integer
											.toString(currentIteration)) / Float.parseFloat(Integer
											.toString(totalIterations))) * 100) + "% Done"); // Time
																								// Remaining
																								// estimate
					timeTracking[0] = timeTracking[1];
					timeTracking[1] = timeTracking[2];
					timeTracking[2] = timeTracking[3];
					timeTracking[3] = System.currentTimeMillis();
					if (currentIteration >= 4) {
						differenceTime = (timeTracking[3] - timeTracking[0]) / 3; // well,
																					// this
																					// is
																					// what
																					// it
																					// boils
																					// down
																					// to
						differenceTime *= 1 + (totalIterations - currentIteration);
						System.out
								.println(MLG
										+ String.format(
												"Estimated time remaining: %dh%dm%ds",
												differenceTime / (1000 * 60 * 60),
												(differenceTime % (1000 * 60 * 60)) / (1000 * 60),
												((differenceTime % (1000 * 60 * 60)) % (1000 * 60)) / 1000));
					} else if (currentIteration == 3) {
						differenceTime = (timeTracking[3] - timeTracking[1]) / 2; // well,
																					// this
																					// is
																					// what
																					// it
																					// boils
																					// down
																					// to
						differenceTime *= 1 + (totalIterations - currentIteration);
						System.out
								.println(MLG
										+ String.format(
												"Estimated time remaining: %dh%dm%ds",
												differenceTime / (1000 * 60 * 60),
												(differenceTime % (1000 * 60 * 60)) / (1000 * 60),
												((differenceTime % (1000 * 60 * 60)) % (1000 * 60)) / 1000));
					} else if (currentIteration == 2) {
						differenceTime = (timeTracking[3] - timeTracking[2]); // well,
																				// this
																				// is
																				// what
																				// it
																				// boils
																				// down
																				// to
						differenceTime *= 1 + (totalIterations - currentIteration);
						System.out
								.println(MLG
										+ String.format(
												"Estimated time remaining: %dh%dm%ds",
												differenceTime / (1000 * 60 * 60),
												(differenceTime % (1000 * 60 * 60)) / (1000 * 60),
												((differenceTime % (1000 * 60 * 60)) % (1000 * 60)) / 1000));
					} else if (currentIteration <= 1) {
						System.out.println(MLG + "Estimated time remaining: Calculating...");
					}

					// Set the spawn point
					setSpawn(serverLevel, currentX + xOffset, 128, currentY + yOffset);

					// Launch the server
					runMinecraft(minecraft, verbose, alternate, javaLine);
					System.out.println("");
				}
			}

			System.out.println(MLG + "Finished generating chunks.");
			copyFile(backupLevel, serverLevel);
			backupLevel.delete();
			System.out.println(MLG + "Restored original level.dat.");
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

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
			System.out.println(MLG + "Seed: " + randomSeed.getValue()); // lets
																		// output
																		// the
																		// seed,
																		// cause
																		// why
																		// not?

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
	 * Changes the spawn point in the given Alpha/Beta level to the given coordinates. Note that, in Minecraft levels, the Y coordinate is height, while Z is what may normally be thought of as Y.
	 * 
	 * @param level
	 *            the level file to change the spawn point in
	 * @param x
	 *            the new X value
	 * @param z
	 *            the new Y value
	 * @param y
	 *            the new Z value
	 * @throws IOException
	 *             if there are any problems reading/writing the file
	 */
	protected static void setSpawn(File level, Integer x, Integer y, Integer z) throws IOException {
		try {
			NBTInputStream input = new NBTInputStream(new FileInputStream(level));
			CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
			input.close();

			// <editor-fold defaultstate="collapsed" desc="structure">
			// Structure:
			//
			// TAG_Compound("Data"): World data.
			// * TAG_Long("Time"): Stores the current "time of day" in ticks.
			// There are 20 ticks per real-life second, and 24000 ticks per
			// Minecraft day, making the day length 20 minutes. 0 appears to be
			// sunrise, 12000 sunset and 24000 sunrise again.
			// * TAG_Long("LastPlayed"): Stores the Unix time stamp (in
			// milliseconds) when the player saved the game.
			// * TAG_Compound("Player"): Player entity information. See Entity
			// Format and Mob Entity Format for details. Has additional
			// elements:
			// o TAG_List("Inventory"): Each TAG_Compound in this list defines
			// an item the player is carrying, holding, or wearing as armor.
			// + TAG_Compound: Inventory item data
			// # TAG_Short("id"): Item or Block ID.
			// # TAG_Short("Damage"): The amount of wear each item has suffered.
			// 0 means undamaged. When the Damage exceeds the item's durability,
			// it breaks and disappears. Only tools and armor accumulate damage
			// normally.
			// # TAG_Byte("Count"): Number of items stacked in this inventory
			// slot. Any item can be stacked, including tools, armor, and
			// vehicles. Range is 1-255. Values above 127 are not displayed
			// in-game.
			// # TAG_Byte("Slot"): Indicates which inventory slot this item is
			// in.
			// o TAG_Int("Score"): Current score, doesn't appear to be
			// implemented yet. Always 0.
			// * TAG_Int("SpawnX"): X coordinate of the player's spawn position.
			// Default is 0.
			// * TAG_Int("SpawnY"): Y coordinate of the player's spawn position.
			// Default is 64.
			// * TAG_Int("SpawnZ"): Z coordinate of the player's spawn position.
			// Default is 0.
			// * TAG_Byte("SnowCovered"): 1 enables, 0 disables, see Winter Mode
			// //Old!
			// * TAG_Long("SizeOnDisk"): Estimated size of the entire world in
			// bytes.
			// * TAG_Long("RandomSeed"): Random number providing the Random Seed
			// for the terrain.
			// </editor-fold>

			Map<String, Tag> originalData =
					((CompoundTag) originalTopLevelTag.getValue().get("Data")).getValue();
			// This is our map of data. It is an unmodifiable map, for some
			// reason, so we have to make a copy.
			Map<String, Tag> newData = new LinkedHashMap<String, Tag>(originalData);
			// .get() a couple of values, just to make sure we're dealing with a
			// valid level file, here. Good for debugging, too.

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
	 */
	protected static void runMinecraft(ProcessBuilder minecraft, boolean verbose,
			boolean alternate, String javaLine) throws IOException {
		System.out.println(MLG + "Starting server.");

		boolean warning = false;

		// monitor output and print to console where required.
		// STOP the server when it's done.

		// Damn it Java! I hate you so much!
		// I can't reuse code the way I want to, like in other langauges.
		// So, here is a bunch of duplicate code...
		// Stupid compile errors...

		if (alternate) { // Alternate - a replication (slightly stripped down)
							// of MLG 1.3.0's code. simplest code possible.
			System.out.println(MLG + "Alternate Launch");
			Process process = minecraft.start();

			// monitor output and print to console where required.
			// STOP the server when it's done.
			BufferedReader pOut =
					new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = pOut.readLine()) != null) {
				System.out.println(line);
				if (line.contains(doneText)) { // EDITED By Morlok8k for
												// Minecraft 1.3+ Beta
					OutputStream outputStream = process.getOutputStream();
					if (waitSave) {
						System.out.println(MLG + "Waiting 30 seconds to save.");

						int count = 1;
						while (count <= 30) {
							System.out.print(".");

							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							count += 1;
						}
						System.out.println("");
					}

					System.out.println(MLG + "Saving server data...");
					byte[] saveall = { 's', 'a', 'v', 'e', '-', 'a', 'l', 'l', '\r', '\n' };
					outputStream.write(saveall);
					outputStream.flush();
					byte[] stop = { 's', 't', 'o', 'p', '\r', '\n' };

					System.out.println(MLG + "Stopping server...");

					outputStream.write(stop);
					outputStream.flush();
					if (waitSave) {
						System.out.println(MLG + "Waiting 10 seconds to save.");
						int count = 1;
						while (count <= 10) {
							System.out.print(".");

							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							count += 1;
						}
						System.out.println("");
					}
				}
			}
			// readLine() returns null when the process exits

		} else { // start minecraft server normally!
			Process process = minecraft.start();
			if (verbose) {
				System.out.println(MLG + "Started Server.");
			}
			BufferedReader pOut =
					new BufferedReader(new InputStreamReader(process.getInputStream()));
			if (verbose) {
				System.out.println(MLG + "Accessing Server Output...");
			}

			String line = null;

			byte[] stop = { 's', 't', 'o', 'p', '\r', '\n' }; // Moved here, so
																// this code
																// wont run
																// every loop,
																// thus Faster!
			// and no, i can't use a string here!

			byte[] saveAll = { 's', 'a', 'v', 'e', '-', 'a', 'l', 'l', '\r', '\n' };

			OutputStream outputStream = process.getOutputStream(); // moved here
																	// to remove
																	// some
																	// redundancy

			while ((line = pOut.readLine()) != null) {
				if (verbose) {
					if (line.contains("[INFO]")) {
						System.out.println(line.substring(line.lastIndexOf("]") + 2));
					} else {
						System.out.println(line);
					}
				} else if (line.contains(preparingText)) {
					System.out.print(line.substring(line.length() - 3, line.length()) + "... ");
				} else if (line.contains(preparingLevel)) {
					if (line.contains("level 0")) { // "Preparing start region for level 0"
						System.out.println("\r\n" + MLG + worldName + ": " + level_0 + ":");
					} else if (line.contains("level 1")) { // "Preparing start region for level 1"
						System.out.println("\r\n" + MLG + worldName + ": " + level_1 + ":");
					} else if (line.contains("level 2")) { // "Preparing start region for level 2"
						System.out.println("\r\n" + MLG + worldName + ": " + level_2 + ":");
					} else if (line.contains("level 3")) { // "Preparing start region for level 3"
						System.out.println("\r\n" + MLG + worldName + ": " + level_3 + ":");
					} else if (line.contains("level 4")) { // "Preparing start region for level 4"
						System.out.println("\r\n" + MLG + worldName + ": " + level_4 + ":");
					} else if (line.contains("level 5")) { // "Preparing start region for level 5"
						System.out.println("\r\n" + MLG + worldName + ": " + level_5 + ":");
					} else if (line.contains("level 6")) { // "Preparing start region for level 6"
						System.out.println("\r\n" + MLG + worldName + ": " + level_6 + ":");
					} else if (line.contains("level 7")) { // "Preparing start region for level 7"
						System.out.println("\r\n" + MLG + worldName + ": " + level_7 + ":");
					} else if (line.contains("level 8")) { // "Preparing start region for level 8"
						System.out.println("\r\n" + MLG + worldName + ": " + level_8 + ":");
					} else if (line.contains("level 9")) { // "Preparing start region for level 9"
						System.out.println("\r\n" + MLG + worldName + ": " + level_9 + ":");
					} else {
						System.out.println(line.substring(line.lastIndexOf("]") + 2));
					}
				}

				if (line.contains(doneText)) { // now this is configurable!
					System.out.println("");
					if (waitSave) {
						System.out.println(MLG + "Waiting 30 seconds to save.");

						int count = 1;
						while (count <= 30) {
							System.out.print(".");

							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							count += 1;
						}
						System.out.println("");
					}
					System.out.println(MLG + "Saving server data.");
					outputStream.write(saveAll);
					outputStream.flush();

					System.out.println(MLG + "Stopping server.");
					// OutputStream outputStream = process.getOutputStream();
					outputStream.write(stop);
					outputStream.flush();
					// outputStream.close();

					if (waitSave) {
						System.out.println(MLG + "Waiting 10 seconds to save.");
						int count = 1;
						while (count <= 10) {
							System.out.print(".");

							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							count += 1;
						}
						System.out.println("");
					}
				}
				if (ignoreWarnings == false) {
					if (line.contains("[WARNING]")) { // If we have a warning,
														// stop...
						System.out.println("");
						System.out
								.println(MLG + "Warning found: Stopping Minecraft Land Generator");
						if (verbose == false) { // If verbose is true, we
												// already displayed it.
							System.out.println(line);
						}
						System.out.println("");
						System.out.println(MLG + "Forcing Save...");
						outputStream.write(saveAll);
						outputStream.flush();
						// OutputStream outputStream =
						// process.getOutputStream();
						outputStream.write(stop); // if the warning was a fail
													// to bind to port, we may
													// need to write stop twice!
						outputStream.flush();
						outputStream.write(stop);
						outputStream.flush();
						// outputStream.close();
						warning = true;
						// System.exit(1);
					}
					if (line.contains("[SEVERE]")) { // If we have a severe
														// error, stop...
						System.out.println("");
						System.out.println(MLG + "Severe error found: Stopping server.");
						if (verbose == false) { // If verbose is true, we
												// already displayed it.
							System.out.println(line);
						}
						System.out.println("");
						System.out.println(MLG + "Forcing Save...");
						outputStream.write(saveAll);
						outputStream.flush();
						// OutputStream outputStream =
						// process.getOutputStream();
						outputStream.write(stop);
						outputStream.flush();
						outputStream.write(stop); // sometimes we need to do
													// stop twice...
						outputStream.flush();
						// outputStream.close();
						warning = true;
						// System.exit(1);
						// Quit!
					}
				}
			}

			if (warning == true) { // in 1.4.4 we had a issue. tried to write
									// stop twice, but we had closed the stream
									// already. this, and other lines should fix
									// this.
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
	public static void copyFile(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) >= 0) {
			if (len > 0) {
				out.write(buf, 0, len);
			}
		}
		in.close();
		out.flush();
		out.close();
	}

	private boolean printSpawn() {
		// ugh, sorry, this is an ugly hack, but it's a last-minute feature.
		// this is a lot of duplicated code.

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

				if (end <= pos) { // If hash is before the '=', we may have a issue... it should be fine, cause we check for issues next, but lets make sure.
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
					}
				}
			}
			in.close();

			if (serverPath == null || javaLine == null) {
				System.err
						.println(MLG
								+ MinecraftLandGeneratorConf
								+ " does not contain all requird properties. Please recreate it by running this application with no arguments.");
				return false;
			}
		} catch (FileNotFoundException ex) {
			System.out
					.println(MLG
							+ "Could not find "
							+ MinecraftLandGeneratorConf
							+ ". It is recommended that you run the application with the -conf option to create it.");
			return false;
		} catch (IOException ex) {
			System.err.println(MLG + "Could not read " + MinecraftLandGeneratorConf + ".");
			return false;
		}

		{
			// verify that we ended up with a good server path, either from the file or from an argument.
			File file = new File(serverPath);
			if (!file.exists() || !file.isDirectory()) {
				System.err.println(MLG + "The server directory is invalid: " + serverPath);
				return false;
			}
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
					}
				}
			}

		} catch (FileNotFoundException ex) {
			System.err.println(MLG + "Could not open " + serverPath + fileSeparator
					+ "server.properties");
			return false;
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}

		File level = new File(worldPath + fileSeparator + "level.dat");
		if (!level.exists() || !level.isFile()) {
			System.err
					.println(MLG
							+ "The currently-configured world does not exist. Please launch the server once, first.");
			return false;
		}

		try {
			Integer[] spawn = getSpawn(level);
			System.out.println(MLG + "The current spawn point is: [" + spawn[0] + ", " + spawn[2]
					+ "]");
			return true;
		} catch (IOException ex) {
			System.err.println(MLG + "Error while reading " + level.getPath());
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
	public static void readMe(String readmeFile) {

		if (readmeFile == "" || readmeFile == null) {
			readmeFile = "MLG-Readme.txt";
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
				+ "When a Minecraft server is launched, it automatically generates chunks within a square area of 20x20 chunks (320x320 blocks), centered on the current spawn point. When provided X and Y ranges as arguments, this program will launch the server repeatedly, editing the level.dat file between sessions, to generate large amounts of land without players having to explore them. The generated land will have about the X and Y ranges as requested by the arguments, though it will not be exact due to the spawn point typically not on the border of a chunk. (Because of this, MLG by default adds a slight overlap with each pass - 300x300) You can use the -x and -y switches to override the spawn offset and center the land generation on a different point." + newLine
				+ "The program makes a backup of level.dat as level_backup.dat before editing, and restores the backup at the end. In the event that a level_backup.dat file already exists, the program will refuse to proceed, leaving the user to determine why the level_backup.dat file exists and whether they would rather restore it or delete it, which must be done manually." + newLine
				+ newLine
				+ "This program is public domain, and the source code is included in the .jar file.  (If accidently missing, like in 1.3.0 and 1.4.0, it is always available at Github.)" + newLine
				+ "The JNLP library is included (inside the .jar) as jnbt-1.1.jar. It is not public domain. Its license is included within its .jar file, as LICENSE.TXT." + newLine
				+ "It is also available at: http://jnbt.sourceforge.net/" + newLine
				+ newLine
				+ "-----------------------------------------------" + newLine
				+ newLine
				+ "Version History:" + newLine
				+ "Morlok8k:" + newLine
				+ newLine
				+ "1.6.0" + newLine
				+ "- TODO: add features" + newLine
				+ "- Added the ability to download files from the internet (specifically for the BuildID file)" + newLine
				+ "- Added the ability to check what version the .jar is. (Using MD5 hashes, timestamps, and the BuildID file)" + newLine
				+ "- Minor Refactoring" + newLine
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
				+ "     + (the goal is to have MLG be configureable, so it can work on any version of the server, past or present.)" + newLine
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
				+ "I recommend using MCE or MCEDIT to relight the map after you generate it. This will take a long time, but should fix all those incorrectly dark spots in your level." + newLine
				+ newLine
				+ "-----------------------------------------------" + newLine
				+ newLine;
		//@formatter:on

		try {
			File readme = new File(readmeFile);
			BufferedWriter out = new BufferedWriter(new FileWriter(readme));

			out.write(ReadMeText + showHelpSTR);

			out.newLine();
			out.close();

			System.out.println(MLG + readmeFile + " file created.");
			return;

		} catch (IOException ex) {
			System.err.println(MLG + "Could not create " + readmeFile + ".");
			return;
		}

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
	 */
	public static void downloadFile(String URL) {

		String fileName = URL.substring(URL.lastIndexOf("/") + 1, URL.length());
		int size = 1024 * 4; // 1024 * n should be tested to get the optimum
								// size (for download speed.)

		if (fileName.equals("")) {
			fileName = String.valueOf(System.currentTimeMillis());
		}

		System.out.println(MLG + "Downloading: " + URL);
		System.out.println(MLG + "Saving as: " + fileName);

		long differenceTime = System.currentTimeMillis();
		Long[] timeTracking = new Long[] { differenceTime, differenceTime };
		timeTracking[0] = System.currentTimeMillis();

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
			}
			bout.close();
			in.close();
			System.out.println(count + " byte(s) copied");

			timeTracking[1] = System.currentTimeMillis();
			differenceTime = (timeTracking[1] - timeTracking[0]) / 2;

			System.out.println(String.format(MLG + "Elapsed Time: %dm%ds",
					(differenceTime % (1000 * 60 * 60)) / (1000 * 60),
					((differenceTime % (1000 * 60 * 60)) % (1000 * 60)) / 1000));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(MLG + "Done");

	}

	/**
	 * This is an "undocumented" function to create a BuildID file. It should only be used right after compiling a .jar file<br>
	 * The resulting BuildID file is uploaded to github, and also distributed with the program.<br>
	 * <b>THE FILE SHOULD ONLY BE MADE FROM SCRATCH AT THE TIME OF PUBLISHING!</b><br>
	 * (Otherwise, the purpose is defeated!)
	 * 
	 * @author Morlok8k
	 */
	public static void buildID() {

		// TODO: Decide to download BuildID from Github.
		// If not available, create.
		// After downloading, check to see if it matches hash.

		// example:
		// downloadFile(MinecraftLandGeneratorConfURL);
		// downloadFile("https://raw.github.com/Morlok8k/MinecraftLandGenerator/master/bin/" + buildIDFile);

		if (MLGFileName == null) {
			try {
				MLGFileName = getClassLoader(cls);
			} catch (Exception e) {
				System.out.println(MLG + "Error: Finding file failed");
				e.printStackTrace();
			}
			if (MLGFileName.equals(rsrcError)) { return; }
		}

		if (MLG_Current_Hash == null) {

			try {
				MLG_Current_Hash = fileMD5(MLGFileName);
				// System.out.println(hash + "  " + MLGFileName);
			} catch (Exception e) {
				System.out.println(MLG + "Error: MD5 from file failed");
				e.printStackTrace();
			}
		}

		Date time = null;
		try {
			time = getCompileTimeStamp(cls);
		} catch (Exception e) {
			System.out.println(MLG + "Error: TimeStamp from file failed");
			e.printStackTrace();
		}
		// System.out.println(d.toString());

		boolean notNew = false;
		String INFO = "";
		if (isCompiledAsJar == false) {
			INFO = " (Class File, Not .Jar)";
		}

		try {
			BufferedReader in = new BufferedReader(new FileReader(buildIDFile));
			BufferedWriter out = new BufferedWriter(new FileWriter(buildIDFile + ".temp"));
			String line;
			while ((line = in.readLine()) != null) {

				if (line.contains(MLG_Current_Hash)) {
					notNew = true;
					// System.out.println("[DEBUG] NotNew");
				}

				out.write(line);
				out.newLine();
			}

			if (notNew == false) {
				out.write(MLG_Current_Hash + "=" + String.valueOf(time.getTime()) + "#MLG v"
						+ VERSION + INFO);
				out.newLine();
			}
			out.close();
			in.close();

			File fileDelete = new File(buildIDFile);
			fileDelete.delete();
			File fileRename = new File(buildIDFile + ".temp");
			fileRename.renameTo(new File(buildIDFile));

		} catch (FileNotFoundException ex) {
			System.out.println(MLG + "\"" + buildIDFile + "\" file not Found.  Generating New \""
					+ buildIDFile + "\" File");
			try {
				BufferedWriter out = new BufferedWriter(new FileWriter(buildIDFile));
				out.write(MLG_Current_Hash + "=" + String.valueOf(time.getTime()) + "#MLG v"
						+ VERSION + INFO);
				out.newLine();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException ex) {
			System.err.println(MLG + "Could not create \"" + buildIDFile + "\".");
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

		if (MLGFileName == null) {
			try {
				MLGFileName = getClassLoader(cls);
			} catch (Exception e) {
				System.out.println(MLG + "Error: Finding file failed");
				e.printStackTrace();
			}
			if (MLGFileName.equals(rsrcError)) { return; }
		}

		if (MLG_Current_Hash == null) {

			try {
				MLG_Current_Hash = fileMD5(MLGFileName);
				// System.out.println(hash + "  " + MLGFileName);
			} catch (Exception e) {
				System.out.println(MLG + "Error: MD5 from file failed");
				e.printStackTrace();
			}
		}

		if (MLG_Last_Modified_Date == null) {
			boolean foundLine = false;
			try {
				BufferedReader in = new BufferedReader(new FileReader(buildIDFile));
				String line;

				while ((line = in.readLine()) != null) {

					if (line.contains(MLG_Current_Hash)) {
						// System.out.println("[DEBUG] Found!");
						foundLine = true;

						int pos = line.indexOf('=');
						int end = line.lastIndexOf('#'); // comments, ignored lines

						if (end == -1) { // If we have no hash sign, then we read till the end of the line
							end = line.length();
						}
						if (end <= pos) { // If hash is before the '=', we may have an issue... it should be fine, cause we check for issues next, but lets make
											// sure.
							end = line.length();
						}

						if (pos != -1) {
							if (line.substring(0, pos).equals(MLG_Current_Hash)) {
								MLG_Last_Modified_Date =
										new Date(new Long(line.substring(pos + 1, end)));
								return;
							}

						}
					}

				}
				in.close();

				if (foundLine == false) {
					// System.out.println("[DEBUG] FoundLine False");
					buildID();
					readBuildID();	// yes I'm calling the function from itself. potential infinite loop? possibly. I haven't encountered it yet!
					// TODO: Add safeguard to prevent Infinite Loops
					return;
				}
			} catch (Exception e) {
				System.err.println(MLG + "Cant Read " + buildIDFile + "!");
				// e.printStackTrace();
				buildID();
				readBuildID();
				// TODO: Add safeguard to prevent Infinite Loops
				return;

			}
		}

	}

	/**
	 * This gets the filename of a .jar (typically this one!)
	 * 
	 * @author Morlok8k
	 */
	public static String getClassLoader(Class<?> classFile) throws IOException {
		ClassLoader loader = classFile.getClassLoader();
		String filename = classFile.getName().replace('.', '/') + ".class";
		URL resource =
				(loader != null) ? loader.getResource(filename) : ClassLoader
						.getSystemResource(filename);
		filename = URLDecoder.decode(resource.toString(), "UTF-8");
		// System.out.println(filename);

		// START Garbage removal:
		int bang = filename.indexOf("!");		// remove everything after xxxx.jar
		if (bang == -1) { 										// a real example:
			bang = filename.length();			// jar:file:/home/morlok8k/test.jar!/me/Morlok8k/test/Main.class
		}
		int file = filename.indexOf("file:");	// removes junk from the beginning
												// of the path
		file = file + 5;
		if (file == -1) {
			file = 0;
		}
		if (filename.contains("rsrc:")) {
			System.out
					.println(MLG
							+ "THIS WAS COMPILED USING \"org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader\"! ");
			System.out.println(MLG + "DO NOT PACKAGE YOUR .JAR'S WITH THIS CLASSLOADER CODE!");
			System.out.println(MLG + "(Your Libraries need to be extracted.)");
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
	public static Date getCompileTimeStamp(Class<?> classFile) throws IOException {
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
	public static String fileMD5(String fileName) throws NoSuchAlgorithmException,
			FileNotFoundException {
		// System.out.println("");
		// System.out.println("");
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
			String output = bigInt.toString(16);
			// System.out.println(MLG + "MD5: " + output);
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
	public static String showHelp(boolean SysOut) {
		String Str = null;

		//@formatter:off
		Str =	"Usage: java -jar MinecraftLandGenerator.jar x y [serverpath] [switches]" + newLine
				+ newLine
				+ "Arguments:" + newLine
				+ "              x : X range to generate" + newLine
				+ "              y : Y range to generate" + newLine
				+ "     serverpath : the path to the directory in which the server runs (takes precedence over the config file setting)" + newLine
				+ newLine
				+ "Switches:" + newLine
				+ "       -verbose : causes the application to output the server's messages to the console" + newLine
				+ "             -v : same as -verbose" + newLine
				+ "             -w : Ignore [WARNING] and [SEVERE] messages." + newLine
				+ "           -alt : alternate server launch sequence" + newLine
				+ "             -a : same as -alt" + newLine
				+ "            -i# : override the iteration spawn offset increment (default 300) (example: -i100)" + newLine
				+ "            -x# : set the X offset to generate land around (example: -x0)" + newLine
				+ "            -y# : set the X offset to generate land around (example: -y0)" + newLine
				+ newLine
				+ "Other options:" + newLine
				+ "  java -jar MinecraftLandGenerator.jar -printspawn" + newLine
				+ "  java -jar MinecraftLandGenerator.jar -ps" + newLine
				+ "        Outputs the current world's spawn point coordinates." + newLine 
				+ newLine
				+ "  java -jar MinecraftLandGenerator.jar -conf" + newLine
				+ "        Generates a "+ MinecraftLandGeneratorConf + " file." + newLine
				+ newLine
				+ "  java -jar MinecraftLandGenerator.jar -readme readme.txt" + newLine
				+ "  java -jar MinecraftLandGenerator.jar -readme" + newLine
				+ "        Generates a readme file using supplied name or the default MLG-Readme.txt" + newLine
				+ newLine
				+ "  java -jar MinecraftLandGenerator.jar -version" + newLine
				+ "  java -jar MinecraftLandGenerator.jar -help" + newLine
				+ "  java -jar MinecraftLandGenerator.jar /?" + newLine
				+ "        Prints this message." + newLine 
				+ newLine
				+ "When launched with the -conf switch, this application creates a " + MinecraftLandGeneratorConf + " file that contains configuration options." + newLine
				+ "If this file does not exist or does not contain all required properties, the application will not run." + newLine 
				+ newLine
				+ MinecraftLandGeneratorConf + " properties:" + newLine
				+ "           Java : The command line to use to launch the server" + newLine
				+ "     ServerPath : The path to the directory in which the server runs (can be overridden by the serverpath argument)" + newLine
				+ "      Done_Text : The output from the server that tells us that we are done" + newLine
				+ " Preparing_Text : The output from the server that tells us the percentage" + newLine
				+ "Preparing_Level : The output from the server that tells us the level it is working on" + newLine 
				+ "        Level-0 : Name of Level 0: The Overworld" + newLine
				+ "        Level-1 : Name of Level 1: The Nether" + newLine
				+ "        Level-2 : Name of Level 2: The End" + newLine
				+ "        Level-3 : Name of Level 3: (Future Level)" + newLine
				+ "        Level-4 : Name of Level 4: (Future Level)" + newLine
				+ "        Level-5 : Name of Level 5: (Future Level)" + newLine
				+ "        Level-6 : Name of Level 6: (Future Level)" + newLine
				+ "        Level-7 : Name of Level 7: (Future Level)" + newLine
				+ "        Level-8 : Name of Level 8: (Future Level)" + newLine
				+ "        Level-9 : Name of Level 9: (Future Level)" + newLine
				+ "       WaitSave : Optional: Wait before saving." + newLine;
		//@formatter:on

		if (SysOut) {
			System.out.print(Str);
			System.out.println("");
			return null;
		} else {
			return Str;
		}

	}

}
