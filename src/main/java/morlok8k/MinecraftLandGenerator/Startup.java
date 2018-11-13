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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * Program Initialization code. placed here so both CLI and GUI can use it.
 * 
 * @author morlok8k
 * 
 */
public class Startup {
	private static Log log = LogFactory.getLog(Main.class);

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
			log.info("Notice: Not waiting for anything...");
		}

		if (var.args.length == 0) {																//we didn't find a an X and Z size, so lets ask for one.
			log.info("Please Enter the size of world you want.  Example: X:1000  Z:1000");
			log.info(var.MLG + "X:");
			var.xRange = Input_CLI.getInt("X:");
			log.info(var.MLG + "Z:");
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
						log.info(var.MinecraftLandGeneratorConf + " file downloaded.");
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
			return true;
		} else if (var.args[0].equalsIgnoreCase("-build")) {
			Update.buildID(false);
			return true;
		} else if (var.args[0].equalsIgnoreCase("-update")) {
			Update.updateMLG();
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
				log.info("No File to Download!");
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
					return true;
				} catch (final IOException ex) {
					System.err.println(var.args[1] + " - Could not read file.");
					return true;
				}
			} else {
				log.info("No File with links!");
			}
			return true;

		} else if (var.args.length == 1) {
			log.info("For help, use java -jar " + var.MLGFileNameShort + " -help");
			return true;
		}

		// ARGUMENTS
		try {
			var.xRange = Integer.parseInt(var.args[0]);
			var.zRange = Integer.parseInt(var.args[1]);

			if ((var.xRange < 1000) && (var.xRange != 0)) {
				var.xRange = 1000;							//if less than 1000, (and not 0) set to 1000 (Calculations don't work well on very small maps)
				log.error("X size too small - Changing X to 1000");
			}
			if ((var.zRange < 1000) && (var.zRange != 0)) {
				var.zRange = 1000;
				log.error("Z size too small - Changing Z to 1000");
			}

		} catch (final NumberFormatException ex) {
			log.error("Invalid X or Z argument.");
			log.error("Please Enter the size of world you want.  Example: X:1000  Z:1000");
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
					log.info("Notice: Verbose Mode");

				} else if (nextSwitch.startsWith("-i")) {
					var.increment = Integer.parseInt(var.args[i + 2].substring(2));
					log.info("Notice: Non-Default Increment: " + var.increment);

				} else if (nextSwitch.startsWith("-w")) {
					var.ignoreWarnings = true;
					log.info("Notice: Warnings from Server are Ignored");

				} else if (nextSwitch.equals("-alt") || nextSwitch.equals("-a")) {
					var.alternate = true;
					log.info("Notice: Using Alternate Launching");

				} else if (nextSwitch.equals("-chunk") || nextSwitch.equals("-c")) {
					var.useChunks = true;
					log.info("Notice: Using Chunks instead of Regions");

				} else if (nextSwitch.startsWith("-x")) {
					var.xOffset = Integer.valueOf(var.args[i + 2].substring(2));
					log.info("Notice: X Offset: " + var.xOffset);

				} else if (nextSwitch.startsWith("-y") || nextSwitch.startsWith("-z")) {		//NOTE: "-y" is just here for backwards compatibility
					var.zOffset = Integer.valueOf(var.args[i + 2].substring(2));
					log.info("Notice: Z Offset: " + var.zOffset);
					if (nextSwitch.startsWith("-y")) {
						log.info(
								"Notice: MLG now uses Z instead of Y.  Please use the -z switch instead");
					}

				} else {
					var.serverPath = var.args[i + 2];
					log.info("Notice: Attempting to use Alternate Server:" + var.serverPath);
				}
			}
		} catch (final NumberFormatException ex) {
			log.error("Invalid switch value.");
			return true;
		}

		return false;		// success!
	}
}
