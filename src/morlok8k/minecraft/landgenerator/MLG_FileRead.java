package morlok8k.minecraft.landgenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import corrodias.minecraft.landgenerator.Main;

public class MLG_FileRead {

	public static ArrayList<Coordinates> readArrayListCoordLog(String file) {

		ArrayList<Coordinates> Return = new ArrayList<Coordinates>();

		try {
			BufferedReader in = new BufferedReader(new FileReader(new File(file)));
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

						Main.resumeX = Integer.parseInt(line.substring(xx + 1, zz));
						Main.resumeZ = Integer.parseInt(line.substring(zz + 1));

					}
				}

			}
			in.close();

		} catch (FileNotFoundException ex) {
			Main.out("Could not find " + file + ".");
			return Return;
		} catch (IOException ex) {
			Main.err("Could not read " + file + ".");
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
			File config = new File(Main.MinecraftLandGeneratorConf);
			BufferedReader in = new BufferedReader(new FileReader(config));
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
						Main.serverPath = value;
					} else if (property.equals("java")) {
						Main.javaLine = value;
					} else if (property.equals("done_text")) {
						Main.doneText = value;
					} else if (property.equals("preparing_text")) {
						Main.preparingText = value;
					} else if (property.equals("preparing_level")) {
						Main.preparingLevel = value;
					} else if (property.equals("level-0")) {
						Main.level_0 = value;
					} else if (property.equals("level-1")) {
						Main.level_1 = value;
					} else if (property.equals("level-2")) {
						Main.level_2 = value;
					} else if (property.equals("level-3")) {
						Main.level_3 = value;
					} else if (property.equals("level-4")) {
						Main.level_4 = value;
					} else if (property.equals("level-5")) {
						Main.level_5 = value;
					} else if (property.equals("level-6")) {
						Main.level_6 = value;
					} else if (property.equals("level-7")) {
						Main.level_7 = value;
					} else if (property.equals("level-8")) {
						Main.level_8 = value;
					} else if (property.equals("level-9")) {
						Main.level_9 = value;
					} else if (property.equals("waitsave")) {
						if (value.toLowerCase().equals("true")) {
							Main.waitSave = true;
						} else {
							Main.waitSave = false;
						}
					} else if (property.equals("weblaunch")) {
						if (value.toLowerCase().equals("true")) {
							Main.webLaunch = true;
						} else {
							Main.webLaunch = false;
						}
					}
				}
			}
			in.close();

			if (Main.testing) {
				Main.outD("Test Output: Reading of Config File ");
				Main.outD("    serverPath: " + Main.serverPath);
				Main.outD("      javaLine: " + Main.javaLine);
				Main.outD("      doneText: " + Main.doneText);
				Main.outD(" preparingText: " + Main.preparingText);
				Main.outD("preparingLevel: " + Main.preparingLevel);
				Main.outD("       level_0: " + Main.level_0);
				Main.outD("       level_1: " + Main.level_1);
				Main.outD("       level_2: " + Main.level_2);
				Main.outD("       level_3: " + Main.level_3);
				Main.outD("       level_4: " + Main.level_4);
				Main.outD("       level_5: " + Main.level_5);
				Main.outD("       level_6: " + Main.level_6);
				Main.outD("       level_7: " + Main.level_7);
				Main.outD("       level_8: " + Main.level_8);
				Main.outD("       level_9: " + Main.level_9);
				Main.outD("      waitSave: " + Main.waitSave);
				Main.outD("     webLaunch: " + Main.webLaunch);
			}
		} catch (FileNotFoundException ex) {
			Main.out("Could not find "
					+ Main.MinecraftLandGeneratorConf
					+ ". It is recommended that you run the application with the -conf option to create it.");
			return;
		} catch (IOException ex) {
			Main.err("Could not read " + Main.MinecraftLandGeneratorConf + ".");
			return;
		}
	}

}
