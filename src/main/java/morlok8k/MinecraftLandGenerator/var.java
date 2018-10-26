/*
#######################################################################
#            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE              #
#                    Version 2, December 2004                         #
#                                                                     #
# Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>                    #
#                                                                     #
# Everyone is permitted to copy and distribute verbatim or modified   #
# copies of this license document, and changing it is allowed as long #
# as the name is changed.                                             #
#                                                                     #
#            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE              #
#   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION   #
#                                                                     #
#  0. You just DO WHAT THE FUCK YOU WANT TO.                          #
#                                                                     #
#######################################################################
*/

package morlok8k.MinecraftLandGenerator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

/**
 * 
 * This holds all the major variables that different sections of MLG uses...
 * 
 * @author morlok8k
 */
public class var {

	//
	// Program Info:

	/** display more output when debugging */
	public static boolean testing = false;

	/** display more output */
	public static boolean verbose = false;

	/** Program Name */
	public static final String PROG_NAME = "Minecraft Land Generator";

	/** Version Number! */
	public static final String VERSION = "1.7.7";

	/** Authors */
	public static final String AUTHORS = "Corrodias, Morlok8k, pr0f1x, jaseg, Gallion";

	/** Website */
	public static final String WEBSITE = "https://sites.google.com/site/minecraftlandgenerator/";

	//
	// Operating System Info:
	/** "/" or "\" depending on system */
	public static final String fileSeparator = System.getProperty("file.separator");

	/** CRLF ('\r\n') on Windows, LF ('\n') on Linux, etc... */
	public static final String newLine = System.getProperty("line.separator");

	//
	// Commonly Used Strings:
	/** "[MLG] " */
	public static final String MLG = "[MLG] ";

	/** "[MLG-ERROR] " */
	public static final String MLGe = "[MLG-ERROR] ";

	//
	// Date & Build-ID stuff:
	/** Style: "January 1, 1970" */
	public static final DateFormat dateFormat_MDY = new SimpleDateFormat("MMMM d, yyyy",
			Locale.ENGLISH);
	/** class files of .jar were last modified on... */
	public static Date MLG_Last_Modified_Date = null;

	/**
	 * Lets get a nice Date format for display<br>
	 * Style: "Sunday, September 16, 2012 at 5:12 PM, Pacific Daylight Time"
	 */
	public static final DateFormat dateFormat = new SimpleDateFormat(
			"EEEE, MMMM d, yyyy 'at' h:mm a, zzzz", Locale.ENGLISH);

	/** a date */
	public static Date date = null;

	/** last modified date stored as a Long */
	public static Long MLG_Last_Modified_Long = 0L;

	/** File: "MLG-BuildID" */
	public static String buildIDFile = "MLG-BuildID";

	//
	// Filenames:
	/** "MinecraftLandGenerator.jar" by default. This actually tracks the current filename for the jar. */
	public static String MLGFileNameShort = null;

	/** "MinecraftLandGenerator.conf" */
	public static final String MinecraftLandGeneratorConf = "MinecraftLandGenerator.conf";

	/** "_MLG_Readme.txt" */
	public static final String defaultReadmeFile = "_MLG_Readme.txt";

	/** "MinecraftLandGenerator.jar" */
	public static final String MLG_JarFile = "MinecraftLandGenerator.jar";

	/** entire path of currently running .jar file */
	public static String MLGFileName = null;

	//
	// Text Input
	/** Command line input scanner */
	public static Scanner sc = new Scanner(System.in);

	//
	// MinecraftLandGenerator.conf Stuff:
	/** Server Output: "[INFO] Done" */
	public static String doneText = null;

	/** Server Output: "[INFO] Preparing spawn area:" */
	public static String preparingText = null;

	/** Server Output: "[INFO] Preparing start region for" */
	public static String preparingLevel = null;

	/** The Overworld */
	public static String level_0 = null;

	/** The Nether */
	public static String level_1 = null;

	/** The End */
	public static String level_2 = null;

	/** Future World (Level 3) */
	public static String level_3 = null;

	/** Future World (Level 4) */
	public static String level_4 = null;

	/** Future World (Level 5) */
	public static String level_5 = null;

	/** Future World (Level 6) */
	public static String level_6 = null;

	/** Future World (Level 7) */
	public static String level_7 = null;

	/** Future World (Level 8) */
	public static String level_8 = null;

	/** Future World (Level 9) */
	public static String level_9 = null;

	/** MLG's recommended default instead of "java -jar minecraft_server.jar" */
	public static final String defaultJavaLine =
			"java -Djava.awt.headless=true -Djline.terminal=jline.UnsupportedTerminal -Duser.language=en"
					+ " -Xms1024m -Xmx1024m -Xincgc -jar minecraft_server.jar nogui";

	/** "." for current folder, else exact path. */
	public static String serverPath = null;

