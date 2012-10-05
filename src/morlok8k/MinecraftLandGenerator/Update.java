package morlok8k.MinecraftLandGenerator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 
 * @author morlok8k
 */
public class Update {

	/**
	 * This is an "undocumented" function to create a BuildID file. It should only be used right after compiling a .jar file<br>
	 * The resulting BuildID file is uploaded to github, and also distributed with the program.<br>
	 * <b>THE FILE SHOULD ONLY BE MADE FROM SCRATCH AT THE TIME OF PUBLISHING!</b><br>
	 * (Otherwise, the purpose is defeated!)<br>
	 * <br>
	 * The problem is that when a .jar file is downloaded from the internet, it gets a new date stamp - the time that it was downloaded. Only the original copy that was compiled on the original
	 * computer will have the correct time stamp. (or possibly a copy from the original computer)<br>
	 * <br>
	 * This saves the hash and the timestamp (now known as the BuildID)
	 * 
	 * @param downloadOnly
	 * @author Morlok8k
	 */
	public static void buildID(final boolean downloadOnly) {

		// download BuildID from Github.
		final boolean fileSuccess =
				DownloadFile.downloadFile(var.github_MLG_BuildID_URL, var.testing);
		if (fileSuccess) {
			Out.out(var.buildIDFile + " file downloaded.");
			var.flag_downloadedBuildID = true;

			if (downloadOnly) { return; }

		}

		if (downloadOnly) {
			Out.err("Couldn't Download new " + var.buildIDFile);
			return;
		}

		// If not available, create.
		// After downloading, check to see if it matches hash.

		if (var.MLGFileName == null) {
			try {
				var.MLGFileName = getClassLoader(var.cls);
			} catch (final Exception e) {
				Out.out("Error: Finding file failed");
				e.printStackTrace();
			}
			if (var.MLGFileName.equals(var.rsrcError)) { return; }
		}

		if (var.MLG_Current_Hash == null) {

			try {
				var.MLG_Current_Hash = MD5.fileMD5(var.MLGFileName);
				// out(hash + "  " + MLGFileName);
			} catch (final Exception e) {
				Out.out("Error: MD5 from file failed");
				e.printStackTrace();
			}
		}

		Date time = new Date(new Long(0));

		try {
			time = getCompileTimeStamp(var.cls);
		} catch (final Exception e) {
			Out.out("Error: TimeStamp from file failed");
			e.printStackTrace();
		}
		// out(d.toString());

		boolean notNew = false;
		String INFO = "";
		if (var.isCompiledAsJar == false) {
			INFO = " (Class File, Not .Jar)";
		}

		try {
			String line;

			final BufferedReader inFile = new BufferedReader(new FileReader(var.buildIDFile));
			final BufferedWriter outFile =
					new BufferedWriter(new FileWriter(var.buildIDFile + ".temp"));

			while ((line = inFile.readLine()) != null) {

				if (line.contains(var.MLG_Current_Hash)) {
					notNew = true;
					if (var.testing) {
						Out.outD("NotNew");
					}
				}

				outFile.write(line);
				outFile.newLine();
			}

			if (notNew == false) {
				outFile.write(var.MLG_Current_Hash + "=" + String.valueOf(time.getTime())
						+ "# MLG v" + var.VERSION + INFO);
				outFile.newLine();
			}
			outFile.close();
			inFile.close();

			final File fileDelete = new File(var.buildIDFile);
			fileDelete.delete();
			final File fileRename = new File(var.buildIDFile + ".temp");
			fileRename.renameTo(new File(var.buildIDFile));

		} catch (final FileNotFoundException ex) {
			Out.out("\"" + var.buildIDFile + "\" file not Found.  Generating New \""
					+ var.buildIDFile + "\" File");

			FileWrite.writeTxtFile(var.buildIDFile,
					var.MLG_Current_Hash + "=" + String.valueOf(time.getTime()) + "#MLG v"
							+ var.VERSION + INFO);

		} catch (final IOException ex) {
			Out.err("Could not create \"" + var.buildIDFile + "\".");
			return;
		}

	}

	/**
	 * This gets the filename of a .jar (typically this one!)
	 * 
	 * @param classFile
	 * @return
	 * @throws IOException
	 * @author Morlok8k
	 */
	public static String getClassLoader(final Class<?> classFile) throws IOException {
		final ClassLoader loader = classFile.getClassLoader();
		String filename = classFile.getName().replace('.', '/') + ".class";
		final URL resource =
				(loader != null) ? loader.getResource(filename) : ClassLoader
						.getSystemResource(filename);
		filename = URLDecoder.decode(resource.toString(), "UTF-8");
		// out(filename);

		// START Garbage removal:
		int bang = filename.indexOf("!");		// remove everything after xxxx.jar
		if (bang == -1) { 						// a real example:
			bang = filename.length();			// jar:file:/home/morlok8k/test.jar!/me/Morlok8k/test/Main.class
		}
		int file = filename.indexOf("file:");	// removes junk from the beginning of the path
		file = file + 5;
		if (file == -1) {
			file = 0;
		}
		if (filename.contains("rsrc:")) {
			Out.err("THIS WAS COMPILED USING \"org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader\"! ");
			Out.err("DO NOT PACKAGE YOUR .JAR'S WITH THIS CLASSLOADER CODE!");
			Out.err("(Your Libraries need to be extracted.)");
			return var.rsrcError;
		}
		if (filename.contains(".jar")) {
			var.isCompiledAsJar = true;
		}
		filename = filename.replace('/', File.separatorChar);
		final String returnString = filename.substring(file, bang);
		// END Garbage removal
		return returnString;
	}

