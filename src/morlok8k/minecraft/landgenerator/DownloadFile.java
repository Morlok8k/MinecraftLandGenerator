package morlok8k.minecraft.landgenerator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.bert_bos.UTF8URL.Unescape;

import corrodias.minecraft.landgenerator.Main;

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
	public static boolean downloadFile(String URL, boolean Output) {

		boolean success = true;

		String fileName = URL.substring(URL.lastIndexOf("/") + 1, URL.length());

		if (fileName.startsWith("\"")) {
			if (fileName.substring(fileName.length() - 1, fileName.length()) == "\"") {
				fileName = fileName.substring(1, fileName.length() - 1);
			}
		}

		int size = 1024 * 4; // 1024 * n should be tested to get the optimum size (for download speed.)

		if (fileName.equals("")) {
			fileName = String.valueOf(System.currentTimeMillis());
		}

		fileName = Unescape.unescape(fileName);

		if (Output) {
			Main.out("Downloading: " + URL);
			Main.out("Saving as: " + fileName);
		}

		long differenceTime = System.currentTimeMillis();
		Long[] timeTracking = new Long[] { differenceTime, differenceTime };
		timeTracking[0] = System.currentTimeMillis();

		if (Output) {
			Main.outP(Main.MLG + "*");
		}

		try {
			BufferedInputStream in;
			in = new BufferedInputStream(new URL(URL).openStream());
			FileOutputStream fos;
			fos = new FileOutputStream(fileName);
			BufferedOutputStream bout = new BufferedOutputStream(fos, size);
			byte[] data = new byte[size];
			int x = 0;
			int count = 0;
			while ((x = in.read(data, 0, size)) >= 0) {
				bout.write(data, 0, x);
				count = count + x;
				if (Output) {
					Main.outP("*");
				}
			}
			bout.close();
			in.close();
			if (Output) {
				Main.outP(Main.newLine);
				Main.out(count + " byte(s) copied");
			}

			timeTracking[1] = System.currentTimeMillis();
			//differenceTime = (timeTracking[1] - timeTracking[0]);
			if (Output) {
				Main.out("Elapsed Time: " + Main.displayTime(timeTracking[0], timeTracking[1]));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			success = false;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			success = false;
		} catch (IOException e) {
			e.printStackTrace();
			success = false;
		}
		if (Output) {
			Main.out("Done");
		}
		return success;
	}

}
