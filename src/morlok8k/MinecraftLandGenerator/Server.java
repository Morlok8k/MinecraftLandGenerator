package morlok8k.MinecraftLandGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Server {

	/**
	 * Starts the process in the given ProcessBuilder, monitors its output for a "[INFO] Done!" message, and sends it a "stop\r\n" message. One message is printed to the console before launching and
	 * one is printed to the console when the Done! message is detected. If "verbose" is true, the process's output will also be printed to the console.
	 * 
	 * @param minecraft
	 * 
	 * @throws IOException
	 * @author Corrodias
	 */
	protected static boolean runMinecraft(final boolean alternate) throws IOException {
		if (var.verbose) {
			Main.out("Starting server.");
		}
		boolean serverSuccess = true;
		boolean warning = false;
		boolean warningsWeCanIgnore = false;
		final boolean ignoreWarningsOriginal = var.ignoreWarnings;

		// monitor output and print to console where required.
		// STOP the server when it's done.

		if (alternate) { // Alternate - a replication (slightly stripped down) of MLG 1.3.0's code. simplest code possible.
			Main.out("Alternate Launch");
			final Process process = var.minecraft.start();

			//byte[] saveAll = { 's', 'a', 'v', 'e', '-', 'a', 'l', 'l', '\r', '\n' };
			final byte[] stop = { 's', 't', 'o', 'p', '\r', '\n' };

			// monitor output and print to console where required.
			// STOP the server when it's done.
			final BufferedReader pOut =
					new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = pOut.readLine()) != null) {		// readLine() returns null when the process exits

				line = line.trim();

				System.out.println(line);
				if (line.contains(var.doneText)) { // EDITED By Morlok8k for Minecraft 1.3+ Beta
					final OutputStream outputStream = process.getOutputStream();

					Main.out("Stopping server...  (Please Wait...)");
					outputStream.write(stop);
					outputStream.flush();

				}
			}
			// End while loop

		} else { // start minecraft server normally!
			final Process process = var.minecraft.start();
			if (var.verbose) {
				Main.out("Started Server.");
			}
			final BufferedReader pOut =
					new BufferedReader(new InputStreamReader(process.getInputStream()));
			if (var.verbose) {
				Main.out("Accessing Server Output...");
			}

			String line = null;
			String shortLine = null;
			String outTmp = "";
			String outTmp2 = null;

			final byte[] stop = { 's', 't', 'o', 'p', '\r', '\n' }; // Moved here, so this code wont run every loop, thus Faster!
			// and no, i can't use a string here!

			final byte[] saveAll = { 's', 'a', 'v', 'e', '-', 'a', 'l', 'l', '\r', '\n' };

			boolean prepTextFirst = true;

			final OutputStream outputStream = process.getOutputStream(); // moved here to remove some redundancy

			boolean convertedMapFormattingFlag = false;		// This allows MLG to track if we converted a map to a new format (such as Chunk-file -> McRegion, or McRegion -> Anvil)
			// just so it gets a line ending after the % output finishes
			while ((line = pOut.readLine()) != null) {			// readLine() returns null when the process exits

				line = line.trim();

				final int posBracket = line.indexOf("]");			//changed from .lastIndexOf to .indexOf, in case we have a custom server that outputs something with an "]".  we want the first one anyways.
				if (posBracket != -1) {
					shortLine = line.substring(posBracket + 2);
					shortLine = shortLine.trim();
				} else {
					shortLine = line;
				}

				if (var.verbose) {
					Main.outS(shortLine);
					//} else if (line.toLowerCase().contains("saving")) {		//this was just clutter
					//	Main.outS(shortLine);
				} else if (line.contains(var.preparingText) || line.contains("Converting...")) {
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
						Main.outP(var.MLG + outTmp + "...");
						prepTextFirst = false;
					} else {
						//Main.outP(" " + outTmp + "...");
						Main.outP("\r" + var.MLG + outTmp + "...");		//here we use \r to go back to the previous line, and rewrite it
					}

					//}

				} else if (line.contains(var.preparingLevel)) {
					prepTextFirst = true;

					if (convertedMapFormattingFlag == true) {
						Main.outP(var.newLine);
						convertedMapFormattingFlag = false;
					}

					if (line.contains("level 0")) { // "Preparing start region for level 0"
						Main.outP(var.MLG + var.worldName + ": " + var.level_0 + ":"
								+ var.newLine);
					} else if (line.contains("level 1")) { // "Preparing start region for level 1"
						Main.outP(var.newLine + var.MLG + var.worldName + ": " + var.level_1
								+ ":" + var.newLine);
					} else if (line.contains("level 2")) { // "Preparing start region for level 2"
						Main.outP(var.newLine + var.MLG + var.worldName + ": " + var.level_2
								+ ":" + var.newLine);
					} else if (line.contains("level 3")) { // "Preparing start region for level 3"
						Main.outP(var.newLine + var.MLG + var.worldName + ": " + var.level_3
								+ ":" + var.newLine);
					} else if (line.contains("level 4")) { // "Preparing start region for level 4"
						Main.outP(var.newLine + var.MLG + var.worldName + ": " + var.level_4
								+ ":" + var.newLine);
					} else if (line.contains("level 5")) { // "Preparing start region for level 5"
						Main.outP(var.newLine + var.MLG + var.worldName + ": " + var.level_5
								+ ":" + var.newLine);
					} else if (line.contains("level 6")) { // "Preparing start region for level 6"
						Main.outP(var.newLine + var.MLG + var.worldName + ": " + var.level_6
								+ ":" + var.newLine);
					} else if (line.contains("level 7")) { // "Preparing start region for level 7"
						Main.outP(var.newLine + var.MLG + var.worldName + ": " + var.level_7
								+ ":" + var.newLine);
					} else if (line.contains("level 8")) { // "Preparing start region for level 8"
						Main.outP(var.newLine + var.MLG + var.worldName + ": " + var.level_8
								+ ":" + var.newLine);
					} else if (line.contains("level 9")) { // "Preparing start region for level 9"
						Main.outP(var.newLine + var.MLG + var.worldName + ": " + var.level_9
								+ ":" + var.newLine);
					} else {
						Main.outP(var.newLine + var.MLG + shortLine);
					}
				} else if (line.contains("server version") || line.contains("Converting map!")) {	//TODO: add to .conf
					Main.outS(shortLine);

					if (line.contains("server version") && var.MC_Server_Version.isEmpty()) {
						// if server version, save string to variable, for use in arraylist save file.
						var.MC_Server_Version = shortLine;
					}

				}

				if (line.contains(var.doneText)) { // now this is configurable!

					Main.outP(var.newLine);
					Main.outS(line.substring(line.lastIndexOf("]") + 2, line.indexOf("!")));
					if (var.waitSave) {
						Main.out("Waiting 30 seconds to save...");

						int count = 1;
						while (count <= 30) {
							Main.outP(".");

							try {
								Thread.sleep(1000);
							} catch (final InterruptedException e) {
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

					if (var.waitSave) {
						Main.out("Waiting 10 seconds to save.");
						int count = 1;
						while (count <= 10) {
							Main.outP(".");

							try {
								Thread.sleep(1000);
							} catch (final InterruptedException e) {
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
					var.ignoreWarnings = true;
				} else if (line.contains("[WARNING] To start the server with more ram")) {
					if (var.verbose == false) { // If verbose is true, we already displayed it.
						Main.outS(line);
					}
					warningsWeCanIgnore = true;			//we can safely ignore this...
					var.ignoreWarnings = true;
				} else if (line.contains("Error occurred during initialization of VM")
						|| line.contains("Could not reserve enough space for object heap")) {
					if (var.verbose == false) { // If verbose is true, we already displayed it.
						Main.outP("[Java Error] " + line);
					}
					warning = true;
				}

				if (var.ignoreWarnings == false) {
					if (line.contains("[WARNING]")) { // If we have a warning, stop...
						Main.out("");
						Main.out("Warning found: Stopping " + var.PROG_NAME);
						if (var.verbose == false) { // If verbose is true, we already displayed it.
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
						if (var.verbose == false) { // If verbose is true, we already displayed it.
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
					var.ignoreWarnings = ignoreWarningsOriginal;
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