	/**
	 * This gets the TimeStamp (last modified date) of a class file (typically this one!) <br>
	 * <br>
	 * Thanks to Roedy Green at <br>
	 * <a href="http://mindprod.com/jgloss/compiletimestamp.html">http://mindprod .com/jgloss/compiletimestamp.html</a>
	 * 
	 * @param classFile
	 * @return
	 * @throws IOException
	 * @author Morlok8k
	 */
	public static Date getCompileTimeStamp(final Class<?> classFile) throws IOException {
		final ClassLoader loader = classFile.getClassLoader();
		final String filename = classFile.getName().replace('.', '/') + ".class";
		// get the corresponding class file as a Resource.
		final URL resource =
				(loader != null) ? loader.getResource(filename) : ClassLoader
						.getSystemResource(filename);
		final URLConnection connection = resource.openConnection();
		// Note, we are using Connection.getLastModified not File.lastModifed.
		// This will then work both or members of jars or standalone class files.
		// NOTE: NOT TRUE! IT READS THE JAR, NOT THE FILES INSIDE!
		final long time = connection.getLastModified();
		return (time != 0L) ? new Date(time) : null;
	}

	/**
	 * Gets the BuildID for MLG
	 * 
	 * @author Morlok8k
	 * 
	 */
	public static void readBuildID() {

		if (var.inf_loop_protect_BuildID > 10) {
			var.MLG_Last_Modified_Date = new Date(new Long(0));  //set the day to Jan 1, 1970 for failure
			return;
		}
		var.inf_loop_protect_BuildID++;		// this is to prevent an infinite loop (however unlikely)

		if (var.MLGFileName == null) {
			try {
				var.MLGFileName = getClassLoader(var.cls);
			} catch (final Exception e) {
				Out.out("Error: Finding file failed");
				e.printStackTrace();
			}
			if (var.MLGFileName.equals(var.rsrcError)) { return; }
		}

		var.MLGFileNameShort =
				var.MLGFileName.substring(var.MLGFileName.lastIndexOf(var.fileSeparator) + 1,
						var.MLGFileName.length());

		if (var.testing) {
			Out.outD("Currently Running as file:" + var.MLGFileNameShort);
		}

		if (var.MLG_Current_Hash == null) {

			try {
				var.MLG_Current_Hash = MD5.fileMD5(var.MLGFileName);
				// out(hash + "  " + MLGFileName);
			} catch (final Exception e) {
				Out.out("Error: MD5 from file failed");
				e.printStackTrace();
			}
		}

		int tsCount = 0;

		var.timeStamps.clear();

		if (var.MLG_Last_Modified_Date == null) {
			boolean foundLine = false;
			try {
				final BufferedReader in = new BufferedReader(new FileReader(var.buildIDFile));
				String line;

				if (var.testing) {
					Out.outD("TimeStamps in buildIDFile:");
				}
				while ((line = in.readLine()) != null) {

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
						if (line.length() != 0) {
							var.timeStamps.add(line.substring(pos + 1, end));
						}
					}

					//timeStamps.add(line.substring(pos + 1, end));

					if (var.testing) {
						Out.outD(var.timeStamps.get(tsCount));
					}

					tsCount++;

					if (line.contains(var.MLG_Current_Hash)) {
						// out("[DEBUG] Found!");
						foundLine = true;

						if (pos != -1) {
							if (line.substring(0, pos).equals(var.MLG_Current_Hash)) {
								var.MLG_Last_Modified_Long = new Long(line.substring(pos + 1, end));
								var.MLG_Last_Modified_Date = new Date(var.MLG_Last_Modified_Long);

								final Long highestModTime =
										Update.ZipGetModificationTime(var.MLGFileName);
								final long tCalc = var.MLG_Last_Modified_Long - highestModTime;

								if (var.testing) {
									Out.outD("tCalc\tMLG_Last_Modified_Long\thighestModTime"
											+ var.newLine + tCalc + "\t"
											+ var.MLG_Last_Modified_Long + "\t" + highestModTime);
								}

								if (highestModTime == 0L) {

									Out.err("Archive Intergrity Check Failed: .zip/.jar file Issue.");
									Out.err("Archive Intergrity Check Failed: (MLG will still run.  Just note that this may not be an official version.)");

								} else {
									if (tCalc < -15000L) {

										//time is newer?  (.zip file is newer than BuildID)
										Out.err("Archive Intergrity Check Failed: .zip file is newer than BuildID. Offset: "
												+ (tCalc / 1000) + "sec.");
										Out.err("Archive Intergrity Check Failed: (MLG will still run.  Just note that this may not be an official version.)");
									}

									if (tCalc < 15000L) {

										//times are within 30 seconds (+/- 15 seconds) of each other.  (typically 1-2 seconds, but left room for real-world error)
										if (var.testing | var.flag_downloadedBuildID) {
											Out.out("Archive Intergrity Check Passed. Offset: "
													+ (tCalc / 1000) + "sec.");
										}

									} else {
										//times dont match.  (.zip file is older than specified BuildID)
										Out.err("Archive Intergrity Check Failed: .zip file is older than BuildID. Offset: "
												+ (tCalc / 1000) + "sec.");
										Out.err("Archive Intergrity Check Failed: (MLG will still run.  Just note that this may not be an official version.)");
									}
								}
								//return;
							}

						}
					}

				}
				in.close();

				if (foundLine == false) {
					// out("[DEBUG] FoundLine False");
					buildID(false);
					readBuildID();	// yes I'm calling the function from itself. potential infinite loop? possibly. I haven't encountered it yet!
					return;
				}
			} catch (final Exception e) {
				Out.err("Cant Read " + var.buildIDFile + "!");
				Out.err(e.getLocalizedMessage());
				Out.err("");
				// e.printStackTrace();
				buildID(false);
				readBuildID();
				return;

			}
		}

	}

	/**
	 * Updates MLG to the Latest Version
	 * 
	 * @author Morlok8k
	 * 
	 */
	public static boolean updateMLG() {

		buildID(true);		//get latest BuildID file.
		var.MLG_Last_Modified_Date = null;
		readBuildID();

		final Iterator<String> e = var.timeStamps.iterator();
		String s;
		int diff;

		while (e.hasNext()) {
			s = e.next();
			diff = var.MLG_Last_Modified_Date.compareTo(new Date(new Long(s)));

			if (diff < 0) {	// if this is less than 0, there is a new version of MLG on the Internet!
				Out.out("There is a NEW VERSION Of " + var.PROG_NAME + " available online!");

				try {
					final File fileRename = new File(var.MLG_JarFile);
					fileRename.renameTo(new File(var.MLG_JarFile + ".old"));
				} catch (final Exception e1) {
					Out.out("Rename attempt #1 failed!");
					e1.printStackTrace();

					try {
						Misc.copyFile(new File(var.MLG_JarFile), new File(var.MLG_JarFile + ".old"));
						final File fileDelete = new File(var.MLG_JarFile);
						fileDelete.delete();
					} catch (final Exception e2) {
						Out.out("Rename attempt #2 failed!");
						e2.printStackTrace();

						return false;
					}

				}

				final boolean fileSuccess = DownloadFile.downloadFile(var.github_MLG_jar_URL, true);
				if (fileSuccess) {
					Out.out(var.MLG_JarFile + " downloaded.");
					return true;
				}

			}
		}
		return false;

	}

	/**
	 * <b>.zip file Get Modification Time</b><br>
	 * 
	 * Takes a string of a path to a .zip file (or .jar), and and returns a Long of the latest "Last Time Modified". <br>
	 * <br>
	 * 
	 * Thanks to the following:<br>
	 * <a href="http://www.java-examples.com/get-modification-time-zip-entry-example">http://www.java-examples.com/get-modification-time-zip-entry-example</a><br>
	 * <a href="http://www.java-examples.com/get-crc-32-checksum-zip-entry-example">http://www.java-examples.com/get-crc-32-checksum-zip-entry-example</a>
	 * 
	 * @param zipFile
	 * @return
	 * @author Morlok8k
	 */
	public static Long ZipGetModificationTime(final String zipFile) {

		Long highestModTime = 0L;

		try {

			final ZipFile zipF = new ZipFile(zipFile);

			/*
			 * Get list of zip entries using entries method of ZipFile class.
			 */

			final Enumeration<? extends ZipEntry> e = zipF.entries();

			if (var.testing) {
				Out.outD("File Name\t\tCRC\t\tModification Time\n---------------------------------\n");
			}

			while (e.hasMoreElements()) {
				final ZipEntry entry = e.nextElement();

				Long modTime = entry.getTime();

				if (!(entry.getName().toUpperCase().contains(".CLASS"))) {			//ignore highest timestamp for non .class files, as they can be injected into the .jar file much later after compiling.
					modTime = 0L;
				}

				if (highestModTime < modTime) {
					highestModTime = modTime;
				}

				if (var.testing) {

					final String entryName = entry.getName();
					final Date modificationTime = new Date(modTime);
					final String CRC = Long.toHexString(entry.getCrc());

					Out.outD(entryName + "\t" + CRC + "\t" + modificationTime + "\t"
							+ modTime.toString());
				}

			}

			zipF.close();

			return highestModTime;

		} catch (final IOException ioe) {
			Out.out("Error opening zip file" + ioe);
			return 0L;		//return Jan. 1, 1970 12:00 GMT for failures
		}
	}
}
