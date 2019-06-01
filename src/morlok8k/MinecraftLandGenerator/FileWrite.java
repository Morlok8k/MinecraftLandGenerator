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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 
 * @author morlok8k
 */
public class FileWrite {

	/**
	 * http://www.roseindia.net/java/example/java/io/java-append-to-file.shtml <br>
	 * Append To File - Java Tutorial
	 * 
	 * @param file
	 * @param appendTxt
	 */
	public static void AppendTxtFile(final String file, final String appendTxt) {
		try {
			// Create file
			final FileWriter fstream = new FileWriter(file, true);
			final BufferedWriter out = new BufferedWriter(fstream);

			out.write(appendTxt);
			//Close the output stream
			out.close();
		} catch (final Exception e) {//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	/**
	 * Generates a Config File.
	 * 
	 * @param newConf
	 *            true: Uses Default values. false: uses existing values
	 * @author Morlok8k
	 */
	public static void saveConf(final boolean newConf) {

		String jL = null;			//javaLine
		String sP = null;			//serverPath

		if (newConf) {
			jL = var.defaultJavaLine;	// reads the default from a constant, makes it easier!
			sP = ".";				//
		} else {
			jL = var.javaLine;			// we read these values from an existing Conf File.
			sP = var.serverPath;		//
		}

		String txt = null;
		//@formatter:off
        txt = "#" + var.PROG_NAME + " Configuration File:  Version: " + var.VERSION + var.newLine
                + "#Authors: " + var.AUTHORS + var.newLine
                + "#Auto-Generated: " + var.dateFormat.format(var.date) + var.newLine
                + var.newLine
                + "#Line to run server:" + var.newLine
                + "Java=" + jL // reads the default from a constant, makes it easier!
                + var.newLine
                + var.newLine
                + "#Location of server.  use \".\" for the same folder as MLG" + var.newLine
                + "ServerPath=" + sP
                + var.newLine
                + var.newLine
                + "#Strings read from the server" + var.newLine
                + "Done_Text=Done" + var.newLine
                + "Preparing_Text=Preparing spawn area:" + var.newLine
                + "Preparing_Level=Preparing start region for" + var.newLine
                + "Level-0=Overworld" + var.newLine
                + "Level-1=Nether" + var.newLine
                + "Level-2=The End" + var.newLine
                + "Level-3=Level 3 (Future Level)" + var.newLine
                + "Level-4=Level 4 (Future Level)" + var.newLine
                + "Level-5=Level 5 (Future Level)" + var.newLine
                + "Level-6=Level 6 (Future Level)" + var.newLine
                + "Level-7=Level 7 (Future Level)" + var.newLine
                + "Level-8=Level 8 (Future Level)" + var.newLine
                + "Level-9=Level 9 (Future Level)" + var.newLine
                + var.newLine
                + "#Optional: Wait a few seconds after saving." + var.newLine
                + "WaitSave=false" + var.newLine
				+ "#Optional: Wait a few seconds after server shutdown for OS memory housekeeping."
				+ "postShutdownDelay=false"
                + "webLaunch=true";
        //@formatter:on

		writeTxtFile(var.MinecraftLandGeneratorConf, txt);

		return;

	}

	/**
	 * @param file
	 * @param txt
	 */
	public static void writeTxtFile(final String file, final String txt) {
		//TODO: element comment

		/*
		 * NOTE: I don't include a generic readTxtFile method, as that code depends on what I'm reading.
		 * For things like that I make a special method for it, if its used in more than one place.
		 * Like reading the config file.
		 */

		try {
			final File oFile = new File(file);
			final BufferedWriter outFile = new BufferedWriter(new FileWriter(oFile));
			outFile.write(txt);
			outFile.newLine();
			outFile.close();
			Out.out(file + " file created.");
			return;
		} catch (final IOException ex) {
			Out.err("Could not create " + var.MinecraftLandGeneratorConf + ".");
			ex.printStackTrace();
			return;
		}

	}
}
