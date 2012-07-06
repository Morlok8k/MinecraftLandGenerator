package morlok8k.minecraft.landgenerator;

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

import corrodias.minecraft.landgenerator.MLG_Misc;
import corrodias.minecraft.landgenerator.Main;

public class MLG_Update {

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
	 * @author Morlok8k
	 */
	public static void buildID(boolean downloadOnly) {

		// download BuildID from Github.
		boolean fileSuccess =
				MLG_DownloadFile.downloadFile(Main.github_MLG_BuildID_URL, Main.testing);
		if (fileSuccess) {
			Main.out(Main.buildIDFile + " file downloaded.");
			Main.flag_downloadedBuildID = true;

			if (downloadOnly) { return; }

		}

		if (downloadOnly) {
			Main.err("Couldn't Download new " + Main.buildIDFile);
			return;
		}

		// If not available, create.
		// After downloading, check to see if it matches hash.

		if (Main.MLGFileName == null) {
			try {
				Main.MLGFileName = getClassLoader(Main.cls);
			} catch (Exception e) {
				Main.out("Error: Finding file failed");
				e.printStackTrace();
			}
			if (Main.MLGFileName.equals(Main.rsrcError)) { return; }
		}

		if (Main.MLG_Current_Hash == null) {

			try {
				Main.MLG_Current_Hash = MLG_MD5.fileMD5(Main.MLGFileName);
				// out(hash + "  " + MLGFileName);
			} catch (Exception e) {
				Main.out("Error: MLG_MD5 from file failed");
				e.printStackTrace();
			}
		}

		Date time = new Date(new Long(0));

		try {
			time = getCompileTimeStamp(Main.cls);
		} catch (Exception e) {
			Main.out("Error: TimeStamp from file failed");
			e.printStackTrace();
		}
		// out(d.toString());

		boolean notNew = false;
		String INFO = "";
		if (Main.isCompiledAsJar == false) {
			INFO = " (Class File, Not .Jar)";
		}

		try {
			String line;

			BufferedReader inFile = new BufferedReader(new FileReader(Main.buildIDFile));
			BufferedWriter outFile = new BufferedWriter(new FileWriter(Main.buildIDFile + ".temp"));

			while ((line = inFile.readLine()) != null) {

				if (line.contains(Main.MLG_Current_Hash)) {
					notNew = true;
					if (Main.testing) {
						Main.outD("NotNew");
					}
				}

				outFile.write(line);
				outFile.newLine();
			}

			if (notNew == false) {
				outFile.write(Main.MLG_Current_Hash + "=" + String.valueOf(time.getTime())
						+ "# MLG v" + Main.VERSION + INFO);
				outFile.newLine();
			}
			outFile.close();
			inFile.close();

			File fileDelete = new File(Main.buildIDFile);
			fileDelete.delete();
			File fileRename = new File(Main.buildIDFile + ".temp");
			fileRename.renameTo(new File(Main.buildIDFile));

		} catch (FileNotFoundException ex) {
			Main.out("\"" + Main.buildIDFile + "\" file not Found.  Generating New \""
					+ Main.buildIDFile + "\" File");

			MLG_FileWrite.writeTxtFile(Main.buildIDFile,
					Main.MLG_Current_Hash + "=" + String.valueOf(time.getTime()) + "#MLG v"
							+ Main.VERSION + INFO);

		} catch (IOException ex) {
			Main.err("Could not create \"" + Main.buildIDFile + "\".");
			return;
		}

	}

