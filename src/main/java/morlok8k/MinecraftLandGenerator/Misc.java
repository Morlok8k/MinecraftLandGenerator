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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author morlok8k
 */
public class Misc {
	private static Log log = LogFactory.getLog(Main.class);

	// TODO replace with Java Copy Path
	@Deprecated
	public static void copyFile(final File src, final File dst) throws IOException {
		FileUtils.copyFile(src, dst);
	}

	//TODO: add description
	/**
	 * @return
	 */
	static boolean printSpawn() {
		// ugh, sorry, this is an ugly hack

		FileRead.readConf();
		try {
			Setup.doSetup();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final File level = new File(var.worldPath + var.fileSeparator + "level.dat");
		try {
			final Vector3i spawn = SpawnPoint.getSpawn(level);
			log.info("The current spawn point is: [X,Y,Z] " + spawn);
			return true;
		} catch (final IOException ex) {
			log.error("Error while reading " + level.getPath());
			return false;
		}
	}
}
