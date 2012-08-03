package morlok8k.minecraft.landgenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import corrodias.minecraft.landgenerator.Main;

/**
 * http://www.roseindia.net/java/example/java/io/java-append-to-file.shtml <br>
 * Append To File - Java Tutorial
 */
public class MLG_FileWrite {

	public static final String newLine = Main.newLine;

	/**
	 * @param file
	 * @param appendTxt
	 */
	public static void AppendTxtFile(String file, String appendTxt) {
		try {
			// Create file 
			FileWriter fstream = new FileWriter(file, true);
			BufferedWriter out = new BufferedWriter(fstream);
			//String output = "Hello Java" + newLine;
			out.write(appendTxt);
			//Close the output stream
			out.close();
		} catch (Exception e) {//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * @param file
	 * @param txt
	 */
	public static void writeTxtFile(String file, String txt) {
		//TODO: element comment

		/*
		 * NOTE: I don't include a generic readTxtFile method, as that code depends on what I'm reading.
		 * For things like that I make a special method for it, if its used in more than one place.
		 * Like reading the config file.
		 */

		try {
			File oFile = new File(file);
			BufferedWriter outFile = new BufferedWriter(new FileWriter(oFile));
			outFile.write(txt);
			outFile.newLine();
			outFile.close();
			Main.out(file + " file created.");
			return;
		} catch (IOException ex) {
			Main.err("Could not create " + Main.MinecraftLandGeneratorConf + ".");
			ex.printStackTrace();
			return;
		}

	}

	/**
	 * Generates a Config File.
	 * 
	 * @param newConf
	 *            true: Uses Default values. false: uses existing values
	 * @author Morlok8k
	 */
	public static void saveConf(boolean newConf) {

		String jL = null;			//javaLine
		String sP = null;			//serverPath

		if (newConf) {
			jL = Main.defaultJavaLine;	// reads the default from a constant, makes it easier!
			sP = ".";				// 
		} else {
			jL = Main.javaLine;			// we read these values from an existing Conf File.
			sP = Main.serverPath;		//
		}

		String txt = null;
		//@formatter:off
		txt = "#" + Main.PROG_NAME + " Configuration File:  Version: " + Main.VERSION + Main.newLine
					+ "#Authors: " + Main.AUTHORS + Main.newLine
					+ "#Auto-Generated: " + Main.dateFormat.format(Main.date) + Main.newLine
					+ Main.newLine
					+ "#Line to run server:" + Main.newLine
					+ "Java=" + jL // reads the default from a constant, makes it easier!
					+ Main.newLine 
					+ Main.newLine
					+ "#Location of server.  use \".\" for the same folder as MLG" + Main.newLine
					+ "ServerPath=" + sP 
					+ Main.newLine 
					+ Main.newLine 
					+ "#Strings read from the server" + Main.newLine 
					+ "Done_Text=[INFO] Done" + Main.newLine
					+ "Preparing_Text=[INFO] Preparing spawn area:" + Main.newLine
					+ "Preparing_Level=[INFO] Preparing start region for" + Main.newLine
					+ "Level-0=The Overworld" + Main.newLine
					+ "Level-1=The Nether" + Main.newLine
					+ "Level-2=The End" + Main.newLine 
					+ "Level-3=Level 3 (Future Level)" + Main.newLine
					+ "Level-4=Level 4 (Future Level)" + Main.newLine 
					+ "Level-5=Level 5 (Future Level)" + Main.newLine 
					+ "Level-6=Level 6 (Future Level)" + Main.newLine
					+ "Level-7=Level 7 (Future Level)" + Main.newLine 
					+ "Level-8=Level 8 (Future Level)" + Main.newLine 
					+ "Level-9=Level 9 (Future Level)" + Main.newLine 
					+ Main.newLine
					+ "#Optional: Wait a few seconds after saving." + Main.newLine
					+ "WaitSave=false" + Main.newLine
					+ "webLaunch=true";
			//@formatter:on

		writeTxtFile(Main.MinecraftLandGeneratorConf, txt);

		return;

	}

}
