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
import java.nio.Buffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author morlok8k
 */

//TODO : Remove var.worldPath entirely because storing that is useless.

public class Setup {

    static boolean doSetup() {
        // Declare your stuff at the beginning, yo.
        final File serverPathFile;
        final BufferedReader serverPropertiesFile;
        final File levelDat;
        final File backupLevel;
        String line = null;


//---------- Verify server path
        serverPathFile = new File(var.serverPath);

        if (!serverPathFile.exists() || !serverPathFile.isDirectory()) {
			/*FileNotFoundException fileException =
					new FileNotFoundException("The server directory is invalid: " + var.serverPath);
			throw fileException;*/
            Out.err("The server directory is invalid: " + var.serverPath);
            return true;
        }


//---------- Verify server.properties
        try {
            serverPropertiesFile = new BufferedReader(new FileReader(new File(var.serverPath + var.fileSeparator
                    + "server.properties")));
        } catch (IOException e) {
            Out.err("Could not open the server.properties file.");
            return true;
        }


//---------- Set world name
        try {
            line = serverPropertiesFile.readLine();
        }
        catch (IOException e) {
        }

        while (line != null) {
            if (line.contains("level-name")) { // Yep, that line contains it
                if (line.contains("#")) { // There is a comment somewhere on the line
                    if (line.indexOf('#') > line.indexOf("level-name")) { // The comment isn't before level-name
                        if (!(line.indexOf('#') > line.indexOf('='))) { // In case only the name is commented out
                            var.worldName = line.substring(line.indexOf('=') + 1, line.indexOf('#')); // Read world name until start of comment
                            var.worldPath = var.serverPath + var.fileSeparator + var.worldName;
                        }
                    }
                } else { // There is no comment on the line
                    var.worldName = line.substring(line.indexOf('=') + 1, line.length());
                    var.worldPath = var.serverPath + var.fileSeparator + var.worldName;
                }
            }
            try {
                line = serverPropertiesFile.readLine();
            }
            catch (IOException e) {
            }

        }
        if (var.worldName == null) { // If after all this we still don't have a proper world name, stop everything and throw an exception
			/*NullPointerException noNameException = new NullPointerException("There is no world name defined in the server.properties file!");
			throw noNameException;*/
            Out.err("There is no world name defined in the server.properties file!");
            return true;
        }

//---------- Verify that the world exists and restore level_backup.dat if it exists. If not, start server once to create the world.
        levelDat = new File(var.worldPath + var.fileSeparator + "level.dat");
        backupLevel = new File(var.worldPath + var.fileSeparator + "level_backup.dat");

        // prepare our two ProcessBuilders
        // minecraft = new ProcessBuilder(javaLine, "-Xms1024m", "-Xmx1024m", "-jar", jarFile, "nogui");
        var.minecraft = new ProcessBuilder(var.javaLine.split("\\s")); // is this always going to work? i don't know.	(most likely yes)
        var.minecraft.directory(new File(var.serverPath));
        var.minecraft.redirectErrorStream(true);

        if (levelDat.exists() && levelDat.isFile()) {
            if (backupLevel.exists()) {
                Out.err("There is a level_backup.dat file left over from a previous attempt that failed.");
                Out.out("Resuming...");

                //use resume data
                final File serverLevel = new File(var.worldPath + var.fileSeparator + "level.dat");
                try {
                    Misc.copyFile(backupLevel, serverLevel);
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                backupLevel.delete();

                //return;

                FileRead.readArrayListCoordLog(var.worldPath + var.fileSeparator + var.logFile);        // we read the .log just for any resume data, if any.

                System.gc();        //run the garbage collector - hopefully free up some memory!

                var.xRange = var.resumeX;
                var.zRange = var.resumeZ;

            }
        } else {
          /*FileNotFoundException fileException =
                new FileNotFoundException("The currently configured world does not exist.");*/
            Out.err("The currently configured world does not exist! Launching the server once to create it...");
            try {
                var.minecraft = new ProcessBuilder(var.javaLine.split("\\s")); // is this always going to work? i don't know.	(most likely yes)
                var.minecraft.directory(new File(var.serverPath));
                var.minecraft.redirectErrorStream(true);
                if (!(Server.runMinecraft())) {
                    Out.err("Huh oh! Something went wrong with the server! Exiting...");
                    System.exit(1);                // we got a warning or severe error
                }
            }
            catch (IOException e){}
            Out.err("World created! Starting world generation...");
        }
        return false;
    }
}
