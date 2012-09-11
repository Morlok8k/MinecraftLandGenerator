package morlok8k.MinecraftLandGenerator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.bert_bos.UTF8URL.Unescape;

public class DownloadFile {

	/**
	 * 
	 * Downloads a File using a URL in a String.<br>
	 * (If the file is a dynamic URL (Not like "http://example.com/file.txt") and it can't get the filename, it saves it as <i>"System.currentTimeMillis();"</i>) <br>
	 * <br>
	 * Thanks to bs123 at <br>
	 * <a href="http://www.daniweb.com/software-development/java/threads/84370"> http://www.daniweb.com/software-development/java/threads/84370</a>
	 * 
	 * @author Morlok8k
	 * @param URL
	 *            URL in a String
	 * @param Output
	 *            Displays output if true
	 * @return Boolean: true if download was successful, false if download wasn't
	 */
	public static boolean downloadFile(final String URL, final boolean Output) {

		boolean success = true;

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
			Out.out("Downloading: " + URL);
			Out.out("Saving as: " + fileName);
		}

		final long differenceTime = System.currentTimeMillis();
		final Long[] timeTracking = new Long[] { differenceTime, differenceTime };
		timeTracking[0] = System.currentTimeMillis();

		if (Output) {
			Out.outP(var.MLG + "*");
		}

		try {
			BufferedInputStream in;
			in = new BufferedInputStream(new URL(URL).openStream());
			FileOutputStream fos;
			fos = new FileOutputStream(fileName);
			final BufferedOutputStream bout = new BufferedOutputStream(fos, size);
			final byte[] data = new byte[size];
			int x = 0;
			int count = 0;
			while ((x = in.read(data, 0, size)) >= 0) {
				bout.write(data, 0, x);
				count = count + x;
				if (Output) {
					Out.outP("*");
				}
			}
			bout.close();
			in.close();
			if (Output) {
				Out.outP(var.newLine);
				Out.out(count + " byte(s) copied");
			}

			timeTracking[1] = System.currentTimeMillis();
			//differenceTime = (timeTracking[1] - timeTracking[0]);
			if (Output) {
				Out.out("Elapsed Time: " + Time.displayTime(timeTracking[0], timeTracking[1]));
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
			success = false;
		} catch (final MalformedURLException e) {
			e.printStackTrace();
			success = false;
		} catch (final IOException e) {
			e.printStackTrace();
			success = false;
		}
		if (Output) {
			Out.out("Done");
		}
		return success;
	}

}