	/**
	 * Gets the BuildID for MLG
	 * 
	 * @author Morlok8k
	 * 
	 */
	public static void readBuildID() {

		if (Main.inf_loop_protect_BuildID > 10) {
			Main.MLG_Last_Modified_Date = new Date(new Long(0));  //set the day to Jan 1, 1970 for failure
			return;
		}
		Main.inf_loop_protect_BuildID++;		// this is to prevent an infinite loop (however unlikely)

		if (Main.MLGFileName == null) {
			try {
				Main.MLGFileName = getClassLoader(Main.cls);
			} catch (Exception e) {
				Main.out("Error: Finding file failed");
				e.printStackTrace();
			}
			if (Main.MLGFileName.equals(Main.rsrcError)) { return; }
		}

		Main.MLGFileNameShort =
				Main.MLGFileName.substring(Main.MLGFileName.lastIndexOf(Main.fileSeparator) + 1,
						Main.MLGFileName.length());

		if (Main.testing) {
			Main.outD("Currently Running as file:" + Main.MLGFileNameShort);
		}

		if (Main.MLG_Current_Hash == null) {

			try {
				Main.MLG_Current_Hash = MLG_MD5.fileMD5(Main.MLGFileName);
				// out(hash + "  " + MLGFileName);
			} catch (Exception e) {
				Main.out("Error: MLG_MD5 from file failed");
				e.printStackTrace();
			}
		}

		int tsCount = 0;

		Main.timeStamps.clear();

		if (Main.MLG_Last_Modified_Date == null) {
			boolean foundLine = false;
			try {
				BufferedReader in = new BufferedReader(new FileReader(Main.buildIDFile));
				String line;

				if (Main.testing) {
					Main.outD("TimeStamps in buildIDFile:");
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
							Main.timeStamps.add(line.substring(pos + 1, end));
						}
					}

					//timeStamps.add(line.substring(pos + 1, end));

					if (Main.testing) {
						Main.outD(Main.timeStamps.get(tsCount));
					}

					tsCount++;

					if (line.contains(Main.MLG_Current_Hash)) {
						// out("[DEBUG] Found!");
						foundLine = true;

						if (pos != -1) {
							if (line.substring(0, pos).equals(Main.MLG_Current_Hash)) {
								Main.MLG_Last_Modified_Long =
										new Long(line.substring(pos + 1, end));
								Main.MLG_Last_Modified_Date = new Date(Main.MLG_Last_Modified_Long);

								Long highestModTime =
										MLG_Update.ZipGetModificationTime(Main.MLGFileName);
								long tCalc = Main.MLG_Last_Modified_Long - highestModTime;

								if (Main.testing) {
									Main.outD("tCalc\tMLG_Last_Modified_Long\thighestModTime"
											+ Main.newLine + tCalc + "\t"
											+ Main.MLG_Last_Modified_Long + "\t" + highestModTime);
								}

								if (highestModTime == 0L) {

									Main.err("Archive Intergrity Check Failed: .zip/.jar file Issue.");
									Main.err("Archive Intergrity Check Failed: (MLG will still run.  Just note that this may not be an official version.)");

								} else {
									if (tCalc < -15000L) {

										//time is newer?  (.zip file is newer than BuildID)
										Main.err("Archive Intergrity Check Failed: .zip file is newer than BuildID. Offset: "
												+ (tCalc / 1000) + "sec.");
										Main.err("Archive Intergrity Check Failed: (MLG will still run.  Just note that this may not be an official version.)");
									}

									if (tCalc < 15000L) {

										//times are within 30 seconds (+/- 15 seconds) of each other.  (typically 1-2 seconds, but left room for real-world error)
										if (Main.testing | Main.flag_downloadedBuildID) {
											Main.out("Archive Intergrity Check Passed. Offset: "
													+ (tCalc / 1000) + "sec.");
										}

									} else {
										//times dont match.  (.zip file is older than specified BuildID)
										Main.err("Archive Intergrity Check Failed: .zip file is older than BuildID. Offset: "
												+ (tCalc / 1000) + "sec.");
										Main.err("Archive Intergrity Check Failed: (MLG will still run.  Just note that this may not be an official version.)");
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
			} catch (Exception e) {
				Main.err("Cant Read " + Main.buildIDFile + "!");
				Main.err(e.getLocalizedMessage());
				Main.err("");
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
	public static void updateMLG() {

		buildID(true);		//get latest BuildID file.  
		Main.MLG_Last_Modified_Date = null;
		readBuildID();

		Iterator<String> e = Main.timeStamps.iterator();
		String s;
		int diff;

		//boolean renameFailed = false;

		while (e.hasNext()) {
			s = e.next();
			diff = Main.MLG_Last_Modified_Date.compareTo(new Date(new Long(s)));
			//out(diff);

			if (diff < 0) {	// if this is less than 0, there is a new version of MLG on the Internet!
				Main.out("There is a NEW VERSION Of " + Main.PROG_NAME + " available online!");

				try {
					File fileRename = new File(Main.MLG_JarFile);
					fileRename.renameTo(new File(Main.MLG_JarFile + ".old"));
				} catch (Exception e1) {
					Main.out("Rename attempt #1 failed!");
					e1.printStackTrace();

					try {
						MLG_Misc.copyFile(new File(Main.MLG_JarFile), new File(Main.MLG_JarFile
								+ ".old"));
						File fileDelete = new File(Main.MLG_JarFile);
						fileDelete.delete();
					} catch (Exception e2) {
						Main.out("Rename attempt #2 failed!");
						e2.printStackTrace();
						//renameFailed = true;
						return;
					}

				}

				boolean fileSuccess = MLG_DownloadFile.downloadFile(Main.github_MLG_jar_URL, true);
				if (fileSuccess) {
					Main.out(Main.MLG_JarFile + " downloaded.");
					return;
				}

			}
		}

	}

	/**
	 * This gets the filename of a .jar (typically this one!)
	 * 
	 * @author Morlok8k
	 */
	public static String getClassLoader(Class<?> classFile) throws IOException {
		ClassLoader loader = classFile.getClassLoader();
		String filename = classFile.getName().replace('.', '/') + ".class";
		URL resource =
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
			Main.err("THIS WAS COMPILED USING \"org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader\"! ");
			Main.err("DO NOT PACKAGE YOUR .JAR'S WITH THIS CLASSLOADER CODE!");
			Main.err("(Your Libraries need to be extracted.)");
			return Main.rsrcError;
		}
		if (filename.contains(".jar")) {
			Main.isCompiledAsJar = true;
		}
		filename = filename.replace('/', File.separatorChar);
		String returnString = filename.substring(file, bang);
		// END Garbage removal
		return returnString;
	}

	/**
	 * This gets the TimeStamp (last modified date) of a class file (typically this one!) <br>
	 * <br>
	 * Thanks to Roedy Green at <br>
	 * <a href="http://mindprod.com/jgloss/compiletimestamp.html">http://mindprod .com/jgloss/compiletimestamp.html</a>
	 * 
	 * @author Morlok8k
	 */
	public static Date getCompileTimeStamp(Class<?> classFile) throws IOException {
		ClassLoader loader = classFile.getClassLoader();
		String filename = classFile.getName().replace('.', '/') + ".class";
		// get the corresponding class file as a Resource.
		URL resource =
				(loader != null) ? loader.getResource(filename) : ClassLoader
						.getSystemResource(filename);
		URLConnection connection = resource.openConnection();
		// Note, we are using Connection.getLastModified not File.lastModifed.
		// This will then work both or members of jars or standalone class files.
		// NOTE: NOT TRUE! IT READS THE JAR, NOT THE FILES INSIDE!
		long time = connection.getLastModified();
		return (time != 0L) ? new Date(time) : null;
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
	 * @param timeBuildID
	 * @author Morlok8k
	 */
	public static Long ZipGetModificationTime(String zipFile) {

		Long highestModTime = 0L;

		try {

			ZipFile zipF = new ZipFile(zipFile);

			/*
			 * Get list of zip entries using entries method of ZipFile class.
			 */

			Enumeration<? extends ZipEntry> e = zipF.entries();

			if (Main.testing) {
				Main.outD("File Name\t\tCRC\t\tModification Time\n---------------------------------\n");
			}

			while (e.hasMoreElements()) {
				ZipEntry entry = e.nextElement();

				Long modTime = entry.getTime();

				if (!(entry.getName().toUpperCase().contains(".CLASS"))) {			//ignore highest timestamp for non .class files, as they can be injected into the .jar file much later after compiling.
					modTime = 0L;
				}

				if (highestModTime < modTime) {
					highestModTime = modTime;
				}

				if (Main.testing) {

					String entryName = entry.getName();
					Date modificationTime = new Date(modTime);
					String CRC = Long.toHexString(entry.getCrc());

					Main.outD(entryName + "\t" + CRC + "\t" + modificationTime + "\t"
							+ modTime.toString());
				}

			}

			zipF.close();

			return highestModTime;

		} catch (IOException ioe) {
			Main.out("Error opening zip file" + ioe);
			return 0L;		//return Jan. 1, 1970 12:00 GMT for failures
		}
	}

}
