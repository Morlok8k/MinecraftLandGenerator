package morlok8k.MinecraftLandGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Misc {

	//TODO: add description
	/**
	 * @return
	 */
	static boolean printSpawn() {
		// ugh, sorry, this is an ugly hack, but it's a last-minute feature. this is a lot of duplicated code.
		// - Fixed by Morlok8k

		FileRead.readConf();
		WorldVerify.verifyWorld();

		final File level = new File(Main.worldPath + Main.fileSeparator + "level.dat");
		try {
			final Coordinates spawn = SpawnPoint.getSpawn(level);
			Main.out("The current spawn point is: [X,Y,Z] " + spawn);
			return true;
		} catch (final IOException ex) {
			Main.err("Error while reading " + level.getPath());
			return false;
		}
	}

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

}
