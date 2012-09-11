package morlok8k.MinecraftLandGenerator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class var {

	//
	//
	// Public Vars:
	public static boolean testing = false;		// display more output when debugging
	public static final String PROG_NAME = "Minecraft Land Generator";		// Program Name
	public static final String VERSION = "1.7.1 test 10";								// Version Number!
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
	public static boolean webLaunch = true;				// Launch website after generation.

}
