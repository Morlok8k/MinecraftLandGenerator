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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joml.Vector3i;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author morlok8k
 */
public class FileRead {

	/**
	 * @param file
	 * @return
	 */

	private static Log log = LogFactory.getLog(Main.class);

	public static ArrayList<Vector3i> readArrayListCoordLog(final String file) {


		final ArrayList<Vector3i> Return = new ArrayList<>();

		try {
			final BufferedReader in = new BufferedReader(new FileReader(new File(file)));
			String line = "";

			while ((line = in.readLine()) != null) {

				line = line.trim();

				int end = line.indexOf('#'); // comments, ignored lines
				boolean ignoreLine = false;
				Vector3i c;

				if (end == -1) { // If we have no hash sign, then we read till the end of the line
					end = line.length();
				}

				if (end == 0) {    //hash is first char, meaning entire line is a comment
					ignoreLine = true;
				}

				if (!(ignoreLine)) {
					c = parseString(line.substring(0, end));
					Return.add(c);
				} else {
					if (line.startsWith("##Size:")) {                // Potential Resume data.
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
			log.info("Could not find " + file + ".");
			return Return;
		} catch (final IOException ex) {
			log.error("Could not read " + file + ".");
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

				if (end == 0) {    //hash is first char, meaning entire line is a comment
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
				log.debug("Test Output: Reading of Config File ");
				log.debug("    serverPath: " + var.serverPath);
				log.debug("      javaLine: " + var.javaLine);
				log.debug("      doneText: " + var.doneText);
				log.debug(" preparingText: " + var.preparingText);
				log.debug("preparingLevel: " + var.preparingLevel);
				log.debug("       level_0: " + var.level_0);
				log.debug("       level_1: " + var.level_1);
				log.debug("       level_2: " + var.level_2);
				log.debug("       level_3: " + var.level_3);
				log.debug("       level_4: " + var.level_4);
				log.debug("       level_5: " + var.level_5);
				log.debug("       level_6: " + var.level_6);
				log.debug("       level_7: " + var.level_7);
				log.debug("       level_8: " + var.level_8);
				log.debug("       level_9: " + var.level_9);
				log.debug("      waitSave: " + var.waitSave);
				log.debug("     webLaunch: " + var.webLaunch);
			}
		} catch (final FileNotFoundException ex) {
			log.error("Could not find "
					+ var.MinecraftLandGeneratorConf
					+ ". It is recommended that you run the application with the -conf option to create it.");
			return;
		} catch (final IOException ex) {
			log.error("Could not read " + var.MinecraftLandGeneratorConf + ".");
			return;
		}
	}

	public static Vector3i parseString(String StringOfCoords) {
		StringOfCoords = StringOfCoords.trim();

		int start = StringOfCoords.indexOf("[");
		int end = StringOfCoords.indexOf("]");

		String[] coordlong = StringOfCoords.substring(start, end).split(",");
		if ((start == -1) || (end == -1)) {

			start = StringOfCoords.indexOf("(");
			end = StringOfCoords.indexOf(")");
			String[] coordshort = StringOfCoords.substring(start, end).split(",");
			if ((start != -1) && (end != -1)) {
				return new Vector3i(Integer.valueOf(coordshort[0]), 64, Integer.valueOf(coordshort[2]));
			} else {
				return new Vector3i(0, 0, 0);
			}
		} else {

			return new Vector3i(Integer.valueOf(coordlong[0]), Integer.valueOf(coordlong[1]), Integer.valueOf(coordlong[2]));
		}
	}
}
