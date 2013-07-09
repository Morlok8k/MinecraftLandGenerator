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
import java.util.ArrayList;

/**
 * 
 * @author morlok8k
 */
public class FileRead {

	/**
	 * 
	 * @param file
	 * @return
	 */
	public static ArrayList<Coordinates> readArrayListCoordLog(final String file) {

		final ArrayList<Coordinates> Return = new ArrayList<Coordinates>();

		try {
			final BufferedReader in = new BufferedReader(new FileReader(new File(file)));
			String line = "";

			while ((line = in.readLine()) != null) {

				line = line.trim();

				int end = line.indexOf('#'); // comments, ignored lines
				boolean ignoreLine = false;
				Coordinates c = new Coordinates();

				if (end == -1) { // If we have no hash sign, then we read till the end of the line
					end = line.length();
				}

				if (end == 0) {	//hash is first char, meaning entire line is a comment
					ignoreLine = true;
				}

				if (!(ignoreLine)) {
					c = Coordinates.parseString(line.substring(0, end));
					Return.add(c);
				} else {
					if (line.startsWith("##Size:")) {				// Potential Resume data.
						int xx = 0;
						int zz = 0;

						xx = line.indexOf('X');
						zz = line.indexOf('Z');

						var.resumeX = Integer.parseInt(line.substring(xx + 1, zz));
						var.resumeZ = Integer.parseInt(line.substring(zz + 1));

					}
				}

			}
			in.close();

		} catch (final FileNotFoundException ex) {
			Out.out("Could not find " + file + ".");
			return Return;
		} catch (final IOException ex) {
			Out.err("Could not read " + file + ".");
			return Return;
		}

		return Return;
	}

	/**
     *
     */
	public static void readConf() {
		//TODO: element comment
		//String errorMsg = "";

		try {
			final File config = new File(var.MinecraftLandGeneratorConf);
			final BufferedReader in = new BufferedReader(new FileReader(config));
			String line = "";
			String property = "";
			String value = "";

			while ((line = in.readLine()) != null) {
				int pos = line.indexOf('=');

				int end = line.lastIndexOf('#'); // comments, ignored lines

				if (end == -1) { // If we have no hash sign, then we read till the end of the line
					end = line.length();

				}
				if (end <= (pos + 1)) { // If hash is before the '=', we may have an issue... it should be fine, cause we check for issues next, but lets make sure.
					end = line.length();
					pos = -1;
				}

				if (end == 0) {	//hash is first char, meaning entire line is a comment
					end = line.length();
					pos = 0;
				}

				if (pos != -1) {
					if (line.length() == 0) {
						property = "";
						value = "";
					} else {
						property = line.substring(0, pos).toLowerCase();
						value = line.substring(pos + 1, end);
					}

					if (property.equals("serverpath")) {
						var.serverPath = value;
					} else if (property.equals("java")) {
						var.javaLine = value;
					} else if (property.equals("done_text")) {
						var.doneText = value;
					} else if (property.equals("preparing_text")) {
						var.preparingText = value;
					} else if (property.equals("preparing_level")) {
						var.preparingLevel = value;
					} else if (property.equals("level-0")) {
						var.level_0 = value;
					} else if (property.equals("level-1")) {
						var.level_1 = value;
					} else if (property.equals("level-2")) {
						var.level_2 = value;
					} else if (property.equals("level-3")) {
						var.level_3 = value;
					} else if (property.equals("level-4")) {
						var.level_4 = value;
					} else if (property.equals("level-5")) {
						var.level_5 = value;
					} else if (property.equals("level-6")) {
						var.level_6 = value;
					} else if (property.equals("level-7")) {
						var.level_7 = value;
					} else if (property.equals("level-8")) {
						var.level_8 = value;
					} else if (property.equals("level-9")) {
						var.level_9 = value;
					} else if (property.equals("waitsave")) {
						if (value.toLowerCase().equals("true")) {
							var.waitSave = true;
						} else {
							var.waitSave = false;
						}
					} else if (property.equals("weblaunch")) {
						if (value.toLowerCase().equals("true")) {
							var.webLaunch = true;
						} else {
							var.webLaunch = false;
						}
					}
				}
			}
			in.close();

			if (var.testing) {
				Out.outD("Test Output: Reading of Config File ");
				Out.outD("    serverPath: " + var.serverPath);
				Out.outD("      javaLine: " + var.javaLine);
				Out.outD("      doneText: " + var.doneText);
				Out.outD(" preparingText: " + var.preparingText);
				Out.outD("preparingLevel: " + var.preparingLevel);
				Out.outD("       level_0: " + var.level_0);
				Out.outD("       level_1: " + var.level_1);
				Out.outD("       level_2: " + var.level_2);
				Out.outD("       level_3: " + var.level_3);
				Out.outD("       level_4: " + var.level_4);
				Out.outD("       level_5: " + var.level_5);
				Out.outD("       level_6: " + var.level_6);
				Out.outD("       level_7: " + var.level_7);
				Out.outD("       level_8: " + var.level_8);
				Out.outD("       level_9: " + var.level_9);
				Out.outD("      waitSave: " + var.waitSave);
				Out.outD("     webLaunch: " + var.webLaunch);
			}
		} catch (final FileNotFoundException ex) {
			Out.err("Could not find "
					+ var.MinecraftLandGeneratorConf
					+ ". It is recommended that you run the application with the -conf option to create it.");
			return;
		} catch (final IOException ex) {
			Out.err("Could not read " + var.MinecraftLandGeneratorConf + ".");
			return;
		}
	}
}