	/** the folder the game save is in... */
	public static String worldPath = null;

	//
	//Server Launching:
	/** the minecraft server */
	public static ProcessBuilder minecraft = null;

	/** the info from "java=" in the conf file. */
	public static String javaLine = null;

	//
	//Server Launching:
	/** the name of the world. usually "world", unless its been changed... */
	public static String worldName = null;

	/** Beta 1.9 glitch workaround. (not needed unless using beta 1.9) */
	public static boolean waitSave = false;

	/** Ignores Warnings from the server. Used for compatibility and special cases */
	public static boolean ignoreWarnings = false;

	/** the worlds seed */
	public static Long randomSeed = (long) 0;

	//
	// Update URLs:
	/** just removing some redundancy */
	public static final String github_URL =
			"https://raw.github.com/Morlok8k/MinecraftLandGenerator/master/bin/";

	/** URL to conf file */
	public static final String github_MLG_Conf_URL = github_URL + MinecraftLandGeneratorConf;

	/** URL to BuildID */
	public static final String github_MLG_BuildID_URL = github_URL + buildIDFile;

	/** URL to .jar file */
	public static final String github_MLG_jar_URL = github_URL + MLG_JarFile;

	//
	// Update Stuff:
	/** The running Main.class */
	public static final Class<?> cls = Main.class;

	/** For bad compiling... */
	public static final String rsrcError = "rsrcERROR";

	/** is running code a .jar file? */
	public static boolean isCompiledAsJar = false;

	/** current hash */
	public static String MLG_Current_Hash = null;

	/** Just a test to make sure we don't get stuck in an infinite loop. should never happen, unless there is bad code. */
	public static int inf_loop_protect_BuildID = 0;

	/** we downloaded a copy of the BuildID... */
	public static boolean flag_downloadedBuildID = false;

	/** a list of timestamps */
	public static ArrayList<String> timeStamps = new ArrayList<>();

	//
	// Resume Data & Log Files
	/** resume data for X, if needed. */
	public static int resumeX = 0;

	/** resume data for Z, if needed. */
	public static int resumeZ = 0;

	/** a saved copy of the original args given */
	public static String[] originalArgs = {};

	/** which version of the server? oh yeah! */
	public static String MC_Server_Version = "";

	//
	// Misc:
	/** Launch website after generation. */
	public static boolean webLaunch = true;

	/** for scripts, we don't wait. for human readability, we wait 10 seconds before exiting program */
	public static boolean dontWait = false;

	/** alternate / compatibility mode */
	public static boolean alternate = false;

	/** standard server creates 625 chunks in a square around spawn. */
	public static int MinecraftServerChunkPlayerCache = 625;	//You see this number when you first launch the server in GUI mode, after the world is loaded, but before anyone has connected.

	/** standard server creates 625 chunks in a square around spawn. */
	//Alpha, and early beta uses 441 chunks.  (or 485 for early Alpha)
	public static int MinecraftServerChunkPlayerCacheAlpha = 441;	//alpha servers (and beta 1.0 only) 

	/** these 441 chunks create a 320x320 block square */
	public static long incrementFullAlpha =
			(int) (Math.sqrt(MinecraftServerChunkPlayerCacheAlpha) * 16);			// 400, the length of a fresh (no players have ever logged in) server map.

	/** these 625 chunks create a 400x400 block square */
	public static long incrementFull = (int) (Math.sqrt(MinecraftServerChunkPlayerCache) * 16);			// 400, the length of a fresh (no players have ever logged in) server map.

	/** due to the edge chunks being not fully populated, we subtract a chunks worth... */
	public static long increment = incrementFull - 16;		//public static int increment = 384;			// 384, what we use to iterate between sections of the map.  Allows for some overlap to prevent "stripes"

	/** debugging use... use "java -ea -jar MinecraftlandGenerator.jar" */
	public static boolean assertsEnabled = false;

	/** when the program started */
	public static long startTime = 0L;

	/** recheck toggle! */
	public static Boolean recheckFlag = false;

	/** output GUI stuff when using GUI mode, or don't. */
	public static boolean UsingGUI = false;

	/** Range of X to generate */
	public static int xRange = 0;

	/** Range of Z to generate */
	public static int zRange = 0;

	/** X Offset (Either spawnpoint or specified) */
	public static Integer xOffset = null;

	/** Z Offset (Either spawnpoint or specified) */
	public static Integer zOffset = null;

	/** args */
	public static String[] args;

	/** Chunks or Regions? */
	public static boolean useChunks = false;						//default to using regions

	/** Log File */
	public static String logFile = "MinecraftLandGenerator.log";

	/** has GUI-mode's Start button been pushed? */
	public static boolean runningServerGUI = false;

	/** has GUI-mode's Stop button been pushed? */
	public static boolean stoppingServerGUI = false;

}
