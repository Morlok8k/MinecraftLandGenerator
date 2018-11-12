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

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author morlok8k
 */
public class DownloadFile {

	private static Log log = LogFactory.getLog(Main.class);

	/**
	 * Downloads a File using a URL in a String.
	 * 
	 * @param url
	 *            The URL of the file to download
	 * @param Output
	 *            Deprecated, does nothing
	 * @return true if download was successful, false if not
	 * @author piegames
	 */
	public static boolean downloadFile(final String url, final boolean Output) {
		try {
			URL download = new URL(url);
			File dest = new File(FilenameUtils.getName(download.getPath()));
			log.info("Downloading " + url + " to " + dest);
			FileUtils.copyURLToFile(download, dest);
		} catch (IOException e) {
			log.warn("Could not download " + url, e);
			return false;
		}
		return true;
	}
}
