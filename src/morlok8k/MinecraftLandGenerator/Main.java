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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import morlok8k.MinecraftLandGenerator.GUI.MLG_GUI;

/**
 * 
 * @author Corrodias, Morlok8k, pr0f1x
 * 
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
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		var.startTime = System.currentTimeMillis();

		var.originalArgs = args;	// we may potentially remove some args later, but we keep a record of the original for the log file.

		// This is really just here for debugging...
		// I plan on adding more asserts later, but for now, this will do.
		// to enable this, run:
		// java -enableassertions -jar MinecraftLandGenerator.jar
		assert var.assertsEnabled = true;  // Intentional side-effect!!!  (This may cause a Warning, which is safe to ignore: "Possible accidental assignment in place of a comparison. A condition expression should not be reduced to an assignment")
		if (var.assertsEnabled) {
			Out.outD("assertsEnabled: " + var.assertsEnabled);
			var.verbose = true;
			Out.outD("Verbose mode forced!");
			var.testing = true;
			Out.outD("Debug mode forced!");
			var.dontWait = true;
			Out.outD("-nowait mode forced!");
			Out.outD("");
		}

		boolean GUI = false;			// GUI needs to be true to run in graphical mode
		boolean NOGUI = false;			// NOGUI is a flag that finds reasons to not use a graphical mode.

		if (args.length != 0) {			// if args are present, then we assume we want NOGUI
			NOGUI = true;				// if no args are present, we will attempt GUI
		}

		String[] argsNOGUI = new String[args.length];
		argsNOGUI = args;
		argsNOGUI = StringArrayParse.Parse(argsNOGUI, "nogui");		//parse out "nogui"
		if (!(args.equals(argsNOGUI))) {								//do the freshly parsed args match the original?
			args = argsNOGUI;											//use the freshly parsed args for everything else now...
			NOGUI = true;
		}

		//MLG_GUI Choosing code...
		if ((!NOGUI) && (!java.awt.GraphicsEnvironment.isHeadless())) {
			GUI = true;
			if (var.testing) {
				Out.outD("MLG_GUI: This is a graphical enviroment.");
			}

			//////
			GUI = false;				// forcing GUI to be false for now, because I don't have the MLG_GUI code ready yet!
			//////

		} else {
			GUI = false;				// No GUI for us today...
			if (var.testing) {
				Out.outD("MLG_GUI: Command Line Only!");
			}
		}

		if (GUI) {	//GUI
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

		} else {	//No GUI
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
	 * @param args
	 * 
	 */
	private static void runCLI() {
		final File backupLevel;
		final File serverLevel;

		// Basic Program Initialization
		Startup.initialStart();
		if (Startup.programArguments()) {
			Out.err("Error in program arguments.");
			return;
		}
		if (Startup.confFile()) {
			Out.err("Error in conf file.");
			return;
		}
		try {
			if (Setup.doSetup()) {
				Out.err("Error in setup.");
				return;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  // Checks for server.properties, checks for world, creates world if needed, checks old data}

		// =====================================================================
		//                              PROCESSING
		// =====================================================================

		Out.out("Processing world \"" + var.worldPath + "\", in " + var.increment
				+ " block increments, with: " + var.javaLine);
		// out( MLG + "Processing \"" + worldName + "\"...");

		Out.out("");

		try {
			final long generationStartTimeTracking = System.currentTimeMillis();		//Start of time remaining calculations

			final boolean serverLaunch = Server.runMinecraft();				//run server once at spawn point to make sure everything works.

			if (!(serverLaunch)) {
				System.exit(1);				// we got a warning or severe error
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

			Out.out("");

			serverLevel = new File(var.worldPath + var.fileSeparator + "level.dat");
			backupLevel = new File(var.worldPath + var.fileSeparator + "level_backup.dat");

			Out.out("Backing up level.dat to level_backup.dat.");
			Misc.copyFile(serverLevel, backupLevel);
			Out.out("");

			final Coordinates spawn = SpawnPoint.getSpawn(serverLevel);
			Out.out("Spawn point detected: [X,Y,Z] " + spawn);

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

			if (var.useChunks) {		// use Chunks or Regions
				var.xRange = (int) (Math.ceil(((double) var.xRange) / ((double) 16))) * 16;			//say xRange was entered as 1000.  this changes it to be 1008, a multiple of 16. (the size of a chunk)
				var.zRange = (int) (Math.ceil(((double) var.zRange) / ((double) 16))) * 16;			//say zRange was entered as 2000.  there is no change, as it already is a multiple of 16.
				var.xOffset = (int) (Math.ceil(((double) var.xOffset) / ((double) 16))) * 16;
				var.zOffset = (int) (Math.ceil(((double) var.zOffset) / ((double) 16))) * 16;
			} else {
				var.xRange = (int) (Math.ceil(((double) var.xRange) / ((double) 512))) * 512;			//say xRange was entered as 1000.  this changes it to be 1024, a multiple of 512. (the size of a region)
				var.zRange = (int) (Math.ceil(((double) var.zRange) / ((double) 512))) * 512;			//say zRange was entered as 2048.  there is no change, as it already is a multiple of 512.
				var.xOffset = (int) (Math.ceil(((double) var.xOffset) / ((double) 512))) * 512;
				var.zOffset = (int) (Math.ceil(((double) var.zOffset) / ((double) 512))) * 512;
			}

			if (overridden) {
				Out.out("Centering land generation on [" + var.xOffset + ", " + var.zOffset
						+ "] due to switches.");
			}

			Out.out("");

			double xLoops, zLoops;
			int curXloops = 0;
			int curZloops = 0;
			int xRangeAdj = 0;
			int zRangeAdj = 0;

			// have main loop make an arraylist of spawnpoints
			// read from a file if MLG has run before on this world.  save to arraylist
			// remove existing points from new list.
			// run mlg on remaining list of spawn points.

			// X
			xLoops = ((double) var.xRange / (double) var.increment);		//How many loops do we need?
			xLoops = Math.ceil(xLoops);								//round up to find out!
			xRangeAdj = (int) (xLoops * var.increment);
			xLoops = xLoops + 1;

			// Z
			zLoops = ((double) var.zRange / (double) var.increment);		//How many loops do we need?
			zLoops = Math.ceil(zLoops);								//round up to find out!
			zRangeAdj = (int) (zLoops * var.increment);
			zLoops = zLoops + 1;

			Out.out("Calculating Spawn Points...");

			// Perfect Squares Code:

			int totalIterations = (int) (xLoops * zLoops);
			int currentIteration = 0;

			long differenceTime = System.currentTimeMillis();

			Long timeTracking = 0L;

			final ArrayList<Coordinates> launchList = new ArrayList<Coordinates>(totalIterations);

			for (int currentX = 0; currentX <= (xRangeAdj / 2); currentX += var.increment) {
				curXloops++;
				boolean eastEdgeReached = false;

				if (curXloops == 1) {
					currentX = (((0 - var.xRange) / 2) + (var.incrementFull / 2));  	// West Edge of map

				} else if (currentX >= ((xRangeAdj / 2) - (var.increment / 2))) {
					currentX = ((var.xRange / 2) - (var.incrementFull / 2));			// East Edge of map
					eastEdgeReached = true;
				}

				for (int currentZ = 0; currentZ <= (zRangeAdj / 2); currentZ += var.increment) {
					currentIteration++;

					curZloops++;
					boolean southEdgeReached = false;
					if (curZloops == 1) {
						currentZ = (((0 - var.zRange) / 2) + (var.incrementFull / 2));	// North Edge of map
					} else if (currentZ >= ((zRangeAdj / 2) - (var.increment / 2))) {
						currentZ = ((var.zRange / 2) - (var.incrementFull / 2));		// South Edge of map
						southEdgeReached = true;
					}

					{
						// add Coordinates to arraylist here
						final Coordinates tempCoords =
								new Coordinates(currentX + var.xOffset, 64, currentZ + var.zOffset);
						launchList.add(tempCoords);

						if (var.testing) {
							System.out.println(tempCoords);
						}
					}

					if (curZloops == 1) {			// We are at the North edge.  We have special code for the North edge, so we need to change currentZ to be normal again.
						currentZ =
								(int) ((Math.ceil((((0 - zRangeAdj) / 2) / var.increment))) * var.increment);
					}
					if (southEdgeReached) {
						currentZ = zRangeAdj;		// We reached the South edge, so we make sure that we exit the "for loop", bypassing the "1152 bug"
					}

				}
				curZloops = 0;
				if (curXloops == 1) {			// We are at the West edge.  We have special code for the West edge, so we need to change currentX to be normal again.
					currentX =
							(int) ((Math.ceil((((0 - xRangeAdj) / 2) / var.increment))) * var.increment);
				}
				if (eastEdgeReached) {
					currentX = xRangeAdj;		// We reached the East edge, so we make sure that we exit the "for loop", bypassing the "1152 bug"
				}

			}

			//get existing list, and remove this list from launchList
			final ArrayList<Coordinates> removeList =
					FileRead.readArrayListCoordLog(var.worldPath + var.fileSeparator + var.logFile);

			if (!(removeList.isEmpty())) {
				Arraylist.arrayListRemove(launchList, removeList);
			}

			removeList.clear();		// we are done with this now.

			System.gc();		//run the garbage collector - hopefully free up some memory!

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
								.length() : (percentDone.indexOf(".") + 3);		//fix index on numbers like 12.3
				percentDone =
						percentDone.substring(0,
								(percentDone.indexOf(".") == -1 ? percentDone.length()
										: percentIndex));			//Trim output, unless whole number

				Out.out("Setting spawn to [X,Y,Z]: " + xyz + " (" + currentIteration + " of "
						+ totalIterations + ") " + percentDone + "% Done"); // Time Remaining estimate

				timeTracking = System.currentTimeMillis();

				//NEW CODE:
				differenceTime =
						(timeTracking - generationStartTimeTracking) / (currentIteration + 1);		// Updated.  we now count all runs, instead of the last 4.
				differenceTime *= 1 + (totalIterations - currentIteration);									// this should provide a more accurate result.
				Out.out("Estimated time remaining: " + Time.displayTime(differenceTime));						// I've noticed it gets pretty accurate after about 8 launches!

				// Set the spawn point
				SpawnPoint.setSpawn(serverLevel, xyz);

				// Launch the server
				boolean serverSuccess = false;

				serverSuccess = Server.runMinecraft();
				Out.out("");

				//////// End server launch code

				if (serverSuccess) {
					// Write the current Coordinates to log file!
					FileWrite.AppendTxtFile(var.worldPath + var.fileSeparator + var.logFile,
							xyz.toString() + var.newLine);
				} else {
					System.exit(1);				// we got a warning or severe error
				}

			}

			if (currentIteration == 0) {
				Out.out("Nothing to generate!");
			} else {
				Out.out("Finished generating chunks.");
			}

			Misc.copyFile(backupLevel, serverLevel);
			backupLevel.delete();
			Out.out("Restored original level.dat.");

			Out.out("Generation complete in: "
					+ Time.displayTime(var.startTime, System.currentTimeMillis()));
			Time.waitTenSec(false);

			//TODO: add if's

			if (var.webLaunch) {		//if webLaunch is already false, don't check for these things
				if (java.awt.GraphicsEnvironment.isHeadless()) {
					var.webLaunch = false;					//headless enviroment - cant bring up webpage!
				}
				final File web1 = new File("web");
				final File web2 = new File("web.txt");		//user has put in the magical file to not launch the webpage
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
					Out.err("Error displaying webpage... " + e.getLocalizedMessage());
				}
			} else {
				Out.out("Please Visit: http://adf.ly/520855/mlg");
				Out.out("Or: " + var.WEBSITE);
				Out.out("Thanks!");
			}

		} catch (final IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
		}
	}
}
