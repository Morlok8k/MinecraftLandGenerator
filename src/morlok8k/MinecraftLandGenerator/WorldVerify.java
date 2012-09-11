package morlok8k.MinecraftLandGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorldVerify {

	/**
	 * 
	 */
	static void verifyWorld() {
		//TODO: element comment

		// verify that we ended up with a good server path, either from the file or from an argument.
		final File file = new File(var.serverPath);
		if (!file.exists() || !file.isDirectory()) {
			Out.err("The server directory is invalid: " + var.serverPath);
			return;
		}

		try {
			// read the name of the current world from the server.properties file
			final BufferedReader props =
					new BufferedReader(new FileReader(new File(var.serverPath + var.fileSeparator
							+ "server.properties")));
			String line;
			while ((line = props.readLine()) != null) {
				String property = "";
				String value = "";

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
						value = line.substring(pos + 1);
					}

					if (property.equals("level-name")) {
						var.worldPath = var.serverPath + var.fileSeparator + value;
						var.worldName = value;
					}

				}
			}

			props.close();

		} catch (final FileNotFoundException ex) {
			Out.err("Could not open " + var.serverPath + var.fileSeparator + "server.properties");
			return;
		} catch (final IOException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			return;
		}

		final File level = new File(var.worldPath + var.fileSeparator + "level.dat");
		if (!level.exists() || !level.isFile()) {
			Out.err("The currently-configured world does not exist.");
			return;
		}

	}

}
