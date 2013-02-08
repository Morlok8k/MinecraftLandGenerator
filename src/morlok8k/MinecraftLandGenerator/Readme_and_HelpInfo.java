package morlok8k.MinecraftLandGenerator;

/**
 * 
 * @author morlok8k
 */
public class Readme_and_HelpInfo {

	/**
	 * Saves a Readme file.
	 * 
	 * @param readmeFile
	 * @author Morlok8k
	 * 
	 */
	public static void readMe(String readmeFile) {

		if ((readmeFile == "") || (readmeFile == null)) {
			readmeFile = var.defaultReadmeFile;
		}

		final String MLG_Last_Modified_MDY = var.dateFormat_MDY.format(var.MLG_Last_Modified_Date);
		final String PROG_NAME = var.PROG_NAME;
		final String VERSION = var.VERSION;

		String showHelpSTR = "";
		String ReadMeText = "";
		String VersionInfo = "";
		final String n = var.newLine;

		//@formatter:off
        ReadMeText = PROG_NAME + " version " + VERSION + n
                + n
                + "Updated " + MLG_Last_Modified_MDY + n
                + "(BuildID: " + var.MLG_Last_Modified_Date.getTime() + ")" + n
                + n
                + "Original Code by Corrodias		November 2010" + n
                + "Enhanced Code by Morlok8k		Feb. 2011 to Now (or at least to " + MLG_Last_Modified_MDY + "!)" + n
                + "Additional Code by pr0f1x		October 2011" + n
                + n
                + "Website: https://sites.google.com/site/minecraftlandgenerator/" + n
                + "Forum: http://www.minecraftforum.net/topic/187737-minecraft-land-generator/" + n
                + "Source: https://github.com/Morlok8k/MinecraftLandGenerator" + n
                + n
                + "-----------------------------------------------" + n
                + n
                + "This program lets you generate an area of land with your Minecraft SMP server (and is prossibly future-proof for newer versions). You set up your java command line and minecraft server paths in the MinecraftLandGenerator.conf file, set up the server's server.properties file with the name of the world you wish to use, and then run this program." + n
                + "When a Minecraft server is launched, it automatically generates chunks within a square area of 25x25 chunks (400x400 blocks), centered on the current spawn point (formally 20x20 chunks, 320x320 blocks). When provided X and Z ranges as arguments, this program will launch the server repeatedly, editing the level.dat file between sessions, to generate large amounts of land without players having to explore them. The generated land will have about the X and Z ranges as requested by the arguments, though it will not be exact due to the spawn point typically not on the border of a chunk. (Because of this, MLG by default adds a slight overlap with each pass - 380x380 blocks) You can use the -x and -z switches to override the spawn offset and center the land generation on a different point." + n
                + "The program makes a backup of level.dat as level_backup.dat before editing, and restores the backup at the end. In the event that a level_backup.dat file already exists, the program will refuse to proceed, leaving the user to determine why the level_backup.dat file exists and whether they would rather restore it or delete it, which must be done manually." + n
                + n
                + "This program is public domain, and the source code is included in the .jar file.  (If accidently missing, like in 1.3.0 and 1.4.0, it is always available at Github.)" + n
                + "The JNBT library is included (inside the .jar). It is not public domain. Its license is included, as LICENSE.TXT." + n
                + "It is also available at: http://jnbt.sourceforge.net/" + n
                + n
                + "The \"unescape\" method/function is also not Public Domain.  Its License is the W3C\u00A9 Software License, and located here: http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231"
                + n
                + "Other Public Domain code has been used in this program, and references to sources are included in the comments of " + PROG_NAME + "'s source code."
                + n
                + "-----------------------------------------------" + n
                + n;
        //@formatter:on

		showHelpSTR = showHelp(false);		//stored as a string for easier manipulation in the future

		//@formatter:off
        VersionInfo = n
                + "-----------------------------------------------" + n
                + n
                + "Version History:" + n
                + "Morlok8k:" + n
                //       + "- TODO: outliers issue / region fix" + n
                //       + "- TODO: 16/512 block selecting" + n
                //       + "- TODO: recaculate existing coords with new code" + n
                //       + "- TODO: change 380 to 384?" + n //TODO
                + "1.7.2" + n
                + "- Fixed a minor display bug (specifically when using Server Generation Fix Mod)" + n
                + "1.7.2" + n
                + "- Fixed \"1152 bug\"" + n
                + "- Updated to JNBT 1.3" + n
                + "- adjusted archive integrity check to account for timezone-related bugs..." + n        
                 + "1.7.1" + n
                + "- Major Code Refactoring" + n
                + "- Updated to JNBT 1.2" + n
                + "- making code ready for a GUI" + n
                + "1.7.0" + n
                + "- Major Code Optimization" + n
                + "- Drastically reduced the amount of time it takes for MLG to expand a world after it has already done so before!" + n
                + "  (To do this, I rewrote the Main loop of the program, and add my own Coordinate object)" + n
                + "- Added Resume Functionality" + n
                + "- Updated Time Output yet again." + n
                + "- Made xx% output nicer by rewriting previous existing line." + n
                + "- Misc. Tweaks" + n
                + "- Misc. Additions" + n
                + "1.6.3" + n
                + "- Minor Code Optimization" + n
                + "- Finely got on the ball and added the JNBT source and everything (as an internal .zip) to be completely faithful to his license" + n
                + "- Also adding script files internally in the .jar for archive (or offline) purposes. (Manual Extract needed for use)" + n
                + "- Modified output of MLG slightly to show whats the server and whats MLG. (I may do more with this later.)" + n
                + "1.6.2" + n
                + "- Major Code Optimization" + n
                + "- Updated Time Output again.  Now says \"1 Minute\" instead of \"1 Minutes\"." + n
                + "- Updated Location Code - the center of the square is now truely centered, and it trys to get as close to the given size as possible." + n
                + "- Added \"-nowait\" and its shorter version \"-n\"" + n
                + "- Added currently non-functional RCON code.  Will try to make functional in the future." + n
                + n
                + "1.6.11" + n
                + "- Removed End-of-Generation ASCII-Graphic - It didn't really fit with MLG." + n
                + "- Updated Time Output." + n
                + "- Changed estimated time remaining to count all runs, not just the last four." + n
                + "- Added the time it took to complete at the end of generation." + n
                + n
                + "1.6.1" + n
                + "- Added some modifications for scripting  (Mainly for a new Initial setup script)" + n
                + "- Changed MLG's Y to Z.  Now it matches Minecraft.  Y in the game is Height." + n
                + "- Renamed -y switch to -z.  MLG will remain backwards compatible if you use the old -y switch." + n
                + "- Updated -printspawn to show X,Y,Z" + n
                + "- Added End-of-Generation ASCII-Graphic" + n
                + "- Slightly altered some text output" + n
                + n
                + "1.6.05" + n
                + "- MLG displays if the server is converting the Map format, when not in verbose mode. (McRegion -> Anvil, or Chunk-File -> McRegion)" + n
                + "- Minor fixes/edits/typos" + n
                + "- Added link to new MLG website to readme file"
                + n
                + "1.6.03" + n
                + "- added decoding of escape characters of URL's (so a space is a \" \" and not \"%20\")" + n
                + "- added \"-downloadlist [list]\" where [list] is a text file with URL's on each line" + n
                + n
                + "1.6.02" + n
                + "- small fix on caculating md5sum where old version didnt pad out to 32chars with zeros on the left side"
                + "- quick Archive intergity fix after injecting source code into .jar after it compiled."
                + "- no new functionality, md5 issue doesnt affect -update on old versions."
                + n
                + "1.6.0" + n
                + "- NOW DOES NOT NEED ANY SCRIPT FILES!" + n
                + "- Added the ability to download files from the internet" + n
                + "- Added a switch to download any file off the internet, if needed (useless for most people, but included it in case I wanted it in the future.)" + n
                + "- Added the ability to check what version the .jar is. (Using MD5 hashes, timestamps, and the BuildID file)" + n
                + "- Added \"-update\" to download new versions of MLG directly from github." + n
                + "- Updated estimated time.  Now shows up on loop 2+ instead of loop 4+." + n
                + "- Standard % output of the Server should look nicer now." + n
                + "- Code Refactoring" + n
                + "- Code Formatting" + n
                + "- Code Optimization" + n
                + "- Duplicate sections of code have been turned into Methods/\"Functions\"" + n
                + n
                + "1.5.1" + n
                + "- pr0f1x: Added the \"save-all\" command to be sent to the server before shutting it down." + n
                + "- pr0f1x: Added a 40 second wait before shutting down." + n
                + "- Morlok8k: Made 40 second wait optional." + n
                + "- Morlok8k: Changed the Dimensions code.  (I had assumed it would be DIM-1, DIM-2, etc.  but it turned out to be DIM-1 and DIM1. Change reflects Server output of \"Level n\")" + n
                + "- Morlok8k: Config file is automatically updated to reflect these changes." + n
                + "- Morlok8k: Cleaned up code." + n
                + n
                + "1.5.0" + n
                + "- Supports Server Beta 1.6.4 (& hopefully future versions as well, while remaining backward compatible.)" + n
                + "- Added \"-a\",\"-alt\" to use alternate method (a slightly simplier version of 1.3.0's code - pure verbose only)" + n
                + "- Added world specific output for 9 dimensions (DIM-1 is the Nether, DIM-2 through DIM-9 dont exist yet, but if and when they do, you can configure it's text).  (\"Level 0\", the default world, is displayed as the worlds name)" + n
                + "- Updated Config File for these Dimensions." + n
                + "- Reads and outputs the Seed to the output. (If you had used text for the Seed, Minecraft converts it into a number. This outputs the number.)" + n
                + "- Changed the default 300 blocks to 380.  The server now makes a 400x400 square block terrain instead of 320x320.  Thus it is faster because there are less loops.  To use the old way, use \"-i300\"" + n
                + "- Added total Percentage done (technically, it displays the % done once the server finishes...)" + n
                + "- Added debugging output vars of conf file (disabled - need to re-compile source to activate)" + n
                + n
                + "\t\t+ (the goal is to have MLG be configureable, so it can work on any version of the server, past or present.)" + n
                + n
                + "*** 1.4.5 (pre 1.5.0) ***" + n
                + "- sorry!  I shouldn't release untested code..." + n
                + "*************************" + n
                + n
                + "1.4.4" + n
                + "- Added ablilty to ignore [WARNING] and [SEVERE] errors with \"-w\"" + n
                + n
                + "1.4.3" + n
                + "- Fixed \"-ps\",\"-printspawn\" as I had forgot I had broken it in 1.4.0 - due to config file change." + n
                + n
                + "1.4.2" + n
                + "- No New Features" + n
                + "- Changed non-verbose mode to display server progress on the same line, saving a lot of space." + n
                + "	- This couldn't wait for 1.5.0 ...  I (Morlok8k) liked it too much." + n
                + n
                + "1.4.0" + n
                + "- Future Proofing" + n
                + "- Configurble Server Message reading. (If server updates and breaks MLG, you can add the new text!)" + n
                + "- Updated config file, and auto updating from old format." + n
                + "- Added % of spawn area to non-verbose output." + n
                + "- Removed datetime stamps from server output in verbose mode" + n
                + "- Other Misc fixes." + n
                + n
                + "1.3.0" + n
                + "- Fixed Problems with Minecraft Beta 1.3 -- Morlok8k" + n
                + n
                + "-----------------------------------------------" + n
                + n
                + "Corrodias:" + n
                + "1.2.0" + n
                + "- land generation now centers on the spawn point instead of [0, 0]" + n
                + "- the server is launched once before the spawn point is changed, to verify that it can run and to create a world if one doesn't exist" + n
                + "- added -printspawn [-ps] switch to print the current spawn coordinates to the console" + n
                + "- added -x and -y switches to override the X and Y offsets" + n
                + "- added -v switch, does the same as -verbose" + n
                + "- improved status message spacing to make things easier to read" + n
                + "- improved time estimation algorithm: it now averages the last 3 launches" + n
                + n
                + "1.1.0" + n
                + "- added MinecraftLandGenerator.conf file to hold the java command line and the server path" + n
                + "- added -conf solo switch to generate a .conf file" + n
                + "- added -verbose switch to output server output to the console (default is to ignore it)" + n
                + "- added -i switch to allow customizing the block increment size (default is 300)" + n
                + "- added instructions output in this version, i think" + n
                + "- improved status message output to include current iteration and total iterations" + n
                + n
                + "1.0.0" + n
                + "- initial release" + n
                + n
                + "-----------------------------------------------" + n
                + n
                + "Notes:" + n
                + "Due to changes in server beta 1.6, it now generates the nether as well as the world at the same time." + n
                + "However, Since beta 1.9 and Minecraft 1.0, the nether or the end is no longer generated."
                + "I recommend using MCEDIT to relight the map after you generate it. This will take a long time, but should fix all those incorrectly dark spots in your level." + n;
        //@formatter:on

		FileWrite.writeTxtFile(readmeFile, ReadMeText + showHelpSTR + VersionInfo);

	}

