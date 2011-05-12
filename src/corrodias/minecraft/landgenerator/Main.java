/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package corrodias.minecraft.landgenerator;

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
//import java.io.OutputStreamWriter;
//import java.util.Date;
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
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;
//import java.io.*;					//if we want to import everything...
//import java.util.*;

/**
 *
 * @author Corrodias, Morlok8k
 */
public class Main {

	//Version Number!
	private static final String VERSION = "1.4.2";
	
	private static final String separator = System.getProperty("file.separator");
	//private static final String classpath = System.getProperty("java.class.path");
	//private static final String javaPath = System.getProperty("java.home") + separator + "bin" + separator + "java";

	private int increment = 300;
	private ProcessBuilder minecraft = null;
	private String javaLine = null;
	private String serverPath = null;
	private String worldPath = null;
	private String worldName = null;
	private static String doneText = null;
	private static String preparingText = null;
	private String hell = null;
	private int xRange = 0;
	private int yRange = 0;
	private Integer xOffset = null;
	private Integer yOffset = null;
	private boolean verbose = false;
	private boolean alternate = false;

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		(new Main()).run(args);  //Why?  idk, but merging this with run() creates errors, and i'm lazy!
	}

	private void run(String[] args) {
		System.out.println("Minecraft Land Generator version " + VERSION);
		System.out.println("Uses a Minecraft server to generate square land of a specified size.");
		System.out.println("");

		// =====================================================================
		//                           INSTRUCTIONS
		// =====================================================================

		if (args.length == 0 || args[0].equals("-version") || args[0].equals("-help") || args[0].equals("/?")) {
			System.out.println("Usage: java -jar MinecraftLandGenerator.jar x y [serverpath] [switches]");
			System.out.println("");
			System.out.println("Arguments:");
			System.out.println("             x : X range to generate");
			System.out.println("             y : Y range to generate");
			System.out.println("    serverpath : the path to the directory in which the server runs (takes precedence over the config file setting)");
			System.out.println("");
			System.out.println("Switches:");
			System.out.println("      -verbose : causes the application to output the server's messages to the console");
			System.out.println("            -v : same as -verbose");
			//System.out.println("          -alt : alternate server launch sequence");
			//System.out.println("            -a : same as -alt");
			System.out.println("           -i# : override the iteration spawn offset increment (default 300) (example: -i100)");
			System.out.println("           -x# : set the X offset to generate land around (example: -x0)");
			System.out.println("           -y# : set the X offset to generate land around (example: -y0)");
			System.out.println("");
			System.out.println("Other options:");
			System.out.println("  java -jar MinecraftLandGenerator.jar -printspawn");
			System.out.println("  java -jar MinecraftLandGenerator.jar -ps");
			System.out.println("        Outputs the current world's spawn point coordinates.");
			System.out.println("");
			System.out.println("  java -jar MinecraftLandGenerator.jar -conf");
			System.out.println("        Generates a MinecraftLandGenerator.conf file.");
			System.out.println("");
			System.out.println("  java -jar MinecraftLandGenerator.jar -version");
			System.out.println("  java -jar MinecraftLandGenerator.jar -help");
			System.out.println("  java -jar MinecraftLandGenerator.jar /?");
			System.out.println("        Prints this message.");
			System.out.println("");
			System.out.println("When launched with the -conf switch, this application creates a MinecraftLandGenerator.conf file that contains configuration options.");
			System.out.println("If this file does not exist or does not contain all required properties, the application will not run.");
			System.out.println("");
			System.out.println("MinecraftLandGenerator.conf properties:");
			System.out.println("          Java : The command line to use to launch the server");
			System.out.println("    ServerPath : The path to the directory in which the server runs (can be overridden by the serverpath argument)");
			System.out.println("     Done_Text : The output from the server that tells us that we are done");
			System.out.println("Preparing_Text : The output from the server that tells us the percentage");

			return;
		}

		// =====================================================================
		//                         STARTUP AND CONFIG
		// =====================================================================

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
        Date date = new Date();
        //dateFormat.format(date);
		
		// the arguments are apparently okay so far. parse the conf file.
		if (args[0].equalsIgnoreCase("-conf")) {
			try {
				File config = new File("MinecraftLandGenerator.conf");
				BufferedWriter out = new BufferedWriter(new FileWriter(config));
				out.write("#Minecraft Land Generator Configuration File:  Version: " + VERSION);
				out.newLine();
				out.write("#Authors: Corrodias, Morlok8k");
				out.newLine();
				out.write("#Auto-Generated: " + dateFormat.format(date));
				out.newLine();
				out.write("Java=java -Djline.terminal=jline.UnsupportedTerminal -Duser.language=en -Xms1024m -Xmx1024m -Xincgc -jar minecraft_server.jar nogui");
					// I added the jline tag for future proofing...
				out.newLine();
				out.write("ServerPath=.");
				out.newLine();
				out.write("Done_Text=[INFO] Done");
				out.newLine();
				out.write("Preparing_Text=[INFO] Preparing spawn area:");
				out.newLine();
				out.close();
				System.out.println("MinecraftLandGenerator.conf file created.");
				return;
			} catch (IOException ex) {
				System.err.println("Could not create MinecraftLandGenerator.conf.");
				return;
			}
		} else if (args[0].equalsIgnoreCase("-ps") || args[0].equalsIgnoreCase("-printspawn")) {
			// okay, sorry, this is an ugly hack, but it's just a last-minute feature.
			printSpawn();
			return;
		} else if (args.length == 1) {
			System.out.println("For help, use java -jar MinecraftLandGenerator.jar -help");
			return;
		}

		try {
			File config = new File("MinecraftLandGenerator.conf");
			BufferedReader in = new BufferedReader(new FileReader(config));
			String line;
			while ((line = in.readLine()) != null) {
				int pos = line.indexOf('=');
				int end = line.lastIndexOf('#');	//comments, ignored lines
				
				
				if (end == -1){		// If we have no hash sign, then we read till the end of the line
					end = line.length();
				}
				
				if (end <= pos){	// If hash is before the '=', we may have a issue... it should be fine, cause we check for issues next, but lets make sure.
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
				System.err.println("MinecraftLandGenerator.conf does not contain all required properties. Please recreate it by running this application with -conf.");
				return;
			}
			
			if (doneText == null || preparingText == null) {
				System.err.println("Old Version of MinecraftLandGenerator.conf found.  Updating...");
				try {
					File configUpdate = new File("MinecraftLandGenerator.conf");
					BufferedWriter out = new BufferedWriter(new FileWriter(configUpdate));
					out.write("#Minecraft Land Generator Configuration File:  Version: " + VERSION);
					out.newLine();
					out.write("#Authors: Corrodias, Morlok8k");
					out.newLine();
					out.write("#Auto-Updated: " + dateFormat.format(date));
					out.newLine();
					out.write("Java=" + javaLine);
					out.newLine();
					out.write("ServerPath=" + serverPath);
					out.newLine();
					out.write("Done_Text=[INFO] Done");
					out.newLine();
					out.write("Preparing_Text=[INFO] Preparing spawn area:");
					out.newLine();
					out.close();
					System.out.println("MinecraftLandGenerator.conf file created.");
					return;
				} catch (IOException ex) {
					System.err.println("Could not create MinecraftLandGenerator.conf.");
					return;
				}
			}
			
		} catch (FileNotFoundException ex) {
			System.out.println("Could not find MinecraftLandGenerator.conf. It is recommended that you run the application with the -conf option to create it.");
			return;
		} catch (IOException ex) {
			System.err.println("Could not read MinecraftLandGenerator.conf");
			return;
		}

		// ARGUMENTS
		try {
			xRange = Integer.parseInt(args[0]);
			yRange = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			System.err.println("Invalid X or Y argument.");
			return;
		}
		
		verbose = false;		// Verifing that these vars are false
		alternate = false;		// before changing them...
		
		// This is embarrassing. Don't look.
		try {
			for (int i = 0; i < args.length - 2; i++) {
				String nextSwitch = args[i + 2].toLowerCase();
				if (nextSwitch.equals("-verbose") || nextSwitch.equals("-v")) {
					verbose = true;
				} else if (nextSwitch.startsWith("-i")) {
					increment = Integer.parseInt(args[i + 2].substring(2));
				} else if (nextSwitch.equals("-alt") || nextSwitch.equals("-a")) {
				//	System.out.println("Using Alternate Launching...");
					System.out.println("Alternate Launch Disabled.");
					// This is a failed attempt to fix issues with Windows XP 32bit.
				//	alternate = true;
					alternate = false; // force Alt to be off, just in case
				} else if (nextSwitch.startsWith("-x")) {
					xOffset = Integer.valueOf(args[i + 2].substring(2));
				} else if (nextSwitch.startsWith("-y")) {
					yOffset = Integer.valueOf(args[i + 2].substring(2));
				} else {
					serverPath = args[i + 2];
				}
			}
		} catch (NumberFormatException ex) {
			System.err.println("Invalid -i switch value.");
			return;
		}

		{
			// verify that we ended up with a good server path, either from the file or from an argument.
			File file = new File(serverPath);
			if (!file.exists() || !file.isDirectory()) {
				System.err.println("The server directory is invalid: " + serverPath);
				return;
			}
		}

		try {
			// read the name of the current world from the server.properties file
			BufferedReader props = new BufferedReader(new FileReader(new File(serverPath + separator + "server.properties")));
			String line;
			while ((line = props.readLine()) != null) {
				int pos = line.indexOf('=');
				if (pos != -1) {
					if (line.substring(0, pos).toLowerCase().equals("level-name")) {
						worldPath = serverPath + separator + line.substring(pos + 1);
						worldName = line.substring(pos + 1);
					} else if (line.substring(0, pos).toLowerCase().equals("hellworld")) {
						hell = line.substring(pos + 1);
						hell = hell.toLowerCase(Locale.ENGLISH);
					}
					
				}
			}

		} catch (FileNotFoundException ex) {
			System.err.println("Could not open " + serverPath + separator + "server.properties");
			return;
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			return;
		}

		{
			File backupLevel = new File(worldPath + separator + "level_backup.dat");
			if (backupLevel.exists()) {
				System.err.println("There is a level_backup.dat file left over from a previous attempt that failed. You should go determine whether to keep the current level.dat"
						+ " or restore the backup.");
				return;
			}
		}

		// =====================================================================
		//                              PROCESSING
		// =====================================================================

		System.out.println("Processing world \"" + worldPath + "\", in " + increment + " block increments, with: " + javaLine);
		if (hell.contains("true")){
			System.out.println("Processing The Nether of \"" + worldName + "\"... (DIM-1)");
		} else if (hell.contains("false")) {
			System.out.println("Processing \"" + worldName + "\"...");
		}
		System.out.println("");

		// prepare our two ProcessBuilders
		//minecraft = new ProcessBuilder(javaLine, "-Xms1024m", "-Xmx1024m", "-jar", jarFile, "nogui");
		minecraft = new ProcessBuilder(javaLine.split("\\s")); // is this always going to work? i don't know.
		minecraft.directory(new File(serverPath));
		minecraft.redirectErrorStream(true);
		

		
		try {
			System.out.println("Launching server once to make sure there is a world.");
			runMinecraft(minecraft, verbose, alternate, javaLine);
			System.out.println("");

			File serverLevel = new File(worldPath + separator + "level.dat");
			File backupLevel = new File(worldPath + separator + "level_backup.dat");

			System.out.println("Backing up level.dat to level_backup.dat.");
			copyFile(serverLevel, backupLevel);
			System.out.println("");

			Integer[] spawn = getSpawn(serverLevel);
			System.out.println("Spawn point detected: [" + spawn[0] + ", " + spawn[2] + "]");
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
					System.out.println("Centering land generation on [" + xOffset + ", " + yOffset + "] due to switches.");
				}
			}
			System.out.println("");

			int totalIterations = (xRange / increment + 1) * (yRange / increment + 1);
			int currentIteration = 0;

			long differenceTime = System.currentTimeMillis();
			Long[] timeTracking = new Long[]{differenceTime, differenceTime, differenceTime, differenceTime};
			for (int currentX = 0 - xRange / 2; currentX <= xRange / 2; currentX += increment) {
				for (int currentY = 0 - yRange / 2; currentY <= yRange / 2; currentY += increment) {
					currentIteration++;
					System.out.println("Setting spawn to [" + Integer.toString(currentX + xOffset) + ", " + Integer.toString(currentY + yOffset) + "] (" + currentIteration + "/" + totalIterations + ")");

					// Time Remaining estimate
					timeTracking[0] = timeTracking[1];
					timeTracking[1] = timeTracking[2];
					timeTracking[2] = timeTracking[3];
					timeTracking[3] = System.currentTimeMillis();
					if (currentIteration >= 4) {
						differenceTime = (timeTracking[3] - timeTracking[0]) / 3; // well, this is what it boils down to
						differenceTime *= 1 + (totalIterations - currentIteration);
						System.out.println(String.format("Estimated time remaining: %dh%dm%ds",
								differenceTime / (1000 * 60 * 60), (differenceTime % (1000 * 60 * 60)) / (1000 * 60), ((differenceTime % (1000 * 60 * 60)) % (1000 * 60)) / 1000));
					}

					// Set the spawn point
					setSpawn(serverLevel, currentX + xOffset, 128, currentY + yOffset);

					// Launch the server
					runMinecraft(minecraft, verbose, alternate, javaLine);
					System.out.println("");
				}
			}

			System.out.println("Finished generating chunks.");
			copyFile(backupLevel, serverLevel);
			backupLevel.delete();
			System.out.println("Restored original level.dat.");
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	protected static Integer[] getSpawn(File level) throws IOException {
		try {
			NBTInputStream input = new NBTInputStream(new FileInputStream(level));
			CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
			input.close();

			Map<String, Tag> originalData = ((CompoundTag) originalTopLevelTag.getValue().get("Data")).getValue();
			// This is our map of data. It is an unmodifiable map, for some reason, so we have to make a copy.
			Map<String, Tag> newData = new LinkedHashMap<String, Tag>(originalData);
			// .get() a couple of values, just to make sure we're dealing with a valid level file, here. Good for debugging, too.
			IntTag spawnX = (IntTag) newData.get("SpawnX");
			IntTag spawnY = (IntTag) newData.get("SpawnY");
			IntTag spawnZ = (IntTag) newData.get("SpawnZ");

			Integer[] ret = new Integer[]{spawnX.getValue(), spawnY.getValue(), spawnZ.getValue()};
			return ret;
		} catch (ClassCastException ex) {
			throw new IOException("Invalid level format.");
		} catch (NullPointerException ex) {
			throw new IOException("Invalid level format.");
		}
	}

	/**
	 * Changes the spawn point in the given Alpha level to the given coordinates.
	 * Note that, in Minecraft levels, the Y coordinate is height, while Z is
	 * what may normally be thought of as Y.
	 * @param level the level file to change the spawn point in
	 * @param x the new X value
	 * @param z the new Y value
	 * @param y the new Z value
	 * @throws IOException if there are any problems reading/writing the file
	 */
	protected static void setSpawn(File level, Integer x, Integer y, Integer z) throws IOException {
		try {
			NBTInputStream input = new NBTInputStream(new FileInputStream(level));
			CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
			input.close();

// <editor-fold defaultstate="collapsed" desc="structure">
// Structure:
//
//TAG_Compound("Data"): World data.
//    * TAG_Long("Time"): Stores the current "time of day" in ticks. There are 20 ticks per real-life second, and 24000 ticks per Minecraft day, making the day length 20 minutes. 0 appears to be sunrise, 12000 sunset and 24000 sunrise again.
//    * TAG_Long("LastPlayed"): Stores the Unix time stamp (in milliseconds) when the player saved the game.
//    * TAG_Compound("Player"): Player entity information. See Entity Format and Mob Entity Format for details. Has additional elements:
//          o TAG_List("Inventory"): Each TAG_Compound in this list defines an item the player is carrying, holding, or wearing as armor.
//                + TAG_Compound: Inventory item data
//                      # TAG_Short("id"): Item or Block ID.
//                      # TAG_Short("Damage"): The amount of wear each item has suffered. 0 means undamaged. When the Damage exceeds the item's durability, it breaks and disappears. Only tools and armor accumulate damage normally.
//                      # TAG_Byte("Count"): Number of items stacked in this inventory slot. Any item can be stacked, including tools, armor, and vehicles. Range is 1-255. Values above 127 are not displayed in-game.
//                      # TAG_Byte("Slot"): Indicates which inventory slot this item is in.
//          o TAG_Int("Score"): Current score, doesn't appear to be implemented yet. Always 0.
//    * TAG_Int("SpawnX"): X coordinate of the player's spawn position. Default is 0.
//    * TAG_Int("SpawnY"): Y coordinate of the player's spawn position. Default is 64.
//    * TAG_Int("SpawnZ"): Z coordinate of the player's spawn position. Default is 0.
//    * TAG_Byte("SnowCovered"): 1 enables, 0 disables, see Winter Mode
//    * TAG_Long("SizeOnDisk"): Estimated size of the entire world in bytes.
//    * TAG_Long("RandomSeed"): Random number providing the Random Seed for the terrain.
// </editor-fold>

			Map<String, Tag> originalData = ((CompoundTag) originalTopLevelTag.getValue().get("Data")).getValue();
			// This is our map of data. It is an unmodifiable map, for some reason, so we have to make a copy.
			Map<String, Tag> newData = new LinkedHashMap<String, Tag>(originalData);
			// .get() a couple of values, just to make sure we're dealing with a valid level file, here. Good for debugging, too.
			IntTag spawnX = (IntTag) newData.get("SpawnX");		//we never use these...
			IntTag spawnY = (IntTag) newData.get("SpawnY");
			IntTag spawnZ = (IntTag) newData.get("SpawnZ");
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
	 * Starts the process in the given ProcessBuilder, monitors its output for a
	 * "[INFO] Done!" message, and sends it a "stop\r\n" message. One message is printed
	 * to the console before launching and one is printed to the console when the
	 * Done! message is detected. If "verbose" is true, the process's output will
	 * also be printed to the console.
	 * @param minecraft
	 * @param verbose
	 * @throws IOException
	 */
	protected static void runMinecraft(ProcessBuilder minecraft, boolean verbose, boolean alternate, String javaLine) throws IOException {
		System.out.println("Starting server.");
		// monitor output and print to console where required.
		// STOP the server when it's done.
		
		// Damn it Java!  I hate you so much!
		// I can't reuse code the way I want to, like in other langauges.
		// So, here is a bunch of duplicate code...
		// Stupid compile errors...
		
		if (alternate) {   //Alternate is currently disabled.
		//	Runtime minecraftAlt = Runtime.getRuntime();
		//	Process process = minecraftAlt.exec(javaLine.split("\\s"));		
		//	//InputStream is = processAlt.getInputStream();
		//			// this didn't work - Minecraft Server uses the error stream for almost all the output. 
		//			// the input stream only reads the amount of recipes the server has, for instance, beta 1.4 reports: "144 recipes"
		//			// with the standard way, ProcessBuilder, we can combine Error and Input.
		//	//InputStreamReader isr = new InputStreamReader(is);
		//  //BufferedReader pOut = new BufferedReader(isr);
		//	BufferedReader pOut = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		//
		//	String line = null;
		//	
		//	byte[] stop = {'s', 't', 'o', 'p', '\r', '\n'};		//Moved here, so this code wont run every loop, thus Faster!
		//			//and no, i can't use a string here!
		//	
		//	while ((line = pOut.readLine()) != null) {
		//		if (verbose) {
		//			System.out.println(line);
		//		}
		//		if (line.contains("[INFO] Done")) {     //EDITED By Morlok8k for Minecraft 1.3+ Beta
		//			System.out.println("Stopping server.");
		//			OutputStream outputStream = process.getOutputStream();
		//			outputStream.write(stop);
		//			outputStream.flush();
		//			outputStream.close();
		//		}
		//		if (line.contains("[SEVERE]")) {		//If we have a severe error, stop...
		//			System.out.println("Severe error found: Stopping server.");
		//			OutputStream outputStream = process.getOutputStream();
		//			outputStream.write(stop);
		//			outputStream.flush();
		//			outputStream.close();
					System.exit(1);
		//			//Quit!
		//		}
		//	}
			
		} else {  //start minecraft server normally!
			Process process = minecraft.start();
			System.out.println("");
			if (verbose) {
				System.out.println("Started Server.");
			}
			BufferedReader pOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
			if (verbose) {
				System.out.println("Accessing Server Output...");
			}
			
			
			String line = null;
			
			byte[] stop = {'s', 't', 'o', 'p', '\r', '\n'};		//Moved here, so this code wont run every loop, thus Faster!
					//and no, i can't use a string here!
			
			while ((line = pOut.readLine()) != null) {
				if (verbose) {
					if (line.contains("[INFO]")){
						System.out.println(line.substring(line.lastIndexOf("]") + 2));
					} else {
						System.out.println(line);
					}
				} else if (line.contains(preparingText)){
					System.out.print(line.substring(line.length() - 3, line.length()) + "... ");
				}
								
				if (line.contains(doneText)) {     // now this is configurable!
					System.out.println("");
					System.out.println("Stopping server.");
					OutputStream outputStream = process.getOutputStream();
					outputStream.write(stop);
					outputStream.flush();
					outputStream.close();
				}
				if (line.contains("[SEVERE]")) {		//If we have a severe error, stop...
					System.out.println("");
					System.out.println("Severe error found: Stopping server.");
					OutputStream outputStream = process.getOutputStream();
					outputStream.write(stop);
					outputStream.flush();
					outputStream.close();
					System.exit(1);
					//Quit!
				}
			}
		}
		
		

		// readLine() returns null when the process exits
	}

	// I'd love to use nio, but it requires Java 7.
	// I could use Apache Commons, but i don't want to include a library for one little thing.
	// Copies src file to dst file.
	// If the dst file does not exist, it is created
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
			File config = new File("MinecraftLandGenerator.conf");
			BufferedReader in = new BufferedReader(new FileReader(config));
			String line;
			while ((line = in.readLine()) != null) {
				int ignoreLine = line.indexOf('#');
				if (ignoreLine == -1){
					ignoreLine = 1;
				} else if (ignoreLine == 0){
					ignoreLine = 1;
				} else if (ignoreLine == 1){
					ignoreLine = 1;
				} else {
					ignoreLine = line.length();
				}
				if (ignoreLine != 1){
					ignoreLine = line.length();
				}
								
				int pos = line.indexOf('=');
				if (pos != -1) {
					if (line.substring(0, pos).toLowerCase().equals("serverpath")) {
						serverPath = line.substring(pos + 1, ignoreLine);
					} else if (line.substring(0, pos).toLowerCase().equals("java")) {
						javaLine = line.substring(pos + 1, ignoreLine);
					}
				}
			}
			in.close();

			if (serverPath == null || javaLine == null) {
				System.err.println("MinecraftLandGenerator.conf does not contain all requird properties. Please recreate it by running this application with no arguments.");
				return false;
			}
		} catch (FileNotFoundException ex) {
			System.out.println("Could not find MinecraftLandGenerator.conf. It is recommended that you run the application with the -conf option to create it.");
			return false;
		} catch (IOException ex) {
			System.err.println("Could not read MinecraftLandGenerator.conf");
			return false;
		}

		{
			// verify that we ended up with a good server path, either from the file or from an argument.
			File file = new File(serverPath);
			if (!file.exists() || !file.isDirectory()) {
				System.err.println("The server directory is invalid: " + serverPath);
				return false;
			}
		}

		try {
			// read the name of the current world from the server.properties file
			BufferedReader props = new BufferedReader(new FileReader(new File(serverPath + separator + "server.properties")));
			String line;
			while ((line = props.readLine()) != null) {
				int pos = line.indexOf('=');
				if (pos != -1) {
					if (line.substring(0, pos).toLowerCase().equals("level-name")) {
						worldPath = serverPath + separator + line.substring(pos + 1);
					}
				}
			}

		} catch (FileNotFoundException ex) {
			System.err.println("Could not open " + serverPath + separator + "server.properties");
			return false;
		} catch (IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}

		File level = new File(worldPath + separator + "level.dat");
		if (!level.exists() || !level.isFile()) {
			System.err.println("The currently-configured world does not exist. Please launch the server once, first.");
			return false;
		}

		try {
			Integer[] spawn = getSpawn(level);
			System.out.println("The current spawn point is: [" + spawn[0] + ", " + spawn[2] + "]");
			return true;
		} catch (IOException ex) {
			System.err.println("Error while reading " + level.getPath());
			return false;
		}
	}
}
