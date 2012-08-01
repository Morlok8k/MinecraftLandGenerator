package morlok8k.minecraft.landgenerator;

import corrodias.minecraft.landgenerator.Main;

public class MLG_Readme_and_HelpInfo {

	static String newLine = Main.newLine;
	static String MLGFileNameShort = Main.MLGFileNameShort;
	static String MinecraftLandGeneratorConf = Main.MinecraftLandGeneratorConf;
	static String defaultReadmeFile = Main.defaultReadmeFile;

	/**
	 * Saves a Readme file.
	 * 
	 * @param readmeFile
	 * @author Morlok8k
	 * 
	 */
	public static void readMe(String readmeFile) {

		if (readmeFile == "" || readmeFile == null) {
			readmeFile = defaultReadmeFile;
		}

		String MLG_Last_Modified_MDY = Main.dateFormat_MDY.format(Main.MLG_Last_Modified_Date);
		String PROG_NAME = Main.PROG_NAME;
		String VERSION = Main.VERSION;

		String showHelpSTR = "";
		String ReadMeText = "";
		String VersionInfo = "";

		//@formatter:off
		ReadMeText = PROG_NAME + " version " + VERSION + newLine
			+ newLine
			+ "Updated " + MLG_Last_Modified_MDY + newLine
			+ "(BuildID: " + Main.MLG_Last_Modified_Date.getTime() + ")" + newLine
			+ newLine
			+ "Original Code by Corrodias		November 2010" + newLine
			+ "Enhanced Code by Morlok8k		Feb. 2011 to Now (or at least to " + MLG_Last_Modified_MDY + "!)" + newLine
			+ "Additional Code by pr0f1x		October 2011" + newLine
			+ newLine
			+ "Website: https://sites.google.com/site/minecraftlandgenerator/" + newLine
			+ "Forum: http://www.minecraftforum.net/topic/187737-minecraft-land-generator/" + newLine
			+ "Source: https://github.com/Morlok8k/MinecraftLandGenerator" + newLine
			+ newLine
			+ "-----------------------------------------------" + newLine
			+ newLine
			+ "This program lets you generate an area of land with your Minecraft Beta SMP server (and is prossibly future-proof for newer versions). You set up your java command line and minecraft server paths in the MinecraftLandGenerator.conf file, set up the server's server.properties file with the name of the world you wish to use, and then run this program." + newLine
			+ "When a Minecraft server is launched, it automatically generates chunks within a square area of 25x25 chunks (400x400 blocks), centered on the current spawn point (formally 20x20 chunks, 320x320 blocks). When provided X and Z ranges as arguments, this program will launch the server repeatedly, editing the level.dat file between sessions, to generate large amounts of land without players having to explore them. The generated land will have about the X and Z ranges as requested by the arguments, though it will not be exact due to the spawn point typically not on the border of a chunk. (Because of this, MLG by default adds a slight overlap with each pass - 380x380 blocks) You can use the -x and -z switches to override the spawn offset and center the land generation on a different point." + newLine
			+ "The program makes a backup of level.dat as level_backup.dat before editing, and restores the backup at the end. In the event that a level_backup.dat file already exists, the program will refuse to proceed, leaving the user to determine why the level_backup.dat file exists and whether they would rather restore it or delete it, which must be done manually." + newLine
			+ newLine
			+ "This program is public domain, and the source code is included in the .jar file.  (If accidently missing, like in 1.3.0 and 1.4.0, it is always available at Github.)" + newLine
			+ "The JNLP library is included (inside the .jar). It is not public domain. Its license is included, as LICENSE.TXT." + newLine
			+ "It is also available at: http://jnbt.sourceforge.net/" + newLine
			+ newLine
			+ "The \"unescape\" method/function is also not Public Domain.  Its License is the W3C\u00A9 Software License, and located here: http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231"
			+ newLine
			+ "Other Public Domain code has been used in this program, and references to sources are included in the comments of " + PROG_NAME + "'s source code."
			+ newLine
			+ "-----------------------------------------------" + newLine
			+ newLine;
		//@formatter:on

		showHelpSTR = showHelp(false);		//stored as a string for easier manipulation in the future

		//@formatter:off
		VersionInfo = newLine
			+ "-----------------------------------------------" + newLine
			+ newLine 
			+ "Version History:" + newLine
			+ "Morlok8k:" + newLine
			+ "1.7.0" + newLine
			+ "- Major Code Optimization" + newLine
			+ "- Drastically reduced the amount of time it takes for MLG to expand a world after it has already done so before!" + newLine
			+ "  (To do this, I needed to rewrite the Main loop of the program, and add my own Coordinate object)" + newLine
			+ "- Added Resume Functionality" + newLine
			+ "- Updated Time Output yet again." + newLine
			+ "- Misc. Tweaks." + newLine
			+ "- Misc. Additions" + newLine
			+ "1.6.3" + newLine
			+ "- Minor Code Optimization" + newLine
			+ "- Finely got on the ball and added the JNBT source and everything (as an internal .zip) to be completely faithful to his license" + newLine
			+ "- Also adding script files internally in the .jar for archive (or offline) purposes. (Manual Extract needed for use)" + newLine
			+ "- Modified output of MLG slightly to show whats the server and whats MLG. (I may do more with this later.)" + newLine
			+ "1.6.2" + newLine
			+ "- Major Code Optimization" + newLine
			+ "- Updated Time Output again.  Now says \"1 Minute\" instead of \"1 Minutes\"." + newLine
			+ "- Updated Location Code - the center of the square is now truely centered, and it trys to get as close to the given size as possible." + newLine
			+ "- Added \"-nowait\" and its shorter version \"-n\"" + newLine
			+ "- Added currently non-functional RCON code.  Will try to make functional in the future." + newLine
			+ newLine
			+ "1.6.11" + newLine
			+ "- Removed End-of-Generation ASCII-Graphic - It didn't really fit with MLG." + newLine
			+ "- Updated Time Output." + newLine
			+ "- Changed estimated time remaining to count all runs, not just the last four." + newLine
			+ "- Added the time it took to complete at the end of generation." + newLine
			+ newLine
			+ "1.6.1" + newLine
			+ "- Added some modifications for scripting  (Mainly for a new Initial setup script)" + newLine
			+ "- Changed MLG's Y to Z.  Now it matches Minecraft.  Y in the game is Height." + newLine
			+ "- Renamed -y switch to -z.  MLG will remain backwards compatible if you use the old -y switch." + newLine
			+ "- Updated -printspawn to show X,Y,Z" + newLine
			+ "- Added End-of-Generation ASCII-Graphic" + newLine
			+ "- Slightly altered some text output" + newLine
			+ newLine
			+ "1.6.05" + newLine
			+ "- MLG displays if the server is converting the Map format, when not in verbose mode. (McRegion -> Anvil, or Chunk-File -> McRegion)" + newLine
			+ "- Minor fixes/edits/typos" + newLine
			+ "- Added link to new MLG website to readme file"
			+ newLine
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
			+ "I recommend using MCEDIT to relight the map after you generate it. This will take a long time, but should fix all those incorrectly dark spots in your level." + newLine;
		//@formatter:on

		MLG_FileWrite.writeTxtFile(readmeFile, ReadMeText + showHelpSTR + VersionInfo);

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
		String NewLine = newLine;
		if (SysOut) {
			NewLine = NewLine + Main.MLG;
		}

		MLGFileNameShort = Main.MLGFileNameShort;
		MinecraftLandGeneratorConf = Main.MinecraftLandGeneratorConf;
		defaultReadmeFile = Main.defaultReadmeFile;

		//@formatter:off
		Str =	"Usage: java -jar " + MLGFileNameShort + " x z [serverpath] [switches]" + NewLine
				+ NewLine
				+ "Arguments:" + NewLine
				+ "              x : X range to generate" + NewLine
				+ "              z : Z range to generate" + NewLine
				+ "     serverpath : the path to the directory in which the server runs (takes precedence over the config file setting)" + NewLine
				+ NewLine
				+ "Switches:" + NewLine
				+ "       -verbose : causes the application to output the server's messages to the console" + NewLine
				+ "             -v : same as -verbose" + NewLine
				+ "             -w : Ignore [WARNING] and [SEVERE] messages." + NewLine
				+ "           -alt : alternate server launch sequence" + NewLine
				+ "             -a : same as -alt" + NewLine
				+ "        -nowait : don't pause for anything" + NewLine
				+ "             -n : same as -nowait" + NewLine
				+ "            -i# : override the iteration spawn offset increment (default 380) (example: -i100)" + NewLine
				+ "            -x# : set the X offset to generate land around (example: -x0 or -x1000 or -x-500)" + NewLine
				+ "            -z# : set the Z offset to generate land around (example: -z0 or -z1000 or -z-500)" + NewLine
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

		String returnString = null;

		if (SysOut) {
			Main.out(Str);
			Main.out("");
		} else {
			returnString = Str;
		}
		return returnString;

	}

}
