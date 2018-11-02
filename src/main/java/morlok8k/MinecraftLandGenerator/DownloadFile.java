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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.bert_bos.UTF8URL.Unescape;

/**
 * @author morlok8k
 */
public class DownloadFile {

	private static Log log = LogFactory.getLog(Main.class);
	/**
	 * Downloads a File using a URL in a String.<br>
	 * (If the file is a dynamic URL (Not like "http://example.com/file.txt") and it can't get the filename, it saves it as <i>"System.currentTimeMillis();"</i>) <br>
	 * <br>
	 * Thanks to bs123 at <br>
	 * <a href="http://www.daniweb.com/software-development/java/threads/84370"> http://www.daniweb.com/software-development/java/threads/84370</a>
	 *
	 * @param URL    URL in a String
	 * @param Output Displays output if true
	 * @return Boolean: true if download was successful, false if download wasn't
	 * @author Morlok8k
	 */
	public static boolean downloadFile(final String URL, final boolean Output) {

		String fileName = URL.substring(URL.lastIndexOf("/") + 1, URL.length());

		if (fileName.startsWith("\"")) {
			if (fileName.substring(fileName.length() - 1, fileName.length()) == "\"") {
				fileName = fileName.substring(1, fileName.length() - 1);
			}
		}

		final int size = 1024 * 4; // 1024 * n should be tested to get the optimum size (for download speed.)

		if (fileName.equals("")) {
			fileName = String.valueOf(System.currentTimeMillis());
		}

		fileName = Unescape.unescape(fileName);

		if (Output) {
			log.info("Downloading: " + URL);
			log.info("Saving as: " + fileName);
		}

		final long differenceTime = System.currentTimeMillis();
		final Long[] timeTracking = new Long[]{differenceTime, differenceTime};
		timeTracking[0] = System.currentTimeMillis();

		if (Output) {
			log.info(var.MLG + "*");
		}

		try {
			BufferedInputStream in = new BufferedInputStream(new URL(URL).openStream());
			FileOutputStream fos = new FileOutputStream(fileName);
			final BufferedOutputStream bout = new BufferedOutputStream(fos, size);
			final byte[] data = new byte[size];
			int x = 0;
			int count = 0;
			while ((x = in.read(data, 0, size)) >= 0) {
				bout.write(data, 0, x);
				count = count + x;
				if (Output) {
					log.info("*");
				}
			}
			bout.close();
			in.close();
			if (Output) {
				log.info(var.newLine);
				log.info(count + " byte(s) copied");
			}

			timeTracking[1] = System.currentTimeMillis();
			long differenceTime1 = (timeTracking[1] - timeTracking[0]);
			if (Output) {
				log.info("Elapsed Time: " + String.format("%02d min:%02d sec", (differenceTime1 / 1000) / 60, (differenceTime1 / 1000) % 60));
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (final MalformedURLException e) {
			e.printStackTrace();
			return  false;
		} catch (final IOException e) {
			e.printStackTrace();
			return false;
		}
		if (Output) {
			log.info("Done");
		}
		return true;
	}
}
