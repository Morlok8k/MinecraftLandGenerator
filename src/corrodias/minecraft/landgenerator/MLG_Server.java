package corrodias.minecraft.landgenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class MLG_Server {

	/**
	 * Starts the process in the given ProcessBuilder, monitors its output for a "[INFO] Done!" message, and sends it a "stop\r\n" message. One message is printed to the console before launching and
	 * one is printed to the console when the Done! message is detected. If "verbose" is true, the process's output will also be printed to the console.
	 * 
	 * @param minecraft
	 * 
	 * @throws IOException
	 * @author Corrodias
	 */
	protected static boolean runMinecraft(boolean alternate) throws IOException {
		if (Main.verbose) {
			Main.out("Starting server.");
		}
		boolean serverSuccess = true;
		boolean warning = false;
		boolean warningsWeCanIgnore = false;
		final boolean ignoreWarningsOriginal = Main.ignoreWarnings;

		// monitor output and print to console where required.
		// STOP the server when it's done.

		if (alternate) { // Alternate - a replication (slightly stripped down) of MLG 1.3.0's code. simplest code possible.
			Main.out("Alternate Launch");
			Process process = Main.minecraft.start();

			//byte[] saveAll = { 's', 'a', 'v', 'e', '-', 'a', 'l', 'l', '\r', '\n' };
			byte[] stop = { 's', 't', 'o', 'p', '\r', '\n' };

			// monitor output and print to console where required.
			// STOP the server when it's done.
			BufferedReader pOut =
					new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = pOut.readLine().trim()) != null) {		// readLine() returns null when the process exits

				System.out.println(line);
				if (line.contains(Main.doneText)) { // EDITED By Morlok8k for Minecraft 1.3+ Beta
					OutputStream outputStream = process.getOutputStream();

					Main.out("Stopping server...  (Please Wait...)");
					outputStream.write(stop);
					outputStream.flush();

				}
			}
			// End while loop

		} else { // start minecraft server normally!
			Process process = Main.minecraft.start();
			if (Main.verbose) {
				Main.out("Started Server.");
			}
			BufferedReader pOut =
					new BufferedReader(new InputStreamReader(process.getInputStream()));
			if (Main.verbose) {
				Main.out("Accessing Server Output...");
			}

			String line = null;
			String shortLine = null;
			String outTmp = "";
			String outTmp2 = null;

			byte[] stop = { 's', 't', 'o', 'p', '\r', '\n' }; // Moved here, so this code wont run every loop, thus Faster!
			// and no, i can't use a string here!

			byte[] saveAll = { 's', 'a', 'v', 'e', '-', 'a', 'l', 'l', '\r', '\n' };

			boolean prepTextFirst = true;

			OutputStream outputStream = process.getOutputStream(); // moved here to remove some redundancy

			boolean convertedMapFormattingFlag = false;		// This allows MLG to track if we converted a map to a new format (such as Chunk-file -> McRegion, or McRegion -> Anvil)
			// just so it gets a line ending after the % output finishes
			while ((line = pOut.readLine().trim()) != null) {			// readLine() returns null when the process exits

				int posBracket = line.indexOf("]");			//changed from .lastIndexOf to .indexOf, in case we have a custom server that outputs something with an "]".  we want the first one anyways.
				if (posBracket != -1) {
					shortLine = line.substring(posBracket + 2);
					shortLine = shortLine.trim();
				} else {
					shortLine = line;
				}

				if (Main.verbose) {
					Main.outS(shortLine);
					//} else if (line.toLowerCase().contains("saving")) {		//this was just clutter
					//	Main.outS(shortLine);
				} else if (line.contains(Main.preparingText) || line.contains("Converting...")) {
					if (line.contains("Converting...")) {
						convertedMapFormattingFlag = true;
					}
					outTmp2 = line.substring(line.length() - 3, line.length());
					outTmp2 = outTmp2.trim();				//we are removing extra spaces here

					//if (outTmp.equals(outTmp2)) {
					//instead of printing the same number, we add another dot
					//Main.outP(".");    
					//} else {
					outTmp = outTmp2;

					if (prepTextFirst) {
						Main.outP(Main.MLG + outTmp + "...");
						prepTextFirst = false;
					} else {
						//Main.outP(" " + outTmp + "...");
						Main.outP("\r" + Main.MLG + outTmp + "...");		//here we use \r to go back to the previous line, and rewrite it
					}

					//}

				} else if (line.contains(Main.preparingLevel)) {
					prepTextFirst = true;

					if (convertedMapFormattingFlag == true) {
						Main.outP(Main.newLine);
						convertedMapFormattingFlag = false;
					}

					if (line.contains("level 0")) { // "Preparing start region for level 0"
						Main.outP(Main.MLG + Main.worldName + ": " + Main.level_0 + ":"
								+ Main.newLine);
					} else if (line.contains("level 1")) { // "Preparing start region for level 1"
						Main.outP(Main.newLine + Main.MLG + Main.worldName + ": " + Main.level_1
								+ ":" + Main.newLine);
					} else if (line.contains("level 2")) { // "Preparing start region for level 2"
						Main.outP(Main.newLine + Main.MLG + Main.worldName + ": " + Main.level_2
								+ ":" + Main.newLine);
					} else if (line.contains("level 3")) { // "Preparing start region for level 3"
						Main.outP(Main.newLine + Main.MLG + Main.worldName + ": " + Main.level_3
								+ ":" + Main.newLine);
					} else if (line.contains("level 4")) { // "Preparing start region for level 4"
						Main.outP(Main.newLine + Main.MLG + Main.worldName + ": " + Main.level_4
								+ ":" + Main.newLine);
					} else if (line.contains("level 5")) { // "Preparing start region for level 5"
						Main.outP(Main.newLine + Main.MLG + Main.worldName + ": " + Main.level_5
								+ ":" + Main.newLine);
					} else if (line.contains("level 6")) { // "Preparing start region for level 6"
						Main.outP(Main.newLine + Main.MLG + Main.worldName + ": " + Main.level_6
								+ ":" + Main.newLine);
					} else if (line.contains("level 7")) { // "Preparing start region for level 7"
						Main.outP(Main.newLine + Main.MLG + Main.worldName + ": " + Main.level_7
								+ ":" + Main.newLine);
					} else if (line.contains("level 8")) { // "Preparing start region for level 8"
						Main.outP(Main.newLine + Main.MLG + Main.worldName + ": " + Main.level_8
								+ ":" + Main.newLine);
					} else if (line.contains("level 9")) { // "Preparing start region for level 9"
						Main.outP(Main.newLine + Main.MLG + Main.worldName + ": " + Main.level_9
								+ ":" + Main.newLine);
					} else {
						Main.outP(Main.newLine + Main.MLG + shortLine);
					}
				} else if (line.contains("server version") || line.contains("Converting map!")) {	//TODO: add to .conf
					Main.outS(shortLine);

					if (line.contains("server version") && Main.MC_Server_Version.isEmpty()) {
						// if server version, save string to variable, for use in arraylist save file.
						Main.MC_Server_Version = shortLine;
					}

				}

				if (line.contains(Main.doneText)) { // now this is configurable!

					Main.outP(Main.newLine);
					Main.outS(line.substring(line.lastIndexOf("]") + 2, line.indexOf("!")));
					if (Main.waitSave) {
						Main.out("Waiting 30 seconds to save...");

						int count = 1;
						while (count <= 30) {
							Main.outP(".");

							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							count += 1;
						}
						Main.out("");
					}
					Main.out("Saving server data...");
					outputStream.write(saveAll);
					outputStream.flush();

					Main.out("Stopping server...  (Please Wait...)");
					// OutputStream outputStream = process.getOutputStream();
					outputStream.write(stop);
					outputStream.flush();
					// outputStream.close();

					if (Main.waitSave) {
						Main.out("Waiting 10 seconds to save.");
						int count = 1;
						while (count <= 10) {
							Main.outP(".");

							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							count += 1;
						}
						Main.out("");
					}
				}

				//Here we want to ignore the most common warning: "Can't keep up!"
				if (line.contains("Can't keep up!")) {	//TODO: add to .conf
					warningsWeCanIgnore = true;			//[WARNING] Can't keep up! Did the system time change, or is the server overloaded?
					Main.ignoreWarnings = true;
				} else if (line.contains("[WARNING] To start the server with more ram")) {
					if (Main.verbose == false) { // If verbose is true, we already displayed it.
						Main.outS(line);
					}
					warningsWeCanIgnore = true;			//we can safely ignore this...
					Main.ignoreWarnings = true;
				} else if (line.contains("Error occurred during initialization of VM")
						|| line.contains("Could not reserve enough space for object heap")) {
					if (Main.verbose == false) { // If verbose is true, we already displayed it.
						Main.outP("[Java Error] " + line);
					}
					warning = true;
				}

				if (Main.ignoreWarnings == false) {
					if (line.contains("[WARNING]")) { // If we have a warning, stop...
						Main.out("");
						Main.out("Warning found: Stopping " + Main.PROG_NAME);
						if (Main.verbose == false) { // If verbose is true, we already displayed it.
							Main.outS(line);
						}
						Main.out("");
						Main.out("Forcing Save...");
						outputStream.write(saveAll);
						outputStream.flush();
						// OutputStream outputStream = process.getOutputStream();
						outputStream.write(stop); // if the warning was a fail to bind to port, we may need to write stop twice!
						outputStream.flush();
						outputStream.write(stop);
						outputStream.flush();
						// outputStream.close();
						warning = true;
						// System.exit(1);
					}
					if (line.contains("[SEVERE]")) { // If we have a severe error, stop...
						Main.out("");
						Main.out("Severe error found: Stopping server.");
						if (Main.verbose == false) { // If verbose is true, we already displayed it.
							Main.outS(line);
						}
						Main.out("");
						Main.out("Forcing Save...");
						outputStream.write(saveAll);
						outputStream.flush();
						// OutputStream outputStream = process.getOutputStream();
						outputStream.write(stop);
						outputStream.flush();
						outputStream.write(stop); // sometimes we need to do stop twice...
						outputStream.flush();
						// outputStream.close();
						warning = true;
						// System.exit(1);
						// Quit!
					}
				}

				if (warningsWeCanIgnore) {
					Main.ignoreWarnings = ignoreWarningsOriginal;
				}
			}

			if (warning == true) { // in 1.4.4 we had a issue. tried to write stop twice, but we had closed the stream already. this, and other lines should fix this.
				outputStream.flush();
				//outputStream.close();
				//System.exit(1);
				serverSuccess = false;
			}

			outputStream.close();
		}

		// while loop has finished now.
		return serverSuccess;
	}

}
