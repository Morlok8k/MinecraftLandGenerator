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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * @author morlok8k
 */
public class Misc {
	private static Log log = LogFactory.getLog(Main.class);

	/**
	 * I'd love to use nio, but it requires Java 7.<br>
	 * I could use Apache Commons, but i don't want to include a library for one little thing.<br>
	 * Copies src file to dst file.<br>
	 * If the dst file does not exist, it is created<br>
	 * 
	 * @author Corrodias
	 * @param src
	 * @param dst
	 * @throws IOException
	 */


	// TODO replace with Java Copy Path
	public static void copyFile(final File src, final File dst) throws IOException {
		final InputStream copyIn = new FileInputStream(src);
		final OutputStream copyOut = new FileOutputStream(dst);

		// Transfer bytes from in to out
		final byte[] buf = new byte[1024];
		int len;
		while ((len = copyIn.read(buf)) >= 0) {
			if (len > 0) {
				copyOut.write(buf, 0, len);
			}
		}
		copyIn.close();
		copyOut.flush();
		copyOut.close();
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
			final Coordinates spawn = SpawnPoint.getSpawn(level);
			log.info("The current spawn point is: [X,Y,Z] " + spawn);
			return true;
		} catch (final IOException ex) {
			log.error("Error while reading " + level.getPath());
			return false;
		}
	}
}