	/**
	 * Displays or returns Help information
	 * 
	 * @param SysOut
	 * <br>
	 *            Set TRUE to display info to System.out. (Returns null) <br>
	 *            Set FALSE to return info as String.
	 * @return
	 * @author Morlok8k
	 */
	public static String showHelp(final boolean SysOut) {
		String Str = null;
		String n = var.newLine;
		if (SysOut) {
			n = n + var.MLG;
		}

		//@formatter:off
        Str = "Usage: java -jar " + var.MLGFileNameShort + " x z [serverpath] [switches]" + n
                + n
                + "Arguments:" + n
                + "              x : X range to generate" + n
                + "              z : Z range to generate" + n
                + "     serverpath : the path to the directory in which the server runs (takes precedence over the config file setting)" + n
                + n
                + "Switches:" + n
                + "       -verbose : causes the application to output the server's messages to the console" + n
                + "             -v : same as -verbose" + n
                + "             -w : Ignore [WARNING] and [SEVERE] messages." + n
                + "           -alt : alternate server launch sequence" + n
                + "             -a : same as -alt" + n
                + "        -nowait : don't pause for anything" + n
                + "             -n : same as -nowait" + n
                + "            -i# : override the iteration spawn offset increment (default 380) (example: -i100)" + n
                + "            -x# : set the X offset to generate land around (example: -x0 or -x1000 or -x-500)" + n
                + "            -z# : set the Z offset to generate land around (example: -z0 or -z1000 or -z-500)" + n
                + n
                + "Other options:" + n
                + "  java -jar " + var.MLGFileNameShort + " -update" + n
                + "        Checks for and downloads new versions of MLG online." + n
                + n
                + "  java -jar " + var.MLGFileNameShort + " -printspawn" + n
                + "  java -jar " + var.MLGFileNameShort + " -ps" + n
                + "        Outputs the current world's spawn point coordinates." + n
                + n
                + "  java -jar " + var.MLGFileNameShort + " -conf" + n
                + "  java -jar " + var.MLGFileNameShort + " -conf download" + n
                + "        Generates or downloads a " + var.MinecraftLandGeneratorConf + " file." + n
                + n
                + "  java -jar " + var.MLGFileNameShort + " -readme readme.txt" + n
                + "  java -jar " + var.MLGFileNameShort + " -readme" + n
                + "        Generates a readme file using supplied name or the default " + var.defaultReadmeFile + n
                + n
                + "  java -jar " + var.MLGFileNameShort + " -downloadfile http://example.com/file.txt" + n
                + "        Downloads whatever file from the internet you give it." + n
                + "  java -jar " + var.MLGFileNameShort + " -downloadlist list.txt" + n
                + "        list.txt (or any other file) contains a URL on each line which will be downloaded." + n
                + n
                + "  java -jar " + var.MLGFileNameShort + " -version" + n
                + "  java -jar " + var.MLGFileNameShort + " -help" + n
                + "  java -jar " + var.MLGFileNameShort + " /?" + n
                + "        Prints this message." + n
                + n
                + "When launched with the -conf switch, this application creates a " + var.MinecraftLandGeneratorConf + " file that contains configuration options." + n
                + "If this file does not exist or does not contain all required properties, the application will not run." + n
                + n
                + var.MinecraftLandGeneratorConf + " properties:" + n
                + "           Java : The command line to use to launch the server" + n
                + "     ServerPath : The path to the directory in which the server runs (can be overridden by the serverpath argument)" + n
                + "      Done_Text : The output from the server that tells us that we are done" + n
                + " Preparing_Text : The output from the server that tells us the percentage" + n
                + "Preparing_Level : The output from the server that tells us the level it is working on" + n
                + "        Level-0 : Name of Level 0: The Overworld" + n
                + "        Level-1 : Name of Level 1: The Nether" + n
                + "        Level-2 : Name of Level 2: The End" + n
                + "        Level-3 : Name of Level 3: (Future Level)" + n
                + "        Level-4 : Name of Level 4: (Future Level)" + n
                + "        Level-5 : Name of Level 5: (Future Level)" + n
                + "        Level-6 : Name of Level 6: (Future Level)" + n
                + "        Level-7 : Name of Level 7: (Future Level)" + n
                + "        Level-8 : Name of Level 8: (Future Level)" + n
                + "        Level-9 : Name of Level 9: (Future Level)" + n
                + "       WaitSave : Optional: Wait before saving." + n;
        //@formatter:on

		String returnString = null;

		if (SysOut) {
			Out.out(Str);
			Out.out("");
		} else {
			returnString = Str;
		}
		return returnString;

	}
}
