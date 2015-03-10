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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * 
 * Program Initialization code. placed here so both CLI and GUI can use it.
 * 
 * @author morlok8k
 * 
 */
public class Startup {

	public static void initialStart() {

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

	}

	/**
	 * 
	 * CLI only: Reads arguments from command line
	 * 
	 * @return
	 */
	public static boolean programArguments() {

		// =====================================================================
		//                           INSTRUCTIONS
		// =====================================================================

		// check for -nowait, and remove from arguments if it exists.  (we remove it for compatibility reasons with the rest of the existing code.)
		// (-nowait is the only universal switch - it can be used with anything.  its basically for scripting, as it turns off the 10sec wait for human readability)
		String[] newArgs = new String[var.args.length];
		newArgs = var.args;
		newArgs = StringArrayParse.Parse(newArgs, "-n");		//parse out -n
		newArgs = StringArrayParse.Parse(newArgs, "-nowait");	//parse out -nowait
		if (!(var.args.equals(newArgs))) {								//do the freshly parsed args match the original?
			var.dontWait = true;											//if not, we dont wait for anything!
			var.args = newArgs;												//use the freshly parsed args for everything else now...
			Out.out("Notice: Not waiting for anything...");
		}

		if (var.args.length == 0) {																//we didn't find a an X and Z size, so lets ask for one.
			Out.out("Please Enter the size of world you want.  Example: X:1000  Z:1000");
			Out.outP(var.MLG + "X:");
			var.xRange = Input_CLI.getInt("X:");
			Out.outP(var.MLG + "Z:");
			var.zRange = Input_CLI.getInt("Z:");
			var.args = new String[] { String.valueOf(var.xRange), String.valueOf(var.zRange) };

		}

		if (var.args[0].equalsIgnoreCase("-version") || var.args[0].equalsIgnoreCase("-help")
				|| var.args[0].equals("/?")) {

			Readme_and_HelpInfo.showHelp(true);

			return true;
		}

		// =====================================================================
		//                         STARTUP AND CONFIG
		// =====================================================================

		// the arguments are apparently okay so far. parse the conf file.
		if (var.args[0].equalsIgnoreCase("-conf")) {

			if (var.args.length == 2) {
				if (var.args[1].equalsIgnoreCase("download")) {
					final boolean fileSuccess =
							DownloadFile.downloadFile(var.github_MLG_Conf_URL, var.testing);
					if (fileSuccess) {
						Out.out(var.MinecraftLandGeneratorConf + " file downloaded.");
						return true;
					}
				}
			}

			FileWrite.saveConf(true);  //new conf file
			return true;

		} else if (var.args[0].equalsIgnoreCase("-ps")
				|| var.args[0].equalsIgnoreCase("-printspawn")) {
			// okay, sorry, this is an ugly hack, but it's just a last-minute feature.
			Misc.printSpawn();
			Time.waitTenSec(false);
			return true;
		} else if (var.args[0].equalsIgnoreCase("-build")) {
			Update.buildID(false);
			return true;
		} else if (var.args[0].equalsIgnoreCase("-update")) {
			Update.updateMLG();
			Time.waitTenSec(false);
			return true;
		} else if (var.args[0].equalsIgnoreCase("-readme")) {

			if (var.args.length == 2) {
				Readme_and_HelpInfo.readMe(var.args[1]);
			} else {
				Readme_and_HelpInfo.readMe(null);
			}
			return true;
		} else if (var.args[0].equalsIgnoreCase("-downloadfile")) {
			if (var.args.length == 2) {
				DownloadFile.downloadFile(var.args[1], true);
			} else {
				Out.out("No File to Download!");
				Time.waitTenSec(false);
			}
			return true;

		} else if (var.args[0].equalsIgnoreCase("-downloadlist")) {

			if (var.args.length == 2) {
				String origMD5 = "";
				String recheckMD5 = "";

				try {
					final File config = new File(var.args[1]);
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
					System.err.println(var.args[1] + " - File not found");
					Time.waitTenSec(false);
					return true;
				} catch (final IOException ex) {
					System.err.println(var.args[1] + " - Could not read file.");
					Time.waitTenSec(false);
					return true;
				}
			} else {
				Out.out("No File with links!");
				Time.waitTenSec(false);
			}
			return true;

		} else if (var.args.length == 1) {
			Out.out("For help, use java -jar " + var.MLGFileNameShort + " -help");
			Time.waitTenSec(false);
			return true;
		}

		// ARGUMENTS
		try {
			var.xRange = Integer.parseInt(var.args[0]);
			var.zRange = Integer.parseInt(var.args[1]);

			if ((var.xRange < 1000) && (var.xRange != 0)) {
				var.xRange = 1000;							//if less than 1000, (and not 0) set to 1000 (Calculations don't work well on very small maps)
				Out.err("X size too small - Changing X to 1000");
			}
			if ((var.zRange < 1000) && (var.zRange != 0)) {
				var.zRange = 1000;
				Out.err("Z size too small - Changing Z to 1000");
			}

		} catch (final NumberFormatException ex) {
			Out.err("Invalid X or Z argument.");
			Out.err("Please Enter the size of world you want.  Example: X:1000  Z:1000");
			var.xRange = Input_CLI.getInt("X:");
			var.zRange = Input_CLI.getInt("Z:");

			//return;
		}

		// This is embarrassing. Don't look.
		try {
			for (int i = 0; i < (var.args.length - 2); i++) {
				final String nextSwitch = var.args[i + 2].toLowerCase();
				if (nextSwitch.equals("-verbose") || nextSwitch.equals("-v")) {
					var.verbose = true;
					Out.out("Notice: Verbose Mode");

				} else if (nextSwitch.startsWith("-i")) {
					var.increment = Integer.parseInt(var.args[i + 2].substring(2));
					Out.out("Notice: Non-Default Increment: " + var.increment);

				} else if (nextSwitch.startsWith("-w")) {
					var.ignoreWarnings = true;
					Out.out("Notice: Warnings from Server are Ignored");

				} else if (nextSwitch.equals("-alt") || nextSwitch.equals("-a")) {
					var.alternate = true;
					Out.out("Notice: Using Alternate Launching");

				} else if (nextSwitch.equals("-chunk") || nextSwitch.equals("-c")) {
					var.useChunks = true;
					Out.out("Notice: Using Chunks instead of Regions");

				} else if (nextSwitch.startsWith("-x")) {
					var.xOffset = Integer.valueOf(var.args[i + 2].substring(2));
					Out.out("Notice: X Offset: " + var.xOffset);

				} else if (nextSwitch.startsWith("-y") || nextSwitch.startsWith("-z")) {		//NOTE: "-y" is just here for backwards compatibility
					var.zOffset = Integer.valueOf(var.args[i + 2].substring(2));
					Out.out("Notice: Z Offset: " + var.zOffset);
					if (nextSwitch.startsWith("-y")) {
						Out.out("Notice: MLG now uses Z instead of Y.  Please use the -z switch instead");
						Time.waitTenSec(false);
					}

				} else {
					var.serverPath = var.args[i + 2];
					Out.out("Notice: Attempting to use Alternate Server:" + var.serverPath);
				}
			}
		} catch (final NumberFormatException ex) {
			Out.err("Invalid switch value.");
			return true;
		}

		return false;		// success!
	}

	public static boolean confFile() {
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
			return true;

		}

		return false;		// success!
	}

}
