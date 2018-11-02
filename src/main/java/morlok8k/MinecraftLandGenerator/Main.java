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

/*
 * The Computer Programmer's Lament: Program complexity grows until it exceeds the capability of the programmer who must maintain it.
 */

package morlok8k.MinecraftLandGenerator;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import morlok8k.MinecraftLandGenerator.GUI.MLG_GUI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Corrodias, Morlok8k, pr0f1x
 */
public class Main {

	//////////////////////////////////////////////////////////
	// REMINDER: Because I always forget/mix up languages:	//
	// "static" in java means that there will only ever be ONE of it created, shared by all the instances of that class.//
	// "final" means "constant"								//
	// public/private shows/hides between classes			//
	//////////////////////////////////////////////////////////

	/**
	 * @param args
	 * the command line arguments
	 */
	private static Log log = LogFactory.getLog(Main.class);

	public static void main(String[] args) {

		var.startTime = System.currentTimeMillis();

		var.originalArgs = args;    // we may potentially remove some args later, but we keep a record of the original for the log file.

		// This is really just here for debugging...
		// I plan on adding more asserts later, but for now, this will do.
		// to enable this, run:
		// java -enableassertions -jar MinecraftLandGenerator.jar
		assert var.assertsEnabled = true;  // Intentional side-effect!!!  (This may cause a Warning, which is safe to ignore: "Possible accidental assignment in place of a comparison. A condition expression should not be reduced to an assignment")
		if (var.assertsEnabled) {
			log.info("assertsEnabled: " + var.assertsEnabled);
			var.verbose = true;
			log.info("Verbose mode forced!");
			var.testing = true;
			log.info("Debug mode forced!");
			var.dontWait = true;
			log.info("-nowait mode forced!");
			log.info("");
		}

		boolean GUI = false;            // GUI needs to be true to run in graphical mode
		boolean NOGUI = false;            // NOGUI is a flag that finds reasons to not use a graphical mode.

		if (args.length != 0) {            // if args are present, then we assume we want NOGUI
			NOGUI = true;                // if no args are present, we will attempt GUI
		}

		String[] argsNOGUI = new String[args.length];
		argsNOGUI = args;
		argsNOGUI = StringArrayParse.Parse(argsNOGUI, "nogui");        //parse out "nogui"
		if (!(args.equals(argsNOGUI))) {                                //do the freshly parsed args match the original?
			args = argsNOGUI;                                            //use the freshly parsed args for everything else now...
			NOGUI = true;
		}

		//MLG_GUI Choosing code...
		if ((!NOGUI) && (!java.awt.GraphicsEnvironment.isHeadless())) {
			GUI = true;
			if (var.testing) {
				log.info("MLG_GUI: This is a graphical enviroment.");
			}

			//////
			GUI = false;                // forcing GUI to be false for now, because I don't have the MLG_GUI code ready yet!
			//////

		} else {
			GUI = false;                // No GUI for us today...
			if (var.testing) {
				log.info("MLG_GUI: Command Line Only!");
			}
		}

		if (GUI) {    //GUI
			// Launch MLG_GUI

			EventQueue.invokeLater(new Runnable() {

				@SuppressWarnings("static-access")
				@Override
				public void run() {

					try {
						final MLG_GUI window = new MLG_GUI();
						window.frmMLG_GUI.setVisible(true);
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			});

		} else {    //No GUI
			// Finally, Lets Start MLG!

			var.UsingGUI = false;
			var.args = args;
			Main.runCLI();
		}

	}

	/**
	 * Start MinecraftLandGenerator (Command Line Interface)
	 *
	 * @author Corrodias, Morlok8k
	 */
	private static void runCLI() {
		final File backupLevel;
		final File serverLevel;

		// Basic Program Initialization
		Startup.initialStart();
		if (Startup.programArguments()) {
			log.error("Error in program arguments.");
			return;
		}
		if (Startup.confFile()) {
			log.error("Error in conf file.");
			return;
		}
		try {
			if (Setup.doSetup()) {
				log.error("Error in setup.");
				return;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  // Checks for server.properties, checks for world, creates world if needed, checks old data}

		// =====================================================================
		//                              PROCESSING
		// =====================================================================

		log.info("Processing world \"" + var.worldPath + "\", in " + var.increment
				+ " block increments, with: " + var.javaLine);
		// out( MLG + "Processing \"" + worldName + "\"...");

		log.info("");

		try {
			final long generationStartTimeTracking = System.currentTimeMillis();        //Start of time remaining calculations

			final boolean serverLaunch = Server.runMinecraft();                //run server once at spawn point to make sure everything works.
			//final boolean serverLaunch = true;	//testing only
			if (!(serverLaunch)) {
				System.exit(1);                // we got a warning or severe error
			}

			if ((var.xRange == 0) & (var.zRange == 0)) {  //If the server is launched with an X and a Z of zero, then we just shutdown MLG after the initial launch.
				return;
			}

			FileWrite.AppendTxtFile(var.worldPath + var.fileSeparator + var.logFile,
					"# " + var.PROG_NAME + " " + var.VERSION + " - " + SelfAware.JVMinfo()
							+ var.newLine + "# " + var.MC_Server_Version + var.newLine
							+ "# Started: " + var.dateFormat.format(generationStartTimeTracking)
							+ var.newLine + "##Size: X" + var.xRange + "Z" + var.zRange
							+ var.newLine);

			log.info("");

			serverLevel = new File(var.worldPath + var.fileSeparator + "level.dat");
			backupLevel = new File(var.worldPath + var.fileSeparator + "level_backup.dat");

			log.info("Backing up level.dat to level_backup.dat.\n");
			Misc.copyFile(serverLevel, backupLevel);
			Files.copy()

			final Coordinates spawn = SpawnPoint.getSpawn(serverLevel);
			log.info("Spawn point detected: [X,Y,Z] " + spawn);

			FileWrite.AppendTxtFile(var.worldPath + var.fileSeparator + var.logFile, "# Seed: "
					+ var.randomSeed + var.newLine + "# Spawn: " + spawn.toString() + var.newLine);

			boolean overridden = false;
			if (var.xOffset == null) {
				var.xOffset = spawn.getX();
			} else {
				overridden = true;
			}
			if (var.zOffset == null) {
				var.zOffset = spawn.getZ();
			} else {
				overridden = true;
			}

			double xR = 0, zR = 0, xO = 0, zO = 0;

			if (var.useChunks) {        // use Chunks or Regions

				xR = var.xRange;            //say xRange was entered as 1000.  this changes it to be 1008, a multiple of 16. (the size of a chunk)
				xR = xR / 16;
				xR = Math.ceil(xR);
				xR = xR * 16;

				zR = var.zRange;            //say zRange was entered as 2000.  there is no change, as it already is a multiple of 16.
				zR = zR / 16;
				zR = Math.ceil(zR);
				zR = zR * 16;

				xO = var.xOffset;
				xO = xO / 16;
				xO = Math.round(xO);        //round instead of Ceiling
				xO = xO * 16;

				zO = var.zOffset;
				zO = zO / 16;
				zO = Math.round(zO);        //round instead of Ceiling
				zO = zO * 16;

			} else {

				xR = var.xRange;            //say xRange was entered as 1000.  this changes it to be 1024, a multiple of 512. (the size of a region)
				xR = xR / 512;
				xR = Math.ceil(xR);
				xR = xR * 512;

				zR = var.zRange;            //say zRange was entered as 2048.  there is no change, as it already is a multiple of 512.
				zR = zR / 512;
				zR = Math.ceil(zR);
				zR = zR * 512;

				xO = var.xOffset;
				xO = xO / 512;
				xO = Math.round(xO);        //round instead of Ceiling
				xO = xO * 512;

				zO = var.zOffset;
				zO = zO / 512;
				zO = Math.round(zO);        //round instead of Ceiling
				zO = zO * 512;

			}

			var.xRange = (int) Math.ceil(xR);
			var.zRange = (int) Math.ceil(zR);
			var.xOffset = (int) Math.ceil(xO);
			var.zOffset = (int) Math.ceil(zO);

			if (overridden) {
				log.info("Centering land generation on [" + var.xOffset + ", " + var.zOffset
						+ "] due to switches.");
			} else {
				log.info("Centering land generation on [" + var.xOffset + ", " + var.zOffset + "]\n");
			}

			double xLoops, zLoops;
			long curXloops = 0;
			long curZloops = 0;
			double xRangeAdj = 0;
			double zRangeAdj = 0;

			// have main loop make an arraylist of spawnpoints
			// read from a file if MLG has run before on this world.  save to arraylist
			// remove existing points from new list.
			// run mlg on remaining list of spawn points.

			// X
			xRangeAdj = var.xRange - var.incrementFull;                //Adjusting our range of X
			xRangeAdj = xRangeAdj / var.increment;
			xRangeAdj = Math.ceil(xRangeAdj);                        //round up to find out!
			xRangeAdj = xRangeAdj + 1;                                //add an additional increment, as we removed a full one above
			xRangeAdj = xRangeAdj * var.increment;

			double inc = var.increment * 2;        //increment*2, and casts to double.

			// thanks to e2pii (on reddit) for correcting my math here.
			// http://www.reddit.com/r/learnmath/comments/2y14fq/what_am_i_overlooking_here/
			xLoops = (var.xRange - var.incrementFull) / inc;                //How many loops do we need?
			xLoops = Math.ceil(xLoops);
			xLoops = xLoops + xLoops + 1;

			// Z
			zRangeAdj = var.zRange - var.incrementFull;                //Adjusting our range of Z
			zRangeAdj = zRangeAdj / var.increment;
			zRangeAdj = Math.ceil(zRangeAdj);                        //round up to find out!
			zRangeAdj = zRangeAdj + 1;                                //add an additional increment, as we removed a full one above
			zRangeAdj = zRangeAdj * var.increment;

			zLoops = (var.zRange - var.incrementFull) / inc;                //How many loops do we need?
			zLoops = Math.ceil(zLoops);
			zLoops = zLoops + zLoops + 1;

			log.info("Calculating Spawn Points...");

			// Perfect Squares Code:

			/*
			if (xLoops > 3) {
				xLoops = xLoops + 1;
			}

			if (zLoops > 3) {
				zLoops = zLoops + 1;
			}
			*/

			long totalIterations = (long) (xLoops * zLoops);

			log.info("Estimated Total Spawn Points: " + totalIterations);

			if (totalIterations > Integer.MAX_VALUE) {
				log.error("TOO BIG!  Please reduce the world size.  World Size can't be larger than 17609200 x 17609200");        //or 17794560 using -i384
				backupLevel.delete();
				log.info("Removed backup file.");
				System.exit(0);
			}

			long currentIteration = 0;

			long differenceTime = System.currentTimeMillis();

			Long timeTracking;

			ArrayList<Coordinates> launchList = new ArrayList<>(0);
			try {
				launchList = new ArrayList<>((int) totalIterations);
			} catch (Exception e1) {
				e1.printStackTrace();
				log.error("TOO BIG!  Your computer can't handle such a large map.  The size is dependant on 32/64 bit and Memory.");
				backupLevel.delete();
				log.info("Removed backup file.");
				System.exit(0);
			}

			// X - West to East
			for (long currentX = 0; currentX <= (xRangeAdj / 2); currentX += var.increment) {
				curXloops++;
				boolean eastEdgeReached = false;

				if (curXloops == 1) {
					currentX = (((0 - var.xRange) / 2) + (var.incrementFull / 2));    // West Edge of map

				} else if (currentX >= ((xRangeAdj / 2) - (var.increment / 2))) {
					currentX = ((var.xRange / 2) - (var.incrementFull / 2));            // East Edge of map
					eastEdgeReached = true;
				}

				// Z - North to South
				for (long currentZ = 0; currentZ <= (zRangeAdj / 2); currentZ += var.increment) {
					currentIteration++;

					curZloops++;
					boolean southEdgeReached = false;
					if (curZloops == 1) {
						currentZ = (((0 - var.zRange) / 2) + (var.incrementFull / 2));    // North Edge of map
					} else if (currentZ >= ((zRangeAdj / 2) - (var.increment / 2))) {
						currentZ = ((var.zRange / 2) - (var.incrementFull / 2));        // South Edge of map
						southEdgeReached = true;
					}

					{ // Middle of Loop

						if (currentIteration % 10000000 == 0) {            //for long calculations, we output an update every 10,000,000 points
							String percentDone =
									Double.toString((((double) currentIteration) / totalIterations) * 100);
							final int percentIndex =
									((percentDone.indexOf(".") + 3) > percentDone.length())
											? percentDone.length() : (percentDone.indexOf(".") + 3);        //fix index on numbers like 12.3
							percentDone =
									percentDone.substring(0, (percentDone.indexOf(".") == -1
											? percentDone.length() : percentIndex));            //Trim output, unless whole number
							log.info("Calculated: " + currentIteration + "/" + totalIterations
									+ " spawn points. (" + percentDone + "% Done)");
						}

						// add Coordinates to arraylist here
						final Coordinates tempCoords =
								new Coordinates((int) currentX + var.xOffset, 64, (int) currentZ
										+ var.zOffset);
						launchList.add(tempCoords);

						// Write the current Coordinates to log file!
						//FileWrite.AppendTxtFile(var.worldPath + var.fileSeparator
						//		+ "MLG_coordinate_list.log", tempCoords.toString() + var.newLine);

						if (var.testing) {
							System.out.println(tempCoords);
						}

					} // End of the Middle of Loop

					if (curZloops == 1) {            // We are at the North edge.  We have special code for the North edge, so we need to change currentZ to be normal again.
						currentZ =
								(long) ((Math.ceil((((0 - zRangeAdj) / 2) / var.increment))) * var.increment);
					}
					if (southEdgeReached) {
						currentZ = (long) zRangeAdj;        // We reached the South edge, so we make sure that we exit the "for loop", bypassing the "1152 bug"
					}

				} // End Z
				curZloops = 0;
				if (curXloops == 1) {            // We are at the West edge.  We have special code for the West edge, so we need to change currentX to be normal again.
					currentX =
							(long) ((Math.ceil((((0 - xRangeAdj) / 2) / var.increment))) * var.increment);
				}
				if (eastEdgeReached) {
					currentX = (long) xRangeAdj;        // We reached the East edge, so we make sure that we exit the "for loop", bypassing the "1152 bug"
				}

			} // End X

			String pD = Double.toString((((double) currentIteration) / totalIterations) * 100);
			final int pI =
					((pD.indexOf(".") + 3) > pD.length()) ? pD.length() : (pD.indexOf(".") + 3);        //fix index on numbers like 12.3
			pD = pD.substring(0, (pD.indexOf(".") == -1 ? pD.length() : pI));            //Trim output, unless whole number
			log.info("Calculated: " + currentIteration + "/" + totalIterations + " spawn points. ("
					+ pD + "% Done)");

			//get existing list, and remove this list from launchList
			final ArrayList<Coordinates> removeList =
					FileRead.readArrayListCoordLog(var.worldPath + var.fileSeparator + var.logFile);

			log.info("Removing known generated areas...");
			if (!removeList.isEmpty()) {
				launchList.removeAll(removeList);
			}

			removeList.clear();        // we are done with this now.

			System.gc();        //run the garbage collector - hopefully free up some memory!

			currentIteration = 0;
			totalIterations = launchList.size();
			Coordinates xyz = null;
			final Iterator<Coordinates> coordArrayIterator = launchList.iterator();
			while (coordArrayIterator.hasNext()) {
				currentIteration++;
				xyz = coordArrayIterator.next();

				//////// Start server launch code

				String percentDone =
						Double.toString((((double) currentIteration - 1) / totalIterations) * 100);
				final int percentIndex =
						((percentDone.indexOf(".") + 3) > percentDone.length()) ? percentDone
								.length() : (percentDone.indexOf(".") + 3);        //fix index on numbers like 12.3
				percentDone =
						percentDone.substring(0,
								(percentDone.indexOf(".") == -1 ? percentDone.length()
										: percentIndex));            //Trim output, unless whole number

				log.info("Setting spawn to [X,Y,Z]: " + xyz + " (" + currentIteration + " of "
						+ totalIterations + ") " + percentDone + "% Done"); // Time Remaining estimate

				timeTracking = System.currentTimeMillis();

				//NEW CODE:
				differenceTime =
						(timeTracking - generationStartTimeTracking) / (currentIteration + 1);        // Updated.  we now count all runs, instead of the last 4.
				differenceTime *= 1 + (totalIterations - currentIteration);                                    // this should provide a more accurate result.
				log.info("Estimated time remaining: " + String.format("%02d:%02d", (differenceTime / 1000) / 60, (differenceTime / 1000) % 60));                        // I've noticed it gets pretty accurate after about 8 launches!

				// Set the spawn point
				SpawnPoint.setSpawn(serverLevel, xyz);

				// Launch the server
				boolean serverSuccess = false;

				serverSuccess = Server.runMinecraft();
				log.info("");

				//////// End server launch code

				if (serverSuccess) {
					// Write the current Coordinates to log file!
					FileWrite.AppendTxtFile(var.worldPath + var.fileSeparator + var.logFile,
							xyz.toString() + var.newLine);
				} else {
					System.exit(1);                // we got a warning or severe error
				}

			}

			if (currentIteration == 0) {
				log.info("Nothing to generate!");
			} else {
				//TODO: write completion code to output.
				// FileWrite.AppendTxtFile(var.worldPath + var.fileSeparator + var.logFile, ####### + var.newLine);
				//
				//add xrange, zrange, region/chunk, xoffset, zoffset, increment    //anything else?
				//
				log.info("Finished generating chunks.");
			}

			Misc.copyFile(backupLevel, serverLevel);
			backupLevel.delete();
			log.info("Restored original level.dat.");
			long completeIn =var.startTime - System.currentTimeMillis();
			log.info("Generation complete in: "
					+ String.format("%02d:%02d", (completeIn / 1000) / 60, (completeIn / 1000) % 60));
		//	Time.waitTenSec(false);

			if (var.webLaunch) {        //if webLaunch is already false, don't check for these things
				if (java.awt.GraphicsEnvironment.isHeadless()) {
					var.webLaunch = false;                    //headless enviroment - cant bring up webpage!
				}
				final File web1 = new File("web");
				final File web2 = new File("web.txt");        //user has put in the magical file to not launch the webpage
				final File web3 = new File("web.txt.txt");
				if (web2.exists() || (web1.exists() || web3.exists())) {  //check for "web.txt", if not found, check for "web", and if still not found, check for "web.txt.txt"
					var.webLaunch = false;
				}
			}

			if (var.webLaunch && java.awt.Desktop.isDesktopSupported()) {
				final URI splashPage = URI.create("http://adf.ly/520855/splashbanner");
				try {
					java.awt.Desktop.getDesktop().browse(splashPage);
				} catch (final IOException e) {
					log.error("Error displaying webpage... " + e.getLocalizedMessage());
				}
			} else {
				log.info("Please Visit: http://adf.ly/520855/mlg");
				log.info("Or: " + var.WEBSITE);
				log.info("Thanks!");
			}

		} catch (final IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
}
